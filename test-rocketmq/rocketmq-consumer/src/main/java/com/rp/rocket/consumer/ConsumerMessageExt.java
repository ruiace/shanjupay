package com.rp.rocket.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消费消息监听类
 */
@Component
@RocketMQMessageListener(consumerGroup = "demo-consumer-ext-group",topic = "my-topic-ext")
public class ConsumerMessageExt implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt message) {

        //获取重试次数
        int reconsumeTimes = message.getReconsumeTimes();
        String msgId = message.getMsgId();

        System.out.println("reconsumeTimes ----->" + reconsumeTimes + "; messageId========>" + msgId);
        if(reconsumeTimes <= 2){
            throw  new RuntimeException("测试重试次数");
        }

        try {
            String s = new String(message.getBody(), "utf-8");
            System.out.println("--------------> "+s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void onMessage(OrderExt orderExt) {
//        System.out.println("my-topic-json");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String format = simpleDateFormat.format(new Date());
//        System.out.println("------(3)---->" + format);
//        System.out.println(orderExt);
//        System.out.println("------(4)---->" + format);
//    }
}
