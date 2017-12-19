package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.provider.wallet.SimpleWalletRpcProvider;
import io.silverstring.core.provider.wallet.WalletRpcProviderFactory;
import io.silverstring.core.repository.hibernate.AdminWalletRepository;
import io.silverstring.core.repository.hibernate.CoinRepository;
import io.silverstring.core.repository.hibernate.LevelRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.core.util.CalculateFeeUtil;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.core.util.WalletUtil;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.*;
import io.silverstring.domain.hibernate.*;
import io.silverstring.domain.util.CompareUtil;
import io.silverstring.domain.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final WalletRpcProviderFactory walletRpcProviderFactory;
    private final LevelRepository levelRepository;
    private final AdminWalletRepository adminWalletRepository;
    private final EntityManager entityManager;

    @Autowired
    public WalletService(WalletRepository walletRepository, CoinRepository coinRepository, WalletRpcProviderFactory walletRpcProviderFactory, LevelRepository levelRepository, AdminWalletRepository adminWalletRepository, EntityManager entityManager) {
        this.walletRepository = walletRepository;
        this.coinRepository = coinRepository;
        this.walletRpcProviderFactory = walletRpcProviderFactory;
        this.levelRepository = levelRepository;
        this.adminWalletRepository = adminWalletRepository;
        this.entityManager = entityManager;
    }

    @HardTransational
    public void add(WalletDTO.ReqAdd request) {

    }

    @HardTransational
    public void edit(WalletDTO.ReqEdit request) {

    }

    @HardTransational
    public void del(WalletDTO.ReqDel request) {

    }

    @HardTransational
    public String sendTo(CoinEnum coinEnum, Long senderUserId, String fromAddress, String toAddress, BigDecimal amount, BigDecimal minFee) throws Exception {
        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.sendTo(senderUserId, fromAddress, toAddress, amount.doubleValue(), minFee);
    }

    @HardTransational
    public String sendFromHotWallet(CoinEnum coinEnum, String fromAddress, String toAddress, BigDecimal amount, BigDecimal minFee) throws Exception {
        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.sendFromHotWallet(fromAddress, toAddress, amount.doubleValue(), minFee);
    }

    public WalletDTO.ResWallet getAll(int pageNo, int pageSize) {
        WalletDTO.ResWallet res = new WalletDTO.ResWallet();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<Wallet> results = walletRepository.findAllByOrderByRegDtDesc(new PageRequest(pageNo, pageSize));
        if (results.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(results.getTotalPages());
            return res;
        }

        res.setContents(results.getContent());
        res.setPageTotalCnt(results.getTotalPages());
        return res;
    }


    public Wallet get(Long userId, Coin coin) {
        Optional<Wallet> walletOptional = walletRepository.findOneByUserIdAndCoin(userId, coin);
        if (!walletOptional.isPresent()) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }
        return walletOptional.get();
    }

    public List<Wallet> getAll(Long userId) {
        return walletRepository.findAllByUserId(userId);
    }

    public WalletDTO.WalletInfos getMyWallets(Long userId, LevelEnum levelEnum) {
        List<WalletDTO.WalletInfos.Info> infos = new ArrayList<>();
        for (CoinEnum coinEnum : CoinEnum.values()) {
            Coin coin = coinRepository.findOne(coinEnum);
            Level level = levelRepository.findOneByCoinNameAndLevel(coinEnum, levelEnum);
            Wallet myWallet = walletRepository.findOneByCoinAndUserId(coin, userId);

            if (myWallet == null) {
                infos.add(
                        WalletDTO.WalletInfos.Info.builder()
                        .coin(coin)
                        .wallet(null)
                        .level(level)
                        .build()
                );
            } else {
                infos.add(
                        WalletDTO.WalletInfos.Info.builder()
                                .coin(coin)
                                .wallet(myWallet)
                                .level(level)
                                .build()
                );
            }
        }
        return WalletDTO.WalletInfos.builder().infos(infos).build();
    }

    public MyHistoryOrder increaseAvailableBalanceAndFeeBalance(Order order, Order toOrder, Coin coin, BigDecimal amount, BigDecimal price) {
        MyHistoryOrder myHistoryOrder = new MyHistoryOrder();
        myHistoryOrder.setUserId(order.getUserId());
        myHistoryOrder.setOrderId(order.getId());
        myHistoryOrder.setToUserId(toOrder.getUserId());
        myHistoryOrder.setToOrderId(toOrder.getId());
        myHistoryOrder.setAmount(amount);
        myHistoryOrder.setCompletedDtm(LocalDateTime.now());
        myHistoryOrder.setDt(DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(LocalDateTime.now()));
        myHistoryOrder.setOrderType(order.getOrderType());
        myHistoryOrder.setPrice(price);
        myHistoryOrder.setRegDtm(LocalDateTime.now());
        myHistoryOrder.setStatus(OrderStatus.COMPLETED);
        myHistoryOrder.setToCoin(order.getToCoin());
        myHistoryOrder.setFromCoin(order.getFromCoin());

        if (OrderType.SELL.equals(order.getOrderType())) {
            BigDecimal sellBalance = CalculateFeeUtil.getRealAmount(
                    WalletUtil.scale(amount.multiply(price))
                    , order.getFromCoin().getTradingFeePercent()
            );

            increaseAvailableBalance(
                    order.getUserId()
                    , coin.getName()
                    , sellBalance
            );

            //fee for admin
            increaseAvailableAdminBalance(
                    coin.getName()
                    , WalletType.COLD
                    , CalculateFeeUtil.getFeeAmount(
                            WalletUtil.scale(amount.multiply(price))
                            , order.getFromCoin().getTradingFeePercent()
                    )
            );

            //my history order
            return myHistoryOrder;

        } else if (OrderType.BUY.equals(order.getOrderType())) {
            increaseAvailableBalance(
                    order.getUserId()
                    , coin.getName()
                    , CalculateFeeUtil.getRealAmount(
                            WalletUtil.scale(amount)
                            , order.getFromCoin().getTradingFeePercent()
                    )
            );

            //fee for admin
            increaseAvailableAdminBalance(
                    coin.getName()
                    , WalletType.COLD
                    , CalculateFeeUtil.getFeeAmount(
                            WalletUtil.scale(amount)
                            , order.getFromCoin().getTradingFeePercent()
                    )
            );

            //my history order
            return myHistoryOrder;

        } else {
            throw new ExchangeException(CodeEnum.ORDER_TYPE_INVALID);
        }
    }

    public BigDecimal getMinFee(CoinEnum coinEnum) {
        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.getMinFee();
    }

    public BigDecimal getRealWalletTotalBalance(CoinEnum coinEnum, Long userId) {
        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        return simpleWalletRpcProvider.getRealBalance(userId);
    }

    @HardTransational
    public AdminWallet decreaseAvailableAdminBalance(CoinEnum coinEnum, WalletType walletType, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        AdminWallet wallet = adminWalletRepository.findOneByCoinNameAndType(coinEnum, walletType).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        return wallet;
    }

    @HardTransational
    public AdminWallet increaseAvailableAdminBalance(CoinEnum coinEnum, WalletType walletType, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        AdminWallet wallet = adminWalletRepository.findOneByCoinNameAndType(coinEnum, walletType).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        return wallet;
    }

    @HardTransational
    public void increaseAvailableBalance(Long userId, CoinEnum coinEnum, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum.name())).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
    }

    @HardTransational
    public void decreaseAvailableBalance(Long userId, CoinEnum coinEnum, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum.name())).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero( wallet.getTotalBalance().subtract(amount)))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
    }

    @HardTransational
    public void increaseUsingBalance(Long userId, CoinEnum coinEnum, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum.name())).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        wallet.setUsingBalance(wallet.getUsingBalance().add(amount));
    }

    @HardTransational
    public void decreaseUsingBalance(Long userId, CoinEnum coinEnum, BigDecimal amount) {
        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(amount))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum.name())).orElseThrow(() ->
                new ExchangeException(CodeEnum.WALLET_NOT_EXIST)
        );

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero( wallet.getUsingBalance().subtract(amount)))) {
            new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_ZERO);
        }

        wallet.setUsingBalance(wallet.getUsingBalance().subtract(amount));
    }


    @SoftTransational
    public WalletDTO.ResCreateWallet precreateWallet(Long userId, CoinEnum coinEnum) {
        Optional<Wallet> existWalletOp = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum));
        if (!existWalletOp.isPresent()) {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setAddress(null);
            wallet.setBankCode(null);
            wallet.setBankName(null);
            wallet.setRecvCorpNm(null);
            wallet.setTag(null);
            wallet.setRegDt(LocalDateTime.now());
            wallet.setAvailableBalance(BigDecimal.ZERO);
            wallet.setUsingBalance(BigDecimal.ZERO);
            wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
            wallet.setCoin(new Coin(coinEnum));

            if (CoinEnum.KRW.equals(coinEnum)) {
                wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
            }
            walletRepository.save(wallet);
            entityManager.flush();
            entityManager.clear();
        }
        return WalletDTO.ResCreateWallet.builder().address(null).tag(null).build();
    }

    @SoftTransational
    public WalletDTO.ResCreateWallet createWallet(Long userId, CoinEnum coinEnum) {
        Optional<Wallet> existWalletOp = walletRepository.findOneByUserIdAndCoin(userId, new Coin(coinEnum));
        if (existWalletOp.isPresent()) {
            if (StringUtils.isEmpty(existWalletOp.get().getAddress())) {
                SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
                WalletDTO.WalletCreateInfo newAddress = simpleWalletRpcProvider.createWallet(userId);

                Wallet wallet = existWalletOp.get();
                wallet.setUserId(userId);
                wallet.setAddress(newAddress.getAddress());
                wallet.setBankCode(newAddress.getBankCode());
                wallet.setBankName(newAddress.getBankName());
                wallet.setRecvCorpNm(newAddress.getRecvCorpNm());
                wallet.setTag(newAddress.getTag());
                wallet.setRegDt(LocalDateTime.now());
                wallet.setAvailableBalance(BigDecimal.ZERO);
                wallet.setUsingBalance(BigDecimal.ZERO);
                wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
                wallet.setCoin(new Coin(coinEnum));
                if (CoinEnum.KRW.equals(coinEnum) && StringUtils.isEmpty(wallet.getDepositDvcd())) {
                    wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
                }
                return WalletDTO.ResCreateWallet.builder().address(wallet.getAddress()).tag(wallet.getTag()).build();
            }
            throw new ExchangeException(CodeEnum.WALLET_ALREADY_EXIST);
        }

        SimpleWalletRpcProvider simpleWalletRpcProvider = walletRpcProviderFactory.get(coinEnum);
        WalletDTO.WalletCreateInfo newAddress = simpleWalletRpcProvider.createWallet(userId);

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setAddress(newAddress.getAddress());
        wallet.setBankCode(newAddress.getBankCode());
        wallet.setBankName(newAddress.getBankName());
        wallet.setRecvCorpNm(newAddress.getRecvCorpNm());
        wallet.setTag(newAddress.getTag());
        wallet.setRegDt(LocalDateTime.now());
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setUsingBalance(BigDecimal.ZERO);
        wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
        wallet.setCoin(new Coin(coinEnum));

        if (CoinEnum.KRW.equals(coinEnum)) {
            wallet.setDepositDvcd(KeyGenUtil.generateNumericKey(8));
        }
        walletRepository.save(wallet);
        return WalletDTO.ResCreateWallet.builder().address(wallet.getAddress()).tag(wallet.getTag()).build();
    }
}
