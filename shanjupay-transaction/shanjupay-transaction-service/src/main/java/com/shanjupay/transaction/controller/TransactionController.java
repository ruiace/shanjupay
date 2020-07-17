package com.shanjupay.transaction.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.shanjupay.common.util.RandomUuidUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TransactionController {

    /**
     * 应用私钥: MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDtnFGXVpC0MtL3m4XGQA7Acrzit9OOta1C/q8Uqog/6fvIyByjy4uiVj6/R0c4j1kH1PJk40rMx0MsG5CbcEdvVPR6a6LU/01adua6D3w453bDPybJSEVvuEG9Oq3ST5ZWsRiLiMF4j6pr3meeoF6FNpELVERhGuI5bKvRmK6fhs7NbeWmZsyAmiNypZPGq524jT+PdOMI3mNrduM+8usudyPkSr3VneqXwD5BTff2m8NQKEKIqmF7IXw5RXC9lObypK9qkCYpHinWsh5iOJ6QIyIvrNGJ4sQ5yMnvgo/xpK1hd5XfZ0BdY7xBkMnEKMf74ABuGTYJUzSndLjZiPWTAgMBAAECggEAAnntk8EwMuCQuEnjpARI4xKPC2HVD5ivHJKg6ns4fkoG+Lm1KrPVWbTCZKXuhRVhXSaH/CsuPWIaPhxvbl9GL0/YWGpEwSmD+dxhXTEKH2GyKCjBU7mwF5D5BmrVIur3ayHfWpzrP0FoCkXAGLQdKBBAhLi1Gbn4/5Y1WDLaJwlwfEas+cSLR6ltrJ51QpEhyZFCynvs91+nuENK8vtK03Ghfc5tXKHbpmCTvPblO7qby5un3ukyzJI0zff0LRC9QAaYxr2vaaFLrRrbTBb73cU6kK+70i53IQHQ9s3ECHmqmBPa844/Lxd2nYycOm47FC/9s59aCi4gE+sZQb5eqQKBgQD3NM5IulLGbxBlGI7qW4b/z1qd6HPKo4oHqicGI+PNifUTF8YZcfUq38Q05GR2/YTJifDkWDSrmYWkXnWaLNZrkfwDYjbqfJKAjLQxueddC6SdU6Gq42CuKwNBl9+3p4O49Quv6N89I+gszg30VlDLYMt1+QJXxoE4z9EI5QJM1QKBgQD2ECE0jo3SRD9nYGkquJVN0BEjb0NE4wj8NSAutoSBcVybsS/e1qVFELsmZUuhuQ4oi5NkuT34r2GbEaAuo+Q4X5eBLUhPC9vB4Hf2zDHJqxCSYn2nnoeXkTp8fZgJTN12NMe3tN0xkzI0vaPmp8SuHL2f94FxXV7ReL6bxjBMxwKBgDX8KjBnwDvldUcenddANbMXrUpewSq3nCUgW3VT0SPJxONuhax43nmGdGq4ldgGdUEuBpVXTelLlYvXtQb+U7UaJFb409VRbwzzrUZOut/u8vwIj32qOirO6d8hM5H/2xxBJ1Q7HsVTWhiuxw1c7Df943DX+FMC3qVxfMzcdiwhAoGBAKqajltBR4V0+jzztJcBOcukqm1WFF7rxuwO7YxV0dNpxNrR5C3txtL3AbtiWij8BJwKAhzA7v3Ao7z/YH9V9MM9S+8tdUgRIcHvn12HQDHiIHBOR27zVHG7KMDt138DQz1U5PAzP3XC5/l3Tu96fLx3guYplh8CrN4xiH66z/w7AoGBAJnf60yCufZrzlA0/3ATSm6Ij+i9KpVa1059vFbF0vClmj2d6CMFjZBE6clU/D55PXYqZs+r23Mq0A0levCdh3tW0vSoJe1zbJHJk06xkC/w7Zp5tLZbyFFyFjqtiQDeqkNkZll+sf+hmewmORLaKDqQ+NTj4I7Y7vH5BUF0MSUq
     */

    /**
     * 应用公钥: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7ZxRl1aQtDLS95uFxkAOwHK84rfTjrWtQv6vFKqIP+n7yMgco8uLolY+v0dHOI9ZB9TyZONKzMdDLBuQm3BHb1T0emui1P9NWnbmug98OOd2wz8myUhFb7hBvTqt0k+WVrEYi4jBeI+qa95nnqBehTaRC1REYRriOWyr0Ziun4bOzW3lpmbMgJojcqWTxquduI0/j3TjCN5ja3bjPvLrLncj5Eq91Z3ql8A+QU339pvDUChCiKpheyF8OUVwvZTm8qSvapAmKR4p1rIeYjiekCMiL6zRieLEOcjJ74KP8aStYXeV32dAXWO8QZDJxCjH++AAbhk2CVM0p3S42Yj1kwIDAQAB
     */

    /**
     * 支付宝公钥: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArEyBOs1gQW2ys9AklK6wpnKnTjSgcl9xUYaGBseeqdW9Mh37FZEINtKhxkvOenKZE8rlziNGlsufmFzB68CUXrqFi66EMm2V29sDuqFaBrUwmf1ijZdHnK2vYXO3c9YRnz0TOoe5uYJAVDLr70C9Qyeuj2IEL8Ez+XgUGrpuWp6D1lQjHrfYNlkwSVMnCX1us8ZSlIdQaCNB95Z3CxYWg+hwpLqqpjGf9RlEWGxBLJorEfRFG0mrYSIqZ/mmm2X7mmb08cNaZCnXeH8q7YSq93so8jC0eCX44qGnstNNFIaEluMF/oJ0+XFLUrzwAFeRE3h/5/7fKpO8BKQr7bdFgQIDAQAB
     */



    private String urlDev = "https://openapi.alipaydev.com/gateway.do";
    private String urlPro = "https://openapi.alipay.com/gateway.do";

    private String APP_ID ="2016102500760430";
    private String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDtnFGXVpC0MtL3m4XGQA7Acrzit9OOta1C/q8Uqog/6fvIyByjy4uiVj6/R0c4j1kH1PJk40rMx0MsG5CbcEdvVPR6a6LU/01adua6D3w453bDPybJSEVvuEG9Oq3ST5ZWsRiLiMF4j6pr3meeoF6FNpELVERhGuI5bKvRmK6fhs7NbeWmZsyAmiNypZPGq524jT+PdOMI3mNrduM+8usudyPkSr3VneqXwD5BTff2m8NQKEKIqmF7IXw5RXC9lObypK9qkCYpHinWsh5iOJ6QIyIvrNGJ4sQ5yMnvgo/xpK1hd5XfZ0BdY7xBkMnEKMf74ABuGTYJUzSndLjZiPWTAgMBAAECggEAAnntk8EwMuCQuEnjpARI4xKPC2HVD5ivHJKg6ns4fkoG+Lm1KrPVWbTCZKXuhRVhXSaH/CsuPWIaPhxvbl9GL0/YWGpEwSmD+dxhXTEKH2GyKCjBU7mwF5D5BmrVIur3ayHfWpzrP0FoCkXAGLQdKBBAhLi1Gbn4/5Y1WDLaJwlwfEas+cSLR6ltrJ51QpEhyZFCynvs91+nuENK8vtK03Ghfc5tXKHbpmCTvPblO7qby5un3ukyzJI0zff0LRC9QAaYxr2vaaFLrRrbTBb73cU6kK+70i53IQHQ9s3ECHmqmBPa844/Lxd2nYycOm47FC/9s59aCi4gE+sZQb5eqQKBgQD3NM5IulLGbxBlGI7qW4b/z1qd6HPKo4oHqicGI+PNifUTF8YZcfUq38Q05GR2/YTJifDkWDSrmYWkXnWaLNZrkfwDYjbqfJKAjLQxueddC6SdU6Gq42CuKwNBl9+3p4O49Quv6N89I+gszg30VlDLYMt1+QJXxoE4z9EI5QJM1QKBgQD2ECE0jo3SRD9nYGkquJVN0BEjb0NE4wj8NSAutoSBcVybsS/e1qVFELsmZUuhuQ4oi5NkuT34r2GbEaAuo+Q4X5eBLUhPC9vB4Hf2zDHJqxCSYn2nnoeXkTp8fZgJTN12NMe3tN0xkzI0vaPmp8SuHL2f94FxXV7ReL6bxjBMxwKBgDX8KjBnwDvldUcenddANbMXrUpewSq3nCUgW3VT0SPJxONuhax43nmGdGq4ldgGdUEuBpVXTelLlYvXtQb+U7UaJFb409VRbwzzrUZOut/u8vwIj32qOirO6d8hM5H/2xxBJ1Q7HsVTWhiuxw1c7Df943DX+FMC3qVxfMzcdiwhAoGBAKqajltBR4V0+jzztJcBOcukqm1WFF7rxuwO7YxV0dNpxNrR5C3txtL3AbtiWij8BJwKAhzA7v3Ao7z/YH9V9MM9S+8tdUgRIcHvn12HQDHiIHBOR27zVHG7KMDt138DQz1U5PAzP3XC5/l3Tu96fLx3guYplh8CrN4xiH66z/w7AoGBAJnf60yCufZrzlA0/3ATSm6Ij+i9KpVa1059vFbF0vClmj2d6CMFjZBE6clU/D55PXYqZs+r23Mq0A0levCdh3tW0vSoJe1zbJHJk06xkC/w7Zp5tLZbyFFyFjqtiQDeqkNkZll+sf+hmewmORLaKDqQ+NTj4I7Y7vH5BUF0MSUq";
    private String CHARSET  = "utf-8";
    private String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArEyBOs1gQW2ys9AklK6wpnKnTjSgcl9xUYaGBseeqdW9Mh37FZEINtKhxkvOenKZE8rlziNGlsufmFzB68CUXrqFi66EMm2V29sDuqFaBrUwmf1ijZdHnK2vYXO3c9YRnz0TOoe5uYJAVDLr70C9Qyeuj2IEL8Ez+XgUGrpuWp6D1lQjHrfYNlkwSVMnCX1us8ZSlIdQaCNB95Z3CxYWg+hwpLqqpjGf9RlEWGxBLJorEfRFG0mrYSIqZ/mmm2X7mmb08cNaZCnXeH8q7YSq93so8jC0eCX44qGnstNNFIaEluMF/oJ0+XFLUrzwAFeRE3h/5/7fKpO8BKQr7bdFgQIDAQAB";


    @GetMapping("/alipayTest")
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        AlipayClient alipayClient = new DefaultAlipayClient(urlDev,
                APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
//        alipayRequest.setBizContent("{" +
//                " \"out_trade_no\":\"99883477882543\"," +
//                " \"total_amount\":\"00.1\"," +
//                " \"subject\":\"Iphone6-16G\"," +
//                " \"product_code\":\"QUICK_WAP_PAY\"" +
//                " }");//填充业务参数

        AlipayTradeAppPayModel bizModel = new AlipayTradeAppPayModel();
        bizModel.setOutTradeNo("999999" + RandomUuidUtil.getUUID());
        bizModel.setTotalAmount("0.01");
        bizModel.setSubject("芮朋 Iphone6-16G");
        bizModel.setProductCode("QUICK_WAP_PAY");
        alipayRequest.setBizModel(bizModel);

        String form="";
        try {
            System.out.println("====" + form);
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            System.out.println("--------" + form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }




}
