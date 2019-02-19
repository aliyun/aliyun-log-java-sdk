package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class EtlJob implements Serializable {

    private static final long serialVersionUID = -5159155546207903399L;
    private String jobName;
    private EtlSourceConfig sourceConfig;
    private EtlTriggerConfig triggerConfig;
    private EtlFunctionConfig functionConfig = null;
    private String functionParameter;
    private EtlLogConfig logConfig;
    boolean enable;

    public EtlSourceConfig getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(EtlSourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public EtlTriggerConfig getTriggerConfig() {
        return triggerConfig;
    }

    public void setTriggerConfig(EtlTriggerConfig triggerConfig) {
        this.triggerConfig = triggerConfig;
    }

    public EtlFunctionConfig getFunctionConfig() {
        return functionConfig;
    }

    public void setFunctionConfig(EtlFunctionConfig functionConfig) {
        this.functionConfig = functionConfig;
    }

    public String getFunctionParameter() {
        return functionParameter;
    }

    public void setFunctionParameter(String functionParameter) {
        this.functionParameter = functionParameter;
    }

    public EtlLogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(EtlLogConfig logConfig) {
        this.logConfig = logConfig;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public EtlJob() {
    }

    public EtlJob(String jobName, EtlSourceConfig sourceConfig, EtlTriggerConfig triggerConfig, EtlFunctionConfig functionConfig, String functionParameter, EtlLogConfig logConfig, boolean enable) {

        this.jobName = jobName;
        this.sourceConfig = sourceConfig;
        this.triggerConfig = triggerConfig;
        this.functionConfig = functionConfig;
        this.functionParameter = functionParameter;
        this.logConfig = logConfig;
        this.enable = enable;
    }

    public JSONObject toJsonObject(boolean withJobName, boolean withSourceConfig) throws LogException {
        JSONObject etlJobJson = new JSONObject();

        if (withJobName) {
            etlJobJson.put(Consts.ETL_JOB_NAME, this.jobName);
        }

        if (withSourceConfig) {
            JSONObject sourceConfigJson = new JSONObject();
            sourceConfigJson.put(Consts.ETL_JOB_LOG_LOGSTORE_NAME, this.sourceConfig.getLogstoreName());
            etlJobJson.put(Consts.ETL_JOB_SOURCE_CONFIG, sourceConfigJson);
        }

        JSONObject triggerConfigJson = new JSONObject();
        triggerConfigJson.put(Consts.ETL_JOB_TRIGGER_ROLEARN, this.triggerConfig.getRoleArn());
        triggerConfigJson.put(Consts.ETL_JOB_TRIGGER_INTERVAL, this.triggerConfig.getTriggerInterval());
        triggerConfigJson.put(Consts.ETL_JOB_TRIGGER_MAX_RETRY_TIME, this.triggerConfig.getMaxRetryTime());
        String startingPosition = this.triggerConfig.getStartingPosition();
        if (startingPosition != null) {
            triggerConfigJson.put(Consts.ETL_JOB_TRIGGER_STARTING_POSITION, startingPosition);
            if (startingPosition.equals(Consts.ETL_JOB_TRIGGER_STARTING_POSITION_AT_UNIXTIME)) {
                triggerConfigJson.put(Consts.ETL_JOB_TRIGGER_STARTING_UNIXTIME, this.triggerConfig.getStartingUnixtime());
            }
        }
        etlJobJson.put(Consts.ETL_JOB_TRIGGER_CONFIG, triggerConfigJson);

        JSONObject functionConfigJson = new JSONObject();
        String functionProvider = this.functionConfig.getFunctionProvider();
        functionConfigJson.put(Consts.ETL_JOB_FUNCTION_PROVIDER, functionProvider);
        if (functionProvider.equals(Consts.FUNCTION_PROVIDER_FC)) {
            EtlFunctionFcConfig fcConfig = (EtlFunctionFcConfig)this.getFunctionConfig();
            functionConfigJson.put(Consts.ETL_JOB_FC_ENDPOINT, fcConfig.getEndpoint());
            functionConfigJson.put(Consts.ETL_JOB_FC_ACCOUNT_ID, fcConfig.getAccountId());
            functionConfigJson.put(Consts.ETL_JOB_FC_REGION_NAME, fcConfig.getRegionName());
            functionConfigJson.put(Consts.ETL_JOB_FC_SERVICE_NAME, fcConfig.getServiceName());
            functionConfigJson.put(Consts.ETL_JOB_FC_FUNCTION_NAME, fcConfig.getFunctionName());
        }
        etlJobJson.put(Consts.ETL_JOB_FUNCTION_CONFIG, functionConfigJson);

        try {
            JSONObject fpJsonObj = JSONObject.fromObject(this.functionParameter);
            etlJobJson.put(Consts.ETL_JOB_FUNCTION_PARAMETER, fpJsonObj);
        } catch (JSONException e) {
            throw new LogException("PostBodyInvalid",  e.getMessage(), e, "");
        }

        JSONObject logConfigJson = new JSONObject();
        logConfigJson.put(Consts.ETL_JOB_LOG_ENDPOINT, this.logConfig.getEndpoint());
        logConfigJson.put(Consts.ETL_JOB_LOG_PROJECT_NAME, this.logConfig.getProjectName());
        logConfigJson.put(Consts.ETL_JOB_LOG_LOGSTORE_NAME, this.logConfig.getLogstoreName());
        etlJobJson.put(Consts.ETL_JOB_LOG_CONFIG, logConfigJson);

        etlJobJson.put(Consts.ETL_ENABLE, this.enable);

        return etlJobJson;
    }

    public String toJsonString(boolean withJobName, boolean withSourceConfig) throws LogException {
        return toJsonObject(withJobName, withSourceConfig).toString();
    }

    public void fromJsonObject(JSONObject etljobJson) throws LogException {
        try {
            JSONObject sourceConfigJson = etljobJson.getJSONObject(Consts.ETL_JOB_SOURCE_CONFIG);
            EtlSourceConfig sourceConfig = new EtlSourceConfig(sourceConfigJson.getString(Consts.ETL_JOB_LOG_LOGSTORE_NAME));
            setSourceConfig(sourceConfig);

            JSONObject triggerConfigJson = etljobJson.getJSONObject(Consts.ETL_JOB_TRIGGER_CONFIG);
            EtlTriggerConfig triggerConfig = new EtlTriggerConfig(triggerConfigJson.getString(Consts.ETL_JOB_TRIGGER_ROLEARN),
                    triggerConfigJson.getInt(Consts.ETL_JOB_TRIGGER_INTERVAL), triggerConfigJson.getInt(Consts.ETL_JOB_TRIGGER_MAX_RETRY_TIME));
            if (triggerConfigJson.containsKey(Consts.ETL_JOB_TRIGGER_STARTING_POSITION)) {
                triggerConfig.setStartingPosition(triggerConfigJson.getString(Consts.ETL_JOB_TRIGGER_STARTING_POSITION));
            }
            if (triggerConfigJson.containsKey(Consts.ETL_JOB_TRIGGER_STARTING_UNIXTIME)) {
                triggerConfig.setStartingPosition(triggerConfigJson.getString(Consts.ETL_JOB_TRIGGER_STARTING_UNIXTIME));
            }
            setTriggerConfig(triggerConfig);

            JSONObject functionConfigJson = etljobJson.getJSONObject(Consts.ETL_JOB_FUNCTION_CONFIG);
            String functionProvider = functionConfigJson.getString(Consts.ETL_JOB_FUNCTION_PROVIDER);
            if (functionProvider.equals(Consts.FUNCTION_PROVIDER_FC)) {
                EtlFunctionFcConfig functionConfig = new EtlFunctionFcConfig(functionProvider, functionConfigJson.getString(Consts.ETL_JOB_FC_ENDPOINT),
                        functionConfigJson.getString(Consts.ETL_JOB_FC_ACCOUNT_ID), functionConfigJson.getString(Consts.ETL_JOB_FC_REGION_NAME),
                        functionConfigJson.getString(Consts.ETL_JOB_FC_SERVICE_NAME), functionConfigJson.getString(Consts.ETL_JOB_FC_FUNCTION_NAME));
                setFunctionConfig(functionConfig);
            } else {
                EtlFunctionConfig functionConfig = new EtlFunctionConfig(functionProvider);
                setFunctionConfig(functionConfig);
            }

            JSONObject logConfigJson = etljobJson.getJSONObject(Consts.ETL_JOB_LOG_CONFIG);
            EtlLogConfig logConfig = new EtlLogConfig(logConfigJson.getString(Consts.ETL_JOB_LOG_ENDPOINT),
                    logConfigJson.getString(Consts.ETL_JOB_LOG_PROJECT_NAME), logConfigJson.getString(Consts.ETL_JOB_LOG_LOGSTORE_NAME));
            setLogConfig(logConfig);

            setFunctionParameter(etljobJson.getJSONObject(Consts.ETL_JOB_FUNCTION_PARAMETER).toString());
            setJobName(etljobJson.getString(Consts.ETL_JOB_NAME));
            setEnable(etljobJson.getBoolean(Consts.ETL_ENABLE));

        } catch (JSONException e) {
            throw new LogException("ParseEtlJobFail", e.getMessage(), e, "");
        }
    }

}
