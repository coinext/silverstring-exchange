package io.silverstring.domain.dto;

import io.silverstring.domain.hibernate.ActionLog;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserDTO {

    @Data
    public static class ReqActionLogs {
        Integer pageNo;
        Integer pageSize;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResActionLogs {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<ActionLog> actionLogs;
    }

    @Data
    public static class ReqRegist {
        private String email;
        private String pwd;
        private String fingetprint;
    }

    @Data
    public static class ResEmailConfirm {
        private String title;
        private String msg;
        private String url;
        private String urlTitle;
    }

    @Data
    public static class ReqChangePassword {
        @NotNull
        private String password;
        @NotNull
        private String newPassword;
        @NotNull
        private String newPasswordRe;
        @NotNull
        private String otp;
    }

    @Data
    public static class ResChangePassword {
        private Long id;
        private String email;
    }

    @Data
    public static class ReqReleaseMember {

    }

    @Data
    public static class ResReleaseMember {

    }
}
