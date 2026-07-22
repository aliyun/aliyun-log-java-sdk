package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class JSONFormat extends StructuredDataFormat {

    private boolean skipInvalidRows = false;

    public JSONFormat() {
        super("JSON");
    }

    public boolean getSkipInvalidRows() {
        return skipInvalidRows;
    }

    public void setSkipInvalidRows(boolean skipInvalidRows) {
        this.skipInvalidRows = skipInvalidRows;
    }

    @Override
    @InternalApi
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        skipInvalidRows = JsonUtils.readBool(jsonObject, "skipInvalidRows", false);
    }
}
