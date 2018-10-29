package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListDashboardRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2229371777283193940L;

	public ListDashboardRequest(String project) {
		super(project);
		setDashboardName("");
		setDisplayName("");
		setOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		setSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}

	public ListDashboardRequest(String project, String dashboardName, String displayName, int offset, int size) {
		super(project);
		setDashboardName(dashboardName);
		setDisplayName(displayName);
		setOffset(offset);
		setSize(size);
	}

	public String getDashboardName() {
		return GetParam("dashboardName");
	}

	public void setDashboardName(String savedSearchName) {
		SetParam("dashboardName", savedSearchName);
	}

	public String getDisplayName() { return GetParam(Consts.CONST_DISPLAY_NAME); }

	public void setDisplayName(String displayName) { SetParam(Consts.CONST_DISPLAY_NAME, displayName); }

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
