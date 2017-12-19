package io.silverstring.core.service;

import io.silverstring.core.provider.feign.CoinMarketCapProvider;
import io.silverstring.core.repository.cache.CoinMarketCapRepository;
import io.silverstring.domain.cache.CoinMarketCap;
import io.silverstring.domain.dto.CoinMarketCapDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class CoinMarketCapService {

    private final CoinMarketCapProvider coinMarketCapProvider;
    private final CoinMarketCapRepository coinMarketCapRepository;

    @Autowired
    public CoinMarketCapService(CoinMarketCapProvider coinMarketCapProvider, CoinMarketCapRepository coinMarketCapRepository) {
        this.coinMarketCapProvider = coinMarketCapProvider;
        this.coinMarketCapRepository = coinMarketCapRepository;
    }

    public List<CoinMarketCapDTO.Ticker> getTickers() {
        CoinMarketCap cachedCoinMarketCap = coinMarketCapRepository.findOne(CoinMarketCap.KEY);
        if (cachedCoinMarketCap == null) {
            log.info("make cache CoinMarketCapService::getTickers");
            CoinMarketCap coinMarketCap = new CoinMarketCap();
            coinMarketCap.setId(CoinMarketCap.KEY);
            coinMarketCap.setResults(coinMarketCapProvider.ticker());
            return coinMarketCapRepository.save(coinMarketCap).getResults();
        }
        log.info("hit cache CoinMarketCapService::getTickers");
        return cachedCoinMarketCap.getResults();
    }
}
