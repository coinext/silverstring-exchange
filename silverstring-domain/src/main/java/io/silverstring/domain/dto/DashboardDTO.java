package io.silverstring.domain.dto;

import io.silverstring.domain.hibernate.Coin;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResInfo {
        Integer userTotalCnt;
        Long totoalKrwTotalBal;
        Integer depositWaitCnt;
        Integer withdrawalWaitCnt;
    }
}
