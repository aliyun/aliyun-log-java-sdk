/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Consts;

/**
 * The response of the ListTopic API from sls server
 * @author sls_dev
 *
 */
public class ListTopicsResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3212369196651202759L;
	private String mNextToken = new String();
	private ArrayList<String> mTopics = new ArrayList<String>();
	
	/**
	 * Construct the response with http headers
	 * @param headers http headers
	 */
	public ListTopicsResponse(Map<String, String> headers)
	{
		super(headers);
		SetNextToken(headers.get(Consts.CONST_X_SLS_NEXT_TOKEN));
	}
	/**
	 * Set the next token to the response
	 * @param nextToken the start topic value
	 */
	public void SetNextToken(String nextToken)
	{
		mNextToken = nextToken;
	}
	
	/**
	 * Set topics to the response
	 * @param topics topics list
	 */
	public void SetTopics(List<String> topics)
	{
		mTopics = new ArrayList<String>(topics);
	}

	/**
	 * Return the count of topics in response
	 * @return topics' count
	 */
	public int GetCount() {
		return mTopics.size();
	}
	
	/**
	 * Return the next token from the response.  If there is no more topics to list, it will return an empty string.
	 * @return next token
	 */
	public String GetNextToken() {
		return mNextToken;
	}

	/**
	 * Get all the topics from the response
	 * @return topics list
	 */
	public ArrayList<String> GetTopics() {
		return mTopics;
	}
}
