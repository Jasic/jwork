package org.jasic.rocketmq;

/**
 * Created by Caiying on 2015/5/23.
 */
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;import java.lang.Exception;import java.lang.String;import java.lang.System;import java.lang.Thread;

public class TestProducer {
    public static void main(String[] args){
        DefaultMQProducer producer = new DefaultMQProducer("Producer");
        producer.setNamesrvAddr("10.173.84.10:9876");
        try {
            producer.start();

            for(;;) {
                Message msg = new Message("game",
                        "war3",
                        "dota1",
                        "Ha ha I just play dota1".getBytes());

                SendResult result = producer.send(msg);
                System.out.println("id:" + result.getMsgId() +
                        " result:" + result.getSendStatus());

                Thread.sleep(1000);
                msg = new Message("game",
                        "war3",
                        "dota2",
                        "Ha ha I just play dota2".getBytes());

                result = producer.send(msg);
                System.out.println("id:" + result.getMsgId() +
                        " result:" + result.getSendStatus());
                Thread.sleep(1000);
                msg = new Message("game",
                        "tx",
                        "lol",
                        "Ha ha I am 00 ,just play lol".getBytes());

                result = producer.send(msg);
                System.out.println("id:" + result.getMsgId() +
                        " result:" + result.getSendStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            producer.shutdown();
        }
    }
}
