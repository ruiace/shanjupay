package com.rp.rocket.test;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;
 
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
 
@Service
public class ProducerService {
 
    private DefaultMQProducer producer = null;
 
    @PostConstruct
    public void initMQProducer() {
        producer = new DefaultMQProducer("defaultGroup");
        producer.setNamesrvAddr("100.200.133.224:9876");
        producer.setRetryTimesWhenSendFailed(3);
 
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
 
    public boolean send(String topic, String tags, String content) {
        Message msg = new Message(topic, tags, "", content.getBytes());
        try {
            producer.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 
    @PreDestroy
    public void shutDownProducer() {
        if(producer != null) {
            producer.shutdown();
        }
    }
}