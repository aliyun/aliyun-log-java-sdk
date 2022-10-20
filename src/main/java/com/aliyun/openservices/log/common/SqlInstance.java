package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class SqlInstance {
    String name;
    int cu;
    boolean useAsDefault;
    int createTime;
    int updateTime;

    public int getCu() {
        return cu;
    }

    public int getCreateTime() {
        return createTime;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public boolean isUseAsDefault() {
        return useAsDefault;
    }

    public SqlInstance(String name, int cu, boolean useAsDefault, int createTime, int updateTime) {
        this.name = name;
        this.cu = cu;
        this.useAsDefault = useAsDefault;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    public SqlInstance(){

    }
    public void fromJson(JSONObject object) throws LogException{
        try{
            name = object.getString("name");
            cu = object.getInteger("cu");
            createTime = object.getInteger("createTime");
            updateTime = object.getInteger("updateTime");
            if(object.containsKey("useAsDefault")) {
                this.useAsDefault = object.getBoolean("useAsDefault");
            }
        }
        catch (JSONException e) {
            throw new LogException("failed to generate sql instance", e.getMessage(),e,"");
        }
    }

    public String getName() {
        return name;
    }
}
