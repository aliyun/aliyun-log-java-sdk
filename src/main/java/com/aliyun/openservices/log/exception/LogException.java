/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.exception;

/**
 * The excpetion is thrown if error happen.
 * 
 * @author sls_dev
 * 
 */
public class LogException extends Exception {

	private static final long serialVersionUID = -4441995860203577032L;

	private String errorCode;

	private String requestId;

	/**
	 * Construct LogException
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            error message
	 * @param requestId
	 *            request id from sls server, if the error is happened in the
	 *            client, the request id is empty
	 */
	public LogException(String code, String message, String requestId) {
		super(message);
		this.errorCode = code;
		this.requestId = requestId;
	}

	/**
	 * Construct LogException
	 * 
	 * @param code
	 *            error code
	 * @param message
	 *            error message
	 * @param cause
	 *            inner exception, which cause the error
	 * @param requestId
	 *            request id from sls server, if the error is happened in the
	 *            client, the request id is empty
	 */
	public LogException(String code, String message, Throwable cause,
			String requestId) {
		super(message, cause);
		this.errorCode = code;
		this.requestId = requestId;
	}

	/**
	 * Get the error code
	 * 
	 * @return error code
	 */
	public String GetErrorCode() {
		return this.errorCode;
	}

	/**
	 * Get the error message
	 * 
	 * @return error message
	 */
	public String GetErrorMessage() {
		return super.getMessage();
	}

	/**
	 * Get the request id
	 * 
	 * @return request id, if the error is happened in the client, the request
	 *         id is empty
	 */
	public String GetRequestId() {
		return this.requestId;
	}
}
