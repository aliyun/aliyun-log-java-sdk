package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class JobRun implements Serializable {
    private static final long serialVersionUID = 1969562078025726631L;

    private String id;

    private String status;

    private Date createTime;

    private Date lastModifiedTime;

    private Date completeTime;

    @JSONField
    private Map<String, Object> parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
    public void deserialize(JSONObject object) {
        id = object.getString("id");
        status = object.getString("status");
        createTime = JsonUtils.readOptionalDate(object, "createTime");
        lastModifiedTime = JsonUtils.readOptionalDate(object, "lastModifiedTime");
        completeTime = JsonUtils.readOptionalDate(object, "completeTime");
        parameters = object.getJSONObject("parameters");
    }
}
