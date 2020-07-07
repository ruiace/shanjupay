package com.shanjupay.merchant.common.intercept;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.ErrorCode;
import com.shanjupay.common.domain.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(HttpServletRequest request, HttpServletResponse response,Exception e){
        if(e instanceof BussinessException){
            log.info("GlobalExceptionHandler - processException,自定义业务--打印异常",e);
            BussinessException bussinessException = (BussinessException)e;

            return new RestErrorResponse(bussinessException.getMessage(),String.valueOf(bussinessException.getCode()));
        }

        log.info("系统异常答应",e);
        return new RestErrorResponse(CommonErrorCode.UNKOWN.getDesc(),String.valueOf(CommonErrorCode.UNKOWN.getCode()));
    }
}
