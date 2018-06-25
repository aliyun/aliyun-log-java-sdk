package com.aliyun.openservices.log.response;

import java.util.Map;

public class GetCursorTimeResponse  extends Response {

	private static final long serialVersionUID = 83320847206807600L;

	
	private int mCursorTime;

	/**
	 * Construct the response with http headers
	 * @param headers http headers
	 * @param cursorTime the result cursor time
	 */
	public GetCursorTimeResponse(Map<String, String> headers, int cursorTime) {
		super(headers);
		mCursorTime = cursorTime;
	}

	/**
	 * @return the cursor
	 */
	public int GetCursorTime() {
		return mCursorTime;
	}
}

