package io.silverstring.core.repository.cache;

import io.silverstring.domain.cache.CoinMarketCap;
import org.springframework.data.repository.CrudRepository;


public interface CoinMarketCapRepository extends CrudRepository<CoinMarketCap, String> {
}
