package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Index;

public class GetIndexResponse extends Response {

	private static final long serialVersionUID = -3827471917355286807L;
	private Index index = new Index();

	public GetIndexResponse(Map<String, String> headers, Index index) {
		super(headers);
		SetIndex(index);
	}

	public Index GetIndex() {
		return index;
	}

	public void SetIndex(Index index) {
		this.index = new Index(index);
	}

}
