package io.silverstring.core.provider.wallet;

import io.silverstring.core.provider.feign.BlockchainInfoProvider;
import io.silverstring.core.provider.wallet.proxy.BitcoinWalletRpcProxyProvider;
import io.silverstring.core.provider.wallet.proxy.SimpleBitcoinWalletRpcProxyProvider;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.WalletType;
import io.silverstring.domain.hibernate.Coin;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BitcoinWalletRpcProvider implements SimpleWalletRpcProvider {

    final BlockchainInfoProvider blockchainInfoProvider;
    final BitcoinWalletRpcProxyProvider bitcoinWalletRpcProxyProvider;
    final ModelMapper modelMapper;

    @Autowired
    public BitcoinWalletRpcProvider(BlockchainInfoProvider blockchainInfoProvider, BitcoinWalletRpcProxyProvider bitcoinWalletRpcProxyProvider, ModelMapper modelMapper) {
        this.blockchainInfoProvider = blockchainInfoProvider;
        this.bitcoinWalletRpcProxyProvider = bitcoinWalletRpcProxyProvider;
        this.modelMapper = modelMapper;
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal(0.0008); //0.00065269
    }

    @Override
    public Coin getCoin() {
        Coin coin = new Coin();
        coin.setName(CoinEnum.BITCOIN);
        return coin;
    }

    @Override
    public String getAccount(Long userId) {
        return SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId);
    }

    @Override
    public String getSendAddressFromTxId(String txid, WalletDTO.TransactionInfo transactionInfo) {
        //TODO 더 효율적으로 구하는 방법을 찾다
        /*String address = "UNKNOWN";
        try {
            Map<String, Object> transaction = blockchainInfoProvider.getTransaction(txid);
            for (Object input : ((List<Object>) transaction.get("inputs"))) {
                Map<String, Object> rowInput = (Map<String, Object>) input;
                Map<String, Object> prevOut = (Map<String, Object>) rowInput.get("prev_out");
                if ((boolean) prevOut.get("spent") == true) {
                    address = (String) prevOut.get("addr");
                    return address;
                }
            }
        } catch (Exception ex) {
            log.error("getSendAddressFromTxId {}", ex.getMessage());
        }
        return address;*/
        return transactionInfo.getAddress();
    }

    @Override
    public List<WalletDTO.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) {
        List<Map<String, Object>> transactions = bitcoinWalletRpcProxyProvider.listtransactions(
                "*"
                , page.getPageSize()
                , page.getPageNo()
        );

        List<WalletDTO.TransactionInfo> transactionInfos = modelMapper.map(transactions, new TypeToken<List<WalletDTO.TransactionInfo>>(){}.getType());
        return transactionInfos.stream().filter(r -> {
            if (categoryEnum.equals(r.getCategory())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public WalletDTO.WalletCreateInfo createWallet(Long userId) {
        return WalletDTO.WalletCreateInfo.builder().address(
                bitcoinWalletRpcProxyProvider.getaccountaddress(getAccount(userId))
        ).build();
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        return new BigDecimal(String.valueOf(bitcoinWalletRpcProxyProvider.getbalance(getAccount(userId))));
    }

    @Override
    public WalletDTO.TransactionInfo getTransaction(String txid) {
        Map<String, Object> transaction = bitcoinWalletRpcProxyProvider.gettransaction(txid);
        return modelMapper.map(transaction, WalletDTO.TransactionInfo.class);
    }

    @Override
    public String sendTo(Long sendUserId, String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return bitcoinWalletRpcProxyProvider.sendfrom(getAccount(sendUserId), toaddress, amount);
    }

    @Override
    public String sendFromHotWallet(String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return bitcoinWalletRpcProxyProvider.sendfrom(WalletType.HOT.name(), toaddress, amount);
    }
}
