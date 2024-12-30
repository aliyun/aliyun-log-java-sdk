package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.CsvExternalStore;

public class UpdateCsvExternalStoreRequest extends Request {
    private static final long serialVersionUID = 8606882056871782816L;

    private CsvExternalStore csvExternalStore;

    public UpdateCsvExternalStoreRequest(String project, CsvExternalStore csvExternalStore) {
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
