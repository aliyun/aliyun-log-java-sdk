package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ProjectConsumerGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListProjectConsumerGroupResponse extends Response {
    private static final long serialVersionUID = -5449137454886127253L;

    private List<ProjectConsumerGroup> consumerGroups = new ArrayList<ProjectConsumerGroup>();

    public ListProjectConsumerGroupResponse(Map<String, String> headers) {
        super(headers);
    }

    public List<ProjectConsumerGroup> getConsumerGroups() {
        return consumerGroups;
    }

    public void setConsumerGroups(List<ProjectConsumerGroup> consumerGroups) {
        this.consumerGroups = consumerGroups;
    }
}