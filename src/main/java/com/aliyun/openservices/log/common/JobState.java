package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum JobState {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    JobState(String value) {
        this.value = value;
    }

    @JsonEnumCreator
    public static JobState fromString(String value) {
        for (JobState state : JobState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        return null;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

}
