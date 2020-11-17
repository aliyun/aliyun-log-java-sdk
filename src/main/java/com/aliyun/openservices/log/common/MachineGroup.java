package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * The config of a machine group
 */
public class MachineGroup implements Serializable {
    private static final long serialVersionUID = -4402651900796187066L;
    protected String groupName = "";
    private String groupType = "";
	private String machineIdentifyType = "";
	private GroupAttribute groupAttribute = new GroupAttribute();
	private ArrayList<String> machineList = new ArrayList<String>();
	private int createTime = 0;
	private int lastModifyTime = 0;

    public MachineGroup() {
    }

    /**
     * Create machine group
     *
     * @param groupName           machine group name
     * @param machineIdentifyType the type of machine list, it only support "ip" and "userdefined"
     * @param machineList         the machine ip list or userdefined id list
     */
    public MachineGroup(String groupName, String machineIdentifyType, ArrayList<String> machineList) {
        super();
        this.groupName = groupName;
        this.machineIdentifyType = machineIdentifyType;
        SetMachineList(machineList);
    }

    /**
     * construct a machine group
     *
     * @param machineGroup the exist machine group
     */
    public MachineGroup(MachineGroup machineGroup) {
        super();
        this.groupName = machineGroup.GetGroupName();
        this.groupType = machineGroup.GetGroupType();
        this.machineIdentifyType = machineGroup.GetMachineIdentifyType();
        SetGroupAttribute(machineGroup.GetGroupAttribute());
        SetMachineList(machineGroup.GetMachineList());
        this.createTime = machineGroup.GetCreateTime();
        this.lastModifyTime = machineGroup.GetLastModifyTime();
    }

    /**
     * @return machine group name
     */
    public String GetGroupName() {
        return groupName;
    }

    /**
     * Set machine group name
     *
     * @param groupName the machine group name
     */
    public void SetGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return machine gorup ip
     */
    public String GetGroupType() {
        return groupType;
    }

    /**
     * Set machine group type
     *
     * @param groupType group type
     */
    public void SetGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * get machine group attribute
     *
     * @return machine group attribute
     */
    public GroupAttribute GetGroupAttribute() {
        return groupAttribute;
    }

    /**
     * Set machine group attribute
     *
     * @param groupAttribute machine group attribute
     */
    public void SetGroupAttribute(GroupAttribute groupAttribute) {
        this.groupAttribute = new GroupAttribute(groupAttribute);
    }

    public String GetMachineIdentifyType() {
        return machineIdentifyType;
    }

    public void SetMachineIdentifyType(String machineIdentifyType) {
        this.machineIdentifyType = machineIdentifyType;
    }

    public ArrayList<String> GetMachineList() {
        return machineList;
    }

    public void SetMachineList(List<String> machineList) {
        this.machineList = new ArrayList<String>(machineList);
    }

    public void SetMachineList(JSONArray machineListJSONArray) {
        machineList = new ArrayList<String>();
        if (machineListJSONArray != null) {
            for (int i = 0; i < machineListJSONArray.size(); i++) {
                String machine = machineListJSONArray.getString(i);
                machineList.add(machine);
            }
        }
    }

    public int GetCreateTime() {
        return createTime;
    }

    public void SetCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int GetLastModifyTime() {
        return lastModifyTime;
    }

    public void SetLastModifyTime(int lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String GetExternalName() {
        return groupAttribute.GetExternalName();
    }

    public void SetExternalName(String externalName) {
        groupAttribute.SetExternalName(externalName);
    }

    public String GetGroupTopic() {
        return groupAttribute.GetGroupTopic();
    }

    public void SetGroupTopic(String groupTopic) {
        groupAttribute.SetGroupTopic(groupTopic);
    }

    private JSONObject ToRequestJson() {
        JSONObject groupDict = new JSONObject();
        groupDict.put("groupName", GetGroupName());
        groupDict.put("groupType", GetGroupType());
        groupDict.put("machineIdentifyType", GetMachineIdentifyType());
        if (groupAttribute != null) {
            groupDict.put("groupAttribute", groupAttribute.ToJsonObject());
        }
        JSONArray machineList = new JSONArray();
        machineList.addAll(GetMachineList());
        groupDict.put("machineList", machineList);
        return groupDict;
    }

    public String ToRequestString() {
        return ToRequestJson().toString();
    }

    public JSONObject ToJsonObject() {
        JSONObject groupDict = ToRequestJson();
        JSONArray machineList = new JSONArray();
        machineList.addAll(GetMachineList());
        groupDict.put("machineList", machineList);
        groupDict.put("createTime", GetCreateTime());
        groupDict.put("lastModifyTime", GetLastModifyTime());
        return groupDict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            String groupName = dict.getString("groupName");
            String machineIdentifyType = dict.getString("machineIdentifyType");
            JSONArray machineList = dict.getJSONArray("machineList");
            SetGroupName(groupName);
            SetMachineIdentifyType(machineIdentifyType);
            SetMachineList(machineList);
            if (dict.containsKey("groupType")) {
                SetGroupType(dict.getString("groupType"));
            }
            if (dict.containsKey("groupAttribute")) {
                JSONObject groupAttributeString = dict.getJSONObject("groupAttribute");
                GroupAttribute groupAttribute = new GroupAttribute();
                groupAttribute.FromJsonObject(groupAttributeString);
                SetGroupAttribute(groupAttribute);
            }
            if (dict.containsKey("createTime")) {
                SetCreateTime(dict.getIntValue("createTime"));
            }
            if (dict.containsKey("lastModifyTime")) {
                SetLastModifyTime(dict.getIntValue("lastModifyTime"));
            }
        } catch (JSONException e) {
            throw new LogException("FailToGenerateMachineGroup", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String machineGroupString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(machineGroupString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateMachineGroup", e.getMessage(), e, "");
        }
    }
}
