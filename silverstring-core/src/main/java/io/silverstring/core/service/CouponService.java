package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.CouponRepository;
import io.silverstring.domain.dto.CouponDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Slf4j
@Service
public class CouponService {

    final CouponRepository couponRepository;
    final ModelMapper modelMapper;

    @Autowired
    public CouponService(CouponRepository couponRepository, ModelMapper modelMapper) {
        this.couponRepository = couponRepository;
        this.modelMapper = modelMapper;
    }

    public CouponDTO.ResCoupon getAll(int pageNo, int pageSize) {
        CouponDTO.ResCoupon res = new CouponDTO.ResCoupon();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<Coupon> result = couponRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(pageNo, pageSize));
        if (result.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(result.getTotalPages());
            return res;
        }

        res.setContents(result.getContent());
        res.setPageTotalCnt(result.getTotalPages());
        return res;
    }

    @SoftTransational
    public void add(CouponDTO.ReqAdd request) {
        Coupon res = modelMapper.map(request, Coupon.class);
        res.setRegDtm(LocalDateTime.now());
        couponRepository.save(res);
    }

    @SoftTransational
    public void edit(CouponDTO.ReqEdit request) {
        Coupon res = modelMapper.map(request, Coupon.class);
        Coupon existOne = couponRepository.findOne(res.getId());
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existOne.setName(request.getName());
        existOne.setContent(request.getContent());
        existOne.setImgUrl(request.getImgUrl());
        existOne.setExpireHr(request.getExpireHr());
    }

    @SoftTransational
    public void del(CouponDTO.ReqDel request) {
        Coupon res = modelMapper.map(request, Coupon.class);
        Coupon existOne = couponRepository.findOne(res.getId());
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existOne.setDelDtm(LocalDateTime.now());
    }
}
