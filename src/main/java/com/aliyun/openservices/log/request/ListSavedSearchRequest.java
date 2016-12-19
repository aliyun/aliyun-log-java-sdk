package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListSavedSearchRequest extends Request {

	private static final long serialVersionUID = 8075773886069792616L;

	public ListSavedSearchRequest(String project) {
		super(project);
		setSavedSearchName(Consts.DEFAULT_REQUEST_PARAM_SAVEDSEARCHNAME);
		setOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		setSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListSavedSearchRequest(String project, int offset, int size) {
		super(project);
		setSavedSearchName(Consts.DEFAULT_REQUEST_PARAM_SAVEDSEARCHNAME);
		setOffset(offset);
		setSize(size);
	}
	
	public ListSavedSearchRequest(String project, String savedSearchName, int offset, int size) {
		super(project);
		setSavedSearchName(savedSearchName);
		setOffset(offset);
		setSize(size);
	}

	public String getSavedSearchName() {
		return GetParam(Consts.CONST_SAVEDSEARCH_NAME);
	}

	public void setSavedSearchName(String savedSearchName) {
		SetParam(Consts.CONST_SAVEDSEARCH_NAME, savedSearchName);
	}

	public int getOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void setOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int getSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void setSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}

}
