package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Logging implements Serializable {

    @JSONField
    private String loggingProject;

    @JSONField
    private List<LoggingDetail> loggingDetails;

    public Logging() {
    }

    public Logging(String loggingProject, List<LoggingDetail> loggingDetails) {
        setLoggingProject(loggingProject);
        setLoggingDetails(loggingDetails);
    }

    public String getLoggingProject() {
        return loggingProject;
    }

    public void setLoggingProject(String loggingProject) {
        Args.notNullOrEmpty(loggingProject, "loggingProject");
        this.loggingProject = loggingProject;
    }

    public List<LoggingDetail> getLoggingDetails() {
        return loggingDetails;
    }

    public void setLoggingDetails(List<LoggingDetail> loggingDetails) {
        Args.notNullOrEmpty(loggingDetails, "loggingDetails");
        this.loggingDetails = loggingDetails;
    }

    public void deserialize(final JSONObject object) {
        Args.notNull(object, "object");
        loggingProject = object.getString("loggingProject");
        loggingDetails = JsonUtils.readList(object, "loggingDetails", new Unmarshaller<LoggingDetail>() {
            @Override
            public LoggingDetail unmarshal(JSONArray value, int index) {
                LoggingDetail detail = new LoggingDetail();
                detail.deserialize(value.getJSONObject(index));
                return detail;
            }
        });
    }
}
