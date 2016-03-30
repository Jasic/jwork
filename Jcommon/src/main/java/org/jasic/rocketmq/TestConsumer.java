package org.jasic.rocketmq;

/**
 * Created by Caiying on 2015/5/23.
 */

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;

import javax.annotation.PreDestroy;
import java.lang.Exception;import java.lang.String;import java.lang.System;import java.util.List;

public class TestConsumer {
    public static void main(String[] args) {
        DefaultMQPushConsumer consumer =
                new DefaultMQPushConsumer("PushConsumer");
        consumer.setNamesrvAddr("10.173.84.10:9876");
        try {
            //订阅PushTopic下Tag为push的消息
            consumer.subscribe("game", "war3||tx");
            //程序第一次启动从消息队列头取数据
            consumer.setConsumeFromWhere(
                    ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(
                                List<MessageExt> list,
                                ConsumeConcurrentlyContext Context) {
                            Message msg = list.get(0);
                            if (msg.getKeys().contains("dota1")) {
                                System.out.println("玩Dota1的小伙:" + new String(msg.getBody()));
                            }
//                           else
                            if (msg.getKeys().contains("dota2")) {
                                System.out.println("玩Dota2的小伙:" + new String(msg.getBody()));
                            }
//                           else
                            if (msg.getKeys().contains("lol")) {
                                System.out.println("玩lol的00后:" + new String(msg.getBody()));
                            }
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            consumer.start();
        } catch (Exception e) {
            consumer.unsubscribe("game");
            System.out.println("取消监听game消息成功！");
            System.out.println("测试失败~原因:" + e.getMessage());
        }
    }
}
