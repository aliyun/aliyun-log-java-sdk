package com.aliyun.openservices.log.request;

public class ConsumerGroupGetCheckPointRequest extends ConsumerGroupRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2592851947189365492L;
	private String consumerGroup;
	
	/**
	 * @param project
	 * 			project name
	 * @param logstore
	 * 			logstore name
	 * @param consumergroup
	 * 			consumer group name
	 * @param shard
	 * 			shard id
	 */
	public ConsumerGroupGetCheckPointRequest(String project, String logstore, String consumergroup, int shard) {
		super(project, logstore);
		this.consumerGroup = consumergroup;
		if(shard >= 0)
		{
			super.SetParam("shard", "" + shard);
		}
	}
	
	public String GetConsumerGroup() {
		return consumerGroup;
	}
	public void SetConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}	
}
