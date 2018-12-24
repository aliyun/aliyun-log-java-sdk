package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Send DingTalk notification.
 */
public class DingTalkNotification extends HttpNotification {

    @JSONField
    private String title;

    @JSONField
    private List<String> atMobiles;

    @JSONField
    private boolean isAtAll;

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

    public boolean getIsAtAll() {
        return isAtAll;
    }

    public void setIsAtAll(boolean isAtAll) {
        this.isAtAll = isAtAll;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        atMobiles = JsonUtils.readStringList(value, "atMobiles");
        isAtAll = JsonUtils.readBool(value, "isAtAll", false);
        title = JsonUtils.readOptionalString(value, "title");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DingTalkNotification that = (DingTalkNotification) o;

        if (getIsAtAll() != that.getIsAtAll()) return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        return getAtMobiles() != null ? getAtMobiles().equals(that.getAtMobiles()) : that.getAtMobiles() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getAtMobiles() != null ? getAtMobiles().hashCode() : 0);
        result = 31 * result + (getIsAtAll() ? 1 : 0);
        return result;
    }
}
