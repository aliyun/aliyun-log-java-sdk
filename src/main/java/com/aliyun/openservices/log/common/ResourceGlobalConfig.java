package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ResourceGlobalConfig implements Serializable {
    @SerializedName("config_id")
    private String configId;
    @SerializedName("config_name")
    private String configName;
    @SerializedName("config_detail")
    private ConfigDetail configDetail;

    public static class CenterLog {

        @SerializedName("region")
        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    public static class ConfigDetail {
        @SerializedName("alert_center_log")
        private CenterLog alertCenterLog;

        public CenterLog getAlertCenterLog() {
            return alertCenterLog;
        }

        public void setAlertCenterLog(CenterLog alertCenterLog) {
            this.alertCenterLog = alertCenterLog;
        }
    }


}
