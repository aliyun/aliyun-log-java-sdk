package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CursorMode;

public class GetCursorRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -181884038613668465L;
	protected String mLogStore = "";
	protected int mShardId = -1;
	/**
	 * Construct a get cursor request
	 * @param project
	 *            project name
	 * @param logStore
	 *            log stream name
	 * @param shardId
	 * 			  shard id
	 * @param fromTime
	 *            from time
	 */
	public GetCursorRequest(String project, String logStore, int shardId, long fromTime) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetFrom(fromTime);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_CURSOR);
	}
	
	public GetCursorRequest(String project, String logStore, int shardId, CursorMode mode) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetFrom(mode);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_CURSOR);
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
	

	/**
	 * @param fromTime the from time to set
	 */
	public void SetFrom(long fromTime) {
		SetParam(Consts.CONST_FROM, String.valueOf(fromTime));
	}
	
	/**
	 * @return the mode
	 */
	public String GetFrom() {
		return GetParam(Consts.CONST_FROM);
	}

	/**
	 * @param mode the mode to set
	 */
	public void SetFrom(CursorMode mode) {
		if (mode != CursorMode.NONE) {
			SetParam(Consts.CONST_FROM, mode.toString());
		}
	}
}
