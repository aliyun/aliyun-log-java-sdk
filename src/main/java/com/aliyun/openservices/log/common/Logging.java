package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public static Logging unmarshal(final JSONObject object) {
        Args.notNull(object, "object");
        final String project = object.getString("loggingProject");
        Args.notNullOrEmpty(project, "loggingProject");
        final JSONArray details = object.getJSONArray("loggingDetails");
        Args.notNullOrEmpty(details, "loggingDetails");
        List<LoggingDetail> loggingDetails = new ArrayList<LoggingDetail>(details.size());
        for (int i = 0; i < details.size(); i++) {
            loggingDetails.add(LoggingDetail.unmarshal(details.getJSONObject(i)));
        }
        return new Logging(project, loggingDetails);
    }
}
