package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;

import java.util.Date;
import java.util.List;

/**
 * Configuration for alert job.
 */
public class AlertConfiguration extends DashboardBasedJobConfiguration {

    /**
     * The trigger condition expression e.g $0.xx > 100 and $1.yy < 100.
     * Which depends on the order of queries in {@code queryList}.
     */
    @JSONField
    private String condition;

    @JSONField
    private List<Query> queryList;

    @JSONField
    private Date muteUntil;

    /**
     * Optional notify threshold, defaults to 1.
     */
    @JSONField
    private Integer notifyThreshold = 1;

    /**
     * Duration with format '1h', '2s'
     */
    @JSONField
    private String throttling;

    @JSONField
    private boolean sendRecoveryMessage;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    public Date getMuteUntil() {
        return muteUntil;
    }

    public void setMuteUntil(Date muteUntil) {
        this.muteUntil = muteUntil;
    }

    public Integer getNotifyThreshold() {
        return notifyThreshold;
    }

    public void setNotifyThreshold(Integer notifyThreshold) {
        this.notifyThreshold = notifyThreshold;
    }

    public String getThrottling() {
        return throttling;
    }

    public boolean getSendRecoveryMessage() {
        return sendRecoveryMessage;
    }

    public void setSendRecoveryMessage(boolean sendRecoveryMessage) {
        this.sendRecoveryMessage = sendRecoveryMessage;
    }

    public void setThrottling(String throttling) {
        Args.checkDuration(throttling);
        this.throttling = throttling;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        condition = value.getString("condition");
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.deserialize(value.getJSONObject(index));
                return query;
            }
        });
        if (value.containsKey("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        notifyThreshold = JsonUtils.readOptionalInt(value, "notifyThreshold");
        throttling = JsonUtils.readOptionalString(value, "throttling");
        sendRecoveryMessage = JsonUtils.readBool(value, "sendRecoveryMessage", false);
    }

    @Override
    Notification makeQualifiedNotification(NotificationType type) {
        switch (type) {
            case DING_TALK:
                return new DingTalkNotification();
            case EMAIL:
                return new EmailNotification();
            case MESSAGE_CENTER:
                return new MessageCenterNotification();
            case SMS:
                return new SmsNotification();
            case WEBHOOK:
                return new WebhookNotification();
            case VOICE:
                return new VoiceNotification();
            default:
                throw new IllegalArgumentException("Unimplemented notification type: " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertConfiguration that = (AlertConfiguration) o;

        if (sendRecoveryMessage != that.sendRecoveryMessage) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (queryList != null ? !queryList.equals(that.queryList) : that.queryList != null) return false;
        if (muteUntil != null ? !muteUntil.equals(that.muteUntil) : that.muteUntil != null) return false;
        if (notifyThreshold != null ? !notifyThreshold.equals(that.notifyThreshold) : that.notifyThreshold != null)
            return false;
        return throttling != null ? throttling.equals(that.throttling) : that.throttling == null;
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (queryList != null ? queryList.hashCode() : 0);
        result = 31 * result + (muteUntil != null ? muteUntil.hashCode() : 0);
        result = 31 * result + (notifyThreshold != null ? notifyThreshold.hashCode() : 0);
        result = 31 * result + (throttling != null ? throttling.hashCode() : 0);
        result = 31 * result + (sendRecoveryMessage ? 1 : 0);
        return result;
    }
}
