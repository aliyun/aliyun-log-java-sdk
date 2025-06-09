package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryResult {

    private static final String METADATA_KEY = "meta";
    private static final String DATA_KEY = "data";

    private String aggQuery = "";
    private String whereQuery = "";
    private boolean isCompleted = false;
    private boolean hasSQL = false;
    private long processedRows = 0;
    private long elapsedMillisecond = 0;
    private double cpuSec = 0;
    private long cpuCores = 0;
    private List<String> keys;
    private List<Term> terms;
    private long limited = 0;
    private String marker = "";
    private int queryMode = 0;
    private boolean isPhraseQuery = false;
    private PhraseQueryInfo phraseQueryInfo;
    private long scanBytes = 0;
    private List<List<LogContent>> highlights;
    private List<String> columnTypes;
    private List<QueriedLog> logs;

    public String getAggQuery() {
        return aggQuery;
    }

    public void setAggQuery(String aggQuery) {
        this.aggQuery = aggQuery;
    }

    public String getWhereQuery() {
        return whereQuery;
    }

    public void setWhereQuery(String whereQuery) {
        this.whereQuery = whereQuery;
    }

    public boolean isHasSQL() {
        return hasSQL;
    }

    public void setHasSQL(boolean hasSQL) {
        this.hasSQL = hasSQL;
    }

    public long getProcessedRows() {
        return processedRows;
    }

    public void setProcessedRows(long processedRows) {
        this.processedRows = processedRows;
    }

    public long getElapsedMillisecond() {
        return elapsedMillisecond;
    }

    public void setElapsedMillisecond(long elapsedMillisecond) {
        this.elapsedMillisecond = elapsedMillisecond;
    }

    public double getCpuSec() {
        return cpuSec;
    }

    public void setCpuSec(double cpuSec) {
        this.cpuSec = cpuSec;
    }

    public long getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(long cpuCores) {
        this.cpuCores = cpuCores;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public long getLimited() {
        return limited;
    }

    public void setLimited(long limited) {
        this.limited = limited;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void setProcessStatus(String processStatus) {
        isCompleted = processStatus.equals(Consts.CONST_RESULT_COMPLETE);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public int getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(int queryMode) {
        this.queryMode = queryMode;
    }

    public boolean isPhraseQuery() {
        return isPhraseQuery;
    }

    public void setPhraseQuery(boolean phraseQuery) {
        isPhraseQuery = phraseQuery;
    }

    public PhraseQueryInfo getPhraseQueryInfo() {
        return phraseQueryInfo;
    }

    public void setPhraseQueryInfo(PhraseQueryInfo phraseQueryInfo) {
        this.phraseQueryInfo = phraseQueryInfo;
    }

    public long getScanBytes() {
        return scanBytes;
    }

    public void setScanBytes(long scanBytes) {
        this.scanBytes = scanBytes;
    }

    public List<List<LogContent>> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<List<LogContent>> highlights) {
        this.highlights = highlights;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(List<String> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public List<QueriedLog> getLogs() {
        return logs;
    }

    public void setLogs(List<QueriedLog> logs) {
        this.logs = logs;
    }

    public void deserializeFrom(String rawResult, String requestId) throws LogException {
        JSONObject asJsonObj = JSONObject.parseObject(rawResult, Feature.DisableSpecialKeyDetect);
        parseMetadata(asJsonObj.getJSONObject(METADATA_KEY));
        logs = parseData(asJsonObj.getJSONArray(DATA_KEY), requestId);
    }

    public static QueriedLog extractLogFromJSON(JSONObject log, String requestId) throws JSONException, LogException {
        String source = "";
        LogItem logItem = new LogItem();
        Set<String> keySet = log.keySet();
        for (String key : keySet) {
            String value = log.getString(key);
            if (key.equals(Consts.CONST_RESULT_SOURCE)) {
                source = value;
            } else if (key.equals(Consts.CONST_RESULT_TIME)) {
                try {
                    logItem.mLogTime = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    throw new LogException(Consts.INVALID_LOG_TIME,
                            "The field __time__ is invalid in your query result: " + value, requestId);
                }
            } else if (key.equals(Consts.CONST_RESULT_TIME_NS_PART)) {
                try {
                    logItem.mLogTimeNsPart = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    throw new LogException(Consts.INVALID_LOG_TIME,
                            "The field __time_ns_part__ is invalid in your query result: " + value, requestId);
                }
            } else {
                logItem.PushBack(key, value);
            }
        }
        return new QueriedLog(source, logItem);
    }

    public static List<QueriedLog> parseData(JSONArray array, String requestId) throws LogException {
        if (array == null) {
            return new ArrayList<QueriedLog>();
        }
        int count = array.size();
        List<QueriedLog> logs = new ArrayList<QueriedLog>(count);
        try {
            for (int i = 0; i < count; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject != null) {
                    logs.add(extractLogFromJSON(jsonObject, requestId));
                }
            }
        } catch (JSONException e) {
            // ignore;
        }
        return logs;
    }

    private void parseMetadata(JSONObject asJsonObj) {
        setProcessStatus(asJsonObj.getString("progress"));
        if (asJsonObj.containsKey("aggQuery")) {
            setAggQuery(asJsonObj.getString("aggQuery"));
        }
        if (asJsonObj.containsKey("whereQuery")) {
            setWhereQuery(asJsonObj.getString("whereQuery"));
        }
        if (asJsonObj.containsKey("hasSQL")) {
            setHasSQL(asJsonObj.getBooleanValue("hasSQL"));
        }
        if (asJsonObj.containsKey("processedRows")) {
            setProcessedRows(asJsonObj.getLongValue("processedRows"));
        }
        if (asJsonObj.containsKey("elapsedMillisecond")) {
            setElapsedMillisecond(asJsonObj.getLongValue("elapsedMillisecond"));
        }
        if (asJsonObj.containsKey("cpuSec")) {
            setCpuSec(asJsonObj.getDoubleValue("cpuSec"));
        }
        if (asJsonObj.containsKey("cpuCores")) {
            setCpuCores(asJsonObj.getLongValue("cpuCores"));
        }
        JSONArray keyList = asJsonObj.getJSONArray("keys");
        if (keyList != null) {
            keys = new ArrayList<String>(keyList.size());
            for (int i = 0; i < keyList.size(); ++i) {
                keys.add(keyList.getString(i));
            }
        } else {
            keys = new ArrayList<String>();
        }
        JSONArray termsAsJson = asJsonObj.getJSONArray("terms");
        terms = new ArrayList<Term>();
        if (termsAsJson != null) {
            for (int i = 0; i < termsAsJson.size(); ++i) {
                JSONObject term = termsAsJson.getJSONObject(i);
                if (term.size() == 2) {
                    terms.add(new Term(term.getString("key"), term.getString("term")));
                }
            }
        }
        if (asJsonObj.containsKey("limited")) {
            limited = asJsonObj.getLongValue("limited");
        }
        if (asJsonObj.containsKey("marker")) {
            marker = asJsonObj.getString("marker");
        }
        if (asJsonObj.containsKey("mode")) {
            queryMode = asJsonObj.getIntValue("mode");
            if (queryMode == 1)
                isPhraseQuery = true;
        }
        setPhraseQueryInfo(PhraseQueryInfo.deserializeFrom(asJsonObj.getJSONObject("phraseQueryInfo")));
        if (asJsonObj.containsKey("scanBytes")) {
            scanBytes = asJsonObj.getLongValue("scanBytes");
        }
        JSONArray jsonArray = asJsonObj.getJSONArray("highlights");
        if (jsonArray != null) {
            highlights = new ArrayList<List<LogContent>>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject == null) {
                    highlights.add(new ArrayList<LogContent>());
                } else {
                    List<LogContent> logContents = new ArrayList<LogContent>(jsonObject.size());
                    Set<String> keySey = jsonObject.keySet();
                    for (String key : keySey) {
                        String value = jsonObject.getString(key);
                        logContents.add(new LogContent(key, value));
                    }
                    highlights.add(logContents);
                }
            }
        }
        JSONArray columnTypesAsJson = asJsonObj.getJSONArray("columnTypes");
        if (columnTypesAsJson != null) {
            columnTypes = new ArrayList<String>(columnTypesAsJson.size());
            for (int i = 0; i < columnTypesAsJson.size(); ++i) {
                columnTypes.add(columnTypesAsJson.getString(i));
            }
        }
    }

    public static class Term {
        private final String key;
        private final String term;

        public Term(String key, String term) {
            this.key = key;
            this.term = term;
        }

        public String getKey() {
            return key;
        }

        public String getTerm() {
            return term;
        }
    }

    public static class PhraseQueryInfo {
        private boolean scanAll = false;
        private long beginOffset = 0;
        private long endOffset = 0;
        private long endTime = 0;

        public void setScanAll(boolean scanAll) {
            this.scanAll = scanAll;
        }

        public void setBeginOffset(long beginOffset) {
            this.beginOffset = beginOffset;
        }

        public void setEndOffset(long endOffset) {
            this.endOffset = endOffset;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public boolean isScanAll() {
            return scanAll;
        }

        public long getBeginOffset() {
            return beginOffset;
        }

        public long getEndOffset() {
            return endOffset;
        }

        public long getEndTime() {
            return endTime;
        }

        public static PhraseQueryInfo deserializeFrom(JSONObject asJson) {
            PhraseQueryInfo queryInfo = new PhraseQueryInfo();
            if (asJson == null) {
                return queryInfo;
            }
            if (asJson.containsKey("scanAll")) {
                queryInfo.setScanAll(asJson.getBooleanValue("scanAll"));
            }
            if (asJson.containsKey("beginOffset")) {
                queryInfo.setBeginOffset(asJson.getLongValue("beginOffset"));
            }
            if (asJson.containsKey("endOffset")) {
                queryInfo.setEndOffset(asJson.getLongValue("endOffset"));
            }
            if (asJson.containsKey("endTime")) {
                queryInfo.setEndTime(asJson.getLongValue("endTime"));
            }
            return queryInfo;
        }
    }
}
