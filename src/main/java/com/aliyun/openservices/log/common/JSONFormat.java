package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        skipInvalidRows = JsonUtils.readBool(jsonObject, "skipInvalidRows", false);
    }
}
