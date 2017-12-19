package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.NoticeService;
import io.silverstring.domain.dto.NoticeDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.Notice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/notice")
public class NoticeRestController {

    final NoticeService noticeService;

    @Autowired
    public NoticeRestController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/getAll")
    public Response<NoticeDTO.ResNotice> getAll(@Valid @RequestBody NoticeDTO.ReqNotice request) {
        return Response.<NoticeDTO.ResNotice>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(noticeService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody NoticeDTO.ReqAdd request) {
        noticeService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody NoticeDTO.ReqEdit request) {
        noticeService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody NoticeDTO.ReqDel request) {
        noticeService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
