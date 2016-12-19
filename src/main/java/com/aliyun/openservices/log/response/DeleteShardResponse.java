/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.Map;

/**
 * The response of the DeleteShard API from sls server
 * @author sls_dev
 *
 */
public class DeleteShardResponse extends Response {
	private static final long serialVersionUID = -4660644764028977169L;

	/**
	 * Construct the response with http headers
	 * @param headers http headers
	 */
	public DeleteShardResponse(Map<String, String> headers) {
		super(headers);
	}
}
