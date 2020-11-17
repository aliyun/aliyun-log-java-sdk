package com.aliyun.openservices.log.internal;


import com.alibaba.fastjson.JSONArray;


public interface Unmarshaller<T> {

    T unmarshal(JSONArray value, int index);
}