package io.silverstring.web.controller.rest;

import io.silverstring.core.service.MarketHistoryOrderService;
import io.silverstring.core.service.MyHistoryOrderService;
import io.silverstring.core.service.OrderService;
import io.silverstring.core.service.composite.TradeCompositeService;
import io.silverstring.domain.dto.OrderDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.dto.TradeStatusDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/trade")
public class TradeRestController {

    private final TradeCompositeService tradeCompositeService;
    private final OrderService orderService;
    private final MarketHistoryOrderService marketHistoryOrderService;
    private final MyHistoryOrderService myHistoryOrderService;

    @Autowired
    public TradeRestController(TradeCompositeService tradeCompositeService, OrderService orderService, MarketHistoryOrderService marketHistoryOrderService, MyHistoryOrderService myHistoryOrderService) {
        this.tradeCompositeService = tradeCompositeService;
        this.orderService = orderService;
        this.marketHistoryOrderService = marketHistoryOrderService;
        this.myHistoryOrderService = myHistoryOrderService;
    }

    @PostMapping("/buy")
    public Response<TradeStatusDTO> buy(@ModelAttribute User user, @Valid @RequestBody OrderDTO.ReqOrder request) {
        log.info("buy ===> " + request.toString());
        return Response.<TradeStatusDTO>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(tradeCompositeService.buy(user.getId(), request))
                .build();
    }

    @PostMapping("/sell")
    public Response<TradeStatusDTO> sell(@ModelAttribute User user, @Valid @RequestBody OrderDTO.ReqOrder request) {
        log.info("sell ===> " + request.toString());
        return Response.<TradeStatusDTO>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(tradeCompositeService.sell(user.getId(), request))
                .build();
    }

    @PostMapping("/cancel")
    public Response<OrderDTO.ResCancel> cancel(@ModelAttribute User user, @Valid @RequestBody OrderDTO.ReqCancel request) {
        log.info("cancel ===> " + request.toString());
        return Response.<OrderDTO.ResCancel>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .data(OrderDTO.ResCancel.builder().order(
                        tradeCompositeService.cancel(
                                user.getId()
                                , request.getOrderId())
                        ).build()
                )
                .build();
    }

    @PostMapping("/getHogas")
    public Response<OrderDTO.ResHogaOrders> getHogas(@Valid @RequestBody OrderDTO.ReqHogaOrders request) {
        return Response.<OrderDTO.ResHogaOrders>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(orderService.getHogaOrders(request))
                .build();
    }

    @RequestMapping("/getMyOrders")
    public Response<OrderDTO.ResMyOrders> getMyOrders(
            @ModelAttribute User user,
            @Valid @RequestBody OrderDTO.ReqMyOrders request) throws Exception {

        Page<Order> myOrders = orderService.getMyOrders(user.getId(), request);
        return Response.<OrderDTO.ResMyOrders>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(OrderDTO.ResMyOrders.builder()
                        .orders(myOrders.getContent())
                        .pageNo(request.getPageNo())
                        .pageSize(request.getPageSize())
                        .pageTotalCnt(myOrders.getTotalPages())
                        .build()
                )
                .build();
    }

    @RequestMapping("/getMarketHistoryOrders")
    public Response<OrderDTO.ResMarketHistoryOrders> getMarketHistoryOrders(
            @ModelAttribute User user,
            @Valid @RequestBody OrderDTO.ReqMarketHistoryOrders request) throws Exception {

        return Response.<OrderDTO.ResMarketHistoryOrders>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(OrderDTO.ResMarketHistoryOrders.builder().historyOrders(marketHistoryOrderService.getMarketHistoryOrders(request)).build())
                .build();
    }

    @RequestMapping("/getMyHistoryOrders")
    public Response<OrderDTO.ResMyHistoryOrders> getMyHistoryOrders(
            @ModelAttribute User user,
            @Valid @RequestBody OrderDTO.ReqMyHistoryOrders request) throws Exception {

        Page<MyHistoryOrder> myHistoryOrderPage = myHistoryOrderService.getMyHistoryOrders(user.getId(),request);

        return Response.<OrderDTO.ResMyHistoryOrders>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(OrderDTO.ResMyHistoryOrders.builder()
                        .historyOrders(myHistoryOrderPage.getContent())
                        .pageNo(request.getPageNo())
                        .pageSize(request.getPageSize())
                        .pageTotalCnt(myHistoryOrderPage.getTotalPages())
                        .build())
                .build();
    }
}
