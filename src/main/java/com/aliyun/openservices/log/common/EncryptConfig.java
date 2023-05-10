package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class EncryptConfig implements Serializable {

    private String SSEAlgorithm;

    private String KMSDataEncryption;

    private String KMSMasterKeyID;


    public static EncryptConfig FromJsonObject(JSONObject object) {
        String sseAlgorithm = object.getString("SSEAlgorithm");
        String kmsDataEncryption = object.getString("KMSDataEncryption");
        String kmsMasterKeyID = object.getString("KMSMasterKeyID");
        EncryptConfig detail = new EncryptConfig();
        detail.setSSEAlgorithm(sseAlgorithm);
        detail.setKMSDataEncryption(kmsDataEncryption);
        detail.setKMSMasterKeyID(kmsMasterKeyID);
        return detail;
    }


    public JSONObject ToJsonObject() {
        JSONObject encryptConf = new JSONObject();
        encryptConf.put("SSEAlgorithm", getSSEAlgorithm());
        encryptConf.put("KMSDataEncryption", getKMSDataEncryption());
        encryptConf.put("KMSMasterKeyID", getKMSMasterKeyID());
        return encryptConf;
    }

    public EncryptConfig() {}

    public EncryptConfig(String SSEAlgorithm, String KMSDataEncryption, String KMSMasterKeyID) {
        this.SSEAlgorithm = SSEAlgorithm;
        this.KMSDataEncryption = KMSDataEncryption;
        this.KMSMasterKeyID = KMSMasterKeyID;
    }

    public String getSSEAlgorithm() {
        return SSEAlgorithm;
    }

    public void setSSEAlgorithm(String SSEAlgorithm) {
        this.SSEAlgorithm = SSEAlgorithm;
    }

    public String getKMSDataEncryption() {
        return KMSDataEncryption;
    }

    public void setKMSDataEncryption(String KMSDataEncryption) {
        this.KMSDataEncryption = KMSDataEncryption;
    }

    public String getKMSMasterKeyID() {
        return KMSMasterKeyID;
    }

    public void setKMSMasterKeyID(String KMSMasterKeyID) {
        this.KMSMasterKeyID = KMSMasterKeyID;
    }

}