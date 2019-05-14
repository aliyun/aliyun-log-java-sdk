package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CursorMode;

public class SplitShardRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -181884038613668465L;
	protected String mLogStore = "";
	protected int mShardId = -1;
	protected int mShardCount = -1;
	/**
	 * Construct a get cursor request
	 * @param project
	 *            project name
	 * @param logStore
	 *            log stream name
	 * @param shardId
	 * 			  shard id
	 * @param midHash
	 *            midHash
	 */
	public SplitShardRequest(String project, String logStore, int shardId, String midHash) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetMidHash(midHash);
		SetParam(Consts.CONST_ACTION, Consts.CONST_ACTION_SPLIT);
	}
	


	public SplitShardRequest(String project, String logStore, int shardId, CursorMode mode) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetParam(Consts.CONST_ACTION, Consts.CONST_ACTION_SPLIT);
	 
	}
	
	public SplitShardRequest(String project, String logStore, int shardId, int shardCount) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetShardCount(shardCount);
		SetParam(Consts.CONST_ACTION, Consts.CONST_ACTION_SPLIT);
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
	
	public String GetShardCount() {
		return GetParam(Consts.CONST_SPLIT_SHARDCOUNT);
	}
	
	public void SetShardCount(int shardCount) {
		SetParam(Consts.CONST_SPLIT_SHARDCOUNT, String.valueOf(shardCount));
	}
 
	public String GetMidHash() {
		return GetParam(Consts.CONST_SPLIT_MID_HASH);
	}

	public void SetMidHash(String midHash) {
		if (midHash != null || midHash.length() > 0) {
			SetParam(Consts.CONST_SPLIT_MID_HASH,midHash);
		}
	}
 }
