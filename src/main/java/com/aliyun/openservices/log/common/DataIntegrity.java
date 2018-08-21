package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class DataIntegrity implements Serializable {
    private String logTimeReg = "";
    private String logStore = "";
    private String projectName = "";
    private boolean functionSwitch = false;
    private int timePos = Consts.CONST_CONFIG_INPUTDETAIL_DATAINTEGRITY_DEFAULT_TIMEPOS;

    public DataIntegrity() {

    }

    /**
     * @param logTimeReg
     *            the regular expression for log time
     * @param logStore
     *            the log store used to store data integrity information
     * @param projectName
     *            the project used to store data integrity information
     * @param functionSwitch
     *            the switch which determines whether to enable data integrity function
     * @param timePos
     *            the position which means the time string begins in one log,
     *            default value is -1, which means we should use regular expression to parse the time string in one log line
     */
    public DataIntegrity(String logTimeReg, String logStore, String projectName, boolean functionSwitch, int timePos) {
        this.logTimeReg = logTimeReg;
        this.logStore = logStore;
        this.projectName = projectName;
        this.functionSwitch = functionSwitch;
        this.timePos = timePos;
    }

    public DataIntegrity(DataIntegrity dataIntegrity) {
        this.logTimeReg = dataIntegrity.logTimeReg;
        this.logStore = dataIntegrity.logStore;
        this.projectName = dataIntegrity.projectName;
        this.functionSwitch = dataIntegrity.functionSwitch;
        this.timePos = dataIntegrity.timePos;
    }

    public JSONObject ToJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOG_TIME_REG, logTimeReg);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE, logStore);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME, projectName);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH, functionSwitch);
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_TIME_POS, timePos);

        return jsonObj;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dataIntegrity) throws LogException {
        try {
            this.logTimeReg = dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOG_TIME_REG);
            this.logStore = dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_LOGSTORE);
            this.projectName = dataIntegrity.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_PROJECT_NAME);
            this.functionSwitch = dataIntegrity.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_SWITCH);
            this.timePos = dataIntegrity.getInt(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS_TIME_POS);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateDataIntegrity", e.getMessage(),
                    e, "");
        }
    }

    public void FromJsonString(String dataIntegrityString) throws LogException {
        try {
            JSONObject dataIntegrity = JSONObject.fromObject(dataIntegrityString);
            FromJsonObject(dataIntegrity);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateDataIntegrity", e.getMessage(),
                    e, "");
        }
    }

    public String GetLogTimeReg() {
        return logTimeReg;
    }

    public void SetLogTimeReg(String logTimeReg) {
        this.logTimeReg = logTimeReg;
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

    public int GetTimePos() {
        return timePos;
    }

    public void SetTimePos(int timePos) {
        this.timePos = timePos;
    }
}
