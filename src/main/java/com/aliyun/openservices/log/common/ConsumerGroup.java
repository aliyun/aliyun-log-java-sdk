package com.aliyun.openservices.log.common;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class ConsumerGroup implements Serializable {
	private String consumerGroupName;
	private int timeout;
	private boolean inOrder;
	
	/**
	 * @param consumerGroupName
	 * 			consumer group name
	 * @param timeout
	 * 			if the time interval of a consumer's heartbeat exceed this value in second,  the consumer will be deleted.
	 * @param inOrder
	 * 			consume data in oder or not
	 */
	public ConsumerGroup(String consumerGroupName, int timeout, boolean inOrder) {
		super();
		this.consumerGroupName = consumerGroupName;
		this.timeout = timeout;
		this.inOrder = inOrder;
	}
	public String getConsumerGroupName() {
		return consumerGroupName;
	}
	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public boolean isInOrder() {
		return inOrder;
	}
	public void setInOrder(boolean inOrder) {
		this.inOrder = inOrder;
	}
	public JSONObject ToRequestJson(){
		JSONObject logStoreDict = new JSONObject();
		logStoreDict.put("consumerGroup", getConsumerGroupName());
		logStoreDict.put("timeout", getTimeout());
		logStoreDict.put("order", isInOrder());
		
		return logStoreDict;
	}
	public String ToRequestString() {	
		return ToRequestJson().toString();
	}
	@Override
	public String toString() {
		return "ConsumerGroup [consumerGroupName=" + consumerGroupName
				+ ", timeout=" + timeout + ", inOrder=" + inOrder + "]";
	}
	
}
