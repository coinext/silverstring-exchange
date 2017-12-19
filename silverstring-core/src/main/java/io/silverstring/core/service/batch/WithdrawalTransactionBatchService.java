package io.silverstring.core.service.batch;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.provider.wallet.WalletRpcProviderFactory;
import io.silverstring.core.repository.hibernate.ManualTransactionRepository;
import io.silverstring.core.repository.hibernate.TransactionRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.core.service.CoinService;
import io.silverstring.core.service.WalletService;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.enums.WalletType;
import io.silverstring.domain.hibernate.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class WithdrawalTransactionBatchService {

    final CoinService coinService;
    final WalletRpcProviderFactory walletRpcProviderFactory;
    final MqPublisher mqPublisher;
    final TransactionRepository transactionRepository;
    final ManualTransactionRepository manualTransactionRepository;
    final WalletRepository walletRepository;
    final WalletService walletService;

    @Autowired
    public WithdrawalTransactionBatchService(CoinService coinService, WalletRpcProviderFactory walletRpcProviderFactory, MqPublisher mqPublisher, TransactionRepository transactionRepository, ManualTransactionRepository manualTransactionRepository, WalletRepository walletRepository, WalletService walletService) {
        this.coinService = coinService;
        this.walletRpcProviderFactory = walletRpcProviderFactory;
        this.mqPublisher = mqPublisher;
        this.transactionRepository = transactionRepository;
        this.manualTransactionRepository = manualTransactionRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }

    @SoftTransational
    public void doPublishTransaction(CoinEnum coinEnum) {
        Coin coin = coinService.getActiveCoin(coinEnum);
        if (coin == null) {
            log.warn("coin status is deactive.");
        }
        Page<Transaction> transactionsPage = transactionRepository.findAllByCoinAndCategoryAndStatus(new Coin(coinEnum), CategoryEnum.send, StatusEnum.APPROVAL, new PageRequest(0, 100));
        for (Transaction transaction : transactionsPage.getContent()) {
            log.info("WithdrawalTransactionBatchService:doPublishTransaction:{}", transaction);

            WalletDTO.TransactionInfo transactionInfo = new WalletDTO.TransactionInfo();
            transactionInfo.setUserId(transaction.getUserId());
            transactionInfo.setCoinEnum(transaction.getCoin().getName());
            transactionInfo.setAddress(transaction.getAddress());
            transactionInfo.setCategory(transaction.getCategory());
            transactionInfo.setAmount(transaction.getAmount());
            transactionInfo.setConfirmations(new BigInteger(transaction.getConfirmation().toString()));
            transactionInfo.setTxid(transaction.getTxId());
            transactionInfo.setBankNm(transaction.getBankNm());
            transactionInfo.setRecvNm(transaction.getRecvNm());
            mqPublisher.withdrawalTransactionInfoPublish(transactionInfo);
        }
    }

    @HardTransational
    public void doTransaction(WalletDTO.TransactionInfo transactionInfo) throws Exception {
        Coin coin = coinService.getActiveCoin(transactionInfo.getCoinEnum());
        if (coin == null) {
            log.error("coin is not active or exist");
            return;
        }

        Optional<Wallet> existWalletOpt = walletRepository.findOneByUserIdAndCoin(transactionInfo.getUserId(), new Coin(transactionInfo.getCoinEnum()));
        if (!existWalletOpt.isPresent()) {
            log.warn("[WithdrawalTransactionBatchService] Not Exist Wallet : {} {} {}", transactionInfo.getUserId(), transactionInfo.getCoinEnum(), transactionInfo.getTxid());
            return;
        }

        Transaction existTransaction = transactionRepository.findOneByCoinAndTxId(new Coin(transactionInfo.getCoinEnum()), transactionInfo.getTxid());
        if (existTransaction == null) {
            log.warn("[WithdrawalTransactionBatchService] Not Exist Transaction : {} {} {}", transactionInfo.getUserId(), transactionInfo.getCoinEnum(), transactionInfo.getTxid());
            return;
        }

        if (existTransaction != null && !StatusEnum.APPROVAL.equals(existTransaction.getStatus()) ) {
            log.warn("Exist Transaction : {}", transactionInfo.getUserId(), transactionInfo.getCoinEnum(), existTransaction.getTxId());
            return;
        }

        ManualTransaction existManualTransaction = manualTransactionRepository.findByIdAndUserId(transactionInfo.getTxid(), transactionInfo.getUserId());
        if (existManualTransaction == null) {
            log.warn("[WithdrawalTransactionBatchService] Not Exist ManualTransaction : {} {}", existManualTransaction.getUserId(), existManualTransaction.getId());
            return;
        }

        existManualTransaction.setRegDt(existManualTransaction.getRegDt() == null ? LocalDateTime.now() : existManualTransaction.getRegDt());
        existManualTransaction.setStatus(StatusEnum.COMPLETED);

        existTransaction.setRegDt(existTransaction.getRegDt() == null ? LocalDateTime.now() : existTransaction.getRegDt());
        existTransaction.setCompleteDtm(LocalDateTime.now());
        existTransaction.setConfirmation(10000l);
        existTransaction.setStatus(StatusEnum.COMPLETED);

        //Long userId, CoinEnum coinEnum, BigDecimal amount
        walletService.decreaseUsingBalance(existWalletOpt.get().getUserId(), existWalletOpt.get().getCoin().getName(), transactionInfo.getAmount());
        walletService.decreaseAvailableBalance(existWalletOpt.get().getUserId(), existWalletOpt.get().getCoin().getName(), transactionInfo.getAmount());

        //send from admin hot wallet
        BigDecimal minFee = walletService.getMinFee(transactionInfo.getCoinEnum());
        BigDecimal sendingAmount = transactionInfo.getAmount().subtract(minFee).setScale(8, RoundingMode.DOWN);
        if (sendingAmount.doubleValue() > minFee.doubleValue()) {
            AdminWallet adminWallet = walletService.decreaseAvailableAdminBalance(transactionInfo.getCoinEnum(), WalletType.HOT, sendingAmount);
            log.info("adminWallet : {} {} {}", adminWallet.getAddress(), sendingAmount.toPlainString());
            String txId = walletService.sendFromHotWallet(transactionInfo.getCoinEnum(), adminWallet.getAddress(), transactionInfo.getAddress(), sendingAmount, minFee);
            log.info("Admin_txId : {} ", txId);
        }
    }
}
