package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ProjectQuota implements Serializable {

    private static final long serialVersionUID = -6475258821072410948L;

    private int logstore;
    private int shard;
    private int config;
    private int machineGroup;
    private int dashboard;
    private int chart;
    private int domain;
    private int savedsearch;
    private int ETL;
    private int export;
    private int ingestion;
    private int alert;
    private int report;

    private int scheduledSQL;

    private long readQpsPerMin;
    private long writeQpsPerMin;
    private long writeSizePerMin;

    public int getLogstore() {
        return logstore;
    }

    public void setLogstore(int logstore) {
        this.logstore = logstore;
    }

    public int getShard() {
        return shard;
    }

    public void setShard(int shard) {
        this.shard = shard;
    }

    public int getConfig() {
        return config;
    }

    public void setConfig(int config) {
        this.config = config;
    }

    public int getMachineGroup() {
        return machineGroup;
    }

    public void setMachineGroup(int machineGroup) {
        this.machineGroup = machineGroup;
    }

    public int getDashboard() {
        return dashboard;
    }

    public void setDashboard(int dashboard) {
        this.dashboard = dashboard;
    }

    public int getChart() {
        return chart;
    }

    public void setChart(int chart) {
        this.chart = chart;
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public int getSavedsearch() {
        return savedsearch;
    }

    public void setSavedsearch(int savedsearch) {
        this.savedsearch = savedsearch;
    }

    public int getETL() {
        return ETL;
    }

    public void setETL(int ETL) {
        this.ETL = ETL;
    }

    public int getExport() {
        return export;
    }

    public void setExport(int export) {
        this.export = export;
    }

    public int getIngestion() {
        return ingestion;
    }

    public void setIngestion(int ingestion) {
        this.ingestion = ingestion;
    }

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public int getScheduledSQL() {
        return scheduledSQL;
    }

    public void setScheduledSQL(int scheduledSQL) {
        this.scheduledSQL = scheduledSQL;
    }

    public long getReadQpsPerMin() {
        return readQpsPerMin;
    }

    public void setReadQpsPerMin(long readQpsPerMin) {
        this.readQpsPerMin = readQpsPerMin;
    }

    public long getWriteQpsPerMin() {
        return writeQpsPerMin;
    }

    public void setWriteQpsPerMin(long writeQpsPerMin) {
        this.writeQpsPerMin = writeQpsPerMin;
    }

    public long getWriteSizePerMin() {
        return writeSizePerMin;
    }

    public void setWriteSizePerMin(long writeSizePerMin) {
        this.writeSizePerMin = writeSizePerMin;
    }

    public static ProjectQuota parseFromJSON(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        ProjectQuota quota = new ProjectQuota();
        quota.fromJSON(jsonObject);
        return quota;
    }

    public void fromJSON(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        logstore = jsonObject.getIntValue("logstore");
        shard = jsonObject.getIntValue("shard");
        config = jsonObject.getIntValue("config");
        machineGroup = jsonObject.getIntValue("machineGroup");
        dashboard = jsonObject.getIntValue("dashboard");
        chart = jsonObject.getIntValue("chart");
        savedsearch = jsonObject.getIntValue("savedsearch");
        ETL = jsonObject.getIntValue("ETL");
        export = jsonObject.getIntValue("export");
        ingestion = jsonObject.getIntValue("ingestion");
        alert = jsonObject.getIntValue("alert");
        report = jsonObject.getIntValue("report");
        scheduledSQL = jsonObject.getIntValue("scheduledSQL");
        writeSizePerMin = jsonObject.getLongValue("writeSizePerMin");
        writeQpsPerMin = jsonObject.getLongValue("writeQpsPerMin");
        readQpsPerMin = jsonObject.getLongValue("readQpsPerMin");
    }

    @Override
    public String toString() {
        return "ProjectQuota{" +
                "logstore=" + logstore +
                ", shard=" + shard +
                ", config=" + config +
                ", machineGroup=" + machineGroup +
                ", dashboard=" + dashboard +
                ", chart=" + chart +
                ", domain=" + domain +
                ", savedsearch=" + savedsearch +
                ", ETL=" + ETL +
                ", export=" + export +
                ", ingestion=" + ingestion +
                ", alert=" + alert +
                ", report=" + report +
                ", scheduledSQL=" + scheduledSQL +
                ", readQpsPerMin=" + readQpsPerMin +
                ", writeQpsPerMin=" + writeQpsPerMin +
                ", writeSizePerMin=" + writeSizePerMin +
                '}';
    }
}

