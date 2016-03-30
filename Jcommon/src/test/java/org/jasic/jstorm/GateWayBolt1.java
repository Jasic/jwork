package org.jasic.jstorm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.TupleImplExt;
import org.jasic.jstorm.bean.GateWayBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author 菜鹰
 * @Date 2016/3/22
 * @Explain:
 */
public class GateWayBolt1 extends BaseRichBolt {
    private static final Logger LOGGER = LoggerFactory.getLogger(GateWayBolt1.class);
    private OutputCollector collector;


    private static final Logger log = LoggerFactory.getLogger("saveBean");
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        GateWayBean bean;
        try {
            bean = (GateWayBean) input.getValue(0);
        } catch (Exception e) {
            LOGGER.error(input.getSourceComponent() + "  " + input.getSourceTask() + " " + input.getSourceStreamId() + " target " + ((TupleImplExt) input));
            throw new RuntimeException(e);
        }

        LOGGER.info(String.format("receive from spout ,gateway bean is : %s", bean));

        // 发送ack信息告知spout 完成处理的消息 ，如果下面的hbase的注释代码打开了，则必须等到插入hbase完毕后才能发送ack信息，这段代码需要删除
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}