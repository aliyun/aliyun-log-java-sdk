package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class MultilineFormat extends LineFormat {

    private int maxLines = -1;
    private boolean negate;
    private String match;
    private String pattern;
    private String flushPattern;

    public MultilineFormat() {
        super("Multiline");
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public boolean getNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFlushPattern() {
        return flushPattern;
    }

    public void setFlushPattern(String flushPattern) {
        this.flushPattern = flushPattern;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        value.put("maxLines", maxLines);
        value.put("negate", negate);
        if (match != null) {
            value.put("match", match);
        }
        if (pattern != null) {
            value.put("pattern", pattern);
        }
        if (flushPattern != null) {
            value.put("flushPattern", flushPattern);
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        if (jsonObject.containsKey("maxLines")) {
            maxLines = jsonObject.getIntValue("maxLines");
        }
        negate = JsonUtils.readBool(jsonObject, "negate", false);
        match = JsonUtils.readOptionalString(jsonObject, "match");
        pattern = JsonUtils.readOptionalString(jsonObject, "pattern");
        flushPattern = JsonUtils.readOptionalString(jsonObject, "flushPattern");
    }
}
