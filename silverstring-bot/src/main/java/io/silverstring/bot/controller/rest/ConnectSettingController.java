package io.silverstring.bot.controller.rest;

import io.silverstring.bot.dto.ConnectDTO;
import io.silverstring.bot.service.BotConnectSettingService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/connectSetting")
public class ConnectSettingController {

    final BotConnectSettingService botConnectSettingService;

    @Autowired
    public ConnectSettingController(BotConnectSettingService botConnectSettingService) {
        this.botConnectSettingService = botConnectSettingService;
    }

    @PostMapping("/getAll")
    public Response<ConnectDTO.ResGetAll> getAll(@ModelAttribute User user, @Valid @RequestBody ConnectDTO.ReqGetAll request) {
        return Response.<ConnectDTO.ResGetAll>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(botConnectSettingService.getAll(user, request))
                .build();
    }

    @PostMapping("/add")
    public Response add(@ModelAttribute User user, @Valid @RequestBody ConnectDTO.ReqAdd request) {
        request.setUserId(user.getId());
        botConnectSettingService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody ConnectDTO.ReqEdit request) {
        botConnectSettingService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody ConnectDTO.ReqDel request) {
        botConnectSettingService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
