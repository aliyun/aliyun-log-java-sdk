package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

/**
 * Configuration for ETL.
 */
public class ETLConfiguration extends JobConfiguration {

    @JSONField
    private String scriptLocation;

    @JSONField
    private String logstore;

    public String getScriptLocation() {
        return scriptLocation;
    }

    public void setScriptLocation(String scriptLocation) {
        this.scriptLocation = scriptLocation;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    @Override
    public void deserialize(JSONObject value) {
        scriptLocation = value.getString("scriptLocation");
        logstore = value.getString("logstore");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ETLConfiguration that = (ETLConfiguration) o;

        if (getScriptLocation() != null ? !getScriptLocation().equals(that.getScriptLocation()) : that.getScriptLocation() != null)
            return false;
        return getLogstore() != null ? getLogstore().equals(that.getLogstore()) : that.getLogstore() == null;
    }

    @Override
    public int hashCode() {
        int result = getScriptLocation() != null ? getScriptLocation().hashCode() : 0;
        result = 31 * result + (getLogstore() != null ? getLogstore().hashCode() : 0);
        return result;
    }
}
