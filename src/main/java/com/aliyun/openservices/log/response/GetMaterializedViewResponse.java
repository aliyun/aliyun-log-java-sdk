package com.aliyun.openservices.log.response;

import java.util.Map;

public class GetMaterializedViewResponse extends Response {
    private final String name;
    private final String logstore;
    private final String originalSql;
    private final int aggIntervalMins;
    private final int startTime;
    private final int ttl;
    private final boolean enabled;

    public GetMaterializedViewResponse(Map<String, String> headers,
                                       String name,
                                       String logstore,
                                       String originalSql,
                                       int aggIntervalMins,
                                       int startTime,
                                       int ttl,
                                       boolean enabled) {
        super(headers);
        this.name = name;
        this.logstore = logstore;
        this.originalSql = originalSql;
        this.aggIntervalMins = aggIntervalMins;
        this.startTime = startTime;
        this.ttl = ttl;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }
}
