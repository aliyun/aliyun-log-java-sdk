package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ProjectConsumerGroup;

public class CreateProjectConsumerGroupRequest extends Request {
    private static final long serialVersionUID = -306374847977621816L;
    private ProjectConsumerGroup consumerGroup;

    /**
     * @param project
     *          project name
     * @param consumerGroup
     *          consumer group
     */
    public CreateProjectConsumerGroupRequest(String project, ProjectConsumerGroup consumerGroup) {
        super(project);
        this.consumerGroup = consumerGroup;
    }

    public ProjectConsumerGroup getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(ProjectConsumerGroup consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}