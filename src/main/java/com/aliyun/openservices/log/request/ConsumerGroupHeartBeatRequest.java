package com.aliyun.openservices.log.request;

import java.util.ArrayList;

import net.sf.json.JSONArray;

public class ConsumerGroupHeartBeatRequest extends ConsumerGroupRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7110490639104895609L;
	private ArrayList<Integer> shards;
	/**
	 * @param project
	 * 			project name
	 * @param logstore
	 * 			logstore name
	 * @param consumerGroup
	 * 			consumer group name
	 * @param consumer
	 * 			consumer name
	 * @param shards
	 * 			the shards hold by the consumer
	 */
	public ConsumerGroupHeartBeatRequest(String project, String logstore, String consumerGroup, String consumer, ArrayList<Integer> shards) {
		super(project, logstore);
		super.SetParam("type", "heartbeat");
		super.SetParam("consumer", consumer);
		this.shards = shards;
	}
	public ArrayList<Integer> GetShards() {
		return shards;
	}
	public void SetShards(ArrayList<Integer> shards) {
		this.shards = shards;
	}
	public String GetRequestBody()
	{
		JSONArray array = new JSONArray();
		array.addAll(shards);
		return array.toString();
	}
}
