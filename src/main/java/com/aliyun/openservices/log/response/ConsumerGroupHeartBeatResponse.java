package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

public class ConsumerGroupHeartBeatResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4941087094480475965L;
	private ArrayList<Integer> shards;
	public ConsumerGroupHeartBeatResponse(Map<String, String> headers, ArrayList<Integer> shards) {
		super(headers);
		this.shards = shards;
	}
	/**
	 * @return
	 * 		the shards consumer should held in time
	 */
	public ArrayList<Integer> GetShards() {
		return shards;
	}
	public void SetShards(ArrayList<Integer> shards) {
		this.shards = shards;
	}
	
}
