package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

public class TimeSpan {

    @JSONField
    private TimeSpanType type;

    @JSONField
    private String start;

    @JSONField
    private String end;

    public TimeSpanType getType() {
        return type;
    }

    public void setType(TimeSpanType type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void validate(long max) {
        Args.notNull(type, "type");
        type.validate(getStart(), getEnd(), max);
    }

    public void deserialize(JSONObject value) {
        setType(TimeSpanType.fromString(value.getString("type")));
        if (value.has("start")) {
            setStart(value.getString("start"));
        }
        if (value.has("end")) {
            setEnd(value.getString("end"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSpan timeSpan = (TimeSpan) o;

        if (getType() != timeSpan.getType()) return false;
        if (getStart() != null ? !getStart().equals(timeSpan.getStart()) : timeSpan.getStart() != null) return false;
        return getEnd() != null ? getEnd().equals(timeSpan.getEnd()) : timeSpan.getEnd() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }
}
