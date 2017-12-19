package io.silverstring.core.provider.feign;

import io.silverstring.core.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Service
@FeignClient(name = "ethereumExplorerProvider", url = "http://api.etherscan.io", configuration = FeignConfig.class)
public interface EthereumExplorerProvider {

    @Data
    public static class Result {
        private String status;
        private String message;
        private List<Map<String, Object>> result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api?module=account&action=txlist&startblock=0&endblock=9999999999999&sort=desc&apikey=YourApiKeyToken")
    Result listTransaction(@RequestParam("address") String address);
}
