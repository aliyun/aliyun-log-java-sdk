package com.aliyun.openservices.log.common;

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
        throw new IllegalArgumentException("Unknown job state: " + value);
    }
}
