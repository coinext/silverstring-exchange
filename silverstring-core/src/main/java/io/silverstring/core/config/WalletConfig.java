package io.silverstring.core.config;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import io.silverstring.core.provider.wallet.proxy.BitcoinWalletRpcProxyProvider;
import io.silverstring.core.provider.wallet.proxy.DashWalletRpcProxyProvider;
import io.silverstring.core.provider.wallet.proxy.EthereumWalletRpcProxyProvider;
import io.silverstring.core.provider.wallet.proxy.LitecoinWalletRpcProxyProvider;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class WalletConfig {

    @Value("${wallet.bitcoin.rpc.url}")
    String WALLET_BITCOIN_RPC_URL;

    @Value("${wallet.litecoin.rpc.url}")
    String WALLET_LITECOIN_RPC_URL;

    @Value("${wallet.dash.rpc.url}")
    String WALLET_DASH_RPC_URL;

    @Value("${wallet.ethereum.rpc.url}")
    String WALLET_ETHEREUM_RPC_URL;


    public static JsonRpcHttpClient genJsonRpcHttpClient(String url) throws MalformedURLException {
        URL _url = new URL(url);
        JsonRpcHttpClient client = new JsonRpcHttpClient(_url);
        Map<String, String> header = new HashMap<>();
        String authStr = _url.getUserInfo() == null ? null : Base64.encodeBase64String(_url.getUserInfo().getBytes(Charset.forName("ISO8859-1")));
        header.put("Authorization", "Basic " + authStr);
        client.setHeaders(header);
        return client;
    }

    @Bean
    public BitcoinWalletRpcProxyProvider bitcoinRpcProxyProvider() throws MalformedURLException {
        BitcoinWalletRpcProxyProvider bitcoinRpcProxyProvider = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                BitcoinWalletRpcProxyProvider.class,
                WalletConfig.genJsonRpcHttpClient(
                        WALLET_BITCOIN_RPC_URL
                ));
        return bitcoinRpcProxyProvider;
    }

    @Bean
    public LitecoinWalletRpcProxyProvider litecoinRpcProxyProvider() throws MalformedURLException {
        LitecoinWalletRpcProxyProvider litecoinRpcProxyProvider = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                LitecoinWalletRpcProxyProvider.class,
                WalletConfig.genJsonRpcHttpClient(
                        WALLET_LITECOIN_RPC_URL
                ));
        return litecoinRpcProxyProvider;
    }

    @Bean
    public DashWalletRpcProxyProvider dashRpcProxyProvider() throws MalformedURLException {
        DashWalletRpcProxyProvider dashWalletRpcProxyProvider = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                DashWalletRpcProxyProvider.class,
                WalletConfig.genJsonRpcHttpClient(
                        WALLET_DASH_RPC_URL
                ));
        return dashWalletRpcProxyProvider;
    }

    @Bean
    public Web3j ethereumWalletWeb3jProxyProvider() {
        Web3j ethereumWalletWeb3jProxyProvider = Web3j.build(new HttpService(WALLET_ETHEREUM_RPC_URL));
        return ethereumWalletWeb3jProxyProvider;
    }

    @Bean
    public EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider() throws MalformedURLException {
        EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                EthereumWalletRpcProxyProvider.class,
                WalletConfig.genJsonRpcHttpClient(
                        WALLET_ETHEREUM_RPC_URL
                ));
        return ethereumWalletRpcProxyProvider;
    }
}
