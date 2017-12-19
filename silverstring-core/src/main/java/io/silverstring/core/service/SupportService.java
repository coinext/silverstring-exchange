package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.SupportRepository;
import io.silverstring.domain.dto.SupportDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.enums.SupportTypeEnum;
import io.silverstring.domain.hibernate.Support;
import io.silverstring.domain.hibernate.User;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Slf4j
@Service
public class SupportService {

    final SupportRepository supportRepository;
    final ModelMapper modelMapper;
    final UmsService umsService;

    @Autowired
    public SupportService(SupportRepository supportRepository, ModelMapper modelMapper, UmsService umsService) {
        this.supportRepository = supportRepository;
        this.modelMapper = modelMapper;
        this.umsService = umsService;
    }

    public SupportDTO.ResSupport getAll(User user, SupportDTO.ReqSupport request) {
        SupportDTO.ResSupport res = new SupportDTO.ResSupport();
        res.setPageNo(request.getPageNo());
        res.setPageSize(request.getPageSize());

        Page<Support> result = supportRepository.findAllByUserOrderByIdDescParentIdDescRegDtmDesc(user, new PageRequest(request.getPageNo(), request.getPageSize()));
        if (result.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(result.getTotalPages());
            return res;
        }

        res.setContents(result.getContent());
        res.setPageTotalCnt(result.getTotalPages());
        return res;
    }

    public SupportDTO.ResSupport getAll(int pageNo, int pageSize) {
        SupportDTO.ResSupport res = new SupportDTO.ResSupport();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<Support> result = supportRepository.findAll(new PageRequest(pageNo, pageSize));
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
    public void add(User user, SupportDTO.ReqAdd request) {
        Support res = modelMapper.map(request, Support.class);
        res.setUser(user);
        res.setType(SupportTypeEnum.REQUEST);
        res.setRegDtm(LocalDateTime.now());
        res.setStatus(StatusEnum.PENDING);
        supportRepository.save(res);
    }

    @SoftTransational
    public void comment(SupportDTO.ReqComment request) throws UnsupportedEncodingException, CannotSendEmailException {
        Support existParent = supportRepository.findOneByIdAndParentIdNotNull(request.getId());
        if (existParent == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existParent.setStatus(StatusEnum.COMPLETED);

        Support existComment = supportRepository.findOne(request.getId());
        if (existComment == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }

        Support commentSupport = new Support();
        commentSupport.setParentId(existComment.getId());
        commentSupport.setUser(existComment.getUser());
        commentSupport.setType(SupportTypeEnum.RESPONSE);
        commentSupport.setTitle(request.getTitle());
        commentSupport.setContent(request.getContent());
        commentSupport.setRegDtm(LocalDateTime.now());
        commentSupport.setStatus(StatusEnum.COMPLETED);
        commentSupport.setReason(request.getReason());
        supportRepository.save(commentSupport);

        //email send..
        if (!StatusEnum.PENDING.equals(request.getStatus())) {
            umsService.emailSupport(commentSupport.getUser().getEmail(), request.getTitle(), request.getContent());
        }
    }

    @SoftTransational
    public void del(SupportDTO.ReqDel request) {
        supportRepository.delete(request.getId());
    }
}
