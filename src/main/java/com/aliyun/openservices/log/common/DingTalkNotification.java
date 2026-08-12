package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Send DingTalk notification.
 */
public class DingTalkNotification extends HttpNotification {

    private String title;

    private List<String> atMobiles;

    /**
     * Ding talk API support POST only.
     */
    @Deprecated
    private String method;

    /**
     * At all group members or not.
     */
    private boolean atAll = false;

    public DingTalkNotification() {
        super(NotificationType.DING_TALK);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    @Deprecated
    public String getMethod() {
        return method;
    }

    @Deprecated
    public void setMethod(String method) {
        this.method = method;
    }

    public boolean getAtAll() {
        return atAll;
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (title != null) {
            value.put(Consts.TITLE, title);
        }
        if (atMobiles != null) {
            value.put(Consts.AT_MOBILES, JsonCodec.toJsonArray(atMobiles));
        }
        if (method != null) {
            value.put(Consts.METHOD, method);
        }
        value.put("atAll", atAll);
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        atMobiles = JsonUtils.readOptionalStrings(value, Consts.AT_MOBILES);
        title = JsonUtils.readOptionalString(value, Consts.TITLE);
        method = JsonUtils.readOptionalString(value, Consts.METHOD);
        atAll = JsonUtils.readBool(value, "atAll", false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DingTalkNotification that = (DingTalkNotification) o;

        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        if (getAtMobiles() != null ? !getAtMobiles().equals(that.getAtMobiles()) : that.getAtMobiles() != null)
            return false;
        if (atAll != that.getAtAll())
            return false;
        return getMethod() != null ? getMethod().equals(that.getMethod()) : that.getMethod() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getAtMobiles() != null ? getAtMobiles().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + (getAtAll() ? 1 : 0);
        return result;
    }
}
