package com.shanjupay.merchant.service;


import com.shanjupay.common.domain.BussinessException;

/**
 * 手机短信服务
 */
public interface SmsService {

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    String sendMsg(String phone);

    /**
     * 校验验证码,抛出异常则无效
     * @param verfyKey 验证码key
     * @param verifyCode 验证吗
     */
    void checkVerifiyCode(String verfyKey,String verifyCode) throws BussinessException;
}
