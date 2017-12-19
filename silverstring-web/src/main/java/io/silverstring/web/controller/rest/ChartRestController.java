package io.silverstring.web.controller.rest;

import io.silverstring.core.service.ChartService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chart")
public class ChartRestController {

    private final ChartService chartService;

    @Autowired
    public ChartRestController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/{coinName}")
    public List<List<Object>> chart(@PathVariable("coinName") CoinEnum coin) {
        return chartService.getChartData(coin);
    }

    @RequestMapping(value = "/getLastChartData/{coin}", method = RequestMethod.GET)
    public Response<List<Object>> getLastChartData(@PathVariable CoinEnum coin) {
        return Response.<List<Object>>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(chartService.getLastChartData(coin).getRow())
                .build();
    }
}
