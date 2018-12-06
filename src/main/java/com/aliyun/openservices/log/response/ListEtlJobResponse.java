package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListEtlJobResponse extends Response {
    private static final long serialVersionUID = 3351846256133877946L;
    protected ArrayList<String> etlJobNameList = null;
    protected int total;

    public ListEtlJobResponse(Map<String, String> headers, int total) {
        super(headers);
        this.total = total;
    }

    public void setEtlJobNameList(List<String> etlJobNameList) {
        this.etlJobNameList = new ArrayList<String>(etlJobNameList);
    }

    public ArrayList<String> getEtlJobNameList() {
        return etlJobNameList;
    }

    public int getTotal() {
        return total;
    }

    public int getCount() {
        if (this.etlJobNameList == null) {
            return 0;
        }
        return this.etlJobNameList.size();
    }
}
