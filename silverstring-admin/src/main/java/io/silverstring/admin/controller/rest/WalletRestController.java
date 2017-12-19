package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.WalletService;
import io.silverstring.domain.dto.NoticeDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.dto.WalletDTO;
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
@RequestMapping("/api/wallet")
public class WalletRestController {

    final WalletService walletService;

    @Autowired
    public WalletRestController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/getAll")
    public Response<WalletDTO.ResWallet> getAll(@Valid @RequestBody WalletDTO.ReqWallet request) {
        return Response.<WalletDTO.ResWallet>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(walletService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody WalletDTO.ReqAdd request) {
        walletService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody WalletDTO.ReqEdit request) {
        walletService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody WalletDTO.ReqDel request) {
        walletService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
