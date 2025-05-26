package com.aliyun.openservices.log.response;

import java.util.List;
import java.util.Map;

public class GetAsyncSqlResponse extends BaseAsyncSqlResponse {
    private final AsyncSqlMeta meta;
    private final List<String> columnNames;
    private final List<List<String>> rows;

    public GetAsyncSqlResponse(Map<String, String> headers,
                               String queryId,
                               String state,
                               String errorCode,
                               String errorMessage,
                               AsyncSqlMeta meta,
                               List<String> columnNames,
                               List<List<String>> rows) {
        super(headers, queryId, state, errorCode, errorMessage);
        this.meta = meta;
        if (isSuccessful()) {
            this.columnNames = columnNames;
            this.rows = rows;
        }
        else {
            this.columnNames = null;
            this.rows = null;
        }
    }

    public AsyncSqlMeta getMeta() {
        return meta;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public static class AsyncSqlMeta {
        private final long resultRows;
        private final long processedRows;
        private final long processedBytes;
        private final long elapsedMillis;
        private final double cpuSec;
        private final boolean accurate;

        public AsyncSqlMeta(long resultRows, long processedRows, long processedBytes, long elapsedMillis, double cpuSec, boolean accurate) {
            this.resultRows = resultRows;
            this.processedRows = processedRows;
            this.processedBytes = processedBytes;
            this.elapsedMillis = elapsedMillis;
            this.cpuSec = cpuSec;
            this.accurate = accurate;
        }

        public long getResultRows() {
            return resultRows;
        }

        public long getProcessedRows() {
            return processedRows;
        }

        public long getProcessedBytes() {
            return processedBytes;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }

        public double getCpuSec() {
            return cpuSec;
        }

        public boolean isAccurate() {
            return accurate;
        }
    }
}
