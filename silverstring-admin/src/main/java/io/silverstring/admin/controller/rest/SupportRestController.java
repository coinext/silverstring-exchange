package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.SupportService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.dto.SupportDTO;
import io.silverstring.domain.enums.CodeEnum;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;


@Slf4j
@RestController
@RequestMapping("/api/support")
public class SupportRestController {

    final SupportService supportService;

    @Autowired
    public SupportRestController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/getAll")
    public Response<SupportDTO.ResSupport> getAll(@Valid @RequestBody SupportDTO.ReqSupport request) {
        return Response.<SupportDTO.ResSupport>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(supportService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/comment")
    public Response comment(@Valid @RequestBody SupportDTO.ReqComment request) throws UnsupportedEncodingException, CannotSendEmailException {
        supportService.comment(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody SupportDTO.ReqDel request) {
        supportService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
