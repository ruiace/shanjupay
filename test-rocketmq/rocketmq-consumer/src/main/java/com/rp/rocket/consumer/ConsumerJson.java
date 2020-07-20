package com.rp.rocket.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消费消息监听类
 */
@Component
@RocketMQMessageListener(consumerGroup = "demo-consumer-json-group",topic = "my-topic-json")
public class ConsumerJson implements RocketMQListener<OrderExt> {


    @Override
    public void onMessage(OrderExt orderExt) {
        System.out.println("my-topic-json");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        System.out.println("------(3)---->" + format);
        System.out.println(orderExt);
        System.out.println("------(4)---->" + format);
    }
}
