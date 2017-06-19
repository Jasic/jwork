package org.jasic.elasticJob;

/**
 * @Author 菜鹰
 * @Date 2016/4/6
 * @Explain:
 */
import com.dangdang.ddframe.job.api.JobConfiguration;
import com.dangdang.ddframe.job.api.JobScheduler;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.PropertyConfigurator;


public class JobDemo {

    // 定义Zookeeper注册中心配置对象
    private ZookeeperConfiguration zkConfig = new ZookeeperConfiguration("10.16.17.215:2181", "elasticJob", 1000, 3000, 3);

    // 定义Zookeeper注册中心
    private CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);

    // 定义作业1配置对象
    private JobConfiguration jobConfig1 = new JobConfiguration("simpleJob", SimpleJobDemo.class, 1, "0/5 * * * * ?");

    // 定义作业2配置对象
    private JobConfiguration jobConfig2 = new JobConfiguration("throughputDataFlowJob", ThroughputDataFlowJobDemo.class, 10, "0/5 * * * * ?");

    // 定义作业3配置对象
    private JobConfiguration jobConfig3 = new JobConfiguration("sequenceDataFlowJob", SequenceDataFlowJobDemo.class, 10, "0/5 * * * * ?");

    public static void main(final String[] args) {
        new JobDemo().init();
    }

    private void init() {

        PropertyConfigurator.configure("D:\\github\\jwork\\Jcommon\\src\\test\\resources\\log4j.properties");
        // 连接注册中心
        regCenter.init();
        // 启动作业1
        new JobScheduler(regCenter, jobConfig1).init();
        // 启动作业2
        new JobScheduler(regCenter, jobConfig2).init();
        // 启动作业3
        new JobScheduler(regCenter, jobConfig3).init();
    }
}