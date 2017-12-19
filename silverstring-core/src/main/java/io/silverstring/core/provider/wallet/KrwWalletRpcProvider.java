package io.silverstring.core.provider.wallet;

import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.TransactionRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.core.service.VirtualAccountService;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.*;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Transaction;
import io.silverstring.domain.hibernate.VirtualAccount;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class KrwWalletRpcProvider implements SimpleWalletRpcProvider {

    private final VirtualAccountService virtualAccountService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public KrwWalletRpcProvider(VirtualAccountService virtualAccountService, WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.virtualAccountService = virtualAccountService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal(0.0001);
    }

    @Override
    public List<WalletDTO.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) throws Exception {
        org.springframework.data.domain.Page<Transaction> transactionPage = transactionRepository.findAllByCoinAndCategoryAndStatus(getCoin(), categoryEnum, StatusEnum.PENDING, new PageRequest(page.getPageNo(), page.getPageSize()));

        List<WalletDTO.TransactionInfo> transactionInfos = new ArrayList<>();
        transactionPage.getContent().stream().forEach(transaction -> {
            transactionInfos.add(
                    WalletDTO.TransactionInfo.builder()
                        .txid(transaction.getTxId())
                        .amount(transaction.getAmount())
                        .address(transaction.getAddress())
                        .category(categoryEnum)
                        .coinEnum(getCoin().getName())
                        .confirmations(BigInteger.valueOf(transaction.getConfirmation()))
                        .build());
            }
        );
        return transactionInfos;
    }

    @Override
    public WalletDTO.WalletCreateInfo createWallet(Long userId) {
        VirtualAccount virtualAccount = virtualAccountService.allocVirtualAccount(userId);
        return WalletDTO.WalletCreateInfo.builder()
                .address(virtualAccount.getAccount())
                .bankName(virtualAccount.getBankName())
                .bankCode(virtualAccount.getBankCode())
                .recvCorpNm(virtualAccount.getRecvCorpNm())
                .build();
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        Optional<Wallet> wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(getCoin().getName()));
        if (!wallet.isPresent()) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }
        return wallet.get().getAvailableBalance();
    }

    @Override
    public WalletDTO.TransactionInfo getTransaction(String txid) {
        return null;
    }

    @Override
    public String sendTo(Long sendUserId, String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return KeyGenUtil.generateTxId();
    }

    @Override
    public String sendFromHotWallet(String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return null;
    }

    @Override
    public Coin getCoin() {
        Coin coin = new Coin();
        coin.setName(CoinEnum.KRW);
        return coin;
    }

    @Override
    public String getAccount(Long userId) {
        return String.valueOf(userId);
    }

    @Override
    public String getSendAddressFromTxId(String txid, WalletDTO.TransactionInfo transactionInfo) {
        return transactionInfo.getAddress();
    }
}
