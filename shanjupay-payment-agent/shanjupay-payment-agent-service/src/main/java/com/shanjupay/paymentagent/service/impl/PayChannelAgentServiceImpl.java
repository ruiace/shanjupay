package com.shanjupay.paymentagent.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.common.AliCodeConstants;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.TradeStatus;
import com.shanjupay.paymentagent.api.dto.WeChatBean;
import com.shanjupay.paymentagent.config.WXSDKConfig;
import com.shanjupay.paymentagent.message.PayProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PayChannelAgentServiceImpl implements PayChannelAgentService {




    @Autowired
    private PayProducer payProducer;

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
            //发送支付结果查询延迟消息
            PaymentResponseDTO notice = new PaymentResponseDTO();
            notice.setOutTradeNo(alipayBean.getOutTradeNo());
            notice.setMsg("ALIPAY_WAP");
            notice.setContent(aliConfigParam);
            payProducer.payOrderNotice(notice);

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


    /**
     * 查询支付宝订单状态
     * @param aliConfigParam 支付渠道参数
     * @param outTradeNo 闪聚平台的订单号
     * @return
     */
    @Override
    public PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam, String outTradeNo) throws BussinessException {

        log.info("调用支付宝手机wap下单接口aliConfigParam ={} ", JSON.toJSONString(aliConfigParam));
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();
        String appId = aliConfigParam.getAppId();
        String charest = aliConfigParam.getCharest();
        String format = aliConfigParam.getFormat();
        String notifyUrl = aliConfigParam.getNotifyUrl();
        String signtype = aliConfigParam.getSigntype();
        String returnUrl = aliConfigParam.getReturnUrl();
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey();
        String getway = aliConfigParam.getUrl();

        AlipayClient client = new DefaultAlipayClient(getway, appId, rsaPrivateKey, format, charest, alipayPublicKey, signtype);

        AlipayTradeQueryRequest queryReqeust = new AlipayTradeQueryRequest();

        AlipayTradePayModel model = new AlipayTradePayModel();
        model.setOutTradeNo(outTradeNo);
        queryReqeust.setBizModel(model);

        PaymentResponseDTO result = null;
        try {
            //根据订单编号查询支付状态
            AlipayTradeQueryResponse execute = client.execute(queryReqeust);

            //接口调用成功
            if(AliCodeConstants.SUCCESSCODE.equals(execute.getCode())){

                TradeStatus tradeStatus = convertAliTradeStatusToShanjuCode(execute.getTradeStatus());

                result = PaymentResponseDTO.success(execute.getTradeNo(), execute.getOutTradeNo(), tradeStatus, execute.getMsg() + " " + execute.getSubMsg());

                log.info("查询支付宝H5支付结果 = {}",JSON.toJSONString(result));
                return result;

            }



        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        result = PaymentResponseDTO.fail("查询支付宝支付结果异常",outTradeNo,TradeStatus.UNKNOWN);

        return result;
    }


    /**
     * 微信jsapi下单接口
     * @param wxConfigParam
     * @param weChatBean
     * @return
     * @throws BussinessException
     */
    @Override
    public Map<String, String> createPayOrderByWeChatJSAPI(WXConfigParam wxConfigParam, WeChatBean weChatBean) throws BussinessException {

        //通过实践支付参数匹配
        WXSDKConfig config = new WXSDKConfig(wxConfigParam);

        try {
            //发送支付结果到延迟消息队列
            PaymentResponseDTO<WXConfigParam> notice = new PaymentResponseDTO();

            notice.setOutTradeNo(weChatBean.getOutTradeNo());
            notice.setContent(wxConfigParam);
            notice.setMsg("WX_JSAPI");
            payProducer.payOrderNotice(notice);



            WXPay wxPay = new WXPay(config);

            //按照微信统一下单接口要求构造请求参数
            // https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1

            Map<String,String> requestParam = new HashMap<>();
            requestParam.put("body",weChatBean.getBody());
            requestParam.put("out_trade_no",weChatBean.getOutTradeNo());
            requestParam.put("fee_type","CNY");
            requestParam.put("total_fee",String.valueOf(weChatBean.getTotalFee()));
            requestParam.put("spbill_create_ip",weChatBean.getSpbillCreateIp());
            requestParam.put("notify_url",weChatBean.getNotifyUrl());
            requestParam.put("trade_type","JSAPI");
            requestParam.put("openid",weChatBean.getOpenId());

            //调用微信统一下单api
            Map<String, String> res = wxPay.unifiedOrder(requestParam);

            //返回h5网页需要的数据

            String timestamp = String.valueOf(System.currentTimeMillis());
            String key = wxConfigParam.getKey();

            Map<String,String> jsapiPayParam = new HashMap<>();
            jsapiPayParam.put("appId",res.get("appid"));
            jsapiPayParam.put("package","prepay_id=" + res.get("prepay_id"));
            jsapiPayParam.put("timeStamp",timestamp);
            jsapiPayParam.put("nonceStr", UUID.randomUUID().toString());
            jsapiPayParam.put("signType","HMAC-SHA256");
            jsapiPayParam.put("paySign", WXPayUtil.generateSignature(jsapiPayParam,key, WXPayConstants.SignType.HMACSHA256));
            log.info("微信jsapi 支付响应内容 = {}",JSON.toJSONString(jsapiPayParam));

            return jsapiPayParam;


        } catch (Exception e) {
            e.printStackTrace();
            throw new BussinessException(CommonErrorCode.E_400001);
        }


    }


    /**
     * 查询微信支付结果
     * @param wxConfigParam
     * @param outTradeNo
     * @return
     * @throws BussinessException
     */
    @Override
    public PaymentResponseDTO queryPayOrderByWeChat(WXConfigParam wxConfigParam, String outTradeNo) throws BussinessException {

        WXSDKConfig config = new WXSDKConfig(wxConfigParam);

        Map<String, String> res = null;
        try {
            WXPay wxPay = new WXPay(config);
            Map<String,String> data = new HashMap<>();
            data.put("out_trade_no",outTradeNo);
            res = wxPay.orderQuery(data);


        } catch (Exception e) {
            e.printStackTrace();
            return PaymentResponseDTO.fail("调用微信查询订单支付结果异常",outTradeNo,TradeStatus.UNKNOWN);
        }

        String returnCode = res.get("return_code");
        String resultCode = res.get("result_code");
        String tradeState = res.get("trade_state");
        String transactionId = res.get("transaction_id");
        String returnMsg = res.get("return_msg");


        if("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)){

            if("SUCCESS".equals(tradeState)){  //支付成功
                return PaymentResponseDTO.success(transactionId,outTradeNo,TradeStatus.SUCCESS,returnMsg);
            }else if("CLOSED".equals(tradeState)){//交易关闭
                return PaymentResponseDTO.success(transactionId,outTradeNo,TradeStatus.REVOKED,returnMsg);
            }else if("USERPAYING".equals(tradeState)){//支付中
                return PaymentResponseDTO.success(transactionId,outTradeNo,TradeStatus.USERPAYING,returnMsg);
            }else if("PAYERROR".equals(tradeState)){//支付失败
                return PaymentResponseDTO.success(transactionId,outTradeNo,TradeStatus.FAILED,returnMsg);

            }

        }


        return PaymentResponseDTO.success("暂不支持其他状态",transactionId,outTradeNo,TradeStatus.UNKNOWN);
    }


    /**
     * 将支付宝查询时订单状态trade_status 转成聚合支付订单状态
     * @param tradeStatus
     * @return
     */
    private TradeStatus convertAliTradeStatusToShanjuCode(String tradeStatus) {

        switch (tradeStatus){
            //订单创建 等待买家付款
            case AliCodeConstants.WAIT_BUYER_PAY :
                return TradeStatus.USERPAYING;
                //交易支付成功
            case AliCodeConstants.TRADE_SUCCESS:
                //交易结束,不可退款
            case AliCodeConstants.TRADE_FINISHED:
                return TradeStatus.SUCCESS;
            default:
                //未付款交易超时关闭 或者支付完成后全额退款
                return TradeStatus.FAILED;

        }

    }
}
