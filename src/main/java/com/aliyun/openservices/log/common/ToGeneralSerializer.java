package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.lang.reflect.Type;


public class ToGeneralSerializer implements ObjectSerializer {

    public static final ToGeneralSerializer instance = new ToGeneralSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
                      int features) {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        if (object instanceof IngestionGeneralSource || object instanceof ExportGeneralSink) {
            out.write(object.toString());
        } else {
            out.write(JSONObject.toJSONString(object));
        }
    }
}