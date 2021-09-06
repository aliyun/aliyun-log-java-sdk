package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public class JDBCSource extends DataSource {

	private String databaseType;
	private String jdbcDatabase;
	private String jdbcUsername;
	private String jdbcPassword;
	private String jdbcIP;
	private String jdbcPort;
	private String tableName;
	private String tableKey;
	private int pageSize;
	private int pageNumber;
	private String appendSQL;
	private String jdbcDriver;
	private String jdbcURL;
	private String mode;
	private String idKey;
	private String timeKey;

	public JDBCSource() {
		super(DataSourceType.JDBC);
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public String getJdbcIP() {
		return jdbcIP;
	}

	public void setJdbcIP(String jdbcIP) {
		this.jdbcIP = jdbcIP;
	}

	public String getAppendSQL() {
		return appendSQL;
	}

	public void setAppendSQL(String appendSQL) {
		this.appendSQL = appendSQL;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getJdbcDatabase() {
		return jdbcDatabase;
	}

	public void setJdbcDatabase(String jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}

	public String getJdbcPort() {
		return jdbcPort;
	}

	public void setJdbcPort(String jdbcPort) {
		this.jdbcPort = jdbcPort;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTablName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableKey() {
		return tableKey;
	}

	public void setTableKey(String tableKey) {
		this.tableKey = tableKey;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getIdKey() {
		return idKey;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	public String getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(String timeKey) {
		this.timeKey = timeKey;
	}

	@Override
	public void deserialize(JSONObject jsonObject) {
		super.deserialize(jsonObject);
		databaseType = jsonObject.getString("databaseType");
		jdbcDatabase = jsonObject.getString("jdbcDatabase");
		jdbcUsername = jsonObject.getString("jdbcUsername");
		jdbcPassword = jsonObject.getString("jdbcPassword");
		jdbcIP = jsonObject.getString("jdbcIP");
		jdbcPort = jsonObject.getString("jdbcPort");
		tableName = jsonObject.getString("tableName");
		tableKey = jsonObject.getString("tableKey");
		pageSize = jsonObject.getIntValue("pageSize");
		pageNumber = jsonObject.getIntValue("pageNumber");
		appendSQL = jsonObject.getString("appendSQL");
		jdbcDriver = jsonObject.getString("jdbcDriver");
		jdbcURL = jsonObject.getString("jdbcURL");
		mode = jsonObject.getString("mode");
		idKey = jsonObject.getString("idKey");
		timeKey = jsonObject.getString("timeKey");
	}

}
