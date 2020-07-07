package com.shanjupay.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.BeanProperty;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MerchantControllerTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void getHtml(){
        String url = "http://www.baidu.com";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String body = forEntity.getBody();
        System.out.println(body);
    }

    @Test
    public void testGetSmsCode(){
        String url = "http://localhost:56085/sailing/generate?name=sms&effectiveTime=600";
        String mobile = "15040023963";

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("mobile",mobile);

        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置数据格式
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //封装请求参数

        HttpEntity entity = new HttpEntity(map,httpHeaders);

        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("exchange------>{}", JSON.toJSONString(exchange));
        Map body = exchange.getBody();
        if(body != null || body.get("result") != null){
            Map result = (Map)body.get("result");
            String key = result.get("key").toString();
            System.out.println("key=======>"+key);
        }

    }
}