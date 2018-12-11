package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

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

    public boolean isAtAll() {
        return isAtAll;
    }

    public void setAtAll(boolean atAll) {
        isAtAll = atAll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DingTalkNotification that = (DingTalkNotification) o;

        if (isAtAll() != that.isAtAll()) return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        return getAtMobiles() != null ? getAtMobiles().equals(that.getAtMobiles()) : that.getAtMobiles() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getAtMobiles() != null ? getAtMobiles().hashCode() : 0);
        result = 31 * result + (isAtAll() ? 1 : 0);
        return result;
    }
}
