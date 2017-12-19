package io.silverstring.core.exception;


import io.silverstring.domain.enums.CodeEnum;


public class ExchangeException extends RuntimeException {

    public final CodeEnum codeEnum;

    public ExchangeException(CodeEnum codeEnum) {
        super(codeEnum.getMessage());
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, Throwable cause) {
        super(codeEnum.getMessage(), cause);
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, String message) {
        super(message);
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, String message, Throwable cause) {
        super(message, cause);
        this.codeEnum = codeEnum;
    }
}
