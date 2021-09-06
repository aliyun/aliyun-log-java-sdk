package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The Request used to get data of a query from sls server
 * 
 * @author sls_dev
 * 
 */
public class GetLogsRequest extends Request {
	private static final long serialVersionUID = -484272901258629068L;

	private String mLogStore;

	/**
	 * Construct a the request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of a project
	 * @param from
	 *            begin time
	 * @param to
	 *            end time
	 * @param topic
	 *            topic name of a log store
	 * @param query
	 *            user query
	 */
	public GetLogsRequest(String project, String logStore, int from, int to,
			String topic, String query) {
		super(project);
		mLogStore = logStore;
		SetTopic(topic);
		SetQuery(query);
		SetFromTime(from);
		SetToTime(to);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_LOG);
	}
	/**
	 * Construct a the request
	 *
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of a project
	 * @param from
	 *            begin time
	 * @param to
	 *            end time
	 * @param topic
	 *            topic name of a log store
	 * @param query
	 *            user query
	 * @param offset
	 *            the log offset to return
	 * @param line
	 *            how many lines to get, the max lines is decided by
	 *            the sls backend server
	 * @param reverse
	 *            if reverse is set to true, the query will return the latest
	 *            logs first
	 */
	public GetLogsRequest(String project, String logStore, int from, int to,
						  String topic, String query, int offset, int line,
						  boolean reverse) {
		this(project, logStore,  from, to, topic, query);
		SetOffset(offset);
		SetLine(line);
		SetReverse(reverse);
	}
	/**
	 * Construct a the request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of a project
	 * @param from
	 *            begin time
	 * @param to
	 *            end time	 
	 * @param topic
	 *            topic name of a log store
	 * @param query
	 *            user query
	 * @param offset
	 *            the log offset to return
	 * @param line
	 *            how many lines to get, the max lines is decided by
	 *            the sls backend server
	 * @param reverse
	 *            if reverse is set to true, the query will return the latest
	 *            logs first
	 */
	public GetLogsRequest(String project, String logStore, int from, int to, 
			String topic, String query, int offset, int line,
			boolean reverse,
			boolean powerSql) {
		this(project, logStore,  from, to, topic, query);
		SetOffset(offset);
		SetLine(line);
		SetReverse(reverse);
		SetPowerSql(powerSql);
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
	 * Set request offset
	 * 
	 * @param offset
	 *            log offset
	 */
	public void SetOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	/**
	 *set request powerSql
	 *
	 * @param powerSql
	 *
	 */
	public void SetPowerSql(boolean powerSql) {
		SetParam(Consts.CONST_POWER_SQL,String.valueOf(powerSql));
	}
	/**
	 * Get request offset
	 * 
	 * @return offset value
	 */
	public int GetOffset() {
		String offset = GetParam(Consts.CONST_OFFSET);
		if (offset.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(offset);
		}
	}

	/**
	 * Get request line number
	 * 
	 * @param line
	 *            line number
	 */
	public void SetLine(int line) {
		SetParam(Consts.CONST_LINE, String.valueOf(line));
	}

	/**
	 * Get request line number
	 * 
	 * @return line number
	 */
	public int GetLine() {
		String line = GetParam(Consts.CONST_LINE);
		if (line.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(line);
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