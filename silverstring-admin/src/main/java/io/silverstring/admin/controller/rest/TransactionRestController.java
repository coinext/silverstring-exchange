package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.ManualTransactionService;
import io.silverstring.domain.dto.ManualTransactionDTO;
import io.silverstring.domain.dto.NoticeDTO;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController {

    final ManualTransactionService manualTransactionService;

    @Autowired
    public TransactionRestController(ManualTransactionService manualTransactionService) {
        this.manualTransactionService = manualTransactionService;
    }

    @PostMapping("/{category}/getAll")
    public Response<ManualTransactionDTO.ResManualTransaction> getAll(@PathVariable("category") CategoryEnum category, @Valid @RequestBody ManualTransactionDTO.ReqManualTransaction request) {
        return Response.<ManualTransactionDTO.ResManualTransaction>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(manualTransactionService.getAll(category, request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/deposit_krw/add")
    public Response depositKrwAdd(@Valid @RequestBody ManualTransactionDTO.ReqDepositKrwAdd request) {
        request.setCoin(CoinEnum.KRW);
        request.setCategory(CategoryEnum.receive);
        manualTransactionService.depositKrwAdd(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/deposit_krw/edit")
    public Response depositKrwEdit(@Valid @RequestBody ManualTransactionDTO.ReqDepositKrwEdit request) {
        request.setCoin(CoinEnum.KRW);
        request.setCategory(CategoryEnum.receive);
        manualTransactionService.depositKrwEdit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/deposit_krw/del")
    public Response depositKrwDel(@Valid @RequestBody ManualTransactionDTO.ReqDepositKrwDel request) {
        manualTransactionService.depositKrwDel(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/withdrawal/add")
    public Response withdrawalAdd(@Valid @RequestBody ManualTransactionDTO.ReqWithdrawalAdd request) {
        request.setCategory(CategoryEnum.send);
        manualTransactionService.withdrawalAdd(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/withdrawal/edit")
    public Response withdrawalEdit(@Valid @RequestBody ManualTransactionDTO.ReqWithdrawalEdit request) {
        request.setCategory(CategoryEnum.send);
        manualTransactionService.withdrawalEdit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/withdrawal/del")
    public Response withdrawalDel(@Valid @RequestBody ManualTransactionDTO.ReqWithdrawalDel request) {
        manualTransactionService.withdrawalDel(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
