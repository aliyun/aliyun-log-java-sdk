package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;

/**
 * The Request used to get data of a query from sls server
 *
 * @author sls_dev
 */
public class GetLogsRequest extends Request {
    private static final long serialVersionUID = -484272901258629068L;

    private String mLogStore;

    private int from;
    private int to;
    private String topic;
    private String query;
    private Long offset;
    private Long line;
    private Boolean reverse;
    private Boolean powerSql;
    private Boolean forward;
    private Integer shard;
    private String session;
    private Boolean accurate;
    private Boolean needHighlight;

    // lz4, zstd, zip, deflate
    private Consts.CompressType compressType = Consts.CompressType.LZ4;

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query) {
        super(project);
        mLogStore = logStore;
        SetTopic(topic);
        SetQuery(query);
        SetFromTime(from);
        SetToTime(to);
    }

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     * @param offset   the log offset to return
     * @param line     how many lines to get, the max lines is decided by
     *                 the sls backend server
     * @param reverse  if reverse is set to true, the query will return the latest
     *                 logs first
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query, long offset, long line,
                          boolean reverse) {
        this(project, logStore, from, to, topic, query);
        SetOffset(offset);
        SetLine(line);
        SetReverse(reverse);
    }

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     * @param offset   the log offset to return
     * @param line     how many lines to get, the max lines is decided by
     *                 the sls backend server
     * @param reverse  if reverse is set to true, the query will return the latest
     *                 logs first
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query, long offset, long line,
                          boolean reverse,
                          boolean powerSql) {
        this(project, logStore, from, to, topic, query);
        SetOffset(offset);
        SetLine(line);
        SetReverse(reverse);
        SetPowerSql(powerSql);
    }

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     * @param offset   the log offset to return
     * @param line     how many lines to get, the max lines is decided by
     *                 the sls backend server
     * @param reverse  if reverse is set to true, the query will return the latest
     *                 logs first
     * @param forward  only usable when phrase query, forward is ture means next page,
     *                 otherwise means previous page
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query, long offset, long line,
                          boolean reverse,
                          boolean powerSql,
                          boolean forward) {
        this(project, logStore, from, to, topic, query);
        SetOffset(offset);
        SetLine(line);
        SetReverse(reverse);
        SetPowerSql(powerSql);
        SetForward(forward);
    }

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     * @param offset   the log offset to return
     * @param line     how many lines to get, the max lines is decided by
     *                 the sls backend server
     * @param reverse  if reverse is set to true, the query will return the latest
     *                 logs first
     * @param shard    specific shard
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query, long offset, long line,
                          boolean reverse, int shard) {
        this(project, logStore, from, to, topic, query, offset, line, reverse);
        SetShard(shard);
    }

    /**
     * Construct a the request
     *
     * @param project  project name
     * @param logStore log store name of a project
     * @param from     begin time
     * @param to       end time
     * @param topic    topic name of a log store
     * @param query    user query
     * @param offset   the log offset to return
     * @param line     how many lines to get, the max lines is decided by
     *                 the sls backend server
     * @param reverse  if reverse is set to true, the query will return the latest
     *                 logs first
     * @param session  query session param
     */
    public GetLogsRequest(String project, String logStore, int from, int to,
                          String topic, String query, long offset, long line,
                          boolean reverse, boolean forward, String session) {
        this(project, logStore, from, to, topic, query, offset, line, reverse);
        SetForward(forward);
        SetSession(session);
    }

    /**
     * Set log store
     *
     * @param logStore log store name
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
     * @param topic topic name
     */
    public void SetTopic(String topic) {
        this.topic = topic == null ? "" : topic;
    }

    /**
     * Get Topic
     *
     * @return topic name
     */
    public String GetTopic() {
        return topic;
    }

    /**
     * Set query
     *
     * @param query user define query
     */
    public void SetQuery(String query) {
        this.query = query == null ? "" : query;
    }

    /**
     * Get Query
     *
     * @return query
     */
    public String GetQuery() {
        return query;
    }

    /**
     * Set begin time
     *
     * @param from begin time
     */
    public void SetFromTime(int from) {
        this.from = from;
    }

    /**
     * Get begin time,
     *
     * @return begin time
     */
    public int GetFromTime() {
        return from;
    }

    public void SetToTime(int to) {
        this.to = to;
    }

    /**
     * Get end time
     *
     * @return end time
     */
    public int GetToTime() {
        return to;
    }

    /**
     * Set request offset
     *
     * @param offset log offset
     */
    public void SetOffset(long offset) {
        this.offset = offset;
    }

    /**
     * Get request offset
     *
     * @return offset value
     */
    public long GetOffset() {
        return offset != null ? offset : 0;
    }

    /**
     * Get request line number
     *
     * @param line line number
     */
    public void SetLine(long line) {
        this.line = line;
    }

    /**
     * Get request line number
     *
     * @return line number
     */
    public long GetLine() {
        return line != null ? line : 0;
    }

    /**
     * Set request reverse flag
     *
     * @param reverse reverse flag
     */
    public void SetReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Get request reverse flag
     *
     * @return reverse flag
     */
    public boolean GetReverse() {
        return reverse != null && reverse;
    }

    /**
     * Set request powerSql flag
     *
     * @param powerSql powerSql flag
     */
    public void SetPowerSql(boolean powerSql) {
        this.powerSql = powerSql;
    }

    public void SetShard(int shard) {
        this.shard = shard;
    }

    public void SetSession(String session) {
        if (session != null)
            this.session = session;
    }

    /**
     * Get request powerSql flag
     *
     * @return powerSql flag
     */
    public boolean GetPowerSql() {
        return powerSql != null && powerSql;
    }

    /**
     * Set request forward flag
     *
     * @param forward forward flag
     */
    public void SetForward(boolean forward) {
        this.forward = forward;
    }

    /**
     * Get request forward flag
     *
     * @return forward flag
     */
    public boolean GetForward() {
        return forward != null && forward;
    }

    /**
     * Set request accurate flag
     *
     * @param accurate accurate flag
     */
    public void SetAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    /**
     * Get request accurate flag
     *
     * @return accurate flag
     */
    public boolean GetAccurate() {
        return accurate != null && accurate;
    }

    public Consts.CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(Consts.CompressType compressType) {
        this.compressType = compressType;
    }

    public void SetNeedHighlight(boolean needHighlight) {
        this.needHighlight = needHighlight;
    }

    public boolean GetNeedHighlight() {
        return needHighlight != null && needHighlight;
    }

    private static void addParameterIfNotNull(JSONObject dest, String key, Object value) {
        if (value != null) {
            dest.put(key, value);
        }
    }

    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put(Consts.CONST_FROM, from);
        body.put(Consts.CONST_TO, to);
        body.put(Consts.CONST_TOPIC, topic);
        body.put(Consts.CONST_QUERY, query);
        addParameterIfNotNull(body, Consts.CONST_LINE, line);
        addParameterIfNotNull(body, Consts.CONST_OFFSET, offset);
        addParameterIfNotNull(body, Consts.CONST_REVERSE, reverse);
        addParameterIfNotNull(body, Consts.CONST_POWER_SQL, powerSql);
        addParameterIfNotNull(body, Consts.CONST_SESSION, session);
        addParameterIfNotNull(body, Consts.CONST_SHARD, shard);
        addParameterIfNotNull(body, Consts.CONST_ACCURATE, accurate);
        addParameterIfNotNull(body, Consts.CONST_FORWARD, forward);
        addParameterIfNotNull(body, Consts.CONST_HIGHLIGHT, needHighlight);
        return body.toString();
    }
}