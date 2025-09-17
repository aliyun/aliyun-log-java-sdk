package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpdateMaterializedViewRequest extends Request {
    private final String name;
    private Optional<String> originalSql = Optional.empty();
    private Optional<Integer> aggIntervalMins = Optional.empty();
    private Optional<Integer> ttl = Optional.empty();
    private Optional<Boolean> enable = Optional.empty();

    public UpdateMaterializedViewRequest(String project, String name) {
        super(project);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = Optional.of(originalSql);
    }

    public Optional<Integer> getAggIntervalMins() {
        return aggIntervalMins;
    }

    public void setAggIntervalMins(int aggIntervalMins) {
        this.aggIntervalMins = Optional.of(aggIntervalMins);
    }

    public Optional<Integer> getTTL() {
        return ttl;
    }

    public void setTTL(int ttl) {
        this.ttl = Optional.of(ttl);
    }

    public Optional<Boolean> getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = Optional.of(enable);
    }

    public void checkValid() {
        if (!originalSql.isPresent() && !aggIntervalMins.isPresent() && !ttl.isPresent() && !enable.isPresent()) {
            throw new IllegalArgumentException("invalid UpdateMaterializedViewRequest");
        }
    }

    public byte[] getRequestBody() {
        Map<String, Object> body = new HashMap<>();
        originalSql.ifPresent(arg -> body.put("originalSql", arg));
        aggIntervalMins.ifPresent(arg -> body.put("aggIntervalMins", arg));
        ttl.ifPresent(arg -> body.put("ttl", arg));
        enable.ifPresent(arg -> body.put("enable", arg));
        return JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8);
    }
}
