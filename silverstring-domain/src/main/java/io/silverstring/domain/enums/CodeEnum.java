package io.silverstring.domain.enums;

public enum CodeEnum {
    SUCCESS("0000", "success"),
    FAIL("1000", "fail"),
    UNKNOWN_ERROR("1001", "unknown error"),
    BAD_REQUEST("1002", "bad request"),
    CONSTANT_VALUE_IS_NULL("1003", "constant value is null"),

    USER_NOT_EXIST("4001", "user not exist"),
    USER_SETTING_NOT_EXIST("4002", "user setting not exist"),
    API_KEY_INVALID("4003", "api key invalid"),
    AMOUNT_IS_UNDER_ZERO("4004", "amount is under 0"),
    WALLET_NOT_EXIST("4005", "wallet not exist"),
    WALLET_ALREADY_EXIST("4006", "wallet already exist"),
    ORDER_TYPE_INVALID("4007", "OrderType invalid."),
    NOT_ENOUGH_BALANCE("4008", "avaliable balance not enough"),
    INVALID_CONFIRM_CODE("4009", "invalid confirm code"),
    INVALID_EMAIL("4010", "invalid email"),
    ORDER_CANCEL_FAIL("4011", "order cancel fail"),
    INVALID_ORDER_TYPE("4012", "invalid order type"),
    ORDER_NOT_EXIST("4013", "order not exist"),
    ORDER_STATUS_IS_NOT_PLACED("4014", "order status is not placed"),
    MIN_AMOUNT("4015", "order amount is under min amount"),
    NOT_SUPPORTED("4016", "not supported"),
    USER_FDS_LOCK("4017", "user fds lock"),
    ADMIN_WALLET_BALANCE_IS_UNDER_ZERO("4018", "admin wallet balance is under zero"),
    WALLET_UNLOCK_IS_FAIL("4019", "wallet unlock is fail"),
    DO_NOT_ALLOW_INNER_TRANSFER_WALLET("4020", "do not allow inner transfer wallet"),
    AMOUNT_IS_UNDER_MIN_AMOUNT("4021", "amount is under min amount"),
    ALREADY_SEND_PROCESS_RUNNING("4022", "already send process is running"),
    MANUAL_TRANSACTION_NOT_EXIST("4023", "manual transaction not exist"),
    ADMIN_WALLET_NOT_EXIST("4023", "admin wallet not exist"),
    ONLY_KRW_RECEIVED_REQUEST("4024", "only krw received request"),
    ONLY_KRW_SEND_REQUEST("4025", "only krw send request"),
    ALREADY_STATUS_IS_NOT_PENDING("4026", "already status is not pending"),
    NOT_ENOUGH_VIRTUAL_ACCOUNT("4027", "not enough vitrual account"),
    ALREADY_MANUAL_TRANSACTION_EXIST("4028", "already manual transaction exist"),
    ALREADY_TRANSACTION_EXIST("4029", "already transaction exist"),
    TRANSACTION_NOT_EXIST("4030", "transaction not exist"),
    AVAILABLE_BALANCE_NOT_ENOUGH("4031", "Available balance not enough"),
    INVALID_PASSWORD("4032", "Invalid password"),

    UNDER_PRICE_ZERO("4033", "Under price zero"),
    UNDER_AMOUNT_ZERO("4034", "Under amount zero"),
    INVAILD_ORDER_TYPE("4035", "주문형식이 일치하지않습니다"),

    ALREADY("501", "already"),
    EQUAL_USER("502", "equal_user");


    private String code;
    private String message;

    CodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
