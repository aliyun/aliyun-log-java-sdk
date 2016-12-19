package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListACLRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5475689473002878781L;
	private String logStore = "";

	public ListACLRequest(String project) {
		super(project);
		SetParam(Consts.CONST_TYPE, "acl");
		SetOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		SetSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListACLRequest(String project, int offset, int size) {
		super(project);
		SetParam(Consts.CONST_TYPE, "acl");
		SetOffset(offset);
		SetSize(size);
	}
	
	public ListACLRequest(String project, String logStore) {
		super(project);
		SetLogStore(logStore);
		SetParam(Consts.CONST_TYPE, "acl");
		SetOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		SetSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListACLRequest(String project, String logStore, int offset, int size) {
		super(project);
		SetLogStore(logStore);
		SetParam(Consts.CONST_TYPE, "acl");
		SetOffset(offset);
		SetSize(size);
	}

	public int GetOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void SetOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int GetSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void SetSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}

	public String GetLogStore() {
		return logStore;
	}

	public void SetLogStore(String logStore) {
		this.logStore = logStore;
	}
	
	
}
