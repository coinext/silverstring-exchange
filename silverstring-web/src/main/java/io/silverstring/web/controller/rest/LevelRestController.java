package io.silverstring.web.controller.rest;

import io.silverstring.core.service.LevelService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/level")
public class LevelRestController {

    final LevelService levelService;

    @Autowired
    public LevelRestController(LevelService levelService) {
        this.levelService = levelService;
    }

    //idcard, doc
    @RequestMapping(value = "/upload/{type}", method = RequestMethod.POST, produces="application/json", consumes="multipart/form-data")
    public Response uploadDoc(@ModelAttribute User user, @PathVariable("type") String type, @RequestParam("file") MultipartFile file) throws IOException {
        levelService.uploadDoc(type, user, file);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/image/{type}/{hash}")
    @ResponseBody
    public void image(@PathVariable("type") String type, @PathVariable("hash") String hash, @ModelAttribute User user, HttpServletResponse response) throws IOException {
        levelService.flushDoc(type, user, hash, response);
    }
}
