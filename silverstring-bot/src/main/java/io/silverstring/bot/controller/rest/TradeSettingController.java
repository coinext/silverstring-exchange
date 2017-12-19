package io.silverstring.bot.controller.rest;

import io.silverstring.bot.dto.TradeSettingDTO;
import io.silverstring.bot.service.BotTradeSettingService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/tradeSetting")
public class TradeSettingController {

    final BotTradeSettingService botTradeSettingService;

    @Autowired
    public TradeSettingController(BotTradeSettingService botTradeSettingService) {
        this.botTradeSettingService = botTradeSettingService;
    }


    @PostMapping("/getAll")
    public Response<TradeSettingDTO.ResGetAll> getAll(@ModelAttribute User user, @Valid @RequestBody TradeSettingDTO.ReqGetAll request) {
        return Response.<TradeSettingDTO.ResGetAll>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(botTradeSettingService.getAll(user, request))
                .build();
    }

    @PostMapping("/add")
    public Response add(@ModelAttribute User user, @Valid @RequestBody TradeSettingDTO.ReqAdd request) {
        request.setUserId(user.getId());
        botTradeSettingService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody TradeSettingDTO.ReqEdit request) {
        botTradeSettingService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody TradeSettingDTO.ReqDel request) {
        botTradeSettingService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
