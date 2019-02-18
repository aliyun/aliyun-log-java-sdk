package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
        if (value.has("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        notifyThreshold = JsonUtils.readOptionalInt(value, "notifyThreshold");
        throttling = JsonUtils.readOptionalString(value, "throttling");
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

        if (getCondition() != null ? !getCondition().equals(that.getCondition()) : that.getCondition() != null)
            return false;
        if (getQueryList() != null ? !getQueryList().equals(that.getQueryList()) : that.getQueryList() != null)
            return false;
        if (getMuteUntil() != null ? !getMuteUntil().equals(that.getMuteUntil()) : that.getMuteUntil() != null)
            return false;
        if (getNotifyThreshold() != null ? !getNotifyThreshold().equals(that.getNotifyThreshold()) : that.getNotifyThreshold() != null)
            return false;
        return getThrottling() != null ? getThrottling().equals(that.getThrottling()) : that.getThrottling() == null;
    }

    @Override
    public int hashCode() {
        int result = getCondition() != null ? getCondition().hashCode() : 0;
        result = 31 * result + (getQueryList() != null ? getQueryList().hashCode() : 0);
        result = 31 * result + (getMuteUntil() != null ? getMuteUntil().hashCode() : 0);
        result = 31 * result + (getNotifyThreshold() != null ? getNotifyThreshold().hashCode() : 0);
        result = 31 * result + (getThrottling() != null ? getThrottling().hashCode() : 0);
        return result;
    }
}
