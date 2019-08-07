package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        if (jsonObject.containsKey("maxLines")) {
            maxLines = jsonObject.getInt("maxLines");
        }
        negate = JsonUtils.readBool(jsonObject, "negate", false);
        match = JsonUtils.readOptionalString(jsonObject, "match");
        pattern = JsonUtils.readOptionalString(jsonObject, "pattern");
        flushPattern = JsonUtils.readOptionalString(jsonObject, "flushPattern");
    }
}
