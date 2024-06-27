/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The Request used to get histograms of a query from sls server
 * 
 * @author sls_dev
 * 
 */
public class GetHistogramsRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1714940205081492986L;

	private String mLogStore;

	/**
	 * Construct a request with full parameters
	 * 
	 * @param project
	 *            sls project
	 * @param logStore
	 *            the logstore in the project
	 * @param topic
	 *            the topic of the logstore
	 * @param query
	 *            user defined query
	 * @param from
	 *            the begin time
	 * @param to
	 *            the end time
	 */
	public GetHistogramsRequest(String project, String logStore, String topic,
			String query, int from, int to) {
		super(project);
		mLogStore = logStore;
		SetTopic(topic);
		SetQuery(query);
		SetFromTime(from);
		SetToTime(to);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_HISTOGRAM);
	}

	/**
	 * Construct a request
	 *
	 * @param project
	 *            sls project
	 * @param logStore
	 *            the logstore in the project
	 * @param topic
	 *            the topic of the logstore
	 * @param query
	 *            user defined query
	 * @param from
	 *            the begin time
	 * @param to
	 *            the end time
	 * @param accurate
	 * 			  accurate flag
	 */
	public GetHistogramsRequest(String project, String logStore, String topic,
								String query, int from, int to, boolean accurate) {
		this(project, logStore, topic, query, from, to);
		SetAccurate(accurate);
	}

	/**
	 * Construct a request
	 *
	 * @param project
	 *            sls project
	 * @param logStore
	 *            the logstore in the project
	 * @param topic
	 *            the topic of the logstore
	 * @param query
	 *            user defined query
	 * @param from
	 *            the begin time
	 * @param to
	 *            the end time
	 * @param accurate
	 * 			  accurate flag
	 * @param reverse
	 *            reverse flag
	 */
	public GetHistogramsRequest(String project, String logStore, String topic,
								String query, int from, int to, boolean accurate, boolean reverse) {
		this(project, logStore, topic, query, from, to);
		SetAccurate(accurate);
		SetReverse(reverse);
	}


	/**
	 * Set log store
	 * 
	 * @param logStore
	 *            log store name
	 */
	public void SetLogStore(String logStore) {
		mLogStore = logStore;
	}

	/**
	 * Get log store name
	 * 
	 * @return log store name
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * Set topic of the log store
	 * 
	 * @param topic
	 *            topic name
	 */
	public void SetTopic(String topic) {
		SetParam(Consts.CONST_TOPIC, topic);
	}

	/**
	 * Get Topic
	 * 
	 * @return topic name
	 */
	public String GetTopic() {
		return GetParam(Consts.CONST_TOPIC);
	}

	/**
	 * Set query
	 * 
	 * @param query
	 *            user define query
	 */
	public void SetQuery(String query) {
		SetParam(Consts.CONST_QUERY, query);
	}

	/**
	 * Get Query
	 * 
	 * @return query
	 */
	public String GetQuery() {
		return GetParam(Consts.CONST_QUERY);
	}

	/**
	 * Set begin time
	 * 
	 * @param from
	 *            begin time
	 */
	public void SetFromTime(int from) {
		SetParam(Consts.CONST_FROM, String.valueOf(from));
	}

	/**
	 * Get begin time,
	 * 
	 * @return begin time
	 */
	public int GetFromTime() {
		String from = GetParam(Consts.CONST_FROM);
		if (from.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(from);
		}
	}

	public void SetToTime(int to) {
		SetParam(Consts.CONST_TO, String.valueOf(to));
	}

	/**
	 * Get end time
	 * 
	 * @return end time
	 */
	public int GetToTime() {
		String to = GetParam(Consts.CONST_TO);
		if (to.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(to);
		}

	}

	/**
	 * Set request accurate flag
	 *
	 * @param accurate
	 *            accurate flag
	 */
	public void SetAccurate(boolean accurate) {
		SetParam(Consts.CONST_ACCURATE, String.valueOf(accurate));
	}

	/**
	 * Get request accurate flag
	 *
	 * @return accurate flag
	 */
	public boolean GetAccurate() {
		String accurate = GetParam(Consts.CONST_ACCURATE);
		if (accurate.isEmpty()) {
			return false;
		} else {
			return Boolean.parseBoolean(accurate);
		}
	}

	/**
	 * Set request reverse flag
	 *
	 * @param reverse
	 *            reverse flag
	 */
	public void SetReverse(boolean reverse) {
		SetParam(Consts.CONST_REVERSE, String.valueOf(reverse));
	}

	/**
	 * Get request reverse flag
	 *
	 * @return reverse flag
	 */
	public boolean GetReverse() {
		String reverse = GetParam(Consts.CONST_REVERSE);
		if (reverse.isEmpty()) {
			return false;
		} else {
			return Boolean.parseBoolean(reverse);
		}
	}

}
