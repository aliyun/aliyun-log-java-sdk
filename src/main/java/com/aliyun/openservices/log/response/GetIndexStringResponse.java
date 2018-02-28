package com.aliyun.openservices.log.response;

import java.util.Map;

public class GetIndexStringResponse extends Response {

	private static final long serialVersionUID = -3827471917355286807L;
	private String index;

	public GetIndexStringResponse(Map<String, String> headers, String index) {
		super(headers);
		SetIndex(index);
	}

	public String GetIndex() {
		return index;
	}

	public void SetIndex(String index) {
		this.index = index;
	}

}
