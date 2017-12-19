package io.silverstring.domain.dto;

import io.silverstring.domain.enums.LevelEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.SubmitDocument;
import lombok.*;

import java.util.List;

public class DocDTO {

    @Getter
    @Setter
    public static class ReqGet {
        Integer pageNo;
        Integer pageSize;
    }

    @Getter
    @Setter
    public static class ResGet {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<SubmitDocument> contents;
    }

    @Getter
    @Setter
    public static class ReqAdd {
    }

    @Getter
    @Setter
    public static class ReqEdit {
        Long userId;
        LevelEnum level;
        StatusEnum status;
        String reason;
    }

    @Getter
    @Setter
    public static class ReqDel {
        Long userId;
    }

}
