package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExternalStore implements Serializable, JsonSerializable, JsonDeserializable {
    private static final long serialVersionUID = 6493904490967634292L;
    protected String externalStoreName;
    protected String storeType;
    protected Parameter parameter;

    public ExternalStore() {
    }

    public ExternalStore(String externalStoreName, String storeType, Parameter parameter) {
        this.externalStoreName = externalStoreName;
        this.storeType = storeType;
        this.parameter = parameter;
    }

    public void fromJsonString(String externalStoreString) throws LogException {
        try {
            fromJsonObject(JSONObject.parseObject(externalStoreString));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateExternalStore", e.getMessage(), e, "");
        }
    }

    @InternalApi
    public ExternalStore(JSONObject object) throws LogException {
        fromJsonObject(object);
    }

    @InternalApi
    public void fromJsonObject(JSONObject object) throws LogException {
        try {
            setExternalStoreName(object.getString(Consts.CONST_EXTERNAL_NAME));
            setStoreType(object.getString("storeType"));
            setParameter(JsonCodec.fromJson(object.getJSONObject("parameter"), Parameter.class));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateExternalStore", e.getMessage(), e, "");
        }
    }

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject object = new JSONObject();
        object.put("externalStoreName", externalStoreName);
        object.put("storeType", storeType);
        object.put("parameter", JsonCodec.toJsonObject(parameter));
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
