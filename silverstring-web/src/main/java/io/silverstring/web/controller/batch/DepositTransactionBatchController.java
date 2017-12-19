package io.silverstring.web.controller.batch;

import io.silverstring.core.service.batch.DepositTransactionBatchService;
import io.silverstring.domain.enums.CoinEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/batch/depositTransaction")
public class DepositTransactionBatchController {

    final DepositTransactionBatchService depositTransactionBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public DepositTransactionBatchController(DepositTransactionBatchService depositTransactionBatchService, Environment environment, RedissonClient redissonClient) {
        this.depositTransactionBatchService = depositTransactionBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(fixedDelay=600_000, initialDelay=600_000)
    private synchronized void doPublishDepositTransaction() {

        String key = environment.getActiveProfiles()[0] + "_doPublishDepositTransaction";
        log.info("### doPublishDepositTransaction ready {} ### ", key);
        RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(10, TimeUnit.MINUTES);
            log.info("### doPublishDepositTransaction begin ###");

            for (CoinEnum coinEnum : CoinEnum.values()) {
                try {
                    depositTransactionBatchService.doPublishTransaction(coinEnum);
                } catch (Exception ex) {
                    log.error("coin : {} {}", coinEnum.name(), ex.getMessage());
                }
            }

            log.info("### doPublishDepositTransaction end ###");
            lock.unlock();
        }
    }

    @GetMapping("/test")
    public String test() throws Exception {
        depositTransactionBatchService.doPublishTransaction(CoinEnum.LITECOIN);
        return "ok";
    }
}
