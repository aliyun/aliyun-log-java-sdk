package com.aliyun.openservices.log.response;

import java.util.Map;

public class GetCursorResponse extends Response {

	private static final long serialVersionUID = 9167468569342193358L;

	private String mCursor;

	/**
	 * Construct the response with http headers
	 * @param headers http headers
	 * @param cursor the result cursor
	 */
	public GetCursorResponse(Map<String, String> headers, String cursor) {
		super(headers);
		mCursor = cursor;
	}

	/**
	 * @return the cursor
	 */
	public String GetCursor() {
		return mCursor;
	}
}
