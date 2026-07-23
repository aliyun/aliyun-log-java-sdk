package com.aliyun.openservices.log.internal;


import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.annotation.InternalApi;


@InternalApi
public interface Unmarshaller<T> {

    T unmarshal(JSONArray value, int index);
}