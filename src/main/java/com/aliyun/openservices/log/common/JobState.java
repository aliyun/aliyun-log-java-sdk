package com.aliyun.openservices.log.common;


import java.lang.reflect.Type;

public enum JobState {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    JobState(String value) {
        this.value = value;
    }

    public static JobState fromString(String value) {
        for (JobState state : JobState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

}
