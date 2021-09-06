package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListExternalStroesResponse extends Response {
    private static final long serialVersionUID = 1176644885893280944L;

    private List<String> externalStores = new ArrayList<String>();
    private int total;
    private int count;

    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public ListExternalStroesResponse(Map<String, String> headers) {
        super(headers);
    }

    public void setExternalStores(List<String> externalStores) {
        this.externalStores = new ArrayList<String>(externalStores);
    }

    public List<String> getExternalStores() {
        return externalStores;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
