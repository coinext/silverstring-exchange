package io.silverstring.core.provider.wallet.proxy;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.List;


public interface EthereumWalletRpcProxyProvider {

    @Slf4j
    @Data
    @Builder
    class EthTransaction {
        private String from;
        private String to;
        private String gas;
        private String gasPrice;
        private String value;
        private String data;
        private BigInteger nonce;  // nonce field is not present on eth_call/eth_estimateGas
        private String passwd;
    }

    String eth_gasPrice();

    String personal_newAccount(String password);

    String personal_unlockAccount(String addr, String passwd, BigInteger duration);

    String eth_getBalance(String address, String latest);

    List<String> eth_accounts();

    String eth_sendTransaction(EthTransaction transaction);
}
