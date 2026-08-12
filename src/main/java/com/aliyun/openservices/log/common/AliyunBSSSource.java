package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

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
    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        roleARN = JsonUtils.readOptionalString(jsonObject, "roleARN");
        historyMonth = JsonUtils.readOptionalInt(jsonObject, "historyMonth");
    }
}
