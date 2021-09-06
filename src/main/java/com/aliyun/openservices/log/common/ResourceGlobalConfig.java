package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class ResourceGlobalConfig implements Serializable {
    @JSONField(name = "config_id")
    private String configId;
    @JSONField(name = "config_name")
    private String configName;
    @JSONField(name = "config_detail")
    private ConfigDetail configDetail;

    public static class CenterLog {

        @JSONField(name = "region")
        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    public static class ConfigDetail {
        @JSONField(name = "alert_center_log")
        private CenterLog alertCenterLog;

        public CenterLog getAlertCenterLog() {
            return alertCenterLog;
        }

        public void setAlertCenterLog(CenterLog alertCenterLog) {
            this.alertCenterLog = alertCenterLog;
        }
    }


}
