package io.silverstring.core.provider.wallet;

import io.silverstring.core.exception.ExchangeException;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class WalletRpcProviderFactory {

    final BitcoinWalletRpcProvider bitcoinRpcProvider;
    final LitecoinWalletRpcProvider litecoinRpcProvider;
    final DashWalletRpcProvider dashRpcProvider;
    final EthereumWalletRpcProvider ethereumWalletRpcProvider;
    final KrwWalletRpcProvider krwWalletRpcProvider;

    @Autowired
    public WalletRpcProviderFactory(BitcoinWalletRpcProvider bitcoinRpcProvider, LitecoinWalletRpcProvider litecoinRpcProvider, DashWalletRpcProvider dashRpcProvider, EthereumWalletRpcProvider ethereumWalletRpcProvider, KrwWalletRpcProvider krwWalletRpcProvider) {
        this.bitcoinRpcProvider = bitcoinRpcProvider;
        this.litecoinRpcProvider = litecoinRpcProvider;
        this.dashRpcProvider = dashRpcProvider;
        this.ethereumWalletRpcProvider = ethereumWalletRpcProvider;
        this.krwWalletRpcProvider = krwWalletRpcProvider;
    }

    public SimpleWalletRpcProvider get(CoinEnum coinEnum) {
        if (CoinEnum.BITCOIN.equals(coinEnum)) {
            return bitcoinRpcProvider;
        } else if (CoinEnum.LITECOIN.equals(coinEnum)) {
            return litecoinRpcProvider;
        } else if (CoinEnum.DASH.equals(coinEnum)) {
            return dashRpcProvider;
        } else if (CoinEnum.ETHEREUM.equals(coinEnum)) {
            return ethereumWalletRpcProvider;
        } else if (CoinEnum.KRW.equals(coinEnum)) {
            return krwWalletRpcProvider;
        } else {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
    }
}
