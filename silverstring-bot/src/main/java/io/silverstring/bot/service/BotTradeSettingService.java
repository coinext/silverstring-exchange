package io.silverstring.bot.service;

import io.silverstring.bot.domain.hibernate.BotConnectSetting;
import io.silverstring.bot.domain.hibernate.BotTradeSetting;
import io.silverstring.bot.dto.ConnectDTO;
import io.silverstring.bot.dto.TradeSettingDTO;
import io.silverstring.bot.enums.ConnectStatusEnum;
import io.silverstring.bot.repository.hibernate.BotConnectSettingRepository;
import io.silverstring.bot.repository.hibernate.BotTradeSettingRepository;
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
public class BotTradeSettingService {

    final BotTradeSettingRepository botTradeSettingRepository;
    final ModelMapper modelMapper;

    @Autowired
    public BotTradeSettingService(BotTradeSettingRepository botTradeSettingRepository, ModelMapper modelMapper) {
        this.botTradeSettingRepository = botTradeSettingRepository;
        this.modelMapper = modelMapper;
    }

    public TradeSettingDTO.ResGetAll getAll(User user, TradeSettingDTO.ReqGetAll request) {
        TradeSettingDTO.ResGetAll res = new TradeSettingDTO.ResGetAll();
        res.setPageNo(request.getPageNo());
        res.setPageSize(request.getPageSize());

        Page<BotTradeSetting> result = botTradeSettingRepository.findAllByUserId(user.getId(), new PageRequest(request.getPageNo(), request.getPageSize()));
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
    public void add(TradeSettingDTO.ReqAdd request) {
        BotTradeSetting res = modelMapper.map(request, BotTradeSetting.class);
        //res.setRegDtm(LocalDateTime.now());
        botTradeSettingRepository.save(res);
    }

    @SoftTransational
    public void edit(TradeSettingDTO.ReqEdit request) {
        BotTradeSetting existOne = botTradeSettingRepository.findOne(request.getId());
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }

        List<BotTradeSetting> botConnectSettings = botTradeSettingRepository.findAll();
        for (BotTradeSetting one: botConnectSettings) {
            //one.setStatus(ConnectStatusEnum.INACTIVE);
        }

       /* existOne.setConnectKey(request.getConnectKey());
        existOne.setRegDtm(LocalDateTime.now());
        existOne.setSecretKey(request.getSecretKey());
        existOne.setService(request.getService());
        existOne.setStatus(request.getStatus());*/
    }

    @SoftTransational
    public void del(TradeSettingDTO.ReqDel request) {
        botTradeSettingRepository.delete(request.getId());
    }
}
