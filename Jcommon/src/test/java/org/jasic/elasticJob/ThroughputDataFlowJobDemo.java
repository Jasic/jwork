package org.jasic.elasticJob;

import com.dangdang.ddframe.job.internal.job.AbstractJobExecutionShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.dataflow.AbstractIndividualThroughputDataFlowElasticJob;

import java.util.List;

/**
 * @Author 菜鹰
 * @Date 2016/4/6
 * @Explain:
 */
public class ThroughputDataFlowJobDemo extends AbstractIndividualThroughputDataFlowElasticJob {
    @Override
    public List fetchData(AbstractJobExecutionShardingContext shardingContext) {
        return null;
    }

    @Override
    public boolean isStreamingProcess() {
        return false;
    }

    @Override
    public boolean processData(AbstractJobExecutionShardingContext shardingContext, Object data) {
        return false;
    }
}
