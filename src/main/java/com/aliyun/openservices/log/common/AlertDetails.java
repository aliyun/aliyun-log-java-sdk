package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

public class AlertDetails extends ExecutionDetails {

    @JSONField
    private boolean fired;

    @JSONField
    private NotificationStatus notificationStatus;

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public enum NotificationStatus {
        FAILED("Failed"),
        SUCCEED("Succeed"),
        PARTIAL_SUCCEED("PartialSucceed");

        private final String value;

        NotificationStatus(String value) {
            this.value = value;
        }

        public static NotificationStatus fromString(String value) {
            for (NotificationStatus type : NotificationStatus.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

    public void deserialize(final JSONObject value) {
        fired = value.getBoolean("fired");
        notificationStatus = NotificationStatus.fromString(value.getString("notificationStatus"));
    }
}
