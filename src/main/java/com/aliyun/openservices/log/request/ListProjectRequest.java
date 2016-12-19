package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListProjectRequest extends Request {
	
	/**
	 * list project request
	 */
	private static final long serialVersionUID = -7830849975374540780L;

	public void SetOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public void SetSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}
	
	public void SetProjectName(String projectName) {
		SetParam(Consts.CONST_PROJECTNAME, String.valueOf(projectName));
	}

	public ListProjectRequest(String project, int offset, int size) {
		super("");
		SetOffset(offset);
		SetSize(size);
		SetProjectName(project);
	}
}
