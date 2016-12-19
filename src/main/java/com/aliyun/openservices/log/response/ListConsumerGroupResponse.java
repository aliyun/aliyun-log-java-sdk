package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

import com.aliyun.openservices.log.common.ConsumerGroup;

public class ListConsumerGroupResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6821099022986023715L;
	private ArrayList<ConsumerGroup> consumerGroups = new ArrayList<ConsumerGroup>();
	public ListConsumerGroupResponse(Map<String, String> headers) {
		super(headers);
	}
	public ArrayList<ConsumerGroup> GetConsumerGroups() {
		return consumerGroups;
	}
	public void SetConsumerGroups(ArrayList<ConsumerGroup> consumerGroups) {
		this.consumerGroups = consumerGroups;
	}
	
}
