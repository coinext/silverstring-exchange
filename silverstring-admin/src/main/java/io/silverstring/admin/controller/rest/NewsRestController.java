package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.NewsService;
import io.silverstring.domain.dto.NewsDTO;
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
@RequestMapping("/api/news")
public class NewsRestController {

    final NewsService newsService;

    @Autowired
    public NewsRestController(NewsService newsService) {
        this.newsService = newsService;
    }


    @PostMapping("/getAll")
    public Response<NewsDTO.ResNews> getAll(@Valid @RequestBody NewsDTO.ReqNews request) {
        return Response.<NewsDTO.ResNews>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(newsService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody NewsDTO.ReqAdd request) {
        newsService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody NewsDTO.ReqEdit request) {
        newsService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody NewsDTO.ReqDel request) {
        newsService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
