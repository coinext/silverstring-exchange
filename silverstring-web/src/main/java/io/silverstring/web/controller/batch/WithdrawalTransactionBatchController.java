package io.silverstring.web.controller.batch;

import io.silverstring.core.service.batch.WithdrawalTransactionBatchService;
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
@RequestMapping("/batch/withdrawalTransaction")
public class WithdrawalTransactionBatchController {

    final WithdrawalTransactionBatchService withdrawalTransactionBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public WithdrawalTransactionBatchController(WithdrawalTransactionBatchService withdrawalTransactionBatchService, Environment environment, RedissonClient redissonClient) {
        this.withdrawalTransactionBatchService = withdrawalTransactionBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(fixedDelay=600_000, initialDelay=600_000)
    private synchronized void doWithdrawalDepositTransaction() {

        String key = environment.getActiveProfiles()[0] + "_doWithdrawalDepositTransaction";
        log.info("### doWithdrawalDepositTransaction ready {} ### ", key);
        RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(10, TimeUnit.MINUTES);
            log.info("### doWithdrawalDepositTransaction begin ###");

            for (CoinEnum coinEnum : CoinEnum.values()) {
                try {
                    withdrawalTransactionBatchService.doPublishTransaction(coinEnum);
                } catch (Exception ex) {
                    log.error("coin : {} {}", coinEnum.name(), ex.getMessage());
                }
            }

            log.info("### doWithdrawalDepositTransaction end ###");
            lock.unlock();
        }
    }

    @GetMapping("/test")
    public String test() throws Exception {
        withdrawalTransactionBatchService.doPublishTransaction(CoinEnum.LITECOIN);
        return "ok";
    }
}
