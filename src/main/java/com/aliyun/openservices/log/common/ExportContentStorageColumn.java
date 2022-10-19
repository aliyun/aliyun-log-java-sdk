package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class ExportContentStorageColumn implements Serializable {

	private static final long serialVersionUID = -4734086474258335426L;
	protected String name = "";
	protected String type = "'";  // support int64, int32, double, float, boolean, string

	public ExportContentStorageColumn() {
	}

	public ExportContentStorageColumn(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
