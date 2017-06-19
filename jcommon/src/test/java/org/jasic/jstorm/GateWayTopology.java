package org.jasic.jstorm;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

/**
 * @Author
 * @Date 2016/3/22
 * @Explain:
 */
public class GateWayTopology {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        new GateWayTopology().start();
    }

    public void start() throws AlreadyAliveException, InvalidTopologyException {

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("GateWaySpout", new GateWaySpout(), 1);
        builder.setBolt("GateWayBolt1", new GateWayBolt1(), 5).shuffleGrouping("GateWaySpout");

        Config conf = new Config();
        conf.setNumAckers(1);
        conf.put(Config.TOPOLOGY_WORKERS, 1);

        StormSubmitter.submitTopology("GateWayTopology", conf, builder.createTopology());
        System.out.println("storm cluster will start");
    }

}