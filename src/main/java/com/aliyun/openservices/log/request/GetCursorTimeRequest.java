package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class GetCursorTimeRequest extends Request {
	private static final long serialVersionUID = -5158398589858489234L;
	protected String mLogStore = "";
	protected int mShardId = -1;

	public GetCursorTimeRequest(String project, String logStore, int shardId,
			String cursor) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetCursor(cursor);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_CURSOR_TIME);
	}

	/**
	 * @return the logStore
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * @param logStore
	 *            the logStore to set
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
	 * @param shardId
	 *            the shardId to set
	 */
	public void SetShardId(int shardId) {
		this.mShardId = shardId;
	}

	/**
	 * 
	 * @return the cursor to query
	 */
	public String GetCursor() {
		return GetParam(Consts.CONST_CURSOR);
	}

	/**
	 * 
	 * @param cursor
	 */
	public void SetCursor(String cursor) {
		SetParam(Consts.CONST_CURSOR, cursor);
	}
}
