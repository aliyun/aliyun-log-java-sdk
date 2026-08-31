package com.aliyun.openservices.log.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetMaterializedViewResponse extends Response {
    private final String name;
    private final String logstore;
    private final String originalSql;
    private final int aggIntervalMins;
    private final int startTime;
    private final int ttl;
    private final int shardCount;
    private final long createTime;
    private final boolean enabled;
    private final Status status;

    public GetMaterializedViewResponse(Map<String, String> headers,
                                       String name,
                                       String logstore,
                                       String originalSql,
                                       int aggIntervalMins,
                                       int startTime,
                                       int ttl,
                                       boolean enabled) {
        this(headers, name, logstore, originalSql, aggIntervalMins, startTime, ttl,
                0, 0L, enabled, null);
    }

    public GetMaterializedViewResponse(Map<String, String> headers,
                                       String name,
                                       String logstore,
                                       String originalSql,
                                       int aggIntervalMins,
                                       int startTime,
                                       int ttl,
                                       boolean enabled,
                                       Status status) {
        this(headers, name, logstore, originalSql, aggIntervalMins, startTime, ttl, 0, enabled, status);
    }

    public GetMaterializedViewResponse(Map<String, String> headers,
                                       String name,
                                       String logstore,
                                       String originalSql,
                                       int aggIntervalMins,
                                       int startTime,
                                       int ttl,
                                       int shardCount,
                                       boolean enabled,
                                       Status status) {
        this(headers, name, logstore, originalSql, aggIntervalMins, startTime, ttl,
                shardCount, 0L, enabled, status);
    }

    public GetMaterializedViewResponse(Map<String, String> headers,
                                       String name,
                                       String logstore,
                                       String originalSql,
                                       int aggIntervalMins,
                                       int startTime,
                                       int ttl,
                                       int shardCount,
                                       long createTime,
                                       boolean enabled,
                                       Status status) {
        super(headers);
        this.name = name;
        this.logstore = logstore;
        this.originalSql = originalSql;
        this.aggIntervalMins = aggIntervalMins;
        this.startTime = startTime;
        this.ttl = ttl;
        this.shardCount = shardCount;
        this.createTime = createTime;
        this.enabled = enabled;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getLogstore() {
        return logstore;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public int getAggIntervalMins() {
        return aggIntervalMins;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getTtl() {
        return ttl;
    }

    public int getShardCount() {
        return shardCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Status getStatus() {
        return status;
    }

    public static class Status implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int maxCursorTime;
        private final int lastRunTime;
        private final String lastRunError;
        private final Stats stats;

        public Status(int maxCursorTime, int lastRunTime, String lastRunError) {
            this(maxCursorTime, lastRunTime, lastRunError, null);
        }

        public Status(int maxCursorTime,
                      int lastRunTime,
                      String lastRunError,
                      Stats stats) {
            this.maxCursorTime = maxCursorTime;
            this.lastRunTime = lastRunTime;
            this.lastRunError = lastRunError;
            this.stats = stats;
        }

        public int getMaxCursorTime() {
            return maxCursorTime;
        }

        public int getLastRunTime() {
            return lastRunTime;
        }

        public String getLastRunError() {
            return lastRunError;
        }

        public Stats getStats() {
            return stats;
        }

        public static class Stats implements Serializable {
            private static final long serialVersionUID = 1L;

            private final long hits;
            private final List<String> queries;

            public Stats(long hits, List<String> queries) {
                this.hits = hits;
                this.queries = Collections.unmodifiableList(new ArrayList<>(queries));
            }

            public long getHits() {
                return hits;
            }

            public List<String> getQueries() {
                return queries;
            }
        }
    }
}
