package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Logging implements Serializable {

    private String loggingProject;
    private List<LoggingDetail> loggingDetails;

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
        this.loggingDetails = new ArrayList<LoggingDetail>(loggingDetails);
    }

    public static Logging fromJsonString(String loggingString) throws LogException {
        try {
            return unmarshal(JSONObject.parseObject(loggingString));
        } catch (JSONException | IllegalArgumentException e) {
            throw new LogException("FailToGenerateLogging", e.getMessage(), e, "");
        }
    }

    @InternalApi
    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("loggingProject", loggingProject);
        JSONArray details = new JSONArray();
        for (LoggingDetail detail : loggingDetails) {
            details.add(detail.marshal());
        }
        object.put("loggingDetails", details);
        return object;
    }

    @InternalApi
    public static Logging unmarshal(final JSONObject object) {
        Args.notNull(object, "object");
        final String project = object.getString("loggingProject");
        Args.notNullOrEmpty(project, "loggingProject");
        final JSONArray details = object.getJSONArray("loggingDetails");
        Args.notNull(details, "loggingDetails");
        if (details.isEmpty()) {
            throw new IllegalArgumentException("loggingDetails must not be empty");
        }
        List<LoggingDetail> loggingDetails = new ArrayList<LoggingDetail>(details.size());
        for (int i = 0; i < details.size(); i++) {
            loggingDetails.add(LoggingDetail.unmarshal(details.getJSONObject(i)));
        }
        return new Logging(project, loggingDetails);
    }
}
