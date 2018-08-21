package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class CustomizedFields implements Serializable {
    private DataIntegrity dataIntegrity = new DataIntegrity();
    private LineCount lineCount = new LineCount();

    public CustomizedFields() {

    }

    /**
     * @param dataIntegrity
     *            the data integrity struct
     * @param lineCount
     *            the line count struct
     */
    public CustomizedFields(DataIntegrity dataIntegrity, LineCount lineCount) {
        this.dataIntegrity = dataIntegrity;
        this.lineCount = lineCount;
    }

    public CustomizedFields(CustomizedFields customizedFields) {
        this.dataIntegrity = customizedFields.dataIntegrity;
        this.lineCount = customizedFields.lineCount;
    }

    public JSONObject ToJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_DATAINTEGRITY, dataIntegrity.ToJsonObject());
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LINECOUNT, lineCount.ToJsonObject());

        return jsonObj;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject customizedFields) throws LogException {
        try {
            SetDataIntegrity(customizedFields.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_DATAINTEGRITY));
            SetLineCount(customizedFields.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LINECOUNT));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateCustomizedFields", e.getMessage(),
                    e, "");
        }
    }

    public void FromJsonString(String customizedFieldsString) throws LogException {
        try {
            JSONObject customizedFields = JSONObject.fromObject(customizedFieldsString);
            FromJsonObject(customizedFields);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateCustomizedFields", e.getMessage(),
                    e, "");
        }
    }

    public DataIntegrity GetDataIntegrity() { return dataIntegrity; }

    public void SetDataIntegrity(DataIntegrity dataIntegrity) { this.dataIntegrity = dataIntegrity; }

    public void SetDataIntegrity(JSONObject dataIntegrity) throws LogException {
        try {
            this.dataIntegrity = new DataIntegrity();
            this.dataIntegrity.SetLogTimeReg(dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOG_TIME_REG));
            this.dataIntegrity.SetLogStore(dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE));
            this.dataIntegrity.SetProjectName(dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME));
            this.dataIntegrity.SetSwitch(dataIntegrity.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH));
            this.dataIntegrity.SetTimePos(dataIntegrity.getInt(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_TIME_POS));
        } catch (JSONException e) {
            throw new LogException("FailToSetDataIntegrity", e.getMessage(),
                    e, "");
        }
    }

    public LineCount GetLineCount() {
        return lineCount;
    }

    public void SetLineCount(LineCount lineCount) {
        this.lineCount = lineCount;
    }

    public void SetLineCount(JSONObject lineCount) throws LogException {
        try {
            this.lineCount = new LineCount();
            this.lineCount.SetLogStore(lineCount.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE));
            this.lineCount.SetProjectName(lineCount.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME));
            this.lineCount.SetSwitch(lineCount.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH));
        } catch (JSONException e) {
            throw new LogException("FailToSetLineCount", e.getMessage(),
                    e, "");
        }
    }
}
