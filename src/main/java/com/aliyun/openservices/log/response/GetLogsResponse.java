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

    private boolean mIsCompleted = false;

    private String mMarker = "";
    private String mAggQuery = "";
    private String mWhereQuery = "";
    private boolean mHasSQL = false;
    private long mProcessedRow = 0;
    private long mElapsedMilliSecond = 0;
    private long mLimited = 0;
    private double mCpuSec = 0;
    private long mCpuCores = 0;

    private boolean mIsPhraseQuery = false;
    private boolean mScanAll = false;
    private long mBeginOffset = 0;
    private long mEndOffset = 0;
    private long mEndTime = 0;
    private int mShard = 0;
    private long mScanBytes = 0;
    private int mQueryMode = 0;

    private ArrayList<String> mKeys;
    private ArrayList<ArrayList<String>> mTerms;
    private List<List<LogContent>> mHighlights;
    private List<String> mColumnTypes;

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
            mKeys = new ArrayList<String>();
            if (keys != null) {
                for (int i = 0; i < keys.size(); ++i) {
                    mKeys.add(keys.getString(i));
                }
            }

            JSONArray terms = object.getJSONArray("terms");
            mTerms = new ArrayList<ArrayList<String>>();
            if (terms != null) {
                for (int i = 0; i < terms.size(); ++i) {
                    ArrayList<String> list = new ArrayList<String>();
                    JSONArray term = terms.getJSONArray(i);
                    if (term.size() == 2) {
                        list.add(term.getString(0));
                        list.add(term.getString(1));
                    }
                    mTerms.add(list);
                }
            }

            if (object.containsKey("limited")) {
                mLimited = Long.parseLong(object.getString("limited"));
            }

            if (object.containsKey("marker")) {
                mMarker = object.getString("marker");
            }

            if (object.containsKey("mode")) {
                mQueryMode = object.getIntValue("mode");
                if (mQueryMode == 1)
                    mIsPhraseQuery = true;
            }

            if (object.containsKey("phraseQueryInfo")) {
                JSONObject phraseQueryInfo = object.getJSONObject("phraseQueryInfo");
                if (phraseQueryInfo.containsKey("scanAll")) {
                    mScanAll = Boolean.parseBoolean(phraseQueryInfo.getString("scanAll"));
                }
                if (phraseQueryInfo.containsKey("beginOffset")) {
                    mBeginOffset = Long.parseLong(phraseQueryInfo.getString("beginOffset"));
                }
                if (phraseQueryInfo.containsKey("endOffset")) {
                    mEndOffset = Long.parseLong(phraseQueryInfo.getString("endOffset"));
                }
                if (phraseQueryInfo.containsKey("endTime")) {
                    mEndTime = Long.parseLong(phraseQueryInfo.getString("endTime"));
                }
            }

            if (object.containsKey("shard")) {
                mShard = object.getIntValue("shard");
            }

            if (object.containsKey("scanBytes")) {
                mScanBytes = object.getLongValue("scanBytes");
            }

            JSONArray highlights = object.getJSONArray("highlights");
            if (highlights != null) {
                mHighlights = new ArrayList<List<LogContent>>(highlights.size());
                for (int i = 0; i < highlights.size(); ++i) {
                    JSONObject jsonObject = highlights.getJSONObject(i);
                    if (jsonObject == null) {
                        mHighlights.add(new ArrayList<LogContent>());
                    } else {
                        ArrayList<LogContent> logContents = new ArrayList<LogContent>(jsonObject.size());
                        Set<String> keySey = jsonObject.keySet();
                        for (String key : keySey) {
                            String value = jsonObject.getString(key);
                            logContents.add(new LogContent(key, value));
                        }
                        mHighlights.add(logContents);
                    }
                }
            }

            JSONArray columnTypesAsJson = object.getJSONArray("columnTypes");
            if (columnTypesAsJson != null) {
                mColumnTypes = new ArrayList<String>(columnTypesAsJson.size());
                for (int i = 0; i < columnTypesAsJson.size(); ++i) {
                    mColumnTypes.add(columnTypesAsJson.getString(i));
                }
            }
        }
    }

    public GetLogsResponse(Map<String, String> headers, QueryResult result) {
        super(headers);
        mIsCompleted = result.isCompleted();
        setAggQuery(result.getAggQuery());
        setWhereQuery(result.getWhereQuery());
        setHasSQL(result.isHasSQL());
        setProcessedRow(result.getProcessedRows());
        setElapsedMilliSecond(result.getElapsedMillisecond());
        setCpuCores(result.getCpuCores());
        setCpuSec(result.getCpuSec());
        mKeys = new ArrayList<String>(result.getKeys());
        List<QueryResult.Term> terms = result.getTerms();
        mTerms = new ArrayList<ArrayList<String>>();
        for (QueryResult.Term term : terms) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(term.getKey());
            list.add(term.getTerm());
            mTerms.add(list);
        }
        setmLimited(result.getLimited());
        setmMarker(result.getMarker());
        mQueryMode = result.getQueryMode();
        mIsPhraseQuery = result.isPhraseQuery();
        QueryResult.PhraseQueryInfo queryInfo = result.getPhraseQueryInfo();
        if (queryInfo != null) {
            mScanAll = queryInfo.isScanAll();
            mBeginOffset = queryInfo.getBeginOffset();
            mEndOffset = queryInfo.getEndOffset();
            mEndTime = queryInfo.getEndTime();
        }
        mShard = result.getShard();
        mScanBytes = result.getScanBytes();
        mHighlights = result.getHighlights();
        mColumnTypes = result.getColumnTypes();
        this.logs = (ArrayList<QueriedLog>) result.getLogs();
    }

    public GetLogsResponse(Map<String, String> headers, String rawQueryResult) {
        super(headers);
        this.rawQueryResult = rawQueryResult;
    }

    public boolean IsPhraseQuery() {
        return mIsPhraseQuery;
    }

    public boolean IsScanAll() {
        return mScanAll;
    }

    public long GetBeginOffset() {
        return mBeginOffset;
    }

    public long GetEndOffset() {
        return mEndOffset;
    }

    public long GetEndTime() {
        return mEndTime;
    }

    public int GetShard() {
        return mShard;
    }

    public long GetScanBytes() {
        return mScanBytes;
    }

    public int GetQueryMode() {
        return mQueryMode;
    }

    public String getmMarker() {
        return mMarker;
    }

    public void setmMarker(String mMarker) {
        this.mMarker = mMarker;
    }

    public long getmLimited() {
        return mLimited;
    }

    public void setmLimited(long mLimited) {
        this.mLimited = mLimited;
    }

    public void setAggQuery(String mAggQuery) {
        this.mAggQuery = mAggQuery;
    }

    public String getAggQuery() {
        return mAggQuery;
    }

    public long getElapsedMilliSecond() {
        return mElapsedMilliSecond;
    }

    public void setElapsedMilliSecond(long mElapsedMilliSecond) {
        this.mElapsedMilliSecond = mElapsedMilliSecond;
    }

    public void setCpuSec(double mCpuSec) {
        this.mCpuSec = mCpuSec;
    }

    public double getCpuSec() {
        return this.mCpuSec;
    }

    public long getCpuCores() {
        return this.mCpuCores;
    }

    public void setCpuCores(long mCpuCores) {
        this.mCpuCores = mCpuCores;
    }

    public long getProcessedRow() {
        return mProcessedRow;
    }

    public void setProcessedRow(long mProcessedRow) {
        this.mProcessedRow = mProcessedRow;
    }

    public boolean isHasSQL() {

        return mHasSQL;
    }

    public void setHasSQL(boolean mHasSQL) {
        this.mHasSQL = mHasSQL;
    }

    public String getWhereQuery() {

        return mWhereQuery;
    }

    public void setWhereQuery(String mWhereQuery) {
        this.mWhereQuery = mWhereQuery;
    }

    /**
     * Set process status to the response
     *
     * @param processStatus process status(Complete/InComplete only)
     */
    public void SetProcessStatus(String processStatus) {
        mIsCompleted = processStatus.equals(Consts.CONST_RESULT_COMPLETE);
    }

    /**
     * Check if the GetHistogram is completed
     *
     * @return true if the query is complete in the sls server
     */
    public boolean IsCompleted() {
        return mIsCompleted;
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
        return mKeys;
    }

    /**
     * Get log query term
     *
     * @return log terms
     */
    public ArrayList<ArrayList<String>> getTerms() {
        return mTerms;
    }

    public List<List<LogContent>> getHighlights() {
        return mHighlights;
    }

    public List<String> getColumnTypes() {
        return mColumnTypes;
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
