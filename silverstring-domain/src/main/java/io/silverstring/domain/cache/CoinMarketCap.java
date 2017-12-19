package io.silverstring.domain.cache;

import io.silverstring.domain.dto.CoinMarketCapDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@RedisHash(value = "CoinMarketCap:v2", timeToLive=10l)
public class CoinMarketCap {

    public final static String KEY = "CoinMarketCap";

    @Id
    private String id;

    private List<CoinMarketCapDTO.Ticker> results;
}
