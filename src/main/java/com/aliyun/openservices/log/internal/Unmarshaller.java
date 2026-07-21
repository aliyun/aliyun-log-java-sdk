package com.aliyun.openservices.log.internal;


import com.aliyun.openservices.log.internal.json.JSONArray;


public interface Unmarshaller<T> {

    T unmarshal(JSONArray value, int index);
}