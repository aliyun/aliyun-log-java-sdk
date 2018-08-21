package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class LineCount implements Serializable {
    private String logStore = "";
    private String projectName = "";
    private boolean functionSwitch = false;

    public LineCount() {

    }

    /**
     * @param logStore
     *            the log store used to store line count information
     * @param projectName
     *            the project used to store line count information
     * @param functionSwitch
     *            the switch which determines whether to enable line count function
     */
    public LineCount(String logStore, String projectName, boolean functionSwitch) {
        this.logStore = logStore;
        this.projectName = projectName;
        this.functionSwitch = functionSwitch;
    }

    public LineCount(LineCount lineCount) {
        this.logStore = lineCount.logStore;
        this.projectName = lineCount.projectName;
        this.functionSwitch = lineCount.functionSwitch;
    }

    public JSONObject ToJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE, logStore);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME, projectName);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH, functionSwitch);

        return jsonObj;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject lineCount) throws LogException {
        try {
            this.logStore = lineCount.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE);
            this.projectName = lineCount.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME);
            this.functionSwitch = lineCount.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateLineCount", e.getMessage(),
                    e, "");
        }
    }

    public void FromJsonString(String lineCountString) throws LogException {
        try {
            JSONObject lineCount = JSONObject.fromObject(lineCountString);
            FromJsonObject(lineCount);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateLineCount", e.getMessage(),
                    e, "");
        }
    }

    public String GetLogStore() {
        return logStore;
    }

    public void SetLogStore(String logStore) {
        this.logStore = logStore;
    }

    public String GetProjectName() {
        return projectName;
    }

    public void SetProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean GetSwitch() {
        return functionSwitch;
    }

    public void SetSwitch(boolean functionSwitch) {
        this.functionSwitch = functionSwitch;
    }
}
