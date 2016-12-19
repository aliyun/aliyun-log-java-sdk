package com.aliyun.openservices.log.common;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class ConsumerGroupShardCheckPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2583391093535157892L;
	private int shard;
	private String checkPoint;
	private long updateTime;
	private String consumer;
	public ConsumerGroupShardCheckPoint(int shard, String checkPoint,
			long updateTime, String consumer) {
		super();
		this.shard = shard;
		this.checkPoint = checkPoint;
		this.updateTime = updateTime;
		this.consumer = consumer;
	}
	public ConsumerGroupShardCheckPoint()
	{
		
	}
	/**
	 * @return
	 * 		shard id
	 */
	public int getShard() {
		return shard;
	}
	public void setShard(int shard) {
		this.shard = shard;
	}
	/**
	 * @return
	 * 		get shard checkpoint
	 */
	public String getCheckPoint() {
		return checkPoint;
	}
	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}
	/**
	 * @return
	 * 		checkpoint last update time
	 */
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getConsumer() {
		return consumer;
	}
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	public void Deserialize(JSONObject obj)
	{
		shard = obj.getInt("shard");
		checkPoint = obj.getString("checkpoint");
		updateTime = obj.getLong("updateTime");
		
		if (obj.containsKey("consumer")) {
			consumer = obj.getString("consumer");
		}
	}
	@Override
	public String toString() {
		return "ConsumerGroupShardCheckPoint [shard=" + shard + ", checkPoint="
				+ checkPoint + ", updateTime=" + updateTime + ", consumer="
				+ consumer + "]";
	}
	
	
}
