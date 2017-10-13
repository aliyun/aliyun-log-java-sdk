package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * The shard info
 * 
 * @author log-service-dev
 * 
 */
public class Shard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7229056055711960465L;
	protected int shardId = 0;
	protected String status ;
	protected String inclusiveBeginKey;
	protected String exclusiveEndKey;
	protected String serverIp = null;
	protected int createTime;


	/**
	 * Return shard status
	 * 
	 * @return shard status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * set shard status
	 * 
	 * @param status
	 *            the shard status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Return shard inclusive begin key
	 * 
	 * @return shard inclusive begin key
	 */
	public String getInclusiveBeginKey() {
		return inclusiveBeginKey;
	}
	/**
	 * set shard inclusive begin key
	 * 
	 * @param inclusiveBeginKey
	 *            the shard inclusive begin key
	 */
	public void setInclusiveBeginKey(String inclusiveBeginKey) {
		this.inclusiveBeginKey = inclusiveBeginKey;
	}
	/**
	 * Return shard exclusive end key
	 * 
	 * @return shard exclusive end key
	 */
	public String getExclusiveEndKey() {
		return exclusiveEndKey;
	}
	/**
	 * set shard exclusive end key
	 * 
	 * @param exclusiveEndKey
	 *            the shard exclusive end key
	 */
	public void setExclusiveEndKey(String exclusiveEndKey) {
		this.exclusiveEndKey = exclusiveEndKey;
	}
	/**
	 * Return shard create time 
	 * 
	 * @return shard createTime
	 */
	public int getCreateTime() {
		return createTime;
	}
	/**
	 * set server ip
	 * @param serverIp the server ip for the shard
	 */
	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
	/**
	 * return server ip for the shard
	 * @return server ip for the shard
	 */
	public String getServerIp()
	{
		return this.serverIp;
	}

	/**
	 * @param createTime 
	 * 			shard create time
	 */
	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}

	/**
	 * Create a shard with shard id
	 * 
	 * @param shardId
	 *            the shard id
	 * @param createTime shard create time
	 * @param end  shard end range key
	 * @param begin shard start key
	 * @param status shard status, "readonly" or "readwrite"
	 */
	public Shard(int shardId, String status, String begin, String end, int createTime) {
		this.shardId = shardId;
		this.status = status;
		this.inclusiveBeginKey = begin;
		this.exclusiveEndKey = end;
		this.createTime = createTime;
	}

	/**
	 * Return shard id
	 * 
	 * @return shard id
	 */
	public int GetShardId() {
		return shardId;
	}
}
