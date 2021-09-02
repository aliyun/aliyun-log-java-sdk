package com.aliyun.openservices.log.response;

import java.util.Map;

public class ListTagResourcesResponse extends Response {
	private static final long serialVersionUID = -2784242482549088661L;
	private String tagList;

	public ListTagResourcesResponse(Map<String, String> headers, String tagList) {
		super(headers);
		this.tagList = tagList;
	}

	public String getTagList() {
		return tagList;
	}

	public void setTagList(String tagList) {
		this.tagList = tagList;
	}

}
