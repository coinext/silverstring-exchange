package io.silverstring.admin.controller.rest;

import io.silverstring.core.service.CouponService;
import io.silverstring.domain.dto.CouponDTO;
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
@RequestMapping("/api/coupon")
public class CouponRestController {

    final CouponService couponService;

    @Autowired
    public CouponRestController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/getAll")
    public Response<CouponDTO.ResCoupon> getAll(@Valid @RequestBody CouponDTO.ReqCoupon request) {
        return Response.<CouponDTO.ResCoupon>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .data(couponService.getAll(request.getPageNo(), request.getPageSize()))
                .build();
    }

    @PostMapping("/add")
    public Response add(@Valid @RequestBody CouponDTO.ReqAdd request) {
        couponService.add(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody CouponDTO.ReqEdit request) {
        couponService.edit(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/del")
    public Response del(@Valid @RequestBody CouponDTO.ReqDel request) {
        couponService.del(request);
        return Response.builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMessage())
                .build();
    }
}
