package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CursorMode;

public class DeleteShardRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -181884038613668465L;
	protected String mLogStore = "";
	protected int mShardId = -1; 
	/**
	 * Construct delete shard request
	 * @param project
	 *            project name
	 * @param logStore
	 *            log stream name
	 * @param shardId
	 * 			  shard id
	 */
	public DeleteShardRequest(String project, String logStore, int shardId) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
	}
	


	public DeleteShardRequest(String project, String logStore, int shardId, CursorMode mode) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		
	}

	/**
	 * @return the logStore
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * @param logStore the logStore to set
	 */
	public void SetLogStore(String logStore) {
		this.mLogStore = logStore;
	}
	
	/**
	 * @return the shardId
	 */
	public int GetShardId() {
		return mShardId;
	}

	/**
	 * @param shardId the shardId to set
	 */
	public void SetShardId(int shardId) {
		this.mShardId = shardId;
	}
	
}
