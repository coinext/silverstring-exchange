package io.silverstring.core.provider.feign;

import io.silverstring.core.config.FeignConfig;
import io.silverstring.domain.dto.CoinMarketCapDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Service
@FeignClient(name = "coinMarketCapProvider", url = "https://api.coinmarketcap.com", configuration = FeignConfig.class)
public interface CoinMarketCapProvider {

    @RequestMapping(method = RequestMethod.GET, value = "/v1/ticker/?convert=KRW&limit=3")
    List<CoinMarketCapDTO.Ticker> ticker();
}
