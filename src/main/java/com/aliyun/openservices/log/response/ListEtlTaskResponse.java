package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.EtlTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListEtlTaskResponse extends Response {
    private static final long serialVersionUID = -3446579910894269157L;
    protected ArrayList<EtlTask> etlTaskList = null;
    protected int total;

    public ArrayList<EtlTask> getEtlTaskList() {
        return etlTaskList;
    }

    public void setEtlTaskList(List<EtlTask> etlTaskList) {
        this.etlTaskList = new ArrayList<EtlTask>(etlTaskList);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        if (this.etlTaskList == null) {
            return 0;
        }
        return this.etlTaskList.size();
    }

    public ListEtlTaskResponse(Map<String, String> headers) {
        super(headers);
    }
}
