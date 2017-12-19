package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.LevelService;
import io.silverstring.core.service.SubmitDocumentService;
import io.silverstring.domain.dto.DocDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/api/doc")
public class DocRestController {

    final LevelService levelService;
    final SubmitDocumentService submitDocumentService;

    @Autowired
    public DocRestController(LevelService levelService, SubmitDocumentService submitDocumentService) {
        this.levelService = levelService;
        this.submitDocumentService = submitDocumentService;
    }

    @GetMapping("/{type}/{hash}")
    @ResponseBody
    public void image(@PathVariable("type") String type, @PathVariable("hash") String hash, @ModelAttribute User user, HttpServletResponse response) throws IOException {
        levelService.flushDoc(type, user, hash, response);
    }

    @PostMapping("/getAll")
    public Response<DocDTO.ResGet> getAll(@Valid @RequestBody DocDTO.ReqGet request) {
        return Response.<DocDTO.ResGet>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(submitDocumentService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody DocDTO.ReqAdd request) {
        submitDocumentService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody DocDTO.ReqEdit request) {
        submitDocumentService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody DocDTO.ReqDel request) {
        submitDocumentService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
