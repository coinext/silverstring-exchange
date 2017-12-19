package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.handler.WebsockMsgBrocker;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.repository.hibernate.ChartRepository;
import io.silverstring.core.repository.hibernate.CoinRepository;
import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.domain.dto.ChartDTO;
import io.silverstring.domain.dto.MaxMinMarketHistoryOrderDTO;
import io.silverstring.domain.dto.MessagePacket;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.CommandEnum;
import io.silverstring.domain.enums.PacketScopeEnum;
import io.silverstring.domain.hibernate.Chart;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ChartService {

    private final ChartRepository chartRepository;
    private final MarketHistoryOrderRepository marketHistoryOrderRepository;
    private final CoinRepository coinRepository;
    private final WebsockMsgBrocker websockMsgBrocker;
    private final MqPublisher mqPublisher;

    @Autowired
    public ChartService(ChartRepository chartRepository, MarketHistoryOrderRepository marketHistoryOrderRepository, CoinRepository coinRepository, WebsockMsgBrocker websockMsgBrocker, MqPublisher mqPublisher) {
        this.chartRepository = chartRepository;
        this.marketHistoryOrderRepository = marketHistoryOrderRepository;
        this.coinRepository = coinRepository;
        this.websockMsgBrocker = websockMsgBrocker;
        this.mqPublisher = mqPublisher;
    }


    public ChartDTO getLastChartData(CoinEnum coinEnum) {
        String dt = DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(LocalDateTime.now());
        PageRequest pageRequest = new PageRequest(0, 1);
        Page<ChartDTO> result = chartRepository.findOneFromCoinOrderByDtDesc(coinEnum, pageRequest);
        if (result.getContent() == null || result.getContent().size() == 0) return new ChartDTO(dt, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        return result.getContent().get(0);
    }

    @SoftTransational
    public void generateChart() {
        LocalDateTime startDt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endDt = LocalDateTime.now().minusMinutes(1);

        String dt = DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(endDt);

        List<Coin> coins = coinRepository.findAll();
        for (Coin coin : coins) {

            BigDecimal open = BigDecimal.ZERO;
            BigDecimal high = BigDecimal.ZERO;
            BigDecimal low = BigDecimal.ZERO;
            BigDecimal close = BigDecimal.ZERO;

            MaxMinMarketHistoryOrderDTO maxMinMarketHistoryOrderDTO = marketHistoryOrderRepository.getMaxMinMarketHistoryOrderBetweenStartDtAndEndDt(coin.getName(), startDt, endDt);
            if (maxMinMarketHistoryOrderDTO != null) {
                low = maxMinMarketHistoryOrderDTO.getMinPrice() == null ? BigDecimal.ZERO : maxMinMarketHistoryOrderDTO.getMinPrice();
                high = maxMinMarketHistoryOrderDTO.getMaxPrice() == null ? BigDecimal.ZERO : maxMinMarketHistoryOrderDTO.getMaxPrice();
            }

            List<MarketHistoryOrder> marketHistoryOrders = marketHistoryOrderRepository.getMarketHistoryOrderBetweenStartDtAndEndDtOrderDesc(coin.getName(), startDt, endDt);
            if (marketHistoryOrders.size() >= 2) {
                open = marketHistoryOrders.get(marketHistoryOrders.size() - 1).getPrice();
                close = marketHistoryOrders.get(0).getPrice();
            } else if (marketHistoryOrders.size() == 1) {
                open = marketHistoryOrders.get(0).getPrice();
                close = open;
            }

            Chart chart = new Chart();
            if (marketHistoryOrders.size() >= 1) {
                chart.setCoin(coin.getName());
                chart.setDt(dt);
                chart.setPrice(BigDecimal.ZERO);
                chart.setOpen(open);
                chart.setHigh(high);
                chart.setLow(low);
                chart.setClose(close);
                chart.setVolume(BigDecimal.ZERO);
                chart.setAdjClose(BigDecimal.ZERO);
                chartRepository.save(chart);
            } else {
                ChartDTO chartDTO = getLastChartData(coin.getName());
                if (chartDTO != null) {
                    chart.setCoin(coin.getName());
                    chart.setDt(dt);
                    chart.setPrice(BigDecimal.ZERO);
                    chart.setOpen(chartDTO.getClose());
                    chart.setHigh(chartDTO.getClose());
                    chart.setLow(chartDTO.getClose());
                    chart.setClose(chartDTO.getClose());
                    chart.setVolume(BigDecimal.ZERO);
                    chart.setAdjClose(BigDecimal.ZERO);
                    chartRepository.save(chart);
                }
            }

            MessagePacket messagePacket = MessagePacket.builder()
                    .cmd(CommandEnum.CHART)
                    .scope(PacketScopeEnum.PUBLIC)
                    .coin(coin.getName())
                    .data(chart.getRow())
                    .build();
            mqPublisher.websockMessagePublish(messagePacket);
        }
    }

    public List<List<Object>> getChartData(CoinEnum coinEnum) {
        List<ChartDTO> chartDTOs = chartRepository.findAllByOrderByDtASC(coinEnum);
        List<List<Object>> charts = new ArrayList<>();
        for (ChartDTO chartDTO : chartDTOs) {
            charts.add(chartDTO.getRow());
        }
        return charts;
    }
}
