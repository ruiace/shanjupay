package com.shanjupay.paymentagent.message;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.TradeStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "TP_PAYMENT_ORDER",consumerGroup = "CID_PAYMENT_CONSUMER")
public class PayConsumer implements RocketMQListener<MessageExt> {


    @Reference
    private PayChannelAgentService payChannelAgentService;


    @Autowired
    private PayProducer payProducer;

    @Override
    public void onMessage(MessageExt message) {

        log.info("开始消费支付结果查询消息 MessageExt message = {}", JSON.toJSONString(message));

        //取出消息内容
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        PaymentResponseDTO response = JSON.parseObject(body, PaymentResponseDTO.class);

        String outTradeNo = response.getOutTradeNo();
        String msg = response.getMsg();
        String param = String.valueOf(response.getContent());


        //判断是支付宝还是微信
        PaymentResponseDTO result = new PaymentResponseDTO();

        if("ALIPAY_WAP".equals(msg)){
            AliConfigParam aliConfigParam = JSON.parseObject(param, AliConfigParam.class);
            //查询支付宝支付结果
            result = payChannelAgentService.queryPayOrderByAli(aliConfigParam, outTradeNo);

        }else if("WX_JSAPI".equals(msg)){
            //查询微信支付结果
            WXConfigParam wxConfigParam = JSON.parseObject(param, WXConfigParam.class);
            result = payChannelAgentService.queryPayOrderByWeChat(wxConfigParam, outTradeNo);

        }

        if(TradeStatus.UNKNOWN.equals(result.getTradeState()) || TradeStatus.USERPAYING.equals(result.getTradeState())){
            //支付状态未知 或者支付中  抛出异常会重新消费此信息
            log.info("支付状态未知 或者支付中  抛出异常会重新消费此信息");
            throw new BussinessException("支付状态未知 或者支付中  抛出异常会重新消费此信息");
        }

        //不管支付成功还是失败,都需要发送支付结果消息
        log.info("交易中心处理支付结果通知，支付代理发送消息:{}",JSON.toJSONString(response));
        payProducer.payResultNotice(result);

    }
}
