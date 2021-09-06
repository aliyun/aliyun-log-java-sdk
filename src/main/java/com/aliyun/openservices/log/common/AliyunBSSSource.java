package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;

public class AliyunBSSSource extends DataSource {

    private String roleARN;

    private Integer historyMonth;

    public AliyunBSSSource() {
        super(DataSourceType.ALIYUN_BSS);
    }

    public String getRoleARN() {
        return roleARN;
    }

    public void setRoleARN(String roleARN) {
        this.roleARN = roleARN;
    }

    public Integer getHistoryMonth() {
        return historyMonth;
    }

    public void setHistoryMonth(Integer historyMonth) {
        this.historyMonth = historyMonth;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        roleARN = JsonUtils.readOptionalString(jsonObject, "roleARN");
        historyMonth = JsonUtils.readOptionalInt(jsonObject, "historyMonth");
    }
}
