package com.aliyun.openservices.log.internal;


import net.sf.json.JSONArray;


public interface Unmarshaller<T> {

    T unmarshal(JSONArray value, int index);
}