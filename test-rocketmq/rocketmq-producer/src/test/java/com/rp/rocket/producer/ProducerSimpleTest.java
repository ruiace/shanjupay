package com.rp.rocket.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerSimpleTest {

    @Autowired
    private ProducerSimple producerSimple;

    @Test
    public void testSendSyncMsg(){
        producerSimple.sendsyncMsg("my-topic","这是第一条消息");
        System.out.println("消息发送完成");
    }


    @Test
    public void testsendAsyncMsg() throws InterruptedException {
        producerSimple.sendAsyncMsg("my-topic","这是一条异步消息");
        System.out.println("发送异步消息完成");

        Thread.sleep(5000);
    }


    @Test
    public void testSendOneWayMsg() throws InterruptedException {
        producerSimple.sendOneWayMsg("my-topic","这是一条单向消息");
        System.out.println("发送单向消息完成");
    }


    @Test
    public void testsendMsgByJson(){
        OrderExt order = new OrderExt();
        order.setId("123");
        order.setCreateTime(new Date());
        order.setMoney(999L);
        order.setTitle("测试发送json格式的数据");
        producerSimple.sendMsgByJson("my-topic-json",order);
        System.out.println("发送json格式的数据完成");
    }


    @Test
    public void testsendMsgByJsonDelay(){
        OrderExt order = new OrderExt();
        order.setId("123444444");
        order.setCreateTime(new Date());
        order.setMoney(999L);
        order.setTitle("测试发送json格式的数据");
        producerSimple.sendMsgByJsonDelay("my-topic-json",order);
    }



    @Test
    public void testsendAsyncMsgByJsonDelay() throws InterruptedException {
        OrderExt order = new OrderExt();
        order.setId("66666666666");
        order.setCreateTime(new Date());
        order.setMoney(999L);
        order.setTitle("测试发送json格式的数据");
        try {
            producerSimple.sendAsyncMsgByJsonDelay("my-topic-json",order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread.sleep(500000);
    }



    @Test
    public void testsendMsgByJsonDelay2(){
        OrderExt order = new OrderExt();
        order.setId("6666666666666");
        order.setCreateTime(new Date());
        order.setMoney(999L);
        order.setTitle("测试发送json格式的数据");
        producerSimple.sendMsgByJsonDelay("my-topic-ext",order);
    }
}