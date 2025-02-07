package com.aliyun.openservices.log.sample;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.GroupAttribute;
import com.aliyun.openservices.log.common.Machine;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.MachineList;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ApplyConfigToMachineGroupResponse;
import com.aliyun.openservices.log.response.CreateMachineGroupResponse;
import com.aliyun.openservices.log.response.DeleteMachineGroupResponse;
import com.aliyun.openservices.log.response.GetMachineGroupResponse;
import com.aliyun.openservices.log.response.ListMachineGroupResponse;
import com.aliyun.openservices.log.response.ListMachinesResponse;
import com.aliyun.openservices.log.response.RemoveConfigFromMachineGroupResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupMachineResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupResponse;

public class MachineGroupSample {
    private Client client;
    private String project = "test-project";
    private String testMachineGroupName = "test-machine-group";

    MachineGroupSample() {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";

        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);

    }

    public static void main(String[] args) {
        MachineGroupSample sample = new MachineGroupSample();
        // create machine group for an existing project,
        // to create a logstore, see file LogStoreSample.java

        sample.CreateMachineGroup();
        sample.UpdateMachineGroup();
        sample.GetMachineGroup();
        sample.AddMachineIntoMachineGroup();
        sample.GetMachineGroup();
        sample.DeleteMachineFromMachineGroup();
        sample.GetMachineGroup();

        sample.ListMachineGroups();
        sample.ListMachines();

        // apply an existing logtail config to machine group,
        // to create a logtail config, see file LogtailConfigSample.java
        String testLogtailConfigName = "test-config";
        sample.ApplyLogtailConfigToMachineGroup(testLogtailConfigName);
        // remove an existing logtail config from machine group
        sample.RemoveLogtailConfigFromMachineGroup(testLogtailConfigName);

        sample.DeleteMachineGroup();
    }

    public void CreateMachineGroup() {
        // Construct machineGroup type1: using ArrayList<Machine> to create machinelist
        String groupType = "";
        String externalName = "testgroup";
        String groupTopic = "testtopic";

        ArrayList<String> machineList = new ArrayList<String>();
        machineList.add("127.0.0.1");
        machineList.add("127.0.0.2");

        MachineGroup group = new MachineGroup(testMachineGroupName, "ip", machineList);
        group.SetGroupType(groupType);
        group.SetExternalName(externalName);
        group.SetGroupTopic(groupTopic);

        try {
            CreateMachineGroupResponse res = client.CreateMachineGroup(project, group);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void UpdateMachineGroup() {
        // Construct machineGroup type2: using JSONArray to create machinelist
        String groupType = "";
        String externalName = "testgroup2";
        String groupTopic = "testtopic2";

        ArrayList<String> machineList = new ArrayList<String>();
        machineList.add("uu_id_1");
        machineList.add("uu_id_2");

        GroupAttribute groupAttribute = new GroupAttribute(externalName, groupTopic);

        MachineGroup group = new MachineGroup(testMachineGroupName, "userdefined", machineList);

        group.SetGroupType(groupType);
        group.SetGroupAttribute(groupAttribute);

        try {
            UpdateMachineGroupResponse res = client.UpdateMachineGroup(project, group);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void AddMachineIntoMachineGroup() {
        // Construct machine list
        ArrayList<String> machineArray = new ArrayList<String>();
        machineArray.add("machine_id_1");
        machineArray.add("machine_id_2");
        MachineList machineList = new MachineList(machineArray);
        try {
            UpdateMachineGroupMachineResponse res = client.AddMachineIntoMahineGroup(project, testMachineGroupName,
                    machineList);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void DeleteMachineFromMachineGroup() {
        // Construct machine list
        // Construct machine list
        ArrayList<String> machineArray = new ArrayList<String>();
        machineArray.add("machine_id_1");
        machineArray.add("machine_id_2");
        MachineList machineList = new MachineList(machineArray);
        try {
            UpdateMachineGroupMachineResponse res = client.DeleteMachineFromMachineGroup(project, testMachineGroupName,
                    machineList);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void GetMachineGroup() {
        try {
            GetMachineGroupResponse res = client.GetMachineGroup(project, testMachineGroupName);
            System.out.println("RequestId:" + res.GetRequestId());
            MachineGroup group = res.GetMachineGroup();
            System.out.println("GroupName:" + group.GetMachineIdentifyType());
            System.out.println("GroupName:" + group.GetGroupName());
            System.out.println("GroupType:" + group.GetGroupType());
            System.out.println("ExternalName:" + group.GetGroupAttribute().GetExternalName());
            System.out.println("GroupTopic:" + group.GetGroupAttribute().GetGroupTopic());

            // Optional get machinelist by json array
            // JSONArray mlRes = res.GetMachineGroup().GetMachineListJSONArray();

            System.out.println("MachineList");
            List<String> mlRes = res.GetMachineGroup().GetMachineList();
            System.out.println("MachineList:" + mlRes.toString());

            System.out.println("CreateTime:" + group.GetCreateTime());
            System.out.println("LastModifyTime:" + group.GetLastModifyTime());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void ListMachineGroups() {
        try {
            ListMachineGroupResponse res = client.ListMachineGroup(project, 0, 3);
            System.out.println("RequestId:" + res.GetRequestId());
            int total = res.GetTotal();
            int size = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("count:" + size);
            System.out.println("GroupName:" + res.GetMachineGroups().toString());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void DeleteMachineGroup() {
        try {
            DeleteMachineGroupResponse res = client.DeleteMachineGroup(project, testMachineGroupName);
            System.out.println("RequestId:" + res.GetRequestId());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void ApplyLogtailConfigToMachineGroup(String testLogtailConfigName) {
        try {
            ApplyConfigToMachineGroupResponse res = client
                    .ApplyConfigToMachineGroup(project, testMachineGroupName,
                            testLogtailConfigName);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void RemoveLogtailConfigFromMachineGroup(String testLogtailConfigName) {
        try {
            RemoveConfigFromMachineGroupResponse res = client
                    .RemoveConfigFromMachineGroup(project,
                            testMachineGroupName, testLogtailConfigName);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ListMachines() {
        try {
            ListMachinesResponse res = client.ListMachines(project, testMachineGroupName, 0, 100);
            System.out.println("RequestId:" + res.GetRequestId());
            System.out.println("return count:" + res.GetCount());
            System.out.println("total count:" + res.GetTotal());
            List<Machine> machines = res.GetMachines();
            for (Machine machine : machines) {
                System.out.println("machine:" + machine.ToJsonString());
            }
        } catch (LogException e) {
            e.printStackTrace();
        }
    }
}
