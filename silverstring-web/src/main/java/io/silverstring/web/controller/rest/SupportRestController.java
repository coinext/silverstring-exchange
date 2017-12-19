package io.silverstring.web.controller.rest;

import io.silverstring.core.service.SupportService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.dto.SupportDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/support")
public class SupportRestController {

    private final ModelMapper modelMapper;
    private final SupportService supportService;

    @Autowired
    public SupportRestController(ModelMapper modelMapper, SupportService supportService) {
        this.modelMapper = modelMapper;
        this.supportService = supportService;
    }

    @PostMapping("/getSupports")
    public Response<SupportDTO.ResSupport> getActionLogs(@ModelAttribute User user, @RequestBody SupportDTO.ReqSupport request) {
        return Response.<SupportDTO.ResSupport>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(supportService.getAll(user, request))
                .build();
    }

    @PostMapping("/regist")
    public Response regist(@ModelAttribute User user, @RequestBody SupportDTO.ReqAdd request) {
        supportService.add(user, request);
        return Response.<SupportDTO.ResSupport>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
