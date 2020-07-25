package com.shanjupay.transaction.message;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.TradeStatus;
import com.shanjupay.transaction.api.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.nio.charset.StandardCharsets;

/**
 * 消费支付结果信息
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = "TP_PAYMENT_RESULT",consumerGroup = "CID_ORDER_CONSUMER")
public class TransactionPayConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private TransactionService transactionService;

    @Override
    public void onMessage(MessageExt message) {
        //获取消息体
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        PaymentResponseDTO res = JSON.parseObject(body,PaymentResponseDTO.class);
        String tradeNo = res.getOutTradeNo();
        TradeStatus tradeState = res.getTradeState();
        String payChannelTradeNo = res.getTradeNo();


        switch (tradeState){

            case SUCCESS:
                //支付成功,修改订单状态为支付成功
                transactionService.updateOrderTradeNoAndTradeState(tradeNo,payChannelTradeNo,"2");
                return;

            case REVOKED:
                //支付关闭,修改订单状态为关闭
                transactionService.updateOrderTradeNoAndTradeState(tradeNo,payChannelTradeNo,"4");
                return;

            case FAILED:
                transactionService.updateOrderTradeNoAndTradeState(tradeNo,payChannelTradeNo,"5");
                return;
            case UNKNOWN:
                throw new BussinessException(String.format("无法解析支付结果:%s",body));

        }

    }
}
