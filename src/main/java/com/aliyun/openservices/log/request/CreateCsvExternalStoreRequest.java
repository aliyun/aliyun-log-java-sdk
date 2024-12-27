package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.CsvExternalStore;

public class CreateCsvExternalStoreRequest extends Request {
    private static final long serialVersionUID = 2001225533755558404L;

    private CsvExternalStore csvExternalStore;

    public CreateCsvExternalStoreRequest(String project, CsvExternalStore csvExternalStore) {
        super(project);
        this.csvExternalStore = csvExternalStore;
    }

    public CsvExternalStore getCsvExternalStore() {
        return csvExternalStore;
    }

    public void setCsvExternalStore(CsvExternalStore csvExternalStore) {
        this.csvExternalStore = csvExternalStore;
    }
}
