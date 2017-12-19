package io.silverstring.web.controller.batch;

import io.silverstring.core.service.batch.ResetWithdrawalLimitTotalBalanceBatchService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/batch/resetWithdrawalLimitTotalBalance")
public class ResetWithdrawalLimitTotalBalanceBatchController {

    final ResetWithdrawalLimitTotalBalanceBatchService resetWithdrawalLimitTotalBalanceBatchService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public ResetWithdrawalLimitTotalBalanceBatchController(ResetWithdrawalLimitTotalBalanceBatchService resetWithdrawalLimitTotalBalanceBatchService, Environment environment, RedissonClient redissonClient) {
        this.resetWithdrawalLimitTotalBalanceBatchService = resetWithdrawalLimitTotalBalanceBatchService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(fixedDelay=86400_000, initialDelay=86400_000)
    private synchronized void doResetWithdrawalLimitTotalBalance() {
        String key = environment.getActiveProfiles()[0] + "_doResetWithdrawalLimitTotalBalance";
        log.info("### doResetWithdrawalLimitTotalBalance ready {} ### ", key);
        RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(86400, TimeUnit.MINUTES);
            log.info("### doResetWithdrawalLimitTotalBalance begin ###");

            resetWithdrawalLimitTotalBalanceBatchService.dailyReset();

            log.info("### doResetWithdrawalLimitTotalBalance end ###");
            lock.unlock();
        }
    }
}
