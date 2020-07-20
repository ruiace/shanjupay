package com.shanjupay.paymentagent.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import java.io.FileReader;

@Service
@Slf4j
public class PayChannelAgentServiceImpl implements PayChannelAgentService {


    /**
     * 调用支付宝手机wap下单接口
     * @param aliConfigParam 支付渠道配置的参数（配置的支付宝的必要参数）
     * @param alipayBean 业务参数（商户订单号，订单标题，订单描述,,）
     * @return
     * @throws BussinessException
     */
    @Override
    public PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BussinessException {

        log.info("调用支付宝手机wap下单接口aliConfigParam ={} ", JSON.toJSONString(aliConfigParam));
        log.info("调用支付宝手机wap下单接口 alipayBean ={}",JSON.toJSONString(alipayBean));
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();
        String appId = aliConfigParam.getAppId();
        String charest = aliConfigParam.getCharest();
        String format = aliConfigParam.getFormat();
        String notifyUrl = aliConfigParam.getNotifyUrl();
        String signtype = aliConfigParam.getSigntype();
        String returnUrl = aliConfigParam.getReturnUrl();
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey();
        String getway = aliConfigParam.getUrl();


        DefaultAlipayClient client = new DefaultAlipayClient(getway, appId, rsaPrivateKey, format, charest, alipayPublicKey, signtype);

        AlipayRequest alipayRequest = new AlipayTradeWapPayRequest();

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(alipayBean.getOutTradeNo());//聚合平台订单编号
        model.setSubject(alipayBean.getSubject());//订单标题
        model.setTotalAmount(alipayBean.getTotalAmount());//订单金额
        model.setBody(alipayBean.getBody());//订单内容
        model.setTimeoutExpress(alipayBean.getExpireTime());//订单过期时间
        model.setProductCode(alipayBean.getProductCode());//商户和支付宝签订的产品码,固定为 QUICK_WAP_WAY

        alipayRequest.setBizModel(model);//请求参数集合
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setReturnUrl(returnUrl);


        try {
            log.info("调用支付宝手机wap下单接口,请求参数alipayRequest = {}",JSON.toJSONString(alipayRequest));
            AlipayResponse alipayResponse = client.pageExecute(alipayRequest);
            log.info("调用支付宝手机wap下单接口,返回结果 alipayResponse = {}",JSON.toJSONString(alipayResponse));
            PaymentResponseDTO res = new PaymentResponseDTO();
            res.setContent(alipayResponse.getBody());
            return res;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BussinessException(CommonErrorCode.E_400002);
        }
    }
}
