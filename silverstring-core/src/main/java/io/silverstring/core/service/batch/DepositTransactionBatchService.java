package io.silverstring.core.service.batch;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.provider.wallet.SimpleWalletRpcProvider;
import io.silverstring.core.provider.wallet.WalletRpcProviderFactory;
import io.silverstring.core.repository.hibernate.TransactionRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.core.service.CoinService;
import io.silverstring.core.service.WalletService;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.enums.WalletType;
import io.silverstring.domain.hibernate.AdminWallet;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Transaction;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DepositTransactionBatchService {

    final CoinService coinService;
    final WalletRpcProviderFactory walletRpcProviderFactory;
    final MqPublisher mqPublisher;
    final TransactionRepository transactionRepository;
    final WalletRepository walletRepository;
    final WalletService walletService;

    @Autowired
    public DepositTransactionBatchService(CoinService coinService, WalletRpcProviderFactory walletRpcProviderFactory, MqPublisher mqPublisher, TransactionRepository transactionRepository, WalletRepository walletRepository, WalletService walletService) {
        this.coinService = coinService;
        this.walletRpcProviderFactory = walletRpcProviderFactory;
        this.mqPublisher = mqPublisher;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }

    @SoftTransational
    public void doPublishTransaction(CoinEnum coinEnum) throws Exception {
        Coin coin = coinService.getActiveCoin(coinEnum);
        if (coin == null) {
            log.warn("coin status is deactive.");
        }

        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        SimpleWalletRpcProvider.Page page = new SimpleWalletRpcProvider.Page();
        page.setPageNo(coin.getDepositScanPageOffset());
        page.setPageSize(coin.getDepositScanPageSize());

        List<WalletDTO.TransactionInfo> transactionInfos = simpleWalletRpcProvider.getTransactions(
                CategoryEnum.receive
                , page
        );

        if (transactionInfos.size() > 0) {
            transactionInfos.stream().forEach(transactionInfo -> {
                Wallet wallet = walletRepository.findOneByCoinAndAddress(new Coin(coinEnum), transactionInfo.getAddress());
                if (wallet != null) {
                    transactionInfo.setUserId(wallet.getUserId());
                    transactionInfo.setCoinEnum(coinEnum);
                    //bitcoin류 코인의 sender address를 구하기 위함.
                    String senderAddress = simpleWalletRpcProvider.getSendAddressFromTxId(transactionInfo.getTxid(), transactionInfo);
                    transactionInfo.setAddress(senderAddress);
                    mqPublisher.depositTransactionInfoPublish(transactionInfo);
                } else {
                    log.warn("wallet is not exist. {}", transactionInfo.getAddress());
                }
            });
        }

        //save offset.
        if (transactionInfos.size() > 0) {
            coin.setDepositScanPageOffset(coin.getDepositScanPageOffset() + 1);
        } else {
            coin.setDepositScanPageOffset(Integer.valueOf(coin.getDepositScanPageOffset() * 80 / 100));
        }
    }

    @HardTransational
    public void doTransaction(WalletDTO.TransactionInfo transactionInfo) throws Exception {
        Optional<Wallet> existWalletOpt = walletRepository.findOneByUserIdAndCoin(transactionInfo.getUserId(), new Coin(transactionInfo.getCoinEnum()));
        if (!existWalletOpt.isPresent()) {
            log.warn("Not Exist Wallet : {} {} {}", transactionInfo.getUserId(), transactionInfo.getCoinEnum(), transactionInfo.getTxid());
            return;
        }

        Transaction existTransaction = transactionRepository.findOneByCoinAndTxId(new Coin(transactionInfo.getCoinEnum()), transactionInfo.getTxid());
        if (existTransaction != null && !StatusEnum.PENDING.equals(existTransaction.getStatus()) ) {
            log.warn("Exist Transaction : {}", transactionInfo.getUserId(), transactionInfo.getCoinEnum(), existTransaction.getTxId());
            return;
        }

        Coin coin = coinService.getActiveCoin(transactionInfo.getCoinEnum());
        Long confirmation = transactionInfo.getConfirmations().longValue();

        if (coin.getDepositAllowConfirmation() <= confirmation) {
            if (existTransaction != null) {
                existTransaction.setRegDt(existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt());
                existTransaction.setCompleteDtm(LocalDateTime.now());
                existTransaction.setConfirmation(confirmation);
                existTransaction.setStatus(StatusEnum.COMPLETED);
            } else {
                Transaction transaction = new Transaction();
                transaction.setId(KeyGenUtil.generateTxId());
                transaction.setUserId(transactionInfo.getUserId());
                transaction.setCategory(transactionInfo.getCategory());
                transaction.setCoin(new Coin(transactionInfo.getCoinEnum()));
                transaction.setTxId(transactionInfo.getTxid());
                transaction.setAddress(transactionInfo.getAddress());
                transaction.setAmount(transactionInfo.getAmount());
                transaction.setRegDt(LocalDateTime.now());
                transaction.setCompleteDtm(LocalDateTime.now());
                transaction.setConfirmation(confirmation);
                transaction.setStatus(StatusEnum.COMPLETED);
                transactionRepository.save(transaction);
            }

            existWalletOpt.get().setAvailableBalance(existWalletOpt.get().getAvailableBalance().add(transactionInfo.getAmount()));

            //send to admin hot wallet
            BigDecimal realTotalBalance = walletService.getRealWalletTotalBalance(transactionInfo.getCoinEnum(), transactionInfo.getUserId());
            BigDecimal minFee = walletService.getMinFee(transactionInfo.getCoinEnum());
            BigDecimal sendingAmount = realTotalBalance.subtract(minFee).setScale(8, RoundingMode.DOWN);
            if (coin.getAutoCollectMinAmount().doubleValue() <= realTotalBalance.doubleValue() && realTotalBalance.doubleValue() > minFee.doubleValue() && sendingAmount.doubleValue() > 0) {
                AdminWallet adminWallet = walletService.increaseAvailableAdminBalance(transactionInfo.getCoinEnum(), WalletType.COLD, sendingAmount);
                log.info("adminWallet : {} {} {}", adminWallet.getAddress(), realTotalBalance.toPlainString(), sendingAmount.toPlainString());
                String txId = walletService.sendTo(transactionInfo.getCoinEnum(), existWalletOpt.get().getUserId(), existWalletOpt.get().getAddress(), adminWallet.getAddress(), sendingAmount, minFee);
                log.info("Admin_Collect_txId : {} ", txId);
            }
        } else {
            if (existTransaction != null) {
                existTransaction.setRegDt(existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt());
                existTransaction.setCompleteDtm(null);
                existTransaction.setConfirmation(confirmation);
                existTransaction.setStatus(StatusEnum.PENDING);
            } else {
                Transaction transaction = new Transaction();
                transaction.setId(KeyGenUtil.generateTxId());
                transaction.setUserId(transactionInfo.getUserId());
                transaction.setCategory(transactionInfo.getCategory());
                transaction.setCoin(new Coin(transactionInfo.getCoinEnum()));
                transaction.setTxId(transactionInfo.getTxid());
                transaction.setAddress(transactionInfo.getAddress());
                transaction.setAmount(transactionInfo.getAmount());
                transaction.setRegDt(LocalDateTime.now());
                transaction.setCompleteDtm(null);
                transaction.setConfirmation(confirmation);
                transaction.setStatus(StatusEnum.PENDING);
                transactionRepository.save(transaction);
            }
        }
    }
}
