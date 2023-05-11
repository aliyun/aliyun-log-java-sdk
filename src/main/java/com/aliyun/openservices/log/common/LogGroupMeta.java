package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * LogGroupMeta is the basic data structure for send, contains rawDataSize and
 * cursorId
 * 
 * @author sls_dev
 * 
 */

public class LogGroupMeta implements Serializable {
    private List<Long> cursorIdList;
    private int rawDataSize = 0;
    private int rawLogGroupCount = 0;
    private byte[] rawBytes = null;
    private int offset;
    private int length;
    private int logGroupCount;
    private String requestId = "";

    public LogGroupMeta(byte[] rawBytes, int offset, int length, int logGroupCount, String requestId) {
        this.requestId = requestId;
        this.rawBytes = rawBytes;
        this.offset = offset;
        this.length = length;
        this.logGroupCount = logGroupCount;
        cursorIdList = new ArrayList<Long>();
    }

    public List<Long> getCursorIdList() {
        return cursorIdList;
    }

    public int getRawDataSize() {
        return rawDataSize;
    }

    public int getRawLogGroupCount() {
        return rawLogGroupCount;
    }

    public void parseMeta() throws LogException {
        if (length <= 0) {
            return;
        }
        String meta = new String(rawBytes, offset, length);
        try {
            JSONObject object = JSONObject.parseObject(meta);
            rawDataSize = object.getIntValue("rawDataSize");
            rawLogGroupCount = object.getIntValue("rawLogGroupCount");
            JSONArray cursorIdList = object.getJSONArray("cursorIdList");
            if (cursorIdList != null) {
                for (int i = 0; i < cursorIdList.size(); i++) {
                    this.cursorIdList.add(cursorIdList.getLong(i));
                }
                if (this.cursorIdList.size() != logGroupCount) {
                    throw new LogException("InvalidLogGroupMeta",
                            "The cursorIdList count does not match with the LogGroup count", requestId);
                }
            }
        } catch (com.alibaba.fastjson.JSONException e) {
            throw new LogException("InvalidLogGroupMeta", "Fail to parse LogGroup meta: " + meta, e, requestId);
        }
    }
}
