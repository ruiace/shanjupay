package com.rp.rocket.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消费消息监听类
 */
@Component
@RocketMQMessageListener(consumerGroup = "demo-consumer-group",topic = "my-topic")
public class ConsumerSimple implements RocketMQListener<String> {


    /**
     * 接收到消息 调用此方法
     * @param s
     */
    @Override
    public void onMessage(String s) {
        System.out.println("开始接收消息");
        System.out.println(s);
    }
}
