package io.silverstring.core.service;

import eu.bitwalker.useragentutils.UserAgent;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.repository.hibernate.ActionLogRepository;
import io.silverstring.domain.dto.TransactionDTO;
import io.silverstring.domain.dto.UserDTO;
import io.silverstring.domain.enums.TagEnum;
import io.silverstring.domain.hibernate.ActionLog;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@Service
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    @SoftTransational
    public void log(Long userId, TagEnum tagEnum) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

        //action logging
        ActionLog actionLog = new ActionLog();
        actionLog.setUserId(userId);
        actionLog.setTag(tagEnum);
        actionLog.setIp(request.getRemoteAddr());
        actionLog.setUserAgent(userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion());
        actionLog.setRegDtm(LocalDateTime.now());
        actionLogRepository.save(actionLog);
    }

    public UserDTO.ResActionLogs getActionLogs(Long userId, UserDTO.ReqActionLogs reqActionLogs) {
        Page<ActionLog> actionLogsPage = actionLogRepository.findAllByUserIdOrderByRegDtmDesc(
                userId
                , new PageRequest(reqActionLogs.getPageNo(),reqActionLogs.getPageSize())
        );

        return UserDTO.ResActionLogs.builder()
                .pageNo(reqActionLogs.getPageNo())
                .pageSize(reqActionLogs.getPageSize())
                .pageTotalCnt(actionLogsPage.getTotalPages())
                .actionLogs(actionLogsPage.getContent())
                .build();
    }
}
