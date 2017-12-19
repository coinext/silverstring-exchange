package io.silverstring.core.provider.wallet;


import io.silverstring.core.provider.wallet.proxy.SimpleBitcoinWalletRpcProxyProvider;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Wallet;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


public interface SimpleWalletRpcProvider {
    List<WalletDTO.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) throws Exception;
    WalletDTO.WalletCreateInfo createWallet(Long userId);
    BigDecimal getRealBalance(Long userId);
    WalletDTO.TransactionInfo getTransaction(String txid);
    String sendTo(Long sendUserId, String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception;
    String sendFromHotWallet(String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception;
    BigDecimal getMinFee();
    Coin getCoin();
    String getAccount(Long userId);
    String getSendAddressFromTxId(String txid, WalletDTO.TransactionInfo transactionInfo);
    public static String ADDRESS_PREFIX = "v1_";

    @Data
    public static class Page {
        private int pageNo;
        private int pageSize;
    }
}
