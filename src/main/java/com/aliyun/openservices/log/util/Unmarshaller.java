package com.aliyun.openservices.log.util;


import net.sf.json.JSONArray;


public interface Unmarshaller<T> {

    T unmarshal(JSONArray value, int index);
}