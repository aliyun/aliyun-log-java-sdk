package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class CsvColumn implements Serializable {
    public static final long serialVersionUID = 1382671890031L;
    private String name;
    private String type; // bigint varchar double

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CsvColumn [name=" + name + ", type=" + type + "]";
    }
}
