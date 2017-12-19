package io.silverstring.core.provider.wallet;

import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.provider.wallet.proxy.EthereumWalletRpcProxyProvider;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.WalletType;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Convert;
import rx.Observable;
import rx.observables.BlockingObservable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


@Slf4j
@Service
public class EthereumWalletRpcProvider implements SimpleWalletRpcProvider {
    final Integer START_BLOCK_NUMBER = 4508590;
    final WalletRepository walletRepository;
    final ModelMapper modelMapper;
    final Web3j ethereumWalletWeb3jProxyProvider;
    final EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider;

    @Autowired
    public EthereumWalletRpcProvider(WalletRepository walletRepository, ModelMapper modelMapper, Web3j ethereumWalletWeb3jProxyProvider, EthereumWalletRpcProxyProvider ethereumWalletRpcProxyProvider1) {
        this.walletRepository = walletRepository;
        this.modelMapper = modelMapper;
        this.ethereumWalletWeb3jProxyProvider = ethereumWalletWeb3jProxyProvider;
        this.ethereumWalletRpcProxyProvider = ethereumWalletRpcProxyProvider1;
    }

    @Override
    public BigDecimal getMinFee() {
        return new BigDecimal(100000);
    }

    @Override
    public Coin getCoin() {
        Coin coin = new Coin();
        coin.setName(CoinEnum.ETHEREUM);
        return coin;
    }

    @Override
    public List<WalletDTO.TransactionInfo> getTransactions(CategoryEnum categoryEnum, Page page) throws Exception {
        BigInteger currentBlockNumber = ethereumWalletWeb3jProxyProvider.ethBlockNumber().send().getBlockNumber();
        BigInteger startBlock = BigInteger.valueOf(currentBlockNumber.longValue() - 5500); //5500컨펌의 기준으로 처리 (2일정도의 시간)
        Observable<EthBlock> ethBlockObservable = ethereumWalletWeb3jProxyProvider.replayBlocksObservable(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock.longValue() + page.getPageNo() * page.getPageSize()))
                ,DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock.longValue() + (page.getPageNo() + 1) * page.getPageSize()))
                , true
        );

        Map<String, Wallet> existWalletMap = new HashMap<>();
        List<Wallet> existWallets = walletRepository.findAllByCoin(getCoin());
        for (Wallet existWallet : existWallets) {
            if (!StringUtils.isEmpty(existWallet.getAddress())) {
                existWalletMap.put(existWallet.getAddress(), existWallet);
            }
        }

        List<WalletDTO.TransactionInfo> transactionInfos = new ArrayList<>();
        BlockingObservable.from(ethBlockObservable).subscribe(block -> {
            List<EthBlock.TransactionResult> transactions =  block.getResult().getTransactions();
            for (EthBlock.TransactionResult transaction : transactions) {
                EthBlock.TransactionObject transactionHash = (EthBlock.TransactionObject)transaction.get();

                CategoryEnum ethCategory = CategoryEnum.send;
                if (existWalletMap.containsKey(transactionHash.getTo())) {
                    ethCategory = CategoryEnum.receive;
                }

                if (categoryEnum.equals(ethCategory)) {
                    transactionInfos.add(
                            WalletDTO.TransactionInfo.builder()
                                    .address(transactionHash.getTo())
                                    .amount(Convert.fromWei(new BigDecimal(transactionHash.getValue()), Convert.Unit.ETHER))
                                    .category(ethCategory)
                                    .confirmations(currentBlockNumber.subtract(transactionHash.getBlockNumber()))
                                    //.timereceived(LocalDateTime.now()) //TODO 이거 채우면 mq오작동. publishing 안됨..
                                    .txid(transactionHash.getHash())
                                    .build()
                    );
                }
            }

        });
        return transactionInfos;
    }

    @Override
    public String getAccount(Long userId) {
        return SimpleWalletRpcProvider.ADDRESS_PREFIX + String.valueOf(userId);
    }

    @Override
    public String getSendAddressFromTxId(String txid, WalletDTO.TransactionInfo transactionInfo) {
        return transactionInfo.getAddress();
    }

    @Override
    public WalletDTO.WalletCreateInfo createWallet(Long userId) {
        String address = ethereumWalletRpcProxyProvider.personal_newAccount(getAccount(userId));
        return WalletDTO.WalletCreateInfo.builder().address(address).tag("").build();
    }

    @Override
    public BigDecimal getRealBalance(Long userId) {
        Optional<Wallet> wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(getCoin().getName()));
        if (!wallet.isPresent()) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }
        String _balance = ethereumWalletRpcProxyProvider.eth_getBalance(wallet.get().getAddress(), "latest").substring(2);
        BigDecimal tmpBalance = new BigDecimal(new BigInteger(_balance, 16));
        String bal = tmpBalance.toString();
        if ("0".equals(bal)) {
            return new BigDecimal("0.0");
        }
        String k = String.format("%019.0f", tmpBalance);
        return new BigDecimal(k.substring(0, k.length()-18) + "." + k.substring(k.length() - 18));
    }

    @Override
    public WalletDTO.TransactionInfo getTransaction(String txid) {
        return null;
    }

    private String _send(String password, String fromAddress, String toaddress, Double amount, BigDecimal txFee)  throws Exception {
        ethereumWalletRpcProxyProvider.personal_unlockAccount(fromAddress, password, new BigInteger("3600"));
        BigInteger gasPrice = Convert.toWei(txFee, Convert.Unit.FINNEY).divideToIntegralValue(getMinFee()).toBigInteger(); ///txFee.multiply(FENNY_UNIT).divide(getMinFee()).toBigInteger();
        return ethereumWalletRpcProxyProvider.eth_sendTransaction(
                EthereumWalletRpcProxyProvider.EthTransaction.builder()
                        .from(fromAddress)
                        .to(toaddress)
                        .gas(String.format("0x%x", getMinFee()))
                        .gasPrice(String.format("0x%x", gasPrice))
                        //.value(String.format("0x%x", new BigDecimal(new BigDecimal(amount).toPlainString()).multiply(FENNY_UNIT).toBigInteger()))
                        .value(String.format("0x%x", Convert.toWei(new BigDecimal(amount), Convert.Unit.ETHER).toBigInteger()))
                        .data("0x")
                        .build()
        );
    }

    @Override
    public String sendTo(Long sendUserId, String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return _send(getAccount(sendUserId), fromAddress, toaddress, amount, txFee);
    }

    @Override
    public String sendFromHotWallet(String fromAddress, String toaddress, Double amount, BigDecimal txFee) throws Exception {
        return _send(WalletType.HOT.name(), fromAddress, toaddress, amount, txFee);
    }
}
