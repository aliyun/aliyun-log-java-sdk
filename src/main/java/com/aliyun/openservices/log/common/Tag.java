package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class Tag implements Serializable {

    private static final long serialVersionUID = -3729515025931612142L;
    private String key;
    private String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
