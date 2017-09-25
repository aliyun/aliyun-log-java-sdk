package com.aliyun.openservices.log.request;

import java.util.ArrayList;
import java.util.List;

public class UpdateEtlTaskStatusRequest extends Request {

    private static final long serialVersionUID = -5422535965083047974L;
    protected String etlJobName;
    protected ArrayList<String> etlTaskIdList;
    protected boolean enable;

    public UpdateEtlTaskStatusRequest(String project, String etlJobName, boolean enable) {
        super(project);
        this.etlJobName = etlJobName;
        this.enable = enable;
    }

    public void setEtlTaskIdList(List<String> etlTaskIdList) {
        this.etlTaskIdList = new ArrayList<String>(etlTaskIdList);
    }

    public ArrayList<String> getEtlTaskIdList() {
        return etlTaskIdList;
    }

    public String getEtlJobName() {
        return etlJobName;
    }

    public boolean getEnable() {
        return enable;
    }
}
