package com.shanjupay.transaction.controller;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.AmountUtil;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.IPUtil;
import com.shanjupay.common.util.ParseURLPairUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.convert.PayOrderConvert;
import com.shanjupay.transaction.vo.OrderConfirmVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.util.locale.provider.LocaleServiceProviderPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
public class PayController {






    @Reference
    private AppService appService;


    @Reference
    private TransactionService transactionService;


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
                    return transactionService.getWXOauth2Code(payOrderDTO);
                default:
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "forword:/pay-page-error";
    }


    @ApiOperation("支付宝门店下单付款")
    @PostMapping("/createAliPayOrder")
    public void createAlipayOrderForStore(OrderConfirmVO orderConfirmVO, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String appId = orderConfirmVO.getAppId();
        if(StringUtils.isBlank(appId)){
            throw new BussinessException(CommonErrorCode.E_300003);
        }

        PayOrderDTO payOrderDTO = PayOrderConvert.INSTANCE.vo2dto(orderConfirmVO);

        //获取下单用户应用信息
        AppDTO app = appService.getAppById(appId);

        //设置所属商户
        payOrderDTO.setMerchantId(app.getMerchantId());


        payOrderDTO.setTotalAmount(Integer.valueOf(AmountUtil.changeF2Y(orderConfirmVO.getTotalAmount())));

        payOrderDTO.setClientIp(IPUtil.getIpAddr(request));


        PaymentResponseDTO paymentResponseDTO = transactionService.submitOrderByAli(payOrderDTO);


        String content = String.valueOf(paymentResponseDTO.getContent());


        log.info("支付宝H5支付响应结果为: String content = {}",content);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(content);
        response.getWriter().flush();
        response.getWriter().close();


    }

    @ApiOperation("微信授权码回调")
    @GetMapping("wx-oauth-code-return")
    public String wxOauth2CodeReturn(@RequestParam("code") String code,@RequestParam("state") String state){
        PayOrderDTO payOrderDTO = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(state), PayOrderDTO.class);
        //获取openid

        String wxOauthOpenId = transactionService.getWXOauthOpenId(code, payOrderDTO.getAppId());
        //重定向到支付确认页面

        try {
            //将订单信息转成query参数的形式拼接起来
            String orderinfo = ParseURLPairUtil.parseURLPair(payOrderDTO);

            return String.format("forword:/pay-page?openId=%s&%s",wxOauthOpenId,orderinfo);

        }catch (Exception e){
            e.printStackTrace();
            return "forword:/pay-page-error";
        }

    }




    @ApiOperation("微信门店下单付款")
    @PostMapping("/wxjsapi")
    public ModelAndView createWXOrderForStore(OrderConfirmVO orderConfirmVO,HttpServletRequest request){
        if(orderConfirmVO == null || StringUtils.isBlank(orderConfirmVO.getAppId())){
            throw new BussinessException(CommonErrorCode.E_300002);
        }
        PayOrderDTO payOrderDTO = PayOrderConvert.INSTANCE.vo2dto(orderConfirmVO);
        //应用id
        String appId = payOrderDTO.getAppId();

        AppDTO appById = appService.getAppById(appId);
        //商户id
        payOrderDTO.setMerchantId(appById.getMerchantId());
        payOrderDTO.setClientIp(IPUtil.getIpAddr(request));
        //将前端传入的元转成分
        payOrderDTO.setTotalAmount(Integer.valueOf(AmountUtil.changeY2F(orderConfirmVO.getTotalAmount().toString())));

        Map<String, String> stringStringMap = transactionService.submitOrderByWechat(payOrderDTO);
        return new ModelAndView("wxpay",stringStringMap);

    }



}
