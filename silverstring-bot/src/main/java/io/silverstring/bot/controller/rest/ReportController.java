package io.silverstring.bot.controller.rest;

import io.silverstring.bot.dto.ConnectDTO;
import io.silverstring.bot.dto.ReportDTO;
import io.silverstring.bot.service.BotReportService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/report")
public class ReportController {

    final BotReportService botReportService;

    @Autowired
    public ReportController(BotReportService botReportService) {
        this.botReportService = botReportService;
    }

    @PostMapping("/getAll")
    public Response<ReportDTO.ResGetAll> getAll(@ModelAttribute User user, @Valid @RequestBody ReportDTO.ReqGetAll request) {
        return Response.<ReportDTO.ResGetAll>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(botReportService.getAll(user, request))
                .build();
    }
}
