package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import com.aliyun.openservices.log.exception.LogException;

/**
 * Created by 冷倾(qingdao.pqd) on 2019/05/28
 *
 * @author <a href="mailto:qingdao.pqd@alibaba-inc.com">lengqing(kingdompan)</a>
 * @date 2019/05/28
 */
public class Advanced {

    @JSONField(name = "force_multiconfig", alternateNames = {"force_multiconfig"})
    private boolean forceMulticonfig = false;

    public final static String KEY_NAME = "force_multiconfig";

    public Advanced() {}

    public Advanced(boolean forceMulticonfig) {
        this.forceMulticonfig = forceMulticonfig;
    }

    public boolean isForceMulticonfig() {
        return forceMulticonfig;
    }

    public void setForceMulticonfig(boolean forceMulticonfig) {
        this.forceMulticonfig = forceMulticonfig;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("force_multiconfig", this.forceMulticonfig);
        return jsonObj;
    }

    public static Advanced fromJsonObject(JSONObject advanced) throws LogException {
        try {
            if (advanced.containsKey(KEY_NAME)) {
                return new Advanced(advanced.getBoolean(KEY_NAME));
            }
            return new Advanced(false);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateAdvanced", e.getMessage(), e, "");
        }
    }

}
