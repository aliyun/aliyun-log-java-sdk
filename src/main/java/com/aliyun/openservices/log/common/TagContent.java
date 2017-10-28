/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * LogContent is a simple data structure used in @LogItem, it presents a
 * key/value pair in @logItem.
 *
 * @author sls_dev
 *
 */
public class TagContent implements Serializable {

    private static final long serialVersionUID = 2417167614162708776L;
    public String key;
    public String value;

    /**
     * Construct a log content pair
     *
     * @param key
     *            log content key
     * @param value
     *            log content value
     */
    public TagContent(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get log tag key
     *
     * @return log tag key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get log content value
     *
     * @return log content value
     */
    public String getValue() {
        return this.value;
    }
}
