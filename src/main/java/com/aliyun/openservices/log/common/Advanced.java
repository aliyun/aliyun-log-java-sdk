package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Advanced {
    private boolean forceMulticonfig = false;
    private ArrayList<String> dirBlacklist = new ArrayList<String>();
    private ArrayList<String> fileNameBlacklist = new ArrayList<String>();
    private ArrayList<String> filePathBlacklist = new ArrayList<String>();
    private Map<String, Object> others = new HashMap<String, Object>();

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

    public ArrayList<String> getDirBlacklist() {
        return dirBlacklist;
    }

    public void setDirBlacklist(ArrayList<String> dirBlacklist) {
        this.dirBlacklist = dirBlacklist;
    }

    public ArrayList<String> getFileNameBlacklist() {
        return fileNameBlacklist;
    }

    public void setFileNameBlacklist(ArrayList<String> fileNameBlacklist) {
        this.fileNameBlacklist = fileNameBlacklist;
    }

    public ArrayList<String> getFilePathBlacklist() {
        return filePathBlacklist;
    }

    public void setFilePathBlacklist(ArrayList<String> filePathBlacklist) {
        this.filePathBlacklist = filePathBlacklist;
    }

    public void setOthers(Map<String, Object> others) { this.others = others; }

    public Map<String, Object> getOthers() { return others; }

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG, this.forceMulticonfig);

        JSONObject blacklistObj = new JSONObject();
        if (!dirBlacklist.isEmpty()) {
            blacklistObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR, fromArrayList(dirBlacklist));
        }
        if (!fileNameBlacklist.isEmpty()) {
            blacklistObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME, fromArrayList(fileNameBlacklist));
        }
        if (!filePathBlacklist.isEmpty()) {
            blacklistObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH, fromArrayList(filePathBlacklist));
        }
        if (!blacklistObj.isEmpty()) {
            jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST, blacklistObj);
        }

        jsonObj.putAll(JsonCodec.toJsonObject(others));
        return jsonObj;
    }

    @InternalApi
    public static Advanced fromJsonObject(JSONObject advanced) throws LogException {
        try {
            Advanced advObj = new Advanced();
            Map<String, Object> otherFields = JsonCodec.toMap(advanced);

            if (advanced.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG)) {
                advObj.setForceMulticonfig(advanced.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG));
                otherFields.remove(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG);
            }

            if (advanced.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST)) {
                JSONObject obj = advanced.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST);
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR)) {
                    advObj.setDirBlacklist(fromJsonArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR)));
                }
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME)) {
                    advObj.setFileNameBlacklist(fromJsonArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME)));
                }
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH)) {
                    advObj.setFilePathBlacklist(fromJsonArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH)));
                }
                otherFields.remove(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST);
            }

            // Keep other key/values.
            advObj.others = otherFields;

            return advObj;
        } catch (JSONException e) {
            throw new LogException("FailToGenerateAdvanced", e.getMessage(), e, "");
        }
    }

    private static JSONArray fromArrayList(ArrayList<String> l) {
        JSONArray arr = new JSONArray();
        for (String item : l) {
            arr.add(item);
        }
        return arr;
    }

    private static ArrayList<String> fromJsonArray(JSONArray a) {
        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < a.size(); i++) {
            l.add(a.getString(i));
        }
        return l;
    }
}
