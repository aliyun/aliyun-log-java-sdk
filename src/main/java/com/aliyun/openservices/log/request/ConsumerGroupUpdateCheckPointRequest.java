package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;

public class ConsumerGroupUpdateCheckPointRequest extends ConsumerGroupRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1478674182919130030L;
	private String consumerGroup;
	private int shard;
	private String checkPoint;
	/**
	 * @param project
	 * 			project name
	 * @param logstore
	 * 			logstore name
	 * @param consumerGroup
	 * 			consumer group name
	 * @param consumer
	 * 			consumer name
	 * @param forceSuccess
	 * 			alwarys update checkpoint success whether the shard is hold by this consumer 
	 * @param shard
	 * 			shard id
	 * @param checkPoint
	 * 			shard cursor
	 */
	public ConsumerGroupUpdateCheckPointRequest(String project, String logstore, String consumerGroup, String consumer, boolean forceSuccess, int shard, String checkPoint) {
		super(project, logstore);
		this.consumerGroup = consumerGroup;
		super.SetParam("type", "checkpoint");
		super.SetParam("consumer", consumer);
		super.SetParam("forceSuccess", forceSuccess ? "true" : "false");
		this.shard = shard;
		this.checkPoint = checkPoint;
	}
	public String GetConsumerGroup() {
		return consumerGroup;
	}
	public void SetConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}
	public String GetRequestBody()
	{
		JSONObject dict = new JSONObject();
		dict.put("shard", shard);
		dict.put("checkpoint", checkPoint);
		return dict.toString();
	}
}
