package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.CouponService;
import io.silverstring.core.service.LevelService;
import io.silverstring.domain.dto.CouponDTO;
import io.silverstring.domain.dto.LevelDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/level")
public class LevelRestController {

    final LevelService levelService;

    @Autowired
    public LevelRestController(LevelService levelService) {
        this.levelService = levelService;
    }

    @PostMapping("/getAll")
    public Response<LevelDTO.ResLevel> getAll(@Valid @RequestBody LevelDTO.ReqLevel request) {
        return Response.<LevelDTO.ResLevel>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(levelService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody LevelDTO.ReqAdd request) {
        levelService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody LevelDTO.ReqEdit request) {
        levelService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody LevelDTO.ReqDel request) {
        levelService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
