package io.silverstring.admin.controller.rest;

import io.silverstring.admin.service.DashboardService;
import io.silverstring.core.service.*;
import io.silverstring.domain.dto.*;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.IcoRecommend;
import io.silverstring.domain.hibernate.Notice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonRestController {

    final CoinMarketCapService coinMarketCapService;
    final NoticeService noticeService;
    final CoinService coinService;
    final IcoRecommendService icoRecommendService;
    final GraphService graphService;
    final DashboardService dashboardService;


    @Autowired
    public CommonRestController(CoinMarketCapService coinMarketCapService, NoticeService noticeService, CoinService coinService, IcoRecommendService icoRecommendService, GraphService graphService, DashboardService dashboardService) {
        this.coinMarketCapService = coinMarketCapService;
        this.noticeService = noticeService;
        this.coinService = coinService;
        this.icoRecommendService = icoRecommendService;
        this.graphService = graphService;
        this.dashboardService = dashboardService;
    }

    @PostMapping("/ticker")
    public Response<List<CoinMarketCapDTO.Ticker>> ticker() {
        return Response.<List<CoinMarketCapDTO.Ticker>>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(coinMarketCapService.getTickers())
                .build();
    }

    @PostMapping("/getTopN1Notice")
    public Response<Notice> getTopN1Notice() {
        return Response.<Notice>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(noticeService.getTopN1Notice())
                .build();
    }

    @PostMapping("/getCoinAvgPrice")
    public Response<CoinDTO.ResCoinAvgPrice> getCoinAvgPrice(@Valid @RequestBody CoinDTO.ReqCoinAvgPrice request) {
        return Response.<CoinDTO.ResCoinAvgPrice>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(coinService.getCoinAvgPrice(request))
                .build();
    }

    @PostMapping("/getAllCoinAvgPrices")
    public Response<List<CoinDTO.ResCoinAvgPrice>> getAllCoinAvgPrices() {
        return Response.<List<CoinDTO.ResCoinAvgPrice>>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(coinService.getAllCoinAvgPrices())
                .build();
    }

    @PostMapping("/getAllIcoRecommend")
    public Response<List<IcoRecommend>> getAllIcoRecommend() {
        return Response.<List<IcoRecommend>>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(icoRecommendService.getAll())
                .build();
    }

    @PostMapping("/get24hGraphData")
    public Response<GraphDTO.ResGraphData24H> get24hGraphData(@Valid @RequestBody GraphDTO.ReqGraphData24H request) {
        return Response.<GraphDTO.ResGraphData24H>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(graphService.get24hGraphData(request.getCoin()))
                .build();
    }

    @PostMapping("/getDashboardInfo")
    public Response<DashboardDTO.ResInfo> getDashboardInfo() {
        return Response.<DashboardDTO.ResInfo>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(dashboardService.getDashboardInfo())
                .build();
    }
}
