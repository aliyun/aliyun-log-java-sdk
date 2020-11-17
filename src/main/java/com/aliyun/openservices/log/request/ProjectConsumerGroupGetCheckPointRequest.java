package com.aliyun.openservices.log.request;

public class ProjectConsumerGroupGetCheckPointRequest extends Request {
    private static final long serialVersionUID = 2242143026314791259L;

    private String consumerGroup;

    public ProjectConsumerGroupGetCheckPointRequest(String project, String consumerGroup, String logStore, int shard) {
        super(project);
        this.consumerGroup = consumerGroup;

        if (!logStore.isEmpty()) {
            super.SetParam("logstore", logStore);
        }
        if (shard >= 0) {
            super.SetParam("shard", String.valueOf(shard));
        }
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}