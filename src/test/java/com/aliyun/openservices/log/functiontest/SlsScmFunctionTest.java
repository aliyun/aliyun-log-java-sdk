package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.ACLPrivileges;
import com.aliyun.openservices.log.common.ApsaraLogConfigInputDetail;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.ConfigInputDetail;
import com.aliyun.openservices.log.common.ConfigOutputDetail;
import com.aliyun.openservices.log.common.Consts.ACLAction;
import com.aliyun.openservices.log.common.Consts.ACLPrivilege;
import com.aliyun.openservices.log.common.DelimiterConfigInputDetail;
import com.aliyun.openservices.log.common.GroupAttribute;
import com.aliyun.openservices.log.common.JsonConfigInputDetail;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.common.StreamLogConfigInputDetail;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetConfigResponse;
import com.aliyun.openservices.log.response.GetMachineGroupResponse;
import com.aliyun.openservices.log.response.ListACLResponse;
import com.aliyun.openservices.log.response.ListConfigResponse;
import com.aliyun.openservices.log.response.ListMachineGroupResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SlsScmFunctionTest extends FunctionTest {
	static private String project = "project-test-scm";
	static private String logStore = "test-java-sdk";
	static private String configName;
	static private String syslogConfigName;
	static private String apsaraConfigName;
	static private String jsonConfigName;
	static private String dilimeterConfigName;
	static private String groupName;
	static private String uuid1;
	static private String uuid2;
	@BeforeClass
	public static void SetupOnce() {
		int timestamp = getNowTimestamp();
		configName = "sdkftconfig" + timestamp;
		syslogConfigName = "syslogConfig" + timestamp;
		apsaraConfigName = "apsaraConfig" + timestamp;
		jsonConfigName = "jsonConfig" + timestamp;
		dilimeterConfigName = "dilimeterConfig" + timestamp;
		
		groupName = "sdkftgroup" + timestamp;
		uuid1 = "uuid0" + timestamp;
		uuid2 = "uuid1" + timestamp;

		LogStore logStoreRes = new LogStore(logStore, 1, 10);
		reCreateLogStore(project, logStoreRes);
	}

    @AfterClass
	public static void CleanUpOnce() {
		try {
			client.DeleteConfig(project, configName);
		} catch (LogException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			client.DeleteMachineGroup(project, groupName);
		} catch (LogException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testSyslogConfigAPI() {
		GetConfigResponse res = null;
		String testConfigName = syslogConfigName;
		try {
			Config config = new Config(testConfigName);
			config.SetInputType("syslog");
			StreamLogConfigInputDetail inputDetail = new StreamLogConfigInputDetail();
			inputDetail.SetLocalStorage(true);
			ArrayList<String> shardHashKey = new ArrayList<String>();
			shardHashKey.add("__source__");
			inputDetail.SetShardHashKeyList(shardHashKey);
			
			config.SetInputDetail(inputDetail);
			
			ConfigOutputDetail outputDetail = new ConfigOutputDetail();
			
			outputDetail.SetLogstoreName("perfcounter");
			config.SetOutputDetail(outputDetail);

			client.CreateConfig(project, config);

			res = client.GetConfig(project, testConfigName);
			assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());

			assertEquals("input type does not match", res.GetConfig().GetInputType(), "syslog");
			assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), res.GetConfig().GetInputDetail().GetLocalStorage());
			assertEquals("logstoreName does not match", outputDetail.GetLogstoreName(), res.GetConfig().GetOutputDetail().GetLogstoreName());

		} catch (LogException e) {
            fail(e.getMessage());
		}
	}
	
	private void apsaraConfigAPI(String testConfigName) {
		GetConfigResponse res = null;
		try {
			Config config = new Config(testConfigName);
			ApsaraLogConfigInputDetail inputDetail = new ApsaraLogConfigInputDetail();
			inputDetail.SetLogType("apsara_log");
			inputDetail.SetLogPath("/apsara");
			inputDetail.SetFilePattern("apsara.log");
			inputDetail.SetLocalStorage(true);
			inputDetail.SetTopicFormat("none");
			
			ArrayList<String> filterKey = new ArrayList<String>();
			filterKey.add("number1");
			filterKey.add("seqno1");
			
			ArrayList<String> filterRegex = new ArrayList<String>();
			filterRegex.add("number2");
			filterRegex.add("seqno2");
			inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
			
			config.SetInputDetail(inputDetail);
			
			ConfigOutputDetail outputDetail = new ConfigOutputDetail();
			
			outputDetail.SetLogstoreName("perfcounter");
			config.SetOutputDetail(outputDetail);

			client.CreateConfig(project, config);

			res = client.GetConfig(project, testConfigName);
			assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());

			assertEquals("logType does not match", inputDetail.GetLogType(), ((ApsaraLogConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogType());
			assertEquals("logPath does not match", inputDetail.GetLogPath(), ((ApsaraLogConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogPath());
			assertEquals("filePattern does not match", inputDetail.GetFilePattern(), ((ApsaraLogConfigInputDetail)res.GetConfig().GetInputDetail()).GetFilePattern());
			assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), res.GetConfig().GetInputDetail().GetLocalStorage());
			assertEquals("timeFormat does not match", inputDetail.GetTimeFormat(), ((ApsaraLogConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeFormat());
			assertEquals("topicFormat does not match", inputDetail.GetTopicFormat(), ((ApsaraLogConfigInputDetail)res.GetConfig().GetInputDetail()).GetTopicFormat());
			
			List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
			assertEquals("filterKey size does not match", filterKey.size(), filterKeyRes.size());
			for (int i = 0;i < filterKey.size();i++) {
				assertEquals("filterKey " + i + " does not match", filterKey.get(0), filterKeyRes.get(0));
			}
			
			List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
			assertEquals("filterRegex size does not match", filterRegex.size(), filterRegexRes.size());
			for (int i = 0;i < filterRegex.size();i++) {
				assertEquals("filterRegex " + i + " does not match", filterRegex.get(0), filterRegexRes.get(0));
			}

			assertEquals("logstoreName does not match", outputDetail.GetLogstoreName(), res.GetConfig().GetOutputDetail().GetLogstoreName());
		} catch (LogException e) {
            fail(e.getMessage());
		}
	}

	@Test
	public void testJsonConfigAPI() {
		GetConfigResponse res;
		final String testConfigName = jsonConfigName;
		try {
			Config config = new Config(testConfigName);
			JsonConfigInputDetail inputDetail = new JsonConfigInputDetail();
			inputDetail.SetLogType("json_log");
			inputDetail.SetLogPath("/json");
			inputDetail.SetFilePattern("json.log");
			inputDetail.SetLocalStorage(true);
			inputDetail.SetTopicFormat("none");
			inputDetail.SetTimeKey("time");
			inputDetail.SetTimeFormat("%H");
			
			ArrayList<String> filterKey = new ArrayList<String>();
			filterKey.add("number1");
			filterKey.add("seqno1");
			
			ArrayList<String> filterRegex = new ArrayList<String>();
			filterRegex.add("number2");
			filterRegex.add("seqno2");
			inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
			
			config.SetInputDetail(inputDetail);
			
			ConfigOutputDetail outputDetail = new ConfigOutputDetail();
			
			outputDetail.SetLogstoreName("perfcounter");
			config.SetOutputDetail(outputDetail);

			client.CreateConfig(project, config);

			res = client.GetConfig(project, testConfigName);
			assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());
			
			assertEquals("logType does not match", inputDetail.GetLogType(), ((JsonConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogType());
			assertEquals("logPath does not match", inputDetail.GetLogPath(), ((JsonConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogPath());
			assertEquals("filePattern does not match", inputDetail.GetFilePattern(), ((JsonConfigInputDetail)res.GetConfig().GetInputDetail()).GetFilePattern());
			assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), res.GetConfig().GetInputDetail().GetLocalStorage());
			assertEquals("timeFormat does not match", inputDetail.GetTimeFormat(), ((JsonConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeFormat());
			assertEquals("timeKey does not match", inputDetail.GetTimeKey(), ((JsonConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeKey());
			assertEquals("topicFormat does not match", inputDetail.GetTopicFormat(), ((JsonConfigInputDetail)res.GetConfig().GetInputDetail()).GetTopicFormat());
			
			List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
			assertEquals("filterKey size does not match", filterKey.size(), filterKeyRes.size());
			for (int i = 0;i < filterKey.size();i++) {
				assertEquals("filterKey " + i + " does not match", filterKey.get(0), filterKeyRes.get(0));
			}
			
			List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
			assertEquals("filterRegex size does not match", filterRegex.size(), filterRegexRes.size());
			for (int i = 0;i < filterRegex.size();i++) {
				assertEquals("filterRegex " + i + " does not match", filterRegex.get(0), filterRegexRes.get(0));
			}

			assertEquals("logstoreName does not match", outputDetail.GetLogstoreName(), res.GetConfig().GetOutputDetail().GetLogstoreName());
			
		} catch (LogException e) {
		    e.printStackTrace();
            fail(e.getMessage());
		}
	}

	@Test
	public void testDelimiterConfigAPI() {
		GetConfigResponse res;
		final String testConfigName = dilimeterConfigName;
		try {
			Config config = new Config(testConfigName);
			DelimiterConfigInputDetail inputDetail = new DelimiterConfigInputDetail();
			inputDetail.SetLogPath("/dilimeterPath");
			inputDetail.SetFilePattern("dilimeter.log");
			inputDetail.SetLocalStorage(true);
			inputDetail.SetTopicFormat("none");
			inputDetail.SetSeparator("\t");
			inputDetail.SetQuote(" ");
			ArrayList<String> key = new ArrayList<String>();
			key.add("number");
			key.add("seqno");
			inputDetail.SetKey(key);
			inputDetail.SetTimeKey("");
			inputDetail.SetTimeFormat("%H%m%S");
			
			ArrayList<String> filterKey = new ArrayList<String>();
			filterKey.add("number1");
			filterKey.add("seqno1");
			
			ArrayList<String> filterRegex = new ArrayList<String>();
			filterRegex.add("number2");
			filterRegex.add("seqno2");
			inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
			
			config.SetInputDetail(inputDetail);
			
			ConfigOutputDetail outputDetail = new ConfigOutputDetail();
			
			outputDetail.SetLogstoreName("perfcounter");
			config.SetOutputDetail(outputDetail);

			client.CreateConfig(project, config);

			res = client.GetConfig(project, testConfigName);
			assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());
			
			assertEquals("logType does not match", inputDetail.GetLogType(), ((DelimiterConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogType());
			assertEquals("logPath does not match", inputDetail.GetLogPath(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogPath());
			assertEquals("filePattern does not match", inputDetail.GetFilePattern(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetFilePattern());
			assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), res.GetConfig().GetInputDetail().GetLocalStorage());
			assertEquals("timeFormat does not match", inputDetail.GetTimeFormat(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeFormat());
			assertEquals("timeKey does not match", inputDetail.GetTimeKey(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeKey());
			assertEquals("topicFormat does not match", inputDetail.GetTopicFormat(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetTopicFormat());
			assertEquals("seperator does not match", inputDetail.GetSeparator(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetSeparator());
			assertEquals("quote does not match", inputDetail.GetQuote(), ((DelimiterConfigInputDetail)res.GetConfig().GetInputDetail()).GetQuote());
			
			List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
			assertEquals("filterKey size does not match", filterKey.size(), filterKeyRes.size());
			for (int i = 0;i < filterKey.size();i++) {
				assertEquals("filterKey " + i + " does not match", filterKey.get(0), filterKeyRes.get(0));
			}
			
			List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
			assertEquals("filterRegex size does not match", filterRegex.size(), filterRegexRes.size());
			for (int i = 0;i < filterRegex.size();i++) {
				assertEquals("filterRegex " + i + " does not match", filterRegex.get(0), filterRegexRes.get(0));
			}

			assertEquals("logstoreName does not match", outputDetail.GetLogstoreName(), res.GetConfig().GetOutputDetail().GetLogstoreName());
		
		} catch (LogException e) {
            fail(e.getMessage());
		}
	}
	
	private void configAPI(String testConfigName) {

		GetConfigResponse res = null;
		try {
			Config config = new Config(testConfigName);
			{
				ConfigInputDetail inputDetail = new ConfigInputDetail();
				inputDetail.SetLogType("common_reg_log");
				inputDetail.SetLogPath("/var/log/httpd/");
				inputDetail.SetFilePattern("access.log");
				inputDetail.SetLocalStorage(true);
				inputDetail.SetTimeFormat("%H%m%S");
				inputDetail.SetLogBeginRegex("\\d+");
				inputDetail.SetRegex("(\\d+) (\\d+)");
				inputDetail.SetTopicFormat("none");
				inputDetail.SetMaxDepth(100);
				
				ArrayList<String> key = new ArrayList<String>();
				key.add("number");
				key.add("seqno");
				inputDetail.SetKey(key);
				
				ArrayList<String> filterKey = new ArrayList<String>();
				filterKey.add("number1");
				filterKey.add("seqno1");
				
				ArrayList<String> filterRegex = new ArrayList<String>();
				filterRegex.add("number2");
				filterRegex.add("seqno2");
				inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
				
				config.SetInputDetail(inputDetail);
				
				ConfigOutputDetail outputDetail = new ConfigOutputDetail();
				
				outputDetail.SetLogstoreName("perfcounter");
				config.SetOutputDetail(outputDetail);

				client.CreateConfig(project, config);

				res = client.GetConfig(project, testConfigName);
				assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());
				
				assertEquals("logType does not match", inputDetail.GetLogType(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogType());
				assertEquals("logPath does not match", inputDetail.GetLogPath(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogPath());
				assertEquals("filePattern does not match", inputDetail.GetFilePattern(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetFilePattern());
				assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), res.GetConfig().GetInputDetail().GetLocalStorage());
				assertEquals("timeFormat does not match", inputDetail.GetTimeFormat(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetTimeFormat());
				assertEquals("logBeginRegex does not match", inputDetail.GetLogBeginRegex(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetLogBeginRegex());
				assertEquals("regex does not match", inputDetail.GetRegex(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetRegex());
				assertEquals("topicFormat does not match", inputDetail.GetTopicFormat(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetTopicFormat());
				assertEquals("maxDepth dose not match", inputDetail.GetMaxDepth(), ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetMaxDepth());
				
				List<String> keyRes = ((ConfigInputDetail)res.GetConfig().GetInputDetail()).GetKey();
				assertEquals("key size does not match", key.size(), keyRes.size());
				for (int i = 0;i < key.size();i++) {
					assertEquals("key " + i + " does not match", key.get(0), keyRes.get(0));
				}
				
				List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
				assertEquals("filterKey size does not match", filterKey.size(), filterKeyRes.size());
				for (int i = 0;i < filterKey.size();i++) {
					assertEquals("filterKey " + i + " does not match", filterKey.get(0), filterKeyRes.get(0));
				}
				
				List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
				assertEquals("filterRegex size does not match", filterRegex.size(), filterRegexRes.size());
				for (int i = 0;i < filterRegex.size();i++) {
					assertEquals("filterRegex " + i + " does not match", filterRegex.get(0), filterRegexRes.get(0));
				}
				
				assertEquals("logstoreName does not match", outputDetail.GetLogstoreName(), res.GetConfig().GetOutputDetail().GetLogstoreName());
			}
			
			{
				JSONObject inputDetailJson = new JSONObject();
				inputDetailJson.put("logType", "common_reg_log");
				inputDetailJson.put("logPath", "/var/log/httpd1/");
				inputDetailJson.put("filePattern", "access1.log");
				inputDetailJson.put("localStorage", false);
				inputDetailJson.put("timeFormat", "%h");
				inputDetailJson.put("logBeginRegex", "\\w+");
				inputDetailJson.put("regex", "(\\w+) (\\w+)");
				inputDetailJson.put("topicFormat", "group_topic");
				
				JSONArray keyJsonArray = new JSONArray();
				keyJsonArray.add("xname");
				keyJsonArray.add("xseqno");
				inputDetailJson.put("key", keyJsonArray);
				
				JSONArray filterKeyJsonArray = new JSONArray();
				filterKeyJsonArray.add("xnumber1");
				filterKeyJsonArray.add("xseqno1");
				inputDetailJson.put("filterKey", filterKeyJsonArray);
				
				JSONArray filterRegexJsonArray = new JSONArray();
				filterRegexJsonArray.add("xnumber2");
				filterRegexJsonArray.add("xseqno2");
				inputDetailJson.put("filterRegex", filterRegexJsonArray);
				
				config.SetInputDetail(inputDetailJson.toString());
				
				JSONObject outputDetailJson = new JSONObject();
				outputDetailJson.put("projectName", "ay421");
				outputDetailJson.put("logstoreName", "perfcounter1");
				config.SetOutputDetail(outputDetailJson.toString());
				
				client.UpdateConfig(project, config);
				
				res = client.GetConfig(project, testConfigName);
				assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());
				
				JSONObject inputDetailJsonRes = JSONObject.fromObject(((ConfigInputDetail)res.GetConfig().GetInputDetail()).ToJsonString());
				
				assertEquals("logType does not match", inputDetailJson.getString("logType"), inputDetailJsonRes.getString("logType"));
				assertEquals("logPath does not match", inputDetailJson.getString("logPath"), inputDetailJsonRes.getString("logPath"));
				assertEquals("filePattern does not match", inputDetailJson.getString("filePattern"), inputDetailJsonRes.getString("filePattern"));
				assertEquals("localStorage does not match", inputDetailJson.getBoolean("localStorage"), inputDetailJsonRes.getBoolean("localStorage"));
				assertEquals("timeFormat does not match", inputDetailJson.getString("timeFormat"), inputDetailJsonRes.getString("timeFormat"));
				assertEquals("logBeginRegex does not match", inputDetailJson.getString("logBeginRegex"), inputDetailJsonRes.getString("logBeginRegex"));
				assertEquals("regex does not match", inputDetailJson.getString("regex"), inputDetailJsonRes.getString("regex"));
				assertEquals("topicFormat does not match", inputDetailJson.getString("topicFormat"), inputDetailJsonRes.getString("topicFormat"));
				
				JSONArray keyJsonArrayRes = inputDetailJsonRes.getJSONArray("key");
				assertEquals("key size does not match", keyJsonArray.size(), keyJsonArrayRes.size());
				for (int i = 0;i < keyJsonArray.size();i++) {
					assertEquals("key " + i + " does not match", keyJsonArray.getString(0), keyJsonArrayRes.getString(0));
				}
				
				JSONArray filterKeyJsonArrayRes = inputDetailJsonRes.getJSONArray("filterKey");
				assertEquals("filterKey size does not match", filterKeyJsonArray.size(), filterKeyJsonArrayRes.size());
				for (int i = 0;i < filterKeyJsonArray.size();i++) {
					assertEquals("filterKey " + i + " does not match", filterKeyJsonArray.getString(0), filterKeyJsonArrayRes.getString(0));
				}
				
				JSONArray filterRegexJsonArrayRes = inputDetailJsonRes.getJSONArray("filterRegex");
				assertEquals("filterRegex size does not match", filterRegexJsonArray.size(), filterRegexJsonArrayRes.size());
				for (int i = 0;i < filterRegexJsonArray.size();i++) {
					assertEquals("filterRegex " + i + " does not match", filterRegexJsonArray.getString(0), filterRegexJsonArrayRes.getString(0));
				}
				
				JSONObject outputDetailJsonRes = JSONObject.fromObject(res.GetConfig().GetOutputDetail().ToJsonString());
				//System.out.println(outputDetailJsonRes.toString());
				//assertEquals("projectName does not match"+outputDetailJson.getString("projectName"), outputDetailJson.getString("projectName"), outputDetailJsonRes.getString("projectName"));
				assertEquals("logstoreName does not match", outputDetailJson.getString("logstoreName"), outputDetailJsonRes.getString("logstoreName"));
				
			}
			
			{
				ListConfigResponse listRes = client.ListConfig(project, testConfigName, 0, 500);
				assertEquals("configNum does not match", 1, listRes.GetConfigs().size());
				assertEquals("configName does not match", testConfigName, listRes.GetConfigs().get(0));
			}
		} catch (LogException e) {
            fail(e.getMessage());
		}
	}
	
	private void machineGroupAPI(String testGroupName, String testConfigName) {
		try {
			String groupType = "Armory";
			String externalName = "testgroup";
			String groupTopic = "groupTopic1";
			String machineIdentifyType = "userdefined";
			
			ArrayList<String> machineList = new ArrayList<String>();
			machineList.add(uuid1);
			machineList.add(uuid2);
			
			GroupAttribute groupAttribute = new GroupAttribute(externalName, groupTopic);
			
			MachineGroup group = new MachineGroup(testGroupName, "userdefined", machineList);
			group.SetMachineIdentifyType(machineIdentifyType);
			group.SetGroupType(groupType);
			group.SetGroupAttribute(groupAttribute);
			
			GetMachineGroupResponse res = null;
			
			client.CreateMachineGroup(project, group);
			
			res = client.GetMachineGroup(project, testGroupName);
			assertEquals("group name does not match", testGroupName, res.GetMachineGroup().GetGroupName());
			assertEquals("group type does not match", groupType, res.GetMachineGroup().GetGroupType());
			assertEquals("external name does not match", externalName, res.GetMachineGroup().GetGroupAttribute().GetExternalName());
			assertEquals("group topic does not match", groupTopic, res.GetMachineGroup().GetGroupAttribute().GetGroupTopic());
			
			ArrayList<String> mlRes = res.GetMachineGroup().GetMachineList();
			assertEquals("machineList size does not match", machineList.size(), mlRes.size());
			for (int i = 0;i < machineList.size();i++) {
				assertTrue("machine " + i + " does not match", mlRes.contains(machineList.get(i)));
			}
			
			groupType = "";
			externalName = "testgroup2";
			groupTopic = "groupTopic2";
			machineIdentifyType = "userdefined";
			
			machineList = new ArrayList<String>();
			machineList.add(uuid1);

			group = new MachineGroup(testGroupName, "userdefined", machineList);
			group.SetMachineIdentifyType(machineIdentifyType);
			group.SetGroupType(groupType);
			group.SetExternalName(externalName);
			group.SetGroupTopic(groupTopic);
			
			client.UpdateMachineGroup(project, group);
			
			res = client.GetMachineGroup(project, testGroupName);
			assertEquals("group name does not match", testGroupName, res.GetMachineGroup().GetGroupName());
			assertEquals("group type does not match", groupType, res.GetMachineGroup().GetGroupType());
			assertEquals("external name does not match", externalName, res.GetMachineGroup().GetGroupAttribute().GetExternalName());
			assertEquals("group topic does not match", groupTopic, res.GetMachineGroup().GetGroupAttribute().GetGroupTopic());
			
			ArrayList<String> mlResUpdate = res.GetMachineGroup().GetMachineList();
			assertEquals("machineList size does not match", machineList.size(), mlResUpdate.size());
			for (int i = 0;i < machineList.size();i++) {
				assertTrue("machine " + i + " does not match", mlResUpdate.contains(machineList.get(i)));
			}
			
			ListMachineGroupResponse listRes = client.ListMachineGroup(project, testGroupName, 0, 500);
			assertEquals("machine group num does not match", 1, listRes.GetMachineGroups().size());
			assertEquals("group name does not match", testGroupName, listRes.GetMachineGroups().get(0));
			
			client.ApplyConfigToMachineGroup(project, testGroupName, testConfigName);
			client.RemoveConfigFromMachineGroup(project, testGroupName, testConfigName);
		} catch (LogException e) {
            fail(e.getMessage());
		}
	}

	private void aclAPI() {
		try {
			String principle = "ANONYMOUS";
			
			List<ACLPrivilege> privilegeList = new ArrayList<ACLPrivilege>();
			privilegeList.add(ACLPrivilege.READ);
			ACLPrivileges privileges = new ACLPrivileges(privilegeList);
			
			ACL acl = new ACL(principle, privileges, ACLAction.GRANT);
			
			client.UpdateACL(project, acl);
			
			ListACLResponse listRes = client.ListACL(project);
			assertEquals("acl num does not match", 2, listRes.GetACLs().size());
			boolean checked = false;
			for (ACL aclRes:listRes.GetACLs()) {
				if (aclRes.GetPrinciple().equals(principle)) {
					checked = true;
					assertEquals(principle, aclRes.GetPrinciple());
					assertTrue(aclRes.GetPrivilege().GetPrivileges().contains(ACLPrivilege.READ));
				}
			}
			assertTrue("expected ACL does not exist", checked);
			
			List<ACLPrivilege> privilegeList2 = new ArrayList<ACLPrivilege>();
			privilegeList2.add(ACLPrivilege.READ);
			privilegeList2.add(ACLPrivilege.WRITE);
			ACLPrivileges privileges2 = new ACLPrivileges(privilegeList2);
			
			ACL acl2 = new ACL(principle, privileges2, ACLAction.GRANT);
			
			client.UpdateACL(project, logStore, acl2);
			
			ListACLResponse listRes2 = client.ListACL(project, logStore);
			assertEquals("acl num does not match", 1, listRes2.GetACLs().size());
			System.out.println(listRes2.GetCount());

			assertEquals(principle, listRes2.GetACLs().get(0).GetPrinciple());
			assertTrue(listRes2.GetACLs().get(0).GetPrivilege().GetPrivileges().contains(ACLPrivilege.READ));
			assertTrue(listRes2.GetACLs().get(0).GetPrivilege().GetPrivileges().contains(ACLPrivilege.WRITE));

		} catch (LogException e) {
            fail(e.GetErrorMessage());
		}
	}
	
	private void testReleaseSCM(String testConfigName, String testMachineGroupName){
		try {
			client.DeleteConfig(project, testConfigName);
			client.DeleteConfig(project, syslogConfigName);
			client.DeleteConfig(project, apsaraConfigName);
			client.DeleteConfig(project, jsonConfigName);
			client.DeleteConfig(project, dilimeterConfigName);
		} catch (LogException e) {
            fail(e.getMessage());
		}

		try {
			client.GetConfig(project, testConfigName);
		} catch (LogException e) {
			assertEquals("Error message does not match", "Config " + testConfigName + " not exist", e.getMessage());
		}

		try {
			client.DeleteMachineGroup(project, testMachineGroupName);
		} catch (LogException e) {
            fail(e.getMessage());
		}
		
		try {
			client.GetMachineGroup(project, testMachineGroupName);
		} catch (LogException e) {
			assertEquals("Error message does not match", "MachineGroup " + testMachineGroupName + " not exist", e.getMessage());
		}
	}

	private void listAllProject() {
		try {
			ListProjectResponse response = client.ListProject();
			for (Project project:response.getProjects()) {
				System.out.println(project.ToJsonString());
			}
			System.out.println(response.getCount());
			System.out.println(response.getTotal());
		} catch (LogException e) {
            fail(e.getMessage());
		}
	}
	
	@Test
	public void TestAll() {
		listAllProject();
		aclAPI();
		apsaraConfigAPI(apsaraConfigName);
		configAPI(configName);
		machineGroupAPI(groupName, configName);
		testReleaseSCM(configName, groupName);
	}
}
