package com.shanjupay.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机短信服务业务层
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.effectiveTime}")
    private String effectiveTime;

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    @Override
    public String sendMsg(String phone) {

        String url = smsUrl +"/generate?name=sms&effectiveTime="+ effectiveTime;
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("mobile",phone);

        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置数据格式
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //封装请求参数
        HttpEntity entity = new HttpEntity(map,httpHeaders);

        Map body;
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("exchange------>{}", JSON.toJSONString(exchange));
            body = exchange.getBody();
        }catch (Exception e){
            throw new RuntimeException("远程调用,发送验证码异常");
        }
        if(body != null || body.get("result") != null){
            Map result = (Map)body.get("result");
            String key = result.get("key").toString();
           return key;
        }else {
            throw new RuntimeException("发送短息验证码异常");
        }

    }


    /**
     * 校验验证码,抛出异常则无效
     * @param verfiyKey
     * @param verfiyCode
     */
    @Override
    public void checkVerifiyCode(String verfiyKey, String verfiyCode) throws BussinessException{

        String url = smsUrl +"/verify?name=sms&verificationKey="+ verfiyKey + "&verificationCode=" +verfiyCode;

        Map body;
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            log.info("checkVerifiyCode - exchange------>{}", JSON.toJSONString(exchange));
            body = exchange.getBody();
        }catch (Exception e){
            throw new BussinessException(CommonErrorCode.E_100102);
        }
        if(body == null || body.get("result") == null || !(Boolean)body.get("result")){
            throw new BussinessException(CommonErrorCode.E_100102);
        }
    }
}
