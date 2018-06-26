package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.ACLPrivileges;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.ConfigInputDetail;
import com.aliyun.openservices.log.common.ConfigOutputDetail;
import com.aliyun.openservices.log.common.Consts.ACLAction;
import com.aliyun.openservices.log.common.Consts.ACLPrivilege;
import com.aliyun.openservices.log.common.GroupAttribute;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.common.IndexLine;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Machine;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.MachineList;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ApplyConfigToMachineGroupResponse;
import com.aliyun.openservices.log.response.CreateConfigResponse;
import com.aliyun.openservices.log.response.CreateIndexResponse;
import com.aliyun.openservices.log.response.CreateLogStoreResponse;
import com.aliyun.openservices.log.response.CreateMachineGroupResponse;
import com.aliyun.openservices.log.response.DeleteConfigResponse;
import com.aliyun.openservices.log.response.DeleteIndexResponse;
import com.aliyun.openservices.log.response.DeleteLogStoreResponse;
import com.aliyun.openservices.log.response.DeleteMachineGroupResponse;
import com.aliyun.openservices.log.response.GetConfigResponse;
import com.aliyun.openservices.log.response.GetIndexResponse;
import com.aliyun.openservices.log.response.GetMachineGroupResponse;
import com.aliyun.openservices.log.response.ListACLResponse;
import com.aliyun.openservices.log.response.ListConfigResponse;
import com.aliyun.openservices.log.response.ListMachineGroupResponse;
import com.aliyun.openservices.log.response.ListMachinesResponse;
import com.aliyun.openservices.log.response.RemoveConfigFromMachineGroupResponse;
import com.aliyun.openservices.log.response.UpdateACLResponse;
import com.aliyun.openservices.log.response.UpdateConfigResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupMachineResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ScmSampleClient {
	private String endPoint;
	private String akId;
	private String ak;
	private Client client;
	
	private String testConfigName;
	private String testMachineGroupName;
	private String project;
	private String logstore;
	
	public ScmSampleClient() {
		akId = "test_accessKeyId";
		ak = "test_accessKey";

		
		endPoint = "";
		client = new Client(endPoint, akId, ak);
		
		project = "ali-sdk-test";
		logstore = "sdk-test";
		testConfigName = "sdk-sample-config";
		testMachineGroupName = "sdk-sample-group";
	}
	
	public void CreateLogStore()
	{
		try {
			LogStore store = new LogStore(this.logstore, 1, 10);
			CreateLogStoreResponse res = client.CreateLogStore(project, store);
			System.out.println(res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	
	public void DeleteLogStore()
	{
		try {
			DeleteLogStoreResponse res = client.DeleteLogStore(project, logstore);
			System.out.println(res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	
	public void CreateConfig() {
		Config config = new Config(testConfigName);
		//construct config type 1: using ConfigInputDetail and ConfigOutputDetail
		
		ConfigInputDetail inputDetail = new ConfigInputDetail();
		inputDetail.SetLogType("common_reg_log");
		inputDetail.SetLogPath("/var/log/httpd/");
		inputDetail.SetFilePattern("access.log");
		inputDetail.SetLocalStorage(true);
		inputDetail.SetTimeFormat("%H%m%S");
		inputDetail.SetLogBeginRegex("\\d+");
		inputDetail.SetRegex("(\\d+) (\\d+)");
		//TopicFormat:none, group_topic, default, using regex
		inputDetail.SetTopicFormat("group_topic"); // using group topic
		
		ArrayList<String> key = new ArrayList<String>();
		key.add("number");
		key.add("seqno");
		inputDetail.SetKey(key);
		
		ArrayList<String> filterKey = new ArrayList<String>();
		filterKey.add("number1");
		filterKey.add("seqno1");
		
		ArrayList<String> filterRegex = new ArrayList<String>();
		filterRegex.add("123-*");
		filterRegex.add("abc-*");
		
		inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
		
		config.SetInputDetail(inputDetail);
		
		ConfigOutputDetail outputDetail = new ConfigOutputDetail();
		
		outputDetail.SetEndpoint("cn-hangzhou-for-sample.sls.aliyuncs.com");
		outputDetail.SetLogstoreName("perfcounter");
		config.SetOutputDetail(outputDetail);
		
		try {
			CreateConfigResponse res = client.CreateConfig(project, config);
			
			System.out.println(res.GetAllHeaders().toString());
			System.out.println("RequestId:" + res.GetRequestId());
			Thread.sleep(100);
		} catch (LogException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	public void UpdateConfig() {
		Config config = new Config(testConfigName);
		JSONObject inputDetail = new JSONObject();
		inputDetail.put("logType", "apsara_log");
		inputDetail.put("logPath", "/var/log/httpd1/");
		inputDetail.put("filePattern", "access1.log");
		inputDetail.put("localStorage", false);
		inputDetail.put("timeFormat", "%h");
		inputDetail.put("logBeginRegex", "\\w+");
		inputDetail.put("regex", "(\\w+) (\\w+)");
		inputDetail.put("topicFormat", "none");
		
		JSONArray key = new JSONArray();
		key.add("name3");
		key.add("seqno3");
		inputDetail.put("key", key);
		
		JSONArray filterKey = new JSONArray();
		inputDetail.put("filterKey", filterKey);
		
		JSONArray filterRegex = new JSONArray();
		inputDetail.put("filterRegex", filterRegex);
		
		try {
			config.SetInputDetail(inputDetail);
		} catch (LogException e) {
			e.printStackTrace();
		}
		
		JSONObject outputDetail = new JSONObject();
		outputDetail.put("projectName", "ay421");
		outputDetail.put("logstoreName", "perfcounter1");
		try {
			config.SetOutputDetail(outputDetail);
		} catch (LogException e) {
			e.printStackTrace();
		}
		
		try {
			UpdateConfigResponse res = client.UpdateConfig(project, config);
			System.out.println("RequestId:" + res.GetRequestId());
			Thread.sleep(100);
		} catch (LogException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void GetConfig() {
		try {
			GetConfigResponse res = client.GetConfig(project, testConfigName);
			System.out.println("RequestId:" + res.GetRequestId());
			Config config = res.GetConfig();
			System.out.println("ConfigName:" + config.GetConfigName());
			
			//Optional get inputDetail by json object
			//JSONObject inputDetail = ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).ToJson();
			
			System.out.println("logType:" + ((ConfigInputDetail) res.GetConfig().GetInputDetail()).GetLogType());
			System.out.println("logPath:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogPath());
			System.out.println("filePattern:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetFilePattern());
			System.out.println("localStorage:" + res.GetConfig().GetInputDetail().GetLocalStorage());
			System.out.println("timeFormat:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetTimeFormat());
			System.out.println("logBeginRegex:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogBeginRegex());
			System.out.println("regex:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetRegex());
			System.out.println("topicFormat:" + ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetTopicFormat());
			
			List<String> keyRes = ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetKey();
			System.out.println("key");
			for (String key:keyRes) {
				System.out.println(key);
			}
			
			List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
			System.out.println("filterKey");
			for (String filterKey:filterKeyRes) {
				System.out.println(filterKey);
			}
			
			List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
			System.out.println("filterRegex");
			for (String filterRegex:filterRegexRes) {
				System.out.println(filterRegex);
			}
			
			//Optional get outputDetail by json object
			//JSONObject outputDetail = res.GetConfig().GetOutputDetail().ToJson();
			
			
			System.out.println("OutputDetail Endpoint:" + config.GetOutputDetail().GetEndpoint());
			System.out.println("OutputDetail LogStoreName:" + config.GetOutputDetail().GetLogstoreName());
			
			//System.out.println("CreateTime:" + config.GetCreateTime());
			//System.out.println("LastModifyTime:" + config.GetLastModifyTime());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	
	public void DeleteConfig() {

		try {
			DeleteConfigResponse res = client.DeleteConfig(project, testConfigName);
			System.out.println("RequestId:" + res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	
	public void ListConfigs() {
		try {
			long s_t = System.currentTimeMillis();
			ListConfigResponse res = client.ListConfig(project);
			long e_t = System.currentTimeMillis();
			System.out.print("ms:" + (e_t - s_t));
			System.out.println("RequestId:" + res.GetRequestId());
			int total = res.GetTotal();
			int cout = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Count:" + cout);
			System.out.println("ConfigNames:" + res.GetConfigs().toString());
			// add config name filter
			res = client.ListConfig(project, "nonexist", 0, 100);
			e_t = System.currentTimeMillis();
			System.out.print("ms:" + (e_t - s_t));
			System.out.println("RequestId:" + res.GetRequestId());
			total = res.GetTotal();
			cout = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Count:" + cout);
			System.out.println("ConfigNames:" + res.GetConfigs().toString());
			res = client.ListConfig(project, testConfigName, 0, 100);
			e_t = System.currentTimeMillis();
			System.out.print("ms:" + (e_t - s_t));
			System.out.println("RequestId:" + res.GetRequestId());
			total = res.GetTotal();
			cout = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Count:" + cout);
			System.out.println("ConfigNames:" + res.GetConfigs().toString());
			res = client.ListConfig(project, testConfigName, "perfcounter", 0, 100);
			e_t = System.currentTimeMillis();
			System.out.print("ms:" + (e_t - s_t));
			System.out.println("RequestId:" + res.GetRequestId());
			total = res.GetTotal();
			cout = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Count:" + cout);
			System.out.println("ConfigNames:" + res.GetConfigs().toString());
			res = client.ListConfig(project, testConfigName, "perfcounter1", 0, 100);
			e_t = System.currentTimeMillis();
			System.out.print("ms:" + (e_t - s_t));
			System.out.println("RequestId:" + res.GetRequestId());
			total = res.GetTotal();
			cout = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Count:" + cout);
			System.out.println("ConfigNames:" + res.GetConfigs().toString());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}

	public void UpdateACL() {
		String principleId = "ANONYMOUS"; // or AliUID
		ACLPrivileges privileges = new ACLPrivileges();
		privileges.AddPrivilege(ACLPrivilege.READ);
		privileges.AddPrivilege(ACLPrivilege.WRITE);
		privileges.AddPrivilege(ACLPrivilege.LIST); 

		ACL acl = new ACL(principleId, privileges, ACLAction.GRANT);

		try {
			UpdateACLResponse res = client.UpdateACL(project, acl);

			System.out.println("RequestId:" + res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
		
		ACLPrivileges privileges_revoke = new ACLPrivileges();
		privileges_revoke.AddPrivilege(ACLPrivilege.WRITE);
		acl = new ACL(principleId, privileges_revoke, ACLAction.REVOKE);
		
		try {
			UpdateACLResponse res = client.UpdateACL(project, acl);

			System.out.println("RequestId:" + res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
		
	}


	public void ListACLs() {
		try {
			ListACLResponse res = client.ListACL(project);
			System.out.println("RequestId:" + res.GetRequestId());
			int total = res.GetTotal();
			int size = res.GetCount();
			System.out.println("total:" + total);
			System.out.println("Size:" + size);


			System.out.println("acls:" + res.GetACLs().toString());
			for (ACL acl : res.GetACLs()) {
				System.out.println("Priciple" + acl.GetPrinciple());
				System.out.println("Privilege"
						+ acl.GetPrivilege().ToJsonString());
			}
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	
	public void CreateMachineGroup() {
		//Construct machineGroup type1: using ArrayList<Machine> to create machinelist
		String groupType = "Armory";
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
		//Construct machineGroup type2: using JSONArray to create machinelist
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
			UpdateMachineGroupMachineResponse res = client.AddMachineIntoMahineGroup(project, testMachineGroupName, machineList);
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
			UpdateMachineGroupMachineResponse res = client.DeleteMachineFromMachineGroup(project, testMachineGroupName, machineList);
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
			
			//Optional get machinelist by json array
			//JSONArray mlRes = res.GetMachineGroup().GetMachineListJSONArray();
			
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
			System.out.println("GroupName:" +  res.GetMachineGroups().toString());
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
	

	
	public void ApplyConfigToMachineGroup() {
		try {
			ApplyConfigToMachineGroupResponse res = client
					.ApplyConfigToMachineGroup(project, testMachineGroupName,
							testConfigName);
			System.out.println("RequestId:" + res.GetRequestId());
			Thread.sleep(100);
		} catch (LogException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void RemoveConfigFromMachineGroup() {
		try {
			RemoveConfigFromMachineGroupResponse res = client
					.RemoveConfigFromMachineGroup(project,
							testMachineGroupName, testConfigName);
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
			for (Machine machine :machines)
			{
				System.out.println("machine:" + machine.ToJsonString());
			}
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	

	
	public void CreateIndex() {
		try {
			IndexKeys keys = new IndexKeys();
			IndexKey key = new IndexKey(new ArrayList<String>(Arrays.asList(
					"  ", " ", ",")), false);
			keys.AddKey("key_1", key);
			IndexLine line = new IndexLine(new ArrayList<String>(Arrays.asList(
					"\t", "\n")), true);
			line.SetIncludeKeys(new ArrayList<String>(Arrays.asList("key_3", "key_4")));
			Index index = new Index(7, keys, line);
			CreateIndexResponse res = client.CreateIndex(project, logstore,
					index);

			System.out.println(res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}

	}
	
	public void GetIndex() {
		try {

			GetIndexResponse res = client.GetIndex(project, logstore);

			System.out.println(res.GetRequestId());
			
			System.out.println("index config :" + res.GetIndex().ToJsonString());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}
	

	public void DeleteIndex() {
		try {

			DeleteIndexResponse res = client.DeleteIndex(project, logstore);

			System.out.println(res.GetRequestId());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}

}

public class ScmSample {
	public static void main(String[] args) {
		// ------------------------SCM API------------------------
		ScmSampleClient sample = new ScmSampleClient();
		sample.CreateLogStore();

		// ------------------------Config------------------------

		sample.CreateConfig();
		sample.UpdateConfig();
		sample.GetConfig();
		sample.ListConfigs();

		// ------------------------MachineGroup------------------------
		sample.CreateMachineGroup();
		sample.UpdateMachineGroup();
		sample.GetMachineGroup();
		sample.AddMachineIntoMachineGroup();
		sample.GetMachineGroup();
		sample.DeleteMachineFromMachineGroup();
		sample.GetMachineGroup();
		
		sample.ListMachineGroups();
		sample.ListMachines();

		sample.ApplyConfigToMachineGroup();
		sample.RemoveConfigFromMachineGroup();

		// ------------------------ACL----------------------------
		sample.UpdateACL();
		sample.ListACLs();

		sample.CreateIndex();
		sample.GetIndex();

		// ------------------------ReleaseAll------------------------
		sample.DeleteIndex();
		sample.DeleteConfig();
		sample.DeleteMachineGroup();
		sample.DeleteLogStore();
	}

}
