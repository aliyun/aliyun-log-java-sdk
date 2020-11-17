package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

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
    private Long freeTtl = 0l;
    private String productType = "";

    public Long getFreeTtl() {
		return freeTtl;
	}

	public void setFreeTtl(Long freeTtl) {
		this.freeTtl = freeTtl;
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

    public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	@Override
    public JSONObject ToJsonObject() {
        JSONObject jsonObj = ToRequestJson();
        jsonObj.put("productType", productType);
        JSONArray operatingAccountJson = new JSONArray();
        operatingAccountJson.addAll(operatingAccount);
        jsonObj.put("operatingAccount", operatingAccountJson);

        JSONArray restrictedActionJson = new JSONArray();
        restrictedActionJson.addAll(restrictedAction);
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
        freeCreditJson.put("ttl", getFreeTtl());
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
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);

            if (dict.containsKey("freeCredit")) {
                JSONObject freeCredit = dict.getJSONObject("freeCredit");
                if (freeCredit.containsKey("paidAccount"))
                    setPaidAccount(freeCredit.getString("paidAccount"));
                if (freeCredit.containsKey("allFree"))
                    setAllFree(freeCredit.getBoolean("allFree"));
                if (freeCredit.containsKey("readCount"))
                    setReadCount(freeCredit.getLong("readCount"));
                if (freeCredit.containsKey("writeCount"))
                    setWriteCount(freeCredit.getLong("writeCount"));
                if (freeCredit.containsKey("inflowSize"))
                    setInflowSize(freeCredit.getLong("inflowSize"));
                if (freeCredit.containsKey("outflowSize"))
                    setOutflowSize(freeCredit.getLong("outflowSize"));
                if (freeCredit.containsKey("indexSize"))
                    setIndexSize(freeCredit.getLong("indexSize"));
                if (freeCredit.containsKey("shardSize"))
                    setShardSize(freeCredit.getLong("shardSize"));
                if (freeCredit.containsKey("ttl"))
                    setFreeTtl(freeCredit.getLong("ttl"));
            }

            if (dict.containsKey("restrictedAction")) {
                JSONArray restrictedActionArray = dict.getJSONArray("restrictedAction");
                for (int index = 0; index < restrictedActionArray.size(); index++) {
                    restrictedAction.add(restrictedActionArray.getString(index));
                }
            }

            if (dict.containsKey("operatingAccount")) {
                JSONArray operatingAccountArray = dict.getJSONArray("operatingAccount");
                for (int index = 0; index < operatingAccountArray.size(); index++) {
                    operatingAccount.add(operatingAccountArray.getString(index));
                }
            }
            
            if (dict.containsKey("productType")) {
            	setProductType(dict.getString("productType"));
            }

        } catch (LogException e) {
            throw new LogException("FailToGenerateInternalLogStore", e.getMessage(), e, "");
        }
    }
}
