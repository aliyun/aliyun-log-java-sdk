/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.common.Logs.LogGroup;
import com.aliyun.openservices.log.exception.LogException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * LogGroup is the basic data structure for send, contains meta and logs
 *
 * @author sls_dev
 */
public class LogGroupData implements Serializable {

    private static final long serialVersionUID = -7939302281903476332L;

    private LogGroup logGroup = null;
    private FastLogGroup fastLogGroup = null;
    private byte[] rawBytes = null;
    private int offset;
    private int length;
    private String requestId = "";
    private List<LogItem> logItems = null;

    /**
     * Construct a empty LogGroup
     */
    public LogGroupData() {
    }

    public LogGroupData(byte[] rawBytes, int offset, int length, String requestId) {
        this.rawBytes = rawBytes;
        this.offset = offset;
        this.length = length;
        this.requestId = requestId;
    }

    public LogGroupData(LogGroup logGroup) {
        this.logGroup = logGroup;
    }

    /**
     * @deprecated Use {@link #getLogGroup()} instead.
     */
    @Deprecated
    public LogGroup GetLogGroup() throws LogException {
        return getLogGroup();
    }

    /**
     * @deprecated Use {@link #setLogGroup(LogGroup)} instead.
     */
    @Deprecated
    public void SetLogGroup(LogGroup logGroup) {
        setLogGroup(logGroup);
    }

    /**
     * @deprecated Use {@link #getFastLogGroup()} instead.
     */
    @Deprecated
    public FastLogGroup GetFastLogGroup() {
        return getFastLogGroup();
    }

    private void parseLogGroup() throws LogException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(this.rawBytes, this.offset, this.length);
        try {
            logGroup = LogGroup.parseFrom(inputStream);
        } catch (IOException e) {
            throw new LogException("InitLogGroupsError", e.getMessage(), e, requestId);
        }
    }

    public LogGroup getLogGroup() throws LogException {
        if (logGroup == null) {
            parseLogGroup();
        }
        return logGroup;
    }

    public void setLogGroup(LogGroup logGroup) {
        this.logGroup = logGroup;
    }

    public FastLogGroup getFastLogGroup() {
        if (fastLogGroup == null) {
            fastLogGroup = new FastLogGroup(this.rawBytes, this.offset, this.length);
        }
        return fastLogGroup;
    }

    public void setLogs(List<LogItem> logItems) {
        this.logItems = logItems;
    }

    public List<LogItem> getLogs() throws LogException {
        if (logItems != null) {
            return logItems;
        }
        LogGroup group = getLogGroup();
        List<Logs.Log> logs = group.getLogsList();
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (Logs.Log log : logs) {
            ArrayList<LogContent> logContents = new ArrayList<LogContent>();
            for (Logs.Log.Content content : log.getContentsList()) {
                logContents.add(new LogContent(content.getKey(), content.getValue()));
            }
            logItems.add(new LogItem(log.getTime(), logContents));
        }
        return logItems;
    }


    public void setFastLogGroup(FastLogGroup fastLogGroup) {
        this.fastLogGroup = fastLogGroup;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public void setRawBytes(byte[] rawBytes) {
        this.rawBytes = rawBytes;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
