package com.aliyun.openservices.log.response;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListEtlMetaNameResponse extends Response {

    private static final long serialVersionUID = -1871635459517244966L;
    protected ArrayList<String> etlMetaNameList = null;
    protected int total;

    public ListEtlMetaNameResponse(Map<String, String> headers, int total) {
        super(headers);
        this.total = total;
    }

    public void setEtlMetaNameList(List<String> etlMetaNameList) {
        this.etlMetaNameList = new ArrayList<String>(etlMetaNameList);
    }

    public ArrayList<String> getEtlMetaNameList() {
        return etlMetaNameList;
    }

    public int getTotal() {
        return total;
    }

    public int getCount() {
        if (this.etlMetaNameList == null) {
            return 0;
        }
        return this.etlMetaNameList.size();
    }
}
