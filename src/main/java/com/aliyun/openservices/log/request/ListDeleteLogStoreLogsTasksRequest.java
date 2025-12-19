package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for listing delete log store logs tasks
 */
public class ListDeleteLogStoreLogsTasksRequest extends BasicRequest {

    private static final long serialVersionUID = 8394572641038947251L;

    private String logstore;
    private int offset;
    private int size;

    /**
     * Construct a list delete log store logs tasks request
     *
     * @param project  project name
     * @param logstore log store name
     * @param offset   offset for pagination
     * @param size     page size for pagination
     */
    public ListDeleteLogStoreLogsTasksRequest(String project, String logstore, int offset, int size) {
        super(project);
        this.logstore = logstore;
        this.offset = offset;
        this.size = size;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUri() {
        return "/logstores/" + logstore + "/deletelogtasks";
    }

    @Override
    public Map<String, String> GetAllParams() {
        Map<String, String> params = new HashMap<>();
        params.put("offset", String.valueOf(offset));
        params.put("size", String.valueOf(size));
        return params;
    }
}
