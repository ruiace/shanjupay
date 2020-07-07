package com.shanjupay.common.domain;

import lombok.Data;

/**
 * 自定义业务异常
 */
@Data
public class BussinessException  extends RuntimeException{

    private int code;

    public BussinessException() {
        super();
    }

    public BussinessException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.code = errorCode.getCode();
    }


    public BussinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BussinessException(String message) {
        super(message);
        this.code = CommonErrorCode.CUSTOM.getCode();
    }

}
