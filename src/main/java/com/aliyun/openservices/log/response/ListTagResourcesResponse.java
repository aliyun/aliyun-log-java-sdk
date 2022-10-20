package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.TagResource;

import java.util.List;
import java.util.Map;

public class ListTagResourcesResponse extends Response {
	private static final long serialVersionUID = -2784242482549088661L;
	private List<TagResource> tagResources;
	private String nextToken;

	public ListTagResourcesResponse(Map<String, String> headers, String nextToken, List<TagResource> tagResources) {
		super(headers);
		this.nextToken = nextToken;
		this.tagResources = tagResources;
	}

	public List<TagResource> getTagResources() {
		return tagResources;
	}

	public void setTagResources(List<TagResource> tagResources) {
		this.tagResources = tagResources;
	}

	public String getNextToken() {
		return nextToken;
	}

	public void setNextToken(String nextToken) {
		this.nextToken = nextToken;
	}

}
