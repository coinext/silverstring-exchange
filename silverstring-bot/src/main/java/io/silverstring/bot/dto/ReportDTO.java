package io.silverstring.bot.dto;

import io.silverstring.bot.domain.hibernate.BotConnectSetting;
import io.silverstring.bot.domain.hibernate.BotReport;
import io.silverstring.bot.enums.ConnectStatusEnum;
import io.silverstring.bot.enums.ServiceEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


public class ReportDTO {
    @Data
    public static class ReqGetAll {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResGetAll {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<BotReport> contents;
    }

    @Data
    public static class ReqAdd {
        Long id;
        ServiceEnum service;
        String connectKey;
        String secretKey;
        LocalDateTime regDtm;
        ConnectStatusEnum status;
    }

    @Data
    public static class ReqEdit extends BotConnectSetting {
    }

    @Data
    public static class ReqDel extends BotConnectSetting {
    }
}
