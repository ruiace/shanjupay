package com.shanjupay.paymentagent.message;


import com.alibaba.fastjson.JSON;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class PayProducer {

    //消息topic
    private static final String TOPIC_ORDER = "TP_PAYMENT_ORDER";


    @Resource
    private RocketMQTemplate rocketMQTemplate;


    public void payOrderNotice(PaymentResponseDTO result){
        log.info("支付通知发送延迟消息:{}", JSON.toJSONString(result));

        Message<PaymentResponseDTO> message = MessageBuilder.withPayload(result).build();
        SendResult sendResult = rocketMQTemplate.syncSend(TOPIC_ORDER, message, 1000, 3);
        log.info("支付通知发送延迟消息完成 SendResult sendResult ={}",JSON.toJSONString(sendResult));

    }



    private static final String TOPIC_RESULT = "TP_PAYMENT_RESULT";


    //在支付渠道代理服务的payproduce 中查询支付结果后,发送到支付结果更新方法
    public void payResultNotice(PaymentResponseDTO result){
        log.info("在支付渠道代理服务的payproduce 中查询支付结果后,发送到支付结果更新方法 result ={}",JSON.toJSONString(result));
        rocketMQTemplate.convertAndSend(TOPIC_RESULT,result);
    }

}
