package io.silverstring.bot.service;

import io.silverstring.bot.domain.hibernate.BotReport;
import io.silverstring.bot.domain.hibernate.BotTradeSetting;
import io.silverstring.bot.dto.ReportDTO;
import io.silverstring.bot.dto.TradeSettingDTO;
import io.silverstring.bot.repository.hibernate.BotReportRepository;
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

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BotReportService {

    final BotReportRepository botReportRepository;
    final ModelMapper modelMapper;

    @Autowired
    public BotReportService(BotReportRepository botReportRepository, ModelMapper modelMapper) {
        this.botReportRepository = botReportRepository;
        this.modelMapper = modelMapper;
    }

    public ReportDTO.ResGetAll getAll(User user, ReportDTO.ReqGetAll request) {
        ReportDTO.ResGetAll res = new ReportDTO.ResGetAll();
        res.setPageNo(request.getPageNo());
        res.setPageSize(request.getPageSize());

        Page<BotReport> result = botReportRepository.findAllByUserId(user.getId(), new PageRequest(request.getPageNo(), request.getPageSize()));
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
    public void add(ReportDTO.ReqAdd request) {
        BotReport res = modelMapper.map(request, BotReport.class);
        //res.setRegDtm(LocalDateTime.now());
        botReportRepository.save(res);
    }

    @SoftTransational
    public void edit(ReportDTO.ReqEdit request) {
        BotReport existOne = botReportRepository.findOne(request.getId());
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }

        List<BotReport> botReports = botReportRepository.findAll();
        for (BotReport one: botReports) {
            //one.setStatus(ConnectStatusEnum.INACTIVE);
        }

       /* existOne.setConnectKey(request.getConnectKey());
        existOne.setRegDtm(LocalDateTime.now());
        existOne.setSecretKey(request.getSecretKey());
        existOne.setService(request.getService());
        existOne.setStatus(request.getStatus());*/
    }

    @SoftTransational
    public void del(ReportDTO.ReqDel request) {
        botReportRepository.delete(request.getId());
    }
}
