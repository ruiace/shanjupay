package com.shanjupay.transaction.controller;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.ParseURLPairUtil;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class PayController {

    @ApiOperation("支付入口")
    @ApiImplicitParam(name = "ticket", value = "票据信息", required = true, paramType = "String", dataType = "path")
    @RequestMapping(value = "/pay-entry/{ticket}")
    public String payEntry(@PathVariable("ticket") String ticket, HttpServletRequest request) {

        try {

            //将ticket的base64还原
            String ticketStr = EncryptUtil.decodeUTF8StringBase64(ticket);
            PayOrderDTO payOrderDTO = JSON.parseObject(ticketStr, PayOrderDTO.class);
            //将对象转成url格式

            String urlPair = ParseURLPairUtil.parseURLPair(payOrderDTO);
            String header = request.getHeader("user-agent");
            BrowserType browserType = BrowserType.valueOfUserAgent(header);

            switch (browserType) {
                case ALIPAY: //zhijie跳转到收收银台pay.html
                    return "forword:/pay-page?" + urlPair;
                case WECHAT: //获取授权码待实现
                    return "forword:/pay-page?" + urlPair;
                default:
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "forword:/pay-page-error";
    }
}
