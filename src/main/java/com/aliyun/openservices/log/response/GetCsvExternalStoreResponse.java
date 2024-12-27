package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.CsvExternalStore;

public class GetCsvExternalStoreResponse extends Response {
    private static final long serialVersionUID = -1995529101064642113L;

    private CsvExternalStore csvExternalStore;

    public GetCsvExternalStoreResponse(Map<String, String> headers, CsvExternalStore csvExternalStore) {
        super(headers);
        setCsvExternalStore(csvExternalStore);
    }

    public CsvExternalStore getCsvExternalStore() {
        return csvExternalStore;
    }

    public void setCsvExternalStore(CsvExternalStore csvExternalStore) {
        this.csvExternalStore = csvExternalStore;
    }
}
