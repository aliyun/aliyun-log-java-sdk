package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CreateMaterializedViewRequest extends Request {
    private final String name;
    private final String logstore;
    private final String originalSql;
    private final int aggIntervalMins;
    private final int startTime;
    private final int ttl;

    public CreateMaterializedViewRequest(String project,
                                         String name,
                                         String logstore,
                                         String originalSql,
                                         int aggIntervalMins,
                                         int startTime,
                                         int ttl) {
        super(project);
        this.name = name;
        this.logstore = logstore;
        this.originalSql = originalSql;
        this.aggIntervalMins = aggIntervalMins;
        this.startTime = startTime;
        this.ttl = ttl;
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

    public byte[] getRequestBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("logstore", logstore);
        body.put("originalSql", originalSql);
        body.put("aggIntervalMins", aggIntervalMins);
        body.put("startTime", startTime);
        body.put("ttl", ttl);
        return JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8);
    }
}
