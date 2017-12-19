package io.silverstring.bot.service;

import io.silverstring.bot.domain.hibernate.BotConnectSetting;
import io.silverstring.bot.dto.ConnectDTO;
import io.silverstring.bot.enums.ConnectStatusEnum;
import io.silverstring.bot.enums.ServiceEnum;
import io.silverstring.bot.repository.hibernate.BotConnectSettingRepository;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BotConnectSettingService {

    final BotConnectSettingRepository botConnectSettingRepository;
    final ModelMapper modelMapper;

    @Autowired
    public BotConnectSettingService(BotConnectSettingRepository botConnectSettingRepository, ModelMapper modelMapper) {
        this.botConnectSettingRepository = botConnectSettingRepository;
        this.modelMapper = modelMapper;
    }

    public ConnectDTO.ResGetAll getAll(User user, ConnectDTO.ReqGetAll request) {
        ConnectDTO.ResGetAll res = new ConnectDTO.ResGetAll();
        res.setPageNo(request.getPageNo());
        res.setPageSize(request.getPageSize());

        Page<BotConnectSetting> result = botConnectSettingRepository.findAllByUserId(user.getId(), new PageRequest(request.getPageNo(), request.getPageSize()));
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
    public void add(ConnectDTO.ReqAdd request) {
        BotConnectSetting res = modelMapper.map(request, BotConnectSetting.class);
        res.setRegDtm(LocalDateTime.now());
        botConnectSettingRepository.save(res);
    }

    @SoftTransational
    public void edit(ConnectDTO.ReqEdit request) {
        BotConnectSetting existOne = botConnectSettingRepository.findOne(request.getId());
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }

        List<BotConnectSetting> botConnectSettings = botConnectSettingRepository.findAll();
        for (BotConnectSetting botConnectSetting: botConnectSettings) {
            botConnectSetting.setStatus(ConnectStatusEnum.INACTIVE);
        }

        existOne.setConnectKey(request.getConnectKey());
        existOne.setRegDtm(LocalDateTime.now());
        existOne.setSecretKey(request.getSecretKey());
        existOne.setService(request.getService());
        existOne.setStatus(request.getStatus());
    }

    @SoftTransational
    public void del(ConnectDTO.ReqDel request) {
        botConnectSettingRepository.delete(request.getId());
    }
}
