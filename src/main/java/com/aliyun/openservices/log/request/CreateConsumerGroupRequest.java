package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ConsumerGroup;

public class CreateConsumerGroupRequest extends ConsumerGroupRequest {
	
	private static final long serialVersionUID = 7471743896305824757L;
	private ConsumerGroup consumerGroup;
	/**
	 * @param project
	 * 			project name
	 * @param logstore
	 * 			logstore name
	 * @param consumerGroup
	 * 			consumer group
	 */
	public CreateConsumerGroupRequest(String project,String logstore, ConsumerGroup consumerGroup) {
		super(project, logstore);
		this.consumerGroup = consumerGroup;
	}
	public ConsumerGroup GetConsumerGroup() {
		return consumerGroup;
	}
	public void SetConsumerGroup(ConsumerGroup consumerGroup) {
		this.consumerGroup = consumerGroup;
	}
	
}
