package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.repository.hibernate.CoinRepository;
import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.core.repository.hibernate.OrderRepository;
import io.silverstring.domain.dto.CoinDTO;
import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Slf4j
@Service
public class CoinService {

    final CoinRepository coinRepository;
    final MarketHistoryOrderRepository marketHistoryOrderRepository;

    @Autowired
    public CoinService(CoinRepository coinRepository, MarketHistoryOrderRepository marketHistoryOrderRepository) {
        this.coinRepository = coinRepository;
        this.marketHistoryOrderRepository = marketHistoryOrderRepository;
    }

    @SoftTransational
    public Coin getActiveCoin(CoinEnum coinEnum) {
        Coin coin = coinRepository.findOne(coinEnum);
        if (!ActiveEnum.Y.equals(coin.getActive())) {
            log.warn("coin status is deactive.");
            return null;
        }
        return coin;
    }

    public CoinDTO.ResInfo getCoins() {
        List<Coin> coins = coinRepository.findAllByOrderByDisplayPriorityAsc();
        return CoinDTO.ResInfo.builder().infos(coins).build();
    }

    public List<CoinDTO.ResCoinAvgPrice> getAllCoinAvgPrices() {
        List<CoinDTO.ResCoinAvgPrice> allCoinAvgPrices = new ArrayList<>();
        for (Coin coin : getCoins().getInfos()) {
            if (CoinEnum.KRW.equals(coin.getName())) continue;
            CoinDTO.ResCoinAvgPrice resCoinAvgPrice = getCoinAvgPrice(CoinDTO.ReqCoinAvgPrice.builder().coin(coin.getName()).build());
            allCoinAvgPrices.add(resCoinAvgPrice);
        }
        return allCoinAvgPrices;
    }

    public CoinDTO.ResCoinAvgPrice getCoinAvgPrice(CoinDTO.ReqCoinAvgPrice request) {
        PageRequest pageRequest = new PageRequest(0, 2);
        Page<MarketHistoryOrder> marketHistoryOrders = marketHistoryOrderRepository.getCandidatesToAvgCoinPrice(request.getCoin(), pageRequest);

        List<MarketHistoryOrder> marketHistoryTradeAmountedOrders = marketHistoryOrderRepository.getCandidatesToTradeCoinAmount(request.getCoin(), LocalDateTime.now().minusHours(24), LocalDateTime.now());
        Long totalTradeAmount24h = marketHistoryTradeAmountedOrders.stream().mapToLong(i -> i.getAmount().longValue()).sum();

        CoinDTO.ResCoinAvgPrice resCoinAvgPrice = new CoinDTO.ResCoinAvgPrice();
        Coin coin = coinRepository.findOne(request.getCoin());

        resCoinAvgPrice.setCoinInfo(coin);
        resCoinAvgPrice.setCoin(request.getCoin());

        if (marketHistoryOrders.getTotalElements() <= 0) {
            resCoinAvgPrice.setMarker("");
            resCoinAvgPrice.setTotalTradeAmount24h(0l);
            resCoinAvgPrice.setPrice(0l);
            resCoinAvgPrice.setGapPrice(0l);
            resCoinAvgPrice.setChangePercent(0f);
        } else if (marketHistoryOrders.getTotalElements() == 1) {
            resCoinAvgPrice.setMarker("");
            resCoinAvgPrice.setTotalTradeAmount24h(totalTradeAmount24h);
            resCoinAvgPrice.setPrice(marketHistoryOrders.getContent().get(0).getPrice().longValue());
            resCoinAvgPrice.setGapPrice(0l);
            resCoinAvgPrice.setChangePercent(0f);
        } else if (marketHistoryOrders.getTotalElements() > 1) {
            Long currentPrice = marketHistoryOrders.getContent().get(0).getPrice().longValue();
            Long previousPrice = marketHistoryOrders.getContent().get(1).getPrice().longValue();
            resCoinAvgPrice.setPrice(currentPrice);
            Long percent = ((currentPrice - previousPrice) / previousPrice) * 100;
            String marker = "";
            if (percent > 0) {
                marker = "+";
            } else if (percent < 0) {
                marker = "-";
            }
            resCoinAvgPrice.setTotalTradeAmount24h(totalTradeAmount24h);
            resCoinAvgPrice.setChangePercent(Float.valueOf(Math.abs(percent)));
            resCoinAvgPrice.setGapPrice(Math.abs(currentPrice - previousPrice));
            resCoinAvgPrice.setMarker(marker);
        }
        return resCoinAvgPrice;
    }
}
