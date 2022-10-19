package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;

public class ScheduledSQLConfiguration extends JobConfiguration {
    private String sourceLogstore;
    private String destProject;
    private String destEndpoint;
    private String destLogstore;
    private String script;
    private String sqlType = "standard";
    private String resourcePool = "default";
    private String roleArn;
    private String destRoleArn;
    private String fromTimeExpr;
    private String toTimeExpr;
    private Integer maxRunTimeInSeconds;
    private Integer maxRetries;
    private Long fromTime = 0L;
    private Long toTime = 0L;
    private String dataFormat = "log2log";
    private ScheduledSQLParameters parameters;

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public ScheduledSQLParameters getParameters() {
        return parameters;
    }

    public void setParameters(ScheduledSQLParameters parameters) {
        this.parameters = parameters;
    }

    public String getDestEndpoint() {
        return destEndpoint;
    }

    public void setDestEndpoint(String destEndpoint) {
        this.destEndpoint = destEndpoint;
    }

    public String getSourceLogstore() {
        return sourceLogstore;
    }

    public void setSourceLogstore(String sourceLogstore) {
        this.sourceLogstore = sourceLogstore;
    }

    public String getDestProject() {
        return destProject;
    }

    public void setDestProject(String destProject) {
        this.destProject = destProject;
    }

    public String getDestLogstore() {
        return destLogstore;
    }

    public void setDestLogstore(String destLogstore) {
        this.destLogstore = destLogstore;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public String getFromTimeExpr() {
        return fromTimeExpr;
    }

    public void setFromTimeExpr(String fromTimeExpr) {
        this.fromTimeExpr = fromTimeExpr;
    }

    public String getToTimeExpr() {
        return toTimeExpr;
    }

    public void setToTimeExpr(String toTimeExpr) {
        this.toTimeExpr = toTimeExpr;
    }

    public Integer getMaxRunTimeInSeconds() {
        return maxRunTimeInSeconds;
    }

    public void setMaxRunTimeInSeconds(Integer maxRunTimeInSeconds) {
        this.maxRunTimeInSeconds = maxRunTimeInSeconds;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }

    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getDestRoleArn() {
        return destRoleArn;
    }

    public void setDestRoleArn(String destRoleArn) {
        this.destRoleArn = destRoleArn;
    }

    public String getResourcePool() {
        return resourcePool;
    }

    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
    }

    @Override
    public void deserialize(JSONObject value) {
        sourceLogstore = value.getString("sourceLogstore");
        roleArn = value.getString("roleArn");
        destRoleArn = value.getString("destRoleArn");
        script = value.getString("script");
        sqlType = value.getString("sqlType");
        resourcePool = value.getString("resourcePool");
        destEndpoint = value.getString("destEndpoint");
        destProject = value.getString("destProject");
        destLogstore = value.getString("destLogstore");
        fromTimeExpr = value.getString("fromTimeExpr");
        toTimeExpr = value.getString("toTimeExpr");
        maxRunTimeInSeconds = value.getIntValue("maxRunTimeInSeconds");
        maxRetries = value.getIntValue("maxRetries");
        fromTime = value.getLongValue("fromTime");
        toTime = value.getLongValue("toTime");
        dataFormat = value.getString("dataFormat");
        if ("log2metric".equals(dataFormat)) {
            parameters = new Log2MetricParameters();
            parameters.deserialize(value.getJSONObject("parameters"));
        } else if ("metric2metric".equals(dataFormat)) {
            parameters = new Metric2MetricParameters();
            parameters.deserialize(value.getJSONObject("parameters"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduledSQLConfiguration that = (ScheduledSQLConfiguration) o;
        if (getScript() != null ? !getScript().equals(that.getScript()) : that.getScript() != null) {
            return false;
        }
        if (getSqlType() != null ? !getSqlType().equals(that.getSqlType()) : that.getSqlType() != null) {
            return false;
        }
        if (getResourcePool() != null ? !getResourcePool().equals(that.getResourcePool()) : that.getResourcePool() != null) {
            return false;
        }
        if (getDestEndpoint() != null ? !getDestEndpoint().equals(that.getDestEndpoint()) : that.getDestEndpoint() != null) {
            return false;
        }
        if (getDestProject() != null ? !getDestProject().equals(that.getDestProject()) : that.getDestProject() != null) {
            return false;
        }
        if (getSourceLogstore() != null ? !getSourceLogstore().equals(that.getSourceLogstore()) : that.getSourceLogstore() != null) {
            return false;
        }
        if (getDestLogstore() != null ? !getDestLogstore().equals(that.getDestLogstore()) : that.getDestLogstore() != null) {
            return false;
        }
        if (getRoleArn() != null ? !getRoleArn().equals(that.getRoleArn()) : that.getRoleArn() != null) {
            return false;
        }
        if (getDestRoleArn() != null ? !getDestRoleArn().equals(that.getDestRoleArn()) : that.getDestRoleArn() != null) {
            return false;
        }
        if (getFromTimeExpr() != null ? !getFromTimeExpr().equals(that.getFromTimeExpr()) : that.getFromTimeExpr() != null) {
            return false;
        }
        if (getToTimeExpr() != null ? !getToTimeExpr().equals(that.getToTimeExpr()) : that.getToTimeExpr() != null) {
            return false;
        }
        if (getMaxRetries() != null ? !getMaxRetries().equals(that.getMaxRetries()) : that.getMaxRetries() != null) {
            return false;
        }
        if (getFromTime() != null ? !getFromTime().equals(that.getFromTime()) : that.getFromTime() != null) {
            return false;
        }
        if (getToTime() != null ? !getToTime().equals(that.getToTime()) : that.getToTime() != null) {
            return false;
        }
        if (getDataFormat() != null ? !getDataFormat().equals(that.getDataFormat()) : that.getDataFormat() != null) {
            return false;
        }
        if (getParameters() != null ? !getParameters().equals(that.getParameters()) : that.getParameters() != null) {
            return false;
        }
        return getMaxRunTimeInSeconds() != null ? !getMaxRunTimeInSeconds().equals(that.getMaxRunTimeInSeconds()) : that.getMaxRunTimeInSeconds() != null;
    }

    @Override
    public int hashCode() {
        int result = getScript() != null ? getScript().hashCode() : 0;
        result = 31 * result + (getSourceLogstore() != null ? getSourceLogstore().hashCode() : 0);
        result = 31 * result + (getDestEndpoint() != null ? getDestEndpoint().hashCode() : 0);
        result = 31 * result + (getDestProject() != null ? getDestProject().hashCode() : 0);
        result = 31 * result + (getDestLogstore() != null ? getDestLogstore().hashCode() : 0);
        result = 31 * result + (getRoleArn() != null ? getRoleArn().hashCode() : 0);
        result = 31 * result + (getDestRoleArn() != null ? getDestRoleArn().hashCode() : 0);
        result = 31 * result + (getFromTimeExpr() != null ? getFromTimeExpr().hashCode() : 0);
        result = 31 * result + (getToTimeExpr() != null ? getToTimeExpr().hashCode() : 0);
        result = 31 * result + (getMaxRetries() != null ? getMaxRetries().hashCode() : 0);
        result = 31 * result + (getMaxRunTimeInSeconds() != null ? getMaxRunTimeInSeconds().hashCode() : 0);
        result = 31 * result + (getDataFormat() != null ? getDataFormat().hashCode() : 0);
        result = 31 * result + (getParameters() != null ? getParameters().hashCode() : 0);
        return result;
    }
}