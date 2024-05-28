package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.utils.CodingUtils;
import com.aliyun.openservices.log.util.Utils;

public class LogGroup implements Serializable {
    private static final long serialVersionUID = -1238325851112921953L;
    private final List<LogItem> mLogItems = new ArrayList<LogItem>();
    private String mTopic = "";
    private String mSource = "";
    protected String mMachineUUID = "";
    private final List<TagContent> mLogTags = new ArrayList<TagContent>();

    public LogGroup() {
    }

    public LogGroup(List<LogItem> items) {
        mLogItems.addAll(items);
    }

    public LogGroup(List<LogItem> items, List<TagContent> tags, String topic, String source) {
        mLogItems.addAll(items);
        mLogTags.addAll(tags);
        mTopic = topic;
        mSource = source;
    }

    public List<LogItem> getLogItems() {
        return mLogItems;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String mTopic) {
        this.mTopic = mTopic;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }

    public String getMachineUUID() {
        return mMachineUUID;
    }

    public void setMachineUUID(String mMachineUUID) {
        this.mMachineUUID = mMachineUUID;
    }

    public List<TagContent> getLogTags() {
        return mLogTags;
    }

    public byte[] serializeToPb() throws LogException {
        Logs.LogGroup.Builder builder = Logs.LogGroup.newBuilder();
        String topic = getTopic();
        if (!Utils.isBlank(topic)) {
            builder.setTopic(topic);
        }
        String source = getSource();
        if (!Utils.isBlank(source)) {
            builder.setSource(source);
        }
        String uuid = getMachineUUID();
        if (!Utils.isBlank(uuid)) {
            builder.setMachineUUID(uuid);
        }
        // tags
        List<TagContent> tags = getLogTags();
        if (tags != null && tags.size() > 0) {
            for (TagContent tag : tags) {
                Logs.LogTag.Builder tagBuilder = builder.addLogTagsBuilder();
                tagBuilder.setKey(tag.getKey());
                tagBuilder.setValue(tag.getValue());
            }
        }
        // logs
        List<LogItem> logs = getLogItems();
        for (LogItem item : logs) {
            Logs.Log.Builder log = builder.addLogsBuilder();
            log.setTime(item.mLogTime);
            if (item.mLogTimeNsPart != 0) {
                log.setTimeNs(item.mLogTimeNsPart);
            }
            for (LogContent content : item.mContents) {
                CodingUtils.assertStringNotNullOrEmpty(content.mKey, "key");
                Logs.Log.Content.Builder contentBuilder = log.addContentsBuilder();
                contentBuilder.setKey(content.mKey);
                if (content.mValue == null) {
                    contentBuilder.setValue("");
                } else {
                    contentBuilder.setValue(content.mValue);
                }
            }
        }
        return builder.build().toByteArray();
    }
}
