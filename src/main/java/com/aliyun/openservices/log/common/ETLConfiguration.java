package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

/**
 * Configuration for ETL.
 */
public class ETLConfiguration extends JobConfiguration {

    @JSONField
    private String script;

    @JSONField
    private String logstore;

    @JSONField
    private String version;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    @Override
    public void deserialize(JSONObject value) {
        script = value.getString("script");
        logstore = value.getString("logstore");
        version = value.getString("version");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ETLConfiguration that = (ETLConfiguration) o;

        if (getScript() != null ? !getScript().equals(that.getScript()) : that.getScript() != null) return false;
        if (getLogstore() != null ? !getLogstore().equals(that.getLogstore()) : that.getLogstore() != null)
            return false;
        return version != null ? version.equals(that.version) : that.version == null;
    }

    @Override
    public int hashCode() {
        int result = getScript() != null ? getScript().hashCode() : 0;
        result = 31 * result + (getLogstore() != null ? getLogstore().hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
