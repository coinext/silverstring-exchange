package io.silverstring.web.controller.rest;

import io.silverstring.core.service.WalletService;
import io.silverstring.domain.dto.Response;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/getMyWalletInfos")
    public Response<WalletDTO.WalletInfos> getMyWalletInfos(@ModelAttribute User user) {
        return Response.<WalletDTO.WalletInfos>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(walletService.getMyWallets(user.getId(), user.getLevel()))
                .build();
    }

    @PostMapping("/create")
    public Response<WalletDTO.ResCreateWallet> create(@ModelAttribute User user, @Valid @RequestBody WalletDTO.ReqCreateWallet request) {
        return Response.<WalletDTO.ResCreateWallet>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(walletService.createWallet(user.getId(), CoinEnum.valueOf(request.getCoinName())))
                .build();
    }


}
