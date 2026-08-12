package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ListProjectResponse extends Response {

    /**
     * list project response
     */
    private static final long serialVersionUID = -6135260934159853531L;
    protected int total = 0;
    protected int count = 0;
    protected List<Project> projects = new ArrayList<Project>();

    public ListProjectResponse(Map<String, String> headers) {
        super(headers);
    }

    public ListProjectResponse(Map<String, String> headers, int total, int count, List<Project> projects) {
        super(headers);
        this.total = total;
        this.count = count;
        this.projects = projects;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = new ArrayList<Project>();
        this.projects.addAll(projects);
    }

    private static List<Project> extractProjects(JSONObject object) throws LogException {
        List<Project> projects = new ArrayList<Project>();
        if (object == null) {
            return projects;
        }
        JSONArray array = object.getJSONArray("projects");
        if (array == null) {
            return projects;
        }
        for (int i = 0; i < array.size(); i++) {
            Project project = new Project();
            JSONObject jsonObject = array.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            project.fromJsonObject(jsonObject);
            projects.add(project);
        }
        return projects;
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) throws LogException {
        total = jsonObject.getIntValue(Consts.CONST_TOTAL);
        count = jsonObject.getIntValue(Consts.CONST_COUNT);
        projects = extractProjects(jsonObject);
    }
}
