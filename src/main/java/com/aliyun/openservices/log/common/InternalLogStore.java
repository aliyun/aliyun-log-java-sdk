package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class InternalLogStore extends LogStore implements Serializable {

    private static final long serialVersionUID = -3083306672673249080L;
    private ArrayList<String> operatingAccount = new ArrayList<String>();
    private ArrayList<String> restrictedAction = new ArrayList<String>();
    private String paidAccount = "";
    private boolean allFree = false;
    private Long readCount = 0l;
    private Long writeCount = 0l;
    private Long inflowSize = 0l;
    private Long outflowSize = 0l;
    private Long indexSize = 0l;
    private Long shardSize = 0l;
    private Long ttl = 0l;

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public InternalLogStore() {
        super();
    }

    public ArrayList<String> getOperatingAccount() {
        return operatingAccount;
    }

    public void setOperatingAccount(ArrayList<String> operatingAccount) {
        this.operatingAccount = operatingAccount;
    }

    public ArrayList<String> getRestrictedAction() {
        return restrictedAction;
    }

    public void setRestrictedAction(ArrayList<String> restrictedAction) {
        this.restrictedAction = restrictedAction;
    }

    public boolean isAllFree() {
        return allFree;
    }

    public void setAllFree(boolean allFree) {
        this.allFree = allFree;
    }

    public Long getReadCount() {
        return readCount;
    }

    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }

    public Long getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(Long writeCount) {
        this.writeCount = writeCount;
    }

    public Long getInflowSize() {
        return inflowSize;
    }

    public void setInflowSize(Long inflowSize) {
        this.inflowSize = inflowSize;
    }

    public Long getOutflowSize() {
        return outflowSize;
    }

    public void setOutflowSize(Long outflowSize) {
        this.outflowSize = outflowSize;
    }

    public Long getIndexSize() {
        return indexSize;
    }

    public void setIndexSize(Long indexSize) {
        this.indexSize = indexSize;
    }

    public Long getShardSize() {
        return shardSize;
    }

    public void setShardSize(Long shardSize) {
        this.shardSize = shardSize;
    }

    public String getPaidAccount() {

        return paidAccount;
    }

    public void setPaidAccount(String paidAccount) {
        this.paidAccount = paidAccount;
    }

    @Override
    public JSONObject ToJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj = ToRequestJson();

        JSONArray operatingAccountJson = new JSONArray();
        for (String account : operatingAccount) {
            operatingAccountJson.add(account);
        }
        jsonObj.put("operatingAccount", operatingAccountJson);

        JSONArray restrictedActionJson = new JSONArray();
        for (String action : restrictedAction) {
            restrictedActionJson.add(action);
        }
        jsonObj.put("restrictedAction", restrictedActionJson);

        JSONObject freeCreditJson = new JSONObject();
        freeCreditJson.put("paidAccount", getPaidAccount());
        freeCreditJson.put("allFree", isAllFree());
        freeCreditJson.put("readCount", getReadCount());
        freeCreditJson.put("writeCount", getWriteCount());
        freeCreditJson.put("inflowSize", getInflowSize());
        freeCreditJson.put("outflowSize", getOutflowSize());
        freeCreditJson.put("indexSize", getIndexSize());
        freeCreditJson.put("shardSize", getShardSize());
        freeCreditJson.put("ttl", getTtl());
        jsonObj.put("freeCredit", freeCreditJson);

        return jsonObj;
    }

    @Override
    public String ToRequestString() {
        return ToJsonObject().toString();
    }

    @Override
    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.fromObject(logStoreString);
            FromJsonObject(dict);

            if (dict.has("freeCredit")) {
                JSONObject freeCredit = dict.getJSONObject("freeCredit");
                if (freeCredit.has("paidAccount"))
                    setPaidAccount(freeCredit.getString("paidAccount"));
                if (freeCredit.has("allFree"))
                    setAllFree(freeCredit.getBoolean("allFree"));
                if (freeCredit.has("readCount"))
                    setReadCount(freeCredit.getLong("readCount"));
                if (freeCredit.has("writeCount"))
                    setWriteCount(freeCredit.getLong("writeCount"));
                if (freeCredit.has("inflowSize"))
                    setInflowSize(freeCredit.getLong("inflowSize"));
                if (freeCredit.has("outflowSize"))
                    setOutflowSize(freeCredit.getLong("outflowSize"));
                if (freeCredit.has("indexSize"))
                    setIndexSize(freeCredit.getLong("indexSize"));
                if (freeCredit.has("shardSize"))
                    setShardSize(freeCredit.getLong("shardSize"));
                if (freeCredit.has("ttl"))
                    setTtl(freeCredit.getLong("ttl"));
            }

            if (dict.has("restrictedAction")) {
                JSONArray restrictedActionArray = dict.getJSONArray("restrictedAction");
                for (int index = 0; index < restrictedActionArray.size(); index++) {
                    restrictedAction.add(restrictedActionArray.getString(index));
                }
            }

            if (dict.has("operatingAccount")) {
                JSONArray operatingAccountArray = dict.getJSONArray("operatingAccount");
                for (int index = 0; index < operatingAccountArray.size(); index++) {
                    operatingAccount.add(operatingAccountArray.getString(index));
                }
            }

        } catch (LogException e) {
            throw new LogException("FailToGenerateInternalLogStore", e.getMessage(), e, "");
        }
    }
}
