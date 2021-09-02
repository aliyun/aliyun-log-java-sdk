package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;

public class ExternalStore implements Serializable {
    private static final long serialVersionUID = 6493904490967634292L;
    private String externalStoreName;
    private String storeType;
    private Parameter parameter;

    public ExternalStore() {
    }

    public ExternalStore(String externalStoreName, String storeType, Parameter parameter) {
        this.externalStoreName = externalStoreName;
        this.storeType = storeType;
        this.parameter = parameter;
    }

    public ExternalStore(JSONObject object) throws LogException {
        fromJson(object);
    }

    public void fromJson(JSONObject object) throws LogException {
        try {
            setExternalStoreName(object.getString(Consts.CONST_EXTERNAL_NAME));
            setStoreType(object.getString("storeType"));
            setParameter(JSONObject.parseObject(object.getString("parameter"), Parameter.class));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateExternalStore", e.getMessage(), e, "");
        }
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("externalStoreName", externalStoreName);
        object.put("storeType", storeType);
        object.put("parameter", parameter);
        return object;
    }

    public String getExternalStoreName() {
        return externalStoreName;
    }

    public void setExternalStoreName(String externalStoreName) {
        this.externalStoreName = externalStoreName;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "ExternalStore{" +
                "externalStoreName='" + externalStoreName + '\'' +
                ", storeType='" + storeType + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
