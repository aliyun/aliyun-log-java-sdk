package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;

public class GetLogsRequestV2 extends Request {

    private static final long DEFAULT_OFFSET = -1;
    private static final long DEFAULT_LINE = -1;
    private static final long serialVersionUID = 3360163999513216113L;

    private String logstore;
    private int from;
    private int to;
    private String topic;
    private String query;
    private long offset = DEFAULT_OFFSET;
    private long line = DEFAULT_LINE;
    private boolean reverse;
    private boolean powerSql;
    private boolean forward;
    private String session;
    private boolean accurate;
    private boolean highlight;

    // lz4, zstd, zip, deflate, TODO expose this parameter
    private String acceptEncoding = Consts.CONST_LZ4;

    public GetLogsRequestV2(String project, String logstore, int from, int to,
                            String topic, String query) {
        super(project);
        this.logstore = logstore;
        this.from = from;
        this.to = to;
        this.topic = topic;
        this.query = query;
        SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_LOG);
    }

    public GetLogsRequestV2(String project, String logstore, int from, int to,
                            String topic, String query, long offset, long line,
                            boolean reverse) {
        this(project, logstore, from, to, topic, query);
        Args.check(offset >= 0, "offset must be >= 0");
        Args.check(line > 0, "line must be > 0");
        this.offset = offset;
        this.line = line;
        this.reverse = reverse;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        Args.check(offset >= 0, "offset must be >= 0");
        this.offset = offset;
    }

    public long getLine() {
        return line;
    }

    public void setLine(long line) {
        Args.check(line > 0, "line must be > 0");
        this.line = line;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isPowerSql() {
        return powerSql;
    }

    public void setPowerSql(boolean powerSql) {
        this.powerSql = powerSql;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void SetAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    public boolean GetAccurate() {
        return accurate;
    }

    public void SetNeedHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean GetNeedHighlight() {
        return highlight;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put(Consts.CONST_FROM, from);
        body.put(Consts.CONST_TO, to);
        if (line > 0) {
            body.put(Consts.CONST_LINE, line);
        }
        if (offset >= 0) {
            body.put(Consts.CONST_OFFSET, offset);
        }
        body.put(Consts.CONST_REVERSE, reverse);
        body.put(Consts.CONST_POWER_SQL, powerSql);
        if (session != null)
            body.put(Consts.CONST_SESSION, session);
        body.put(Consts.CONST_TOPIC, topic);
        body.put(Consts.CONST_QUERY, query);
        body.put(Consts.CONST_FORWARD, forward);
        body.put(Consts.CONST_ACCURATE, accurate);
        body.put(Consts.CONST_HIGHLIGHT, highlight);
        return body.toString();
    }
}