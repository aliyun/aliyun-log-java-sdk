package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Project;

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
		for (Project project:projects) {
			this.projects.add(project);
		}
	}
}
