package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

/**
 * @author xizongzheng.xzz
 */
public class JobDownSamplingConfiguration extends JobConfiguration {
    private String sourceLogstore;
    private String destProject;
    private String destEndpoint;
    private String destLogstore;
    private String sqlType = "standard";
    private String resourcePool = "default";
    private String roleArn;
    private String fromTimeExpr;
    private String toTimeExpr;
    private Long fromTime = 0L;
    private Long toTime = 0L;
    private String dataFormat = "metric2metric";

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

    public String getDestEndpoint() {
        return destEndpoint;
    }

    public void setDestEndpoint(String destEndpoint) {
        this.destEndpoint = destEndpoint;
    }

    public String getDestLogstore() {
        return destLogstore;
    }

    public void setDestLogstore(String destLogstore) {
        this.destLogstore = destLogstore;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getResourcePool() {
        return resourcePool;
    }

    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
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

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }


    @Override
    public void deserialize(JSONObject value) {
        sourceLogstore = value.getString("sourceLogstore");
        roleArn = value.getString("roleArn");
        sqlType = value.getString("sqlType");
        resourcePool = value.getString("resourcePool");
        destEndpoint = value.getString("destEndpoint");
        destProject = value.getString("destProject");
        destLogstore = value.getString("destLogstore");
        fromTimeExpr = value.getString("fromTimeExpr");
        toTimeExpr = value.getString("toTimeExpr");
        fromTime = value.getLongValue("fromTime");
        toTime = value.getLongValue("toTime");
        dataFormat = value.getString("dataFormat");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JobDownSamplingConfiguration that = (JobDownSamplingConfiguration) o;

        if (sourceLogstore != null ? !sourceLogstore.equals(that.sourceLogstore) : that.sourceLogstore != null) {
            return false;
        }
        if (destProject != null ? !destProject.equals(that.destProject) : that.destProject != null) {
            return false;
        }
        if (destEndpoint != null ? !destEndpoint.equals(that.destEndpoint) : that.destEndpoint != null) {
            return false;
        }
        if (destLogstore != null ? !destLogstore.equals(that.destLogstore) : that.destLogstore != null) {
            return false;
        }
        if (sqlType != null ? !sqlType.equals(that.sqlType) : that.sqlType != null) {
            return false;
        }
        if (resourcePool != null ? !resourcePool.equals(that.resourcePool) : that.resourcePool != null) {
            return false;
        }
        if (roleArn != null ? !roleArn.equals(that.roleArn) : that.roleArn != null) {
            return false;
        }
        if (fromTimeExpr != null ? !fromTimeExpr.equals(that.fromTimeExpr) : that.fromTimeExpr != null) {
            return false;
        }
        if (toTimeExpr != null ? !toTimeExpr.equals(that.toTimeExpr) : that.toTimeExpr != null) {
            return false;
        }
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null) {
            return false;
        }
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) {
            return false;
        }
        return dataFormat != null ? dataFormat.equals(that.dataFormat) : that.dataFormat == null;
    }

    @Override
    public int hashCode() {
        int result = sourceLogstore != null ? sourceLogstore.hashCode() : 0;
        result = 31 * result + (destProject != null ? destProject.hashCode() : 0);
        result = 31 * result + (destEndpoint != null ? destEndpoint.hashCode() : 0);
        result = 31 * result + (destLogstore != null ? destLogstore.hashCode() : 0);
        result = 31 * result + (sqlType != null ? sqlType.hashCode() : 0);
        result = 31 * result + (resourcePool != null ? resourcePool.hashCode() : 0);
        result = 31 * result + (roleArn != null ? roleArn.hashCode() : 0);
        result = 31 * result + (fromTimeExpr != null ? fromTimeExpr.hashCode() : 0);
        result = 31 * result + (toTimeExpr != null ? toTimeExpr.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (dataFormat != null ? dataFormat.hashCode() : 0);
        return result;
    }
}