package com.rp.rocket.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * rocketmq 发送消息
 */
@Component
public class ProducerSimple {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 发送同步消息
     * @param topic
     * @param msg
     */
    public void sendsyncMsg(String topic,String msg){
        rocketMQTemplate.syncSend(topic,msg);
    }


    /**
     * 发送异步消息
     * @param topic
     * @param msg
     */
    public void sendAsyncMsg(String topic,String msg){

        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {

            //发送成功的回调
            @Override
            public void onSuccess(SendResult sendResult) {

                System.out.println("发送异步消息,发送成功回调" +  sendResult.toString());
            }

            //发送异常的回调
            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送异步消息,发送成功失败" +  throwable.getMessage());

            }
        });
    }


    /**
     * 发送单项消息
     * producer向 broker 发送消息，执行 API 时直接返回，不等待broker 服务器的结果
     *
     * @param topic
     * @param msg
     */
    public void sendOneWayMsg(String topic,String msg){
        rocketMQTemplate.sendOneWay(topic,msg);
    }


    /**
     * 消息内容为json格式
     * @param topic
     * @param ext
     */
    public  void sendMsgByJson(String topic, OrderExt ext){
        rocketMQTemplate.convertAndSend(topic,ext);
        System.out.println(ext);
    }


    /**
     * 消息同步延迟
     *
     * 发送延迟消息,消息内容为json
     * [size=medium]RocketMQ支持延迟/定时消息，但并不支持任意的时间精度，而是支持特定的level，例如5s，10s，1m等。
     * 其中level=0表示不延时，level=1表示1级延时，level=2表示2级延时，以此类推。[/size]
     *
     * [size=medium][color=red]延迟级别配置[/color][/size]
     * [size=medium]在rocketmq的broker端的属性配置文件中加入以下行：
     * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     *
     * 描述了各级别与延时时间的对应映射关系。
     * 1、这个配置项配置了从1级开始，各级延时的时间，可以修改这个指定级别的延时时间；
     * 2、时间单位支持：s、m、h、d，分别表示秒、分、时、天；
     * 3、默认值就是上面声明的，可手工调整[/size]
     * ————————————————
     * 版权声明：本文为CSDN博主「hanzhdy」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/hanzhdy/article/details/84920316
     * @param topic
     * @param ext
     */
    public void sendMsgByJsonDelay(String topic,OrderExt ext){
        Message<OrderExt> message = MessageBuilder.withPayload(ext).build();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("------(1)---->" + simpleDateFormat.format(new Date()));
        rocketMQTemplate.syncSend(topic,message,2000,4);
        System.out.println("------(2)---->" + simpleDateFormat.format(new Date()));
        System.out.println("发送延迟消息成功" + ext.toString());
    }


    /**
     * 发送异步延迟消息
     * @param topic
     * @param ext
     */
    public void sendAsyncMsgByJsonDelay(String topic,OrderExt ext) throws JsonProcessingException, RemotingException, MQClientException, InterruptedException {

        //将消息内容ext转成json
        String jsonString = rocketMQTemplate.getObjectMapper().writeValueAsString(ext);

        org.apache.rocketmq.common.message.Message message = new org.apache.rocketmq.common.message.Message(topic,jsonString.getBytes(Charset.forName("utf-8")));
        message.setDelayTimeLevel(3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("------(1)---->" + simpleDateFormat.format(new Date()));
        rocketMQTemplate.getProducer().send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送异步延迟消息成功" +  sendResult.toString());
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送异步延迟消息是失败" + throwable.getMessage());
            }
        });

        System.out.println("------(2)---->" + simpleDateFormat.format(new Date()));
        System.out.println("发送异步延迟消息成功" + ext.toString());
    }



}
