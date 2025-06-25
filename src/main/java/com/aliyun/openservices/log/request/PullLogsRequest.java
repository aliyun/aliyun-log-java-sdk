package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;

import java.util.Map;

/**
 * The request used to get log data from Log Service.
 */
public class PullLogsRequest extends Request {
    private static final long serialVersionUID = 1374616714086668195L;

    private String logStore;
    private int shardId;
    private int count;
    private String cursor;
    private String endCursor;
    private String query;
    private String processor;
    private String pullMode;
    private Consts.CompressType compressType = Consts.CompressType.LZ4;

    /**
     * Construct a get cursor request
     *
     * @param project  project name
     * @param logStore log stream name
     * @param shardId  shard id
     * @param cursor   current cursor
     * @param count    LogGroup number
     */
    public PullLogsRequest(String project, String logStore, int shardId, int count, String cursor) {
        this(project, logStore, shardId, count, cursor, null);
    }

    /**
     * Construct a PullLogsRequest
     *
     * @param project   project name
     * @param logStore  log store name
     * @param shardId   shard id
     * @param cursor    current cursor
     * @param count     LogGroup number
     * @param endCursor the end cursor
     */
    public PullLogsRequest(String project, String logStore, int shardId, int count, String cursor, String endCursor) {
        super(project);
        setLogStore(logStore);
        setShardId(shardId);
        setCount(count);
        setCursor(cursor);
        setEndCursor(endCursor);
    }

    /**
     * Construct a PullLogsRequest
     *
     * @param project   project name
     * @param logStore  log store name
     * @param shardId   shard id
     * @param cursor    current cursor
     * @param count     LogGroup number
     * @param endCursor the end cursor
     * @param query     query
     */
    public PullLogsRequest(String project, String logStore, int shardId, int count, String cursor, String endCursor,
                           String query) {
        this(project, logStore, shardId, count, cursor, endCursor);
        setQuery(query);
    }

    @Deprecated
    public PullLogsRequest(String project, String logStore, int shardId, int count, String cursor, String endCursor,
                           String query, String pullmode) {
        this(project, logStore, shardId, count, cursor, endCursor);
        setQuery(query);
    }

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        Args.notNullOrEmpty(logStore, "logStore");
        this.logStore = logStore;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        Args.check(shardId >= 0, "shardId cannot be negative");
        this.shardId = shardId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        Args.check(count > 0, "count must be positive");
        this.count = count;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        Args.notNullOrEmpty(cursor, "cursor");
        this.cursor = cursor;
    }

    public String getEndCursor() {
        return endCursor;
    }

    public void setEndCursor(String endCursor) {
        this.endCursor = endCursor;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    @Deprecated
    public void setPullMode(String pullMode) {
        this.pullMode = pullMode;
    }

    public Consts.CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(Consts.CompressType compressType) {
        this.compressType = compressType;
    }

    @Override
    public Map<String, String> GetAllParams() {
        SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_LOG);
        SetParam(Consts.CONST_CURSOR, cursor);
        SetParam(Consts.CONST_COUNT, String.valueOf(count));
        if (endCursor != null && !endCursor.isEmpty()) {
            SetParam(Consts.CONST_END_CURSOR, endCursor);
        }
        if (query != null && !query.isEmpty()) {
            SetParam(Consts.CONST_QUERY, query);
        }
        if (processor != null && !processor.isEmpty()) {
            SetParam(Consts.CONST_PROCESSOR, processor);
        }

        return super.GetAllParams();
    }
}
