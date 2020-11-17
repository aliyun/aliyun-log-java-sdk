package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;

/**
 * Created by 冷倾(qingdao.pqd) on 2019/05/28
 *
 * @author <a href="mailto:qingdao.pqd@alibaba-inc.com">lengqing(kingdompan)</a>
 */
public class Advanced {
    private boolean forceMulticonfig = false;
    private ArrayList<String> dirBlacklist = new ArrayList<String>();
    private ArrayList<String> fileNameBlacklist = new ArrayList<String>();
    private ArrayList<String> filePathBlacklist = new ArrayList<String>();
    private JSONObject others = new JSONObject();

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

    public void setOthers(JSONObject others) { this.others = others; }

    public JSONObject getOthers() { return others; }

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

        jsonObj.putAll(others);
        return jsonObj;
    }

    public static Advanced fromJsonObject(JSONObject advanced) throws LogException {
        try {
            Advanced advObj = new Advanced();

            if (advanced.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG)) {
                advObj.setForceMulticonfig(advanced.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG));
                advanced.remove(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG);
            }

            if (advanced.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST)) {
                JSONObject obj = advanced.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST);
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR)) {
                    advObj.setDirBlacklist(fromJSONArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR)));
                }
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME)) {
                    advObj.setFileNameBlacklist(fromJSONArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME)));
                }
                if (obj.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH)) {
                    advObj.setFilePathBlacklist(fromJSONArray(obj.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH)));
                }
                advanced.remove(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST);
            }

            // Keep other key/values.
            advObj.others = advanced;

            return advObj;
        } catch (JSONException e) {
            throw new LogException("FailToGenerateAdvanced", e.getMessage(), e, "");
        }
    }

    private static JSONArray fromArrayList(ArrayList<String> l) {
        JSONArray arr = new JSONArray();
        arr.addAll(l);
        return arr;
    }

    private static ArrayList<String> fromJSONArray(JSONArray a) {
        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < a.size(); i++) {
            l.add(a.getString(i));
        }
        return l;
    }
}
