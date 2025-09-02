/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.TagContent;

/**
 * The request used to send data to sls server
 */
public class PutLogsRequest extends Request {
    private static final long serialVersionUID = 7226856831224917838L;
    private String logStore;
    private String topic;
    private String source;
    private String hashKey;
    private ArrayList<LogItem> logItems;
    private ArrayList<TagContent> tags = null;
    private CompressType compressType = CompressType.LZ4;
    private String contentType = Consts.CONST_PROTO_BUF;
    private byte[] logGroupBytes = null;
    private Integer hashRouteKeySeqId;
    private String processor;
    private boolean metricsAutoHash = false;

    public boolean isMetricsAutoHash() {
        return metricsAutoHash;
    }

    /**
     * @return the compressType
     */
    public CompressType GetCompressType() {
        return getCompressType();
    }

    /**
     * @param compressType the compressType to set
     */
    public void SetCompressType(CompressType compressType) {
        setCompressType(compressType);
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Construct a put log request
     *
     * @param project  project name
     * @param logStore log store name of the project
     * @param topic    topic name of the log store
     * @param source   source of the log
     * @param logItems log data
     * @param hashKey  hashKey
     */
    public PutLogsRequest(String project, String logStore, String topic,
                          String source, List<LogItem> logItems, String hashKey) {
        super(project);
        this.logStore = logStore;
        this.topic = topic;
        this.source = source;
        this.logItems = new ArrayList<LogItem>(logItems);
        this.hashKey = hashKey;
    }

    /**
     * Construct a put log request
     *
     * @param project  project name
     * @param logStore log store name of the project
     * @param topic    topic name of the log store
     * @param source   source of the log
     * @param logItems log data
     */
    public PutLogsRequest(String project, String logStore, String topic,
                          String source, List<LogItem> logItems) {
        this(project, logStore, topic, source, logItems, null);
    }

    /**
     * Construct a put log request
     *
     * @param project  project name
     * @param logStore log store name of the project
     * @param topic    topic name of the log store
     * @param logItems log data
     */
    public PutLogsRequest(String project, String logStore, String topic,
                          List<LogItem> logItems) {
        super(project);
        this.logStore = logStore;
        this.topic = topic;
        this.logItems = new ArrayList<LogItem>(logItems);
    }

    /**
     * Construct a put log request
     *
     * @param project       project name
     * @param logStore      log store name of the project
     * @param topic         topic name of the log store
     * @param source        source of the log
     * @param logGroupBytes Porotbuf serialized string of LogGroup
     * @param hashKey       hashKey
     */
    public PutLogsRequest(String project, String logStore, String topic,
                          String source, byte[] logGroupBytes, String hashKey) {
        super(project);
        this.logStore = logStore;
        this.topic = topic;
        this.source = source;
        this.logGroupBytes = logGroupBytes;
        this.hashKey = hashKey;
    }

    /**
     * Get log store
     *
     * @return log store
     */
    public String GetLogStore() {
        return logStore;
    }

    /**
     * Set log store
     *
     * @param logStore log store name
     */
    public void SetLogStore(String logStore) {
        this.logStore = logStore;
    }

    /**
     * Get the topic
     *
     * @return the topic
     */
    public String GetTopic() {
        return topic;
    }

    /**
     * Set topic value
     *
     * @param topic topic value
     */
    public void SetTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Get log source
     *
     * @return log source
     */
    public String GetSource() {
        return source;
    }

    /**
     * Set log source
     *
     * @param source log source
     */
    public void SetSource(String source) {
        this.source = source;
    }

    /**
     * Get all the log data
     *
     * @return log data
     */
    public ArrayList<LogItem> GetLogItems() {
        return logItems;
    }

    /**
     * Get all the tag
     *
     * @return tag
     */
    public ArrayList<TagContent> GetTags() {
        return tags;
    }

    /**
     * Get all the logGroupBytes
     *
     * @return logGroupBytes
     */
    public byte[] GetLogGroupBytes() {
        return logGroupBytes;
    }

    /**
     * Set the log data , shallow copy is used to set the log data
     *
     * @param logItems log data
     */
    public void SetlogItems(List<LogItem> logItems) {
        this.logItems = new ArrayList<LogItem>(logItems);
    }

    public void SetTags(List<TagContent> tags) {
        this.tags = new ArrayList<TagContent>(tags);
    }

    /**
     * Use setHashKey
     *
     * @param hashKey The hash key
     * @deprecated Use setHashKey() instead.
     */
    @Deprecated
    public void SetRouteKey(String hashKey) {
        setHashKey(hashKey);
    }

    /**
     * Use getHashKey
     *
     * @return The hash key
     * @deprecated Use getHashKey() instead.
     */
    @Deprecated
    public String GetRouteKey() {
        return hashKey;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Integer getHashRouteKeySeqId() {
        return hashRouteKeySeqId;
    }

    public void setHashRouteKeySeqId(Integer hashRouteKeySeqId) {
        this.hashRouteKeySeqId = hashRouteKeySeqId;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public void setMetricsAutoHash(boolean metricsAutoHash) {
        this.metricsAutoHash = metricsAutoHash;
    }
}
