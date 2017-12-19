package io.silverstring.core.provider.wallet.proxy;

import java.util.List;
import java.util.Map;


public interface SimpleBitcoinWalletRpcProxyProvider {
    Map<String, String> getinfo();
    Map<String, Object> getrawtransaction(String txid, Integer verbose);
    List<Map<String, Object>> listtransactions(String account, Integer count, Integer from);
    String getaccount(String bitcoinaddress);
    List<String> getaccounts();
    String getaccountaddress(String account);
    List<String> getaddressesbyaccount(String account);
    Object getbalance(String account);
    Map<String, Object> gettransaction(String txid);
    Map<String, Object> decoderawtransaction(String hex);
    String sendmany(String fromaccount, Map<String, Double> amounts) throws Exception;
    String sendfrom(String fromaccount, String tobitcoinaddress, Double amount) throws Exception;
}
