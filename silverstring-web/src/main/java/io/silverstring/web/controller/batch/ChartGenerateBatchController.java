package io.silverstring.web.controller.batch;

import io.silverstring.core.service.ChartService;
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
@RequestMapping("/batch/chart")
public class ChartGenerateBatchController {

    private final ChartService chartService;
    private final Environment environment;
    private final RedissonClient redissonClient;

    @Autowired
    public ChartGenerateBatchController(ChartService chartService, Environment environment, RedissonClient redissonClient) {
        this.chartService = chartService;
        this.environment = environment;
        this.redissonClient = redissonClient;
    }

    @Scheduled(cron="0 0/1 * * * ?")
    private synchronized void generateChartData() {
        String key = environment.getActiveProfiles()[0] + "_generateChartData";
        log.info("### generateChartData ready {} ### ", key);
        RLock lock = redissonClient.getLock(key);
        if (!lock.isLocked()) {
            lock.lock(1, TimeUnit.MINUTES);
            log.info("### generateChartData begin ###");
            chartService.generateChart();
            log.info("### generateChartData end ###");
            lock.unlock();
        }
    }

    @GetMapping("/test")
    public String test() {
        chartService.generateChart();
        return "ok";
    }
}
