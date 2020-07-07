package com.shanjupay.merchant.service.impl;

import com.shanjupay.merchant.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsServiceImplTest {

    @Autowired
    private SmsService smsService;


    /**
     * 校验验证码
     */
    @Test
    public void testVerifyCode(){
        String key ="sms:ab9ff707902343b7a09fb1fbfccedef1";
        String code ="152170";
        smsService.checkVerifiyCode(key,code);
    }
}