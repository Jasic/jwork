package org.jasic.jstorm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.jasic.jstorm.bean.GateWayBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author 菜鹰
 * @Date 2016/3/22
 * @Explain:
 */
public class GateWaySpout extends BaseRichSpout {
    private static final Logger LOGGER = LoggerFactory.getLogger(GateWaySpout.class);

    private Random random;

    private SpoutOutputCollector collector;

    private BlockingQueue<GateWayBean> bufferQueue;

    private static AtomicLong pendNum = new AtomicLong(0);

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

        this.random = new Random();
        this.collector = collector;
        this.bufferQueue = new LinkedBlockingDeque<GateWayBean>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    GateWayBean bean = mockBean();
                    try {
                        bufferQueue.put(bean);
                        Thread.sleep(random.nextInt(1000));
                    } catch (InterruptedException e) {
                    }
                    LOGGER.info(" >> Put the bean{} in the queue...", bean);
                }
            }
        }).start();
    }

    private GateWayBean mockBean() {
        Long a = pendNum.incrementAndGet();
        GateWayBean bean = new GateWayBean();
        bean.setBeanId(a);
        bean.setBeanName("gw[" + a + "]");
        bean.setCount(random.nextInt(10));
        return bean;
    }

    @Override
    public void nextTuple() {
        while (true) {
            GateWayBean bean = this.bufferQueue.poll();
            if (bean != null) {
                Values values =new Values(bean);
                this.collector.emit(values);
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("log"));
    }

    /**
     * 启用 ack 机制，详情参考：https://github.com/alibaba/jstorm/wiki/Ack-%E6%9C%BA%E5%88%B6
     *
     * @param msgId
     */
    @Override
    public void ack(Object msgId) {
        super.ack(msgId);
        LOGGER.info(" >> Process msg {} success~" ,msgId);
    }

    /**
     * 消息处理失败后需要自己处理
     *
     * @param msgId
     */
    @Override
    public void fail(Object msgId) {
        super.fail(msgId);
        LOGGER.info("ack fail,msgId" + msgId);
    }

}