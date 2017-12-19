package io.silverstring.domain.enums;

public enum LevelEnum {
    LEVEL1("level1", "1단계 인증회원"),
    LEVEL2("level2", "2단계 인증회원"),
    LEVEL3("level3", "3단계 인증회원");

    private String code;
    private String detail;

    LevelEnum(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }
    public String getDetail() {
        return detail;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

}
