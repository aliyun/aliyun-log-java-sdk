/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.QueryResult;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.util.GzipUtils;
import com.aliyun.openservices.log.util.LZ4Encoder;


/**
 * The response of the GetLog API from sls server
 *
 * @author sls_dev
 */
public class GetLogsResponse extends BasicGetLogsResponse {

    private static final long serialVersionUID = -7866328557378599379L;

    private boolean isCompleted = false;

    private String marker = "";
    private String aggQuery = "";
    private String whereQuery = "";
    private boolean hasSQL = false;
    private long processedRow = 0;
    private long elapsedMilliSecond = 0;
    private long limited = 0;
    private double cpuSec = 0;
    private long cpuCores = 0;

    private boolean isPhraseQuery = false;
    private boolean scanAll = false;
    private long beginOffset = 0;
    private long endOffset = 0;
    private long endTime = 0;
    private int shard = 0;
    private long scanBytes = 0;
    private int queryMode = 0;

    private ArrayList<String> keys;
    private ArrayList<ArrayList<String>> terms;
    private List<List<LogContent>> highlights;
    private List<String> columnTypes;

    private String rawQueryResult;

    /**
     * Construct the response with http headers
     *
     * @param headers http headers
     */
    public GetLogsResponse(Map<String, String> headers) {
        super(headers);
        this.SetProcessStatus(headers.get(Consts.CONST_X_SLS_PROCESS));

        // check x-log-agg-query
        if (headers.containsKey(Consts.CONST_X_LOG_AGGQUERY))
            this.setAggQuery(headers.get(Consts.CONST_X_LOG_AGGQUERY));
        // check x-log-where-query
        if (headers.containsKey(Consts.CONST_X_LOG_WHEREQUERY))
            this.setWhereQuery(headers.get(Consts.CONST_X_LOG_WHEREQUERY));
        // check x-log-has-sql
        if (headers.containsKey(Consts.CONST_X_LOG_HASSQL))
            this.setHasSQL(Boolean.parseBoolean(headers.get(Consts.CONST_X_LOG_HASSQL)));
        // check x-log-processed-rows
        if (headers.containsKey(Consts.CONST_X_LOG_PROCESSEDROWS))
            this.setProcessedRow(Long.parseLong(headers.get(Consts.CONST_X_LOG_PROCESSEDROWS)));
        // checck x-log-elapsed-millisecond
        if (headers.containsKey(Consts.CONST_X_LOG_ELAPSEDMILLISECOND))
            this.setElapsedMilliSecond(Long.parseLong(headers.get(Consts.CONST_X_LOG_ELAPSEDMILLISECOND)));

        //check x-log-cpu-sec
        if (headers.containsKey(Consts.CONST_X_LOG_CPU_SEC))
            this.setCpuSec(Double.parseDouble(headers.get(Consts.CONST_X_LOG_CPU_SEC)));
        if (headers.containsKey(Consts.CONST_X_LOG_CPU_CORES))
            this.setCpuCores(Long.parseLong(headers.get(Consts.CONST_X_LOG_CPU_CORES)));

        if (headers.containsKey(Consts.CONST_X_LOG_QUERY_INFO)) {
            com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(headers.get(Consts.CONST_X_LOG_QUERY_INFO));
            JSONArray keys = object.getJSONArray("keys");
            this.keys = new ArrayList<String>();
            if (keys != null) {
                for (int i = 0; i < keys.size(); ++i) {
                    this.keys.add(keys.getString(i));
                }
            }

            JSONArray terms = object.getJSONArray("terms");
            this.terms = new ArrayList<ArrayList<String>>();
            if (terms != null) {
                for (int i = 0; i < terms.size(); ++i) {
                    ArrayList<String> list = new ArrayList<String>();
                    JSONArray term = terms.getJSONArray(i);
                    if (term.size() == 2) {
                        list.add(term.getString(0));
                        list.add(term.getString(1));
                    }
                    this.terms.add(list);
                }
            }

            if (object.containsKey("limited")) {
                this.limited = Long.parseLong(object.getString("limited"));
            }

            if (object.containsKey("marker")) {
                this.marker = object.getString("marker");
            }

            if (object.containsKey("mode")) {
                this.queryMode = object.getIntValue("mode");
                if (this.queryMode == 1)
                    this.isPhraseQuery = true;
            }

            if (object.containsKey("phraseQueryInfo")) {
                JSONObject phraseQueryInfo = object.getJSONObject("phraseQueryInfo");
                if (phraseQueryInfo.containsKey("scanAll")) {
                    this.scanAll = Boolean.parseBoolean(phraseQueryInfo.getString("scanAll"));
                }
                if (phraseQueryInfo.containsKey("beginOffset")) {
                    this.beginOffset = Long.parseLong(phraseQueryInfo.getString("beginOffset"));
                }
                if (phraseQueryInfo.containsKey("endOffset")) {
                    this.endOffset = Long.parseLong(phraseQueryInfo.getString("endOffset"));
                }
                if (phraseQueryInfo.containsKey("endTime")) {
                    this.endTime = Long.parseLong(phraseQueryInfo.getString("endTime"));
                }
            }

            if (object.containsKey("shard")) {
                this.shard = object.getIntValue("shard");
            }

            if (object.containsKey("scanBytes")) {
                this.scanBytes = object.getLongValue("scanBytes");
            }

            JSONArray highlights = object.getJSONArray("highlights");
            if (highlights != null) {
                this.highlights = new ArrayList<List<LogContent>>(highlights.size());
                for (int i = 0; i < highlights.size(); ++i) {
                    JSONObject jsonObject = highlights.getJSONObject(i);
                    if (jsonObject == null) {
                        this.highlights.add(new ArrayList<LogContent>());
                    } else {
                        ArrayList<LogContent> logContents = new ArrayList<LogContent>(jsonObject.size());
                        Set<String> keySey = jsonObject.keySet();
                        for (String key : keySey) {
                            String value = jsonObject.getString(key);
                            logContents.add(new LogContent(key, value));
                        }
                        this.highlights.add(logContents);
                    }
                }
            }

            JSONArray columnTypesAsJson = object.getJSONArray("columnTypes");
            if (columnTypesAsJson != null) {
                this.columnTypes = new ArrayList<String>(columnTypesAsJson.size());
                for (int i = 0; i < columnTypesAsJson.size(); ++i) {
                    this.columnTypes.add(columnTypesAsJson.getString(i));
                }
            }
        }
    }

    public GetLogsResponse(Map<String, String> headers, QueryResult result) {
        super(headers);
        isCompleted = result.isCompleted();
        setAggQuery(result.getAggQuery());
        setWhereQuery(result.getWhereQuery());
        setHasSQL(result.isHasSQL());
        setProcessedRow(result.getProcessedRows());
        setElapsedMilliSecond(result.getElapsedMillisecond());
        setCpuCores(result.getCpuCores());
        setCpuSec(result.getCpuSec());
        keys = new ArrayList<String>(result.getKeys());
        List<QueryResult.Term> terms = result.getTerms();
        this.terms = new ArrayList<ArrayList<String>>();
        for (QueryResult.Term term : terms) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(term.getTerm());
            list.add(term.getKey());
            this.terms.add(list);
        }
        setLimited(result.getLimited());
        setMarker(result.getMarker());
        queryMode = result.getQueryMode();
        isPhraseQuery = result.isPhraseQuery();
        QueryResult.PhraseQueryInfo queryInfo = result.getPhraseQueryInfo();
        if (queryInfo != null) {
            scanAll = queryInfo.isScanAll();
            beginOffset = queryInfo.getBeginOffset();
            endOffset = queryInfo.getEndOffset();
            endTime = queryInfo.getEndTime();
        }
        shard = result.getShard();
        scanBytes = result.getScanBytes();
        highlights = result.getHighlights();
        columnTypes = result.getColumnTypes();
        this.logs = (ArrayList<QueriedLog>) result.getLogs();
    }

    public GetLogsResponse(Map<String, String> headers, String rawQueryResult) {
        super(headers);
        this.rawQueryResult = rawQueryResult;
    }

    public boolean IsPhraseQuery() {
        return isPhraseQuery;
    }

    public boolean IsScanAll() {
        return scanAll;
    }

    public long GetBeginOffset() {
        return beginOffset;
    }

    public long GetEndOffset() {
        return endOffset;
    }

    public long GetEndTime() {
        return endTime;
    }

    public int GetShard() {
        return shard;
    }

    public long GetScanBytes() {
        return scanBytes;
    }

    public int GetQueryMode() {
        return queryMode;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public long getLimited() {
        return limited;
    }

    public void setLimited(long limited) {
        this.limited = limited;
    }

    public void setAggQuery(String aggQuery) {
        this.aggQuery = aggQuery;
    }

    public String getAggQuery() {
        return aggQuery;
    }

    public long getElapsedMilliSecond() {
        return elapsedMilliSecond;
    }

    public void setElapsedMilliSecond(long elapsedMilliSecond) {
        this.elapsedMilliSecond = elapsedMilliSecond;
    }

    public void setCpuSec(double cpuSec) {
        this.cpuSec = cpuSec;
    }

    public double getCpuSec() {
        return this.cpuSec;
    }

    public long getCpuCores() {
        return this.cpuCores;
    }

    public void setCpuCores(long cpuCores) {
        this.cpuCores = cpuCores;
    }

    public long getProcessedRow() {
        return processedRow;
    }

    public void setProcessedRow(long processedRow) {
        this.processedRow = processedRow;
    }

    public boolean isHasSQL() {

        return hasSQL;
    }

    public void setHasSQL(boolean hasSQL) {
        this.hasSQL = hasSQL;
    }

    public String getWhereQuery() {

        return whereQuery;
    }

    public void setWhereQuery(String whereQuery) {
        this.whereQuery = whereQuery;
    }

    /**
     * Set process status to the response
     *
     * @param processStatus process status(Complete/InComplete only)
     */
    public void SetProcessStatus(String processStatus) {
        isCompleted = processStatus.equals(Consts.CONST_RESULT_COMPLETE);
    }

    /**
     * Check if the GetHistogram is completed
     *
     * @return true if the query is complete in the sls server
     */
    public boolean IsCompleted() {
        return isCompleted;
    }

    /**
     * Set all the log data to the response
     *
     * @param logs log datas
     */
    public void SetLogs(List<QueriedLog> logs) {
        setLogs(logs);
    }

    /**
     * Add one log to the response
     *
     * @param log log data to add
     * @deprecated Use addLog(QueriedLog log) instead.
     */
    @Deprecated
    public void AddLog(QueriedLog log) {
        addLog(log);
    }

    /**
     * Get all logs from the response
     *
     * @return all log data
     * @deprecated Use getLogs() instead.
     */
    @Deprecated
    public ArrayList<QueriedLog> GetLogs() {
        return logs;
    }

    /**
     * Get log number from the response
     *
     * @return log number
     */
    public int GetCount() {
        return logs.size();
    }

    /**
     * Get log query key's sort
     *
     * @return log keys
     */
    public ArrayList<String> getKeys() {
        return keys;
    }

    /**
     * Get log query term
     *
     * @return log terms
     */
    public ArrayList<ArrayList<String>> getTerms() {
        return terms;
    }

    public List<List<LogContent>> getHighlights() {
        return highlights;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public String getRawQueryResult() {
        return rawQueryResult;
    }

    public static GetLogsResponse deserializeFrom(ResponseMessage response, boolean deserialize) throws LogException {
        byte[] rawData = response.GetRawBody();
        Map<String, String> headers = response.getHeaders();
        String compressType = headers.get(Consts.CONST_X_SLS_COMPRESSTYPE);
        String rawSizeStr = headers.get(Consts.CONST_X_SLS_BODYRAWSIZE);
        String requestId = response.getRequestId();
        if (compressType != null && rawSizeStr != null) {
            int rawSize = Integer.parseInt(rawSizeStr);
            Consts.CompressType type = Consts.CompressType.fromString(compressType);
            switch (type) {
                case LZ4:
                    rawData = LZ4Encoder.decompressFromLhLz4Chunk(rawData, rawSize);
                    break;
                case GZIP:
                    try {
                        rawData = GzipUtils.uncompress(rawData);
                    } catch (Exception ex) {
                        throw new LogException(ErrorCodes.BAD_RESPONSE, "Fail to uncompress GZIP data", requestId);
                    }
                    break;
                default:
                    throw new LogException(ErrorCodes.BAD_RESPONSE, "The compress type is not supported: " + compressType, requestId);
            }
        }
        try {
            String data = new String(rawData, Consts.UTF_8_ENCODING);
            if (deserialize) {
                QueryResult result = new QueryResult();
                result.deserializeFrom(data, requestId);
                return new GetLogsResponse(response.getHeaders(), result);
            }
            return new GetLogsResponse(response.getHeaders(), data);
        } catch (UnsupportedEncodingException ex) {
            throw new LogException(ErrorCodes.ENCODING_EXCEPTION, ex.getMessage(), response.getRequestId());
        }
    }
}
