package com.aliyun.openservices.log.util;


import net.sf.json.JSONObject;


public interface Unmarshaller<T> {

    T unmarshal(JSONObject value);
}