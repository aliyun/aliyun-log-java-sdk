package com.aliyun.openservices.log.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.ACLPrivileges;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.ConfigInputDetail;
import com.aliyun.openservices.log.common.ConfigOutputDetail;
import com.aliyun.openservices.log.common.GroupAttribute;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.LZ4Encoder;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.ACLAction;
import com.aliyun.openservices.log.common.Consts.ACLPrivilege;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ApplyConfigToMachineGroupRequest;
import com.aliyun.openservices.log.request.BatchGetLogRequest;
import com.aliyun.openservices.log.request.DeleteConfigRequest;
import com.aliyun.openservices.log.request.DeleteLogStoreRequest;
import com.aliyun.openservices.log.request.DeleteMachineGroupRequest;
import com.aliyun.openservices.log.request.GetConfigRequest;
import com.aliyun.openservices.log.request.GetCursorRequest;
import com.aliyun.openservices.log.request.GetHistogramsRequest;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.request.GetMachineGroupRequest;
import com.aliyun.openservices.log.request.ListACLRequest;
import com.aliyun.openservices.log.request.ListConfigRequest;
import com.aliyun.openservices.log.request.ListMachineGroupRequest;
import com.aliyun.openservices.log.request.ListShardRequest;
import com.aliyun.openservices.log.request.ListTopicsRequest;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.request.RemoveConfigFromMachineGroupRequest;
import com.aliyun.openservices.log.request.Request;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetConfigResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.GetMachineGroupResponse;
import com.aliyun.openservices.log.response.ListACLResponse;
import com.aliyun.openservices.log.response.ListConfigResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListMachineGroupResponse;
import com.aliyun.openservices.log.response.ListShardResponse;
import com.aliyun.openservices.log.response.ListTopicsResponse;
import com.aliyun.openservices.log.response.Response;

/**
 * java sdk unittest
 * 
 * @author bozhi.ch
 * 
 */
public class SlsClientUnitTest {

	private SlsClientMock logClientMock = new SlsClientMock();
	private SlsClientMockSend mock = new SlsClientMockSend("http://mock-sls.aliyun-inc.com", SlsClientTestData.TEST_ACCESS_KEY_ID,
			SlsClientTestData.TEST_ACCESS_KEY);

	@BeforeClass
	public static void SetupOnce() {
	}

	@AfterClass
	public static void CleanUpOnce() {
	}

	// test cases for auxiliary (private) functions

	@Test
	public void TestConstruct() {
		@SuppressWarnings("unused")
		Client test = null;
		test = new Client("http://mock-sls.aliyun-inc.com", SlsClientTestData.TEST_ACCESS_KEY_ID,
				SlsClientTestData.TEST_ACCESS_KEY);
		test = new Client("http://mock-sls.aliyun-inc.com", SlsClientTestData.TEST_ACCESS_KEY_ID,
				SlsClientTestData.TEST_ACCESS_KEY, "");
		test = new Client("https://mock-sls.aliyun-inc.com", SlsClientTestData.TEST_ACCESS_KEY_ID,
				SlsClientTestData.TEST_ACCESS_KEY);
		test = new Client("mock-sls.aliyun-inc.com", SlsClientTestData.TEST_ACCESS_KEY_ID,
				SlsClientTestData.TEST_ACCESS_KEY);
		test = new Client("mock-sls.aliyun-inc.com/", SlsClientTestData.TEST_ACCESS_KEY_ID,
				SlsClientTestData.TEST_ACCESS_KEY);
		
		try {
			test = new Client("10.1.11.12", SlsClientTestData.TEST_ACCESS_KEY_ID,
					SlsClientTestData.TEST_ACCESS_KEY);
		} catch (IllegalArgumentException e) {
			assertEquals("EndpontInvalid", e.getMessage());
		}
	}
	
	@Test
	public void TestGetMd5Value() {
		for (int i = 0; i < SlsClientTestData.TEST_HEX2MD5.length; ++i) {
			assertEquals(
					SlsClientTestData.TEST_HEX2MD5[i][1],
					logClientMock
							.GetMd5Value(Hex2Byte(SlsClientTestData.TEST_HEX2MD5[i][0])));
		}
	}

	@Test
	public void TestBuildUrlParameter() {
		// private String BuildUrlParameter(Map<String, String> paras)
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "log");
		params.put("topic", "topic_1");
		params.put("from", "1300");
		params.put("to", "1400");
		params.put("query", "sls query string");
		params.put("reverse", "true");
		String urlParam = new String(
				"from=1300&query=sls query string&reverse=true&to=1400&topic=topic_1&type=log");
		assertEquals(urlParam, logClientMock.BuildUrlParameter(params));
	}

	@Test
	public void TestGetSignature() {

	}

	@Test
	public void TestExtractJsonArray() {
		// normal case
		JSONObject jObj = JSONObject
				.fromObject(SlsClientTestData.TEST_LIST_LOGSTORE);
		ArrayList<String> logStores = logClientMock.ExtractJsonArray("logstores",
				jObj);
		assertEquals("log_store_1", logStores.get(0));
		assertEquals("log_store_2", logStores.get(1));
	}

	@Test
	public void TestExtractHistograms() {
		// normal case
		JSONArray jObj_1 = 
				JSONArray.fromObject(SlsClientTestData.TEST_HISTOGRAM_DATA_1);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_INCOMPLETE);
		GetHistogramsResponse response_1 = new GetHistogramsResponse(
				headers);
		
		logClientMock.ExtractHistograms(response_1, jObj_1);
		// assertEquals(100, meta.mTotalogNum);
		assertEquals(response_1.GetTotalCount(), 200);
		//assertEquals(response_1.IsCompleted(), false);
		ArrayList<Histogram> his_1 = response_1.GetHistograms();
		assertEquals(his_1.size(), 2);
		assertEquals(his_1.get(0).mCount, 100);
		assertEquals(his_1.get(0).mFromTime, 100);
		assertEquals(his_1.get(0).mToTime, 200);
		assertEquals(his_1.get(0).mIsCompleted, true);
		assertEquals(his_1.get(1).mCount, 100);
		assertEquals(his_1.get(1).mFromTime, 200);
		assertEquals(his_1.get(1).mToTime, 300);
		assertEquals(his_1.get(1).mIsCompleted, false);
		assertEquals(response_1.IsCompleted(), false);

		JSONArray jObj_2 = JSONArray
				.fromObject(SlsClientTestData.TEST_HISTOGRAM_DATA_2);
		headers = new HashMap<String, String>();
		headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
		GetHistogramsResponse response_2 = new GetHistogramsResponse(headers);
		logClientMock.ExtractHistograms(response_2, jObj_2);
		// assertEquals(100, meta.mTotalogNum);
		assertEquals(response_2.GetTotalCount(), 300);
		//assertEquals(response_2.IsCompleted(), true);
		ArrayList<Histogram> his_2 = response_2.GetHistograms();
		assertEquals(his_2.size(), 2);
		assertEquals(his_2.get(0).mCount, 100);
		assertEquals(his_2.get(0).mFromTime, 100);
		assertEquals(his_2.get(0).mToTime, 200);
		assertEquals(his_2.get(0).mIsCompleted, true);
		assertEquals(his_2.get(1).mCount, 200);
		assertEquals(his_2.get(1).mFromTime, 200);
		assertEquals(his_2.get(1).mToTime, 300);
		assertEquals(his_2.get(1).mIsCompleted, true);
		assertEquals(response_2.IsCompleted(), true);
	}

	@Test
	public void TestExtractResponseMessage() {
		// / normal case
		JSONArray jObj_1 = JSONArray
				.fromObject(SlsClientTestData.TEST_LOGS_1);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_INCOMPLETE);
		GetLogsResponse response_1 = new GetLogsResponse(headers);
		logClientMock.ExtractLogs(response_1, jObj_1);
		// assertEquals(100, meta.mTotalogNum);
		assertEquals(response_1.GetCount(), 1);
		assertEquals(response_1.IsCompleted(), false);
		ArrayList<QueriedLog> logs_1 = response_1.GetLogs();
		assertEquals(logs_1.size(), 1);
		QueriedLog queriedLog = logs_1.get(0);
		LogItem logItem = queriedLog.GetLogItem();
		assertEquals(logItem.mLogTime, 100);
		assertEquals(queriedLog.mSource, "10.10.10.10");
		ArrayList<LogContent> contents = logItem.mContents;
		assertEquals(contents.size(), 2);
		assertEquals(contents.get(0).mKey, "key_1");
		assertEquals(contents.get(0).mValue, "value_1");
		assertEquals(contents.get(1).mKey, "key_2");
		assertEquals(contents.get(1).mValue, "value_2");

		JSONArray jObj_2 = JSONArray
				.fromObject(SlsClientTestData.TEST_LOGS_2);
		headers = new HashMap<String, String>();
		headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
		GetLogsResponse response_2 = new GetLogsResponse(headers);
		logClientMock.ExtractLogs(response_2, jObj_2);
		// assertEquals(100, meta.mTotalogNum);
		assertEquals(response_2.GetCount(), 2);
		assertEquals(response_2.IsCompleted(), true);
		ArrayList<QueriedLog> logs_2 = response_2.GetLogs();
		assertEquals(logs_2.size(), 2);
		queriedLog = logs_2.get(0);
		logItem = queriedLog.GetLogItem();
		assertEquals(logItem.mLogTime, 100);
		assertEquals(queriedLog.mSource, "10.10.10.10");
		contents = logItem.mContents;
		assertEquals(contents.size(), 2);
		assertEquals(contents.get(0).mKey, "key_1");
		assertEquals(contents.get(0).mValue, "value_1");
		assertEquals(contents.get(1).mKey, "key_2");
		assertEquals(contents.get(1).mValue, "value_2");

		queriedLog = logs_2.get(1);
		logItem = queriedLog.GetLogItem();
		assertEquals(logItem.mLogTime, 200);
		assertEquals(queriedLog.mSource, "20.20.20.20");
		contents = logItem.mContents;
		assertEquals(contents.size(), 2);
		assertEquals(contents.get(0).mKey, "key_3");
		assertEquals(contents.get(0).mValue, "value_3");
		assertEquals(contents.get(1).mKey, "key_4");
		assertEquals(contents.get(1).mValue, "value_4");
	}

	@Test(expected = LogException.class)
	public void TestErrorCheck() throws LogException {
		JSONObject jObj = JSONObject.fromObject(SlsClientTestData.TEST_ERROR);
		logClientMock.ErrorCheck(jObj);
	}

	@Test
	public void TestParserResponseMessage() {

		// the content of ResponseMessage is null
		ResponseMessage message = new ResponseMessage();
		try {
			logClientMock.ParserResponseMessage(message);
		} catch (Exception e) {
			if (e instanceof LogException) {
				assertEquals("BadResponse", ((LogException) e).GetErrorCode());
			} else {
				assertTrue(e.getMessage(), false);
			}
		}

		// read the content of ResponseMessage error
		message.setContent(new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException();
			}

		});
		try {
			logClientMock.ParserResponseMessage(message);
		} catch (Exception e) {
			if (e instanceof LogException) {
				assertEquals("BadResponse",
						((LogException) e).GetErrorCode());
				// assertTrue(((LogException)e).getMessage().contains("Io exception"));
			} else {
				assertTrue(e.getMessage(), false);
			}
		}

		// the content is an illegal json string
		message.setContent(new ByteArrayInputStream(
				Hex2Byte(SlsClientTestData.TEST_LIST_TOPIC_HEX.substring(1))));
		try {
			logClientMock.ParserResponseMessage(message);
		} catch (Exception e) {
			if (e instanceof LogException) {
				assertEquals("BadResponse",
						((LogException) e).GetErrorCode());
				// assertTrue(((LogException)e).getMessage().contains("not valid json string"));
			} else {
				assertTrue(e.getMessage(), false);
			}
		}

		// normal case
		message.setContent(new ByteArrayInputStream(
				Hex2Byte(SlsClientTestData.TEST_LIST_TOPIC_HEX)));
		message.SetBody(Hex2Byte(SlsClientTestData.TEST_LIST_TOPIC_HEX));
		try {
			JSONObject jObj = logClientMock.ParserResponseMessage(message);
			assertEquals("{\"ListTopic\":[\"Topic1\",\"Topic2\"]}",
					jObj.toString());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestExtractConfigs() {
		JSONArray array = new JSONArray();
		array.add("testconfig1");
		
		JSONObject testObj = new JSONObject();
		testObj.put("configs", array);
		
		try {
			List<String> res = logClientMock.ExtractConfigs(testObj, "");
			
			assertEquals("configNum does not match", array.size(), res.size());
			for (int i = 0;i < array.size();i++) {
				assertEquals("config name does not match", array.getString(i), res.get(i));
			}
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void TestExtractMachineGroups() {
		JSONArray array = new JSONArray();
		array.add("testmachinegroup1");
		
		JSONObject testObj = new JSONObject();
		testObj.put("machinegroups", array);
		
		try {
			List<String> res = logClientMock.ExtractMachineGroups(testObj, "");
			
			assertEquals("machineGroupNum does not match", array.size(), res.size());
			for (int i = 0;i < array.size();i++) {
				assertEquals("group name does not match", array.getString(i), res.get(i));
			}
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestExtractShards() {
		JSONObject obj = new JSONObject();
		obj.put("shardID", 0);
		obj.put("status", "readwrite");
		obj.put("inclusiveBeginKey", "e7000000000000000000000000000000");
		obj.put("exclusiveEndKey", "f7000000000000000000000000000000");
		obj.put("createTime", String.valueOf((new Date().getTime())/1000));
		JSONArray array = new JSONArray();
		array.add(obj);
		
		try {
			ArrayList<Shard> res = logClientMock.ExtractShards(array, "");
			
			assertEquals("shard num does not match", array.size(), res.size());
			for (int i = 0;i < array.size();i++) {
				assertEquals("shard does not match", array.getJSONObject(i).getInt("shardID"), res.get(i).GetShardId());
			}
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestExtractACLs() {
		JSONObject jObj = new JSONObject();
		jObj.put("principle", "test1");
		
		JSONArray p = new JSONArray();
		p.add("READ");
		jObj.put("privilege", p);

		jObj.put("lastModifyTime", 1434520236);
		jObj.put("createTime", 1434520236);
		
		JSONArray array = new JSONArray();
		array.add(jObj);
		
		JSONObject testObj = new JSONObject();
		testObj.put("acls", array);
		
		try {
			List<ACL> res = logClientMock.ExtractACLs(testObj, "");
			
			assertEquals("projectNum does not match", array.size(), res.size());
			for (int i = 0;i < array.size();i++) {
				assertEquals("principle does not match", array.getJSONObject(i).getString("principle"), res.get(i).GetPrinciple());
				assertEquals("privilege does not match", array.getJSONObject(i).getString("privilege"), res.get(i).GetPrivilege().ToJsonString());
				assertEquals("lastModifyTime does not match", array.getJSONObject(i).getInt("lastModifyTime"), res.get(i).GetLastModifyTime());
				assertEquals("createTime does not match", array.getJSONObject(i).getInt("createTime"), res.get(i).GetCreateTime());
			}
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestExtractConfigFromResponse() {
		JSONObject jObj = new JSONObject();
		jObj.put("configName", "testconfig");
		jObj.put("inputType", "file");
		
		JSONObject inputDetail = new JSONObject();
		inputDetail.put("logType", "common_reg_log");
		inputDetail.put("logPath", "/var/log/httpd/");
		inputDetail.put("filePattern", "access.log");
		inputDetail.put("localStorage", true);
		inputDetail.put("timeFormat", "test1");
		inputDetail.put("logBeginRegex", "test2");
		inputDetail.put("regex", "test3");
		inputDetail.put("topicFormat", "none");
		
		JSONArray key = new JSONArray();
		key.add("name");
		key.add("seqno");
		inputDetail.put("key", key);
		
		JSONArray filterRegex = new JSONArray();
		filterRegex.add("x1");
		filterRegex.add("x2");
		inputDetail.put("filterRegex", filterRegex);
		
		JSONArray filterKey = new JSONArray();
		filterKey.add("x3");
		filterKey.add("x4");
		inputDetail.put("filterKey", filterKey);
		
		jObj.put("inputDetail", inputDetail);
		
		jObj.put("outputType", "loghub");
		
		JSONObject outputDetail = new JSONObject();
		outputDetail.put("projectName", "ay42");
		outputDetail.put("logstoreName", "perfcounter");
		jObj.put("outputDetail", outputDetail);
		
		jObj.put("createTime", 12343235);
		jObj.put("lastModifyTime", 12343234);
		
		Config config = null;
		try {
			config = logClientMock.ExtractConfigFromResponse(jObj, "");
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		assertEquals(config.GetConfigName(), "testconfig");
		
		assertEquals(((ConfigInputDetail)(((ConfigInputDetail)(config.GetInputDetail())))).GetLogType(), inputDetail.getString("logType"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetLogPath(), inputDetail.getString("logPath"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetFilePattern(), inputDetail.getString("filePattern"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetLocalStorage(), inputDetail.getBoolean("localStorage"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetTimeFormat(), inputDetail.getString("timeFormat"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetLogBeginRegex(), inputDetail.getString("logBeginRegex"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetRegex(), inputDetail.getString("regex"));
		assertEquals(((ConfigInputDetail)(config.GetInputDetail())).GetTopicFormat(), inputDetail.getString("topicFormat"));
		
		List<String> keyList = ((ConfigInputDetail)(config.GetInputDetail())).GetKey();
		assertEquals(keyList.size(), key.size());
		for (int i = 0;i < keyList.size();i++) {
			assertEquals(keyList.get(i), key.getString(i));
		}
		
		List<String> filterRegexList = ((ConfigInputDetail)(config.GetInputDetail())).GetFilterRegex();
		assertEquals(filterRegexList.size(), filterRegex.size());
		for (int i = 0;i < filterRegexList.size();i++) {
			assertEquals(filterRegexList.get(i), filterRegex.getString(i));
		}
		
		List<String> filterKeyList = ((ConfigInputDetail)(config.GetInputDetail())).GetFilterKey();
		assertEquals(filterKeyList.size(), filterKey.size());
		for (int i = 0;i < filterKeyList.size();i++) {
			assertEquals(filterKeyList.get(i), filterKey.getString(i));
		}
	
		
		assertEquals(false, outputDetail.has("endpoint"));
		assertEquals(config.GetOutputDetail().GetLogstoreName(), outputDetail.getString("logstoreName"));
		
		assertEquals(config.GetCreateTime(), 12343235);
		assertEquals(config.GetLastModifyTime(), 12343234);
	}
	
	@Test
	public void TestExtractMachineGroupFromResponse() {
		JSONObject jObj = new JSONObject();
		jObj.put("groupName", "testgroup");
		jObj.put("groupType", "Armory");
		jObj.put("machineIdentifyType", "uuid");
		
		JSONObject groupAttribute = new JSONObject();
		groupAttribute.put("externalName", "testgroupX");
		groupAttribute.put("groupTopic", "topic");
		jObj.put("groupAttribute", groupAttribute);
		
		JSONArray machineList = new JSONArray();
		String machine1 = "uu94ff0127x";
		String machine2 = "uu14ff0127y";
		machineList.add(machine1);
		machineList.add(machine2);
		jObj.put("machineList", machineList);
		
		jObj.put("createTime", 12343235);
		jObj.put("lastModifyTime", 12343234);
		
		MachineGroup group = null;
		try {
			group = logClientMock.ExtractMachineGroupFromResponse(jObj, "");
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		assertEquals(group.GetGroupName(), "testgroup");
		assertEquals(group.GetGroupType(), "Armory");
		assertEquals(group.GetMachineIdentifyType(), "uuid");
		assertEquals(group.GetExternalName(), "testgroupX");
		assertEquals(group.GetGroupTopic(), "topic");
		
		assertEquals(group.GetMachineList().size(), machineList.size());
		for (int i = 0;i < group.GetMachineList().size();i++) {
			assertEquals(group.GetMachineList().get(i), machineList.getString(i));
		}

		assertEquals(group.GetCreateTime(), 12343235);
		assertEquals(group.GetLastModifyTime(), 12343234);
	}
	
	@Test
	public void TestExtractACLFromResponse() {
		JSONObject jObj = new JSONObject();
		jObj.put("principle", "test1");
		JSONArray p = new JSONArray();
		p.add("READ");
		jObj.put("privilege", p);
		jObj.put("lastModifyTime", 1434520236);
		jObj.put("createTime", 1434520236);

		ACL acl = new ACL();
		try {
			acl = logClientMock.ExtractACLFromResponse(jObj, "");
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		assertEquals(acl.GetPrinciple(), "test1");
		assertEquals(acl.GetPrivilege().ToJsonString(), "[\"READ\"]");
		assertEquals(acl.GetLastModifyTime(), 1434520236);
		assertEquals(acl.GetCreateTime(), 1434520236);
	}
	
	
	@Test
	public void TestParseResponseMessage() {
		ResponseMessage response = new ResponseMessage();
		
		byte[] testBytes = null;
		try {
			testBytes = SlsClientTestData.TEST_RESPONSE_RAW_DATA.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		
		response.setContent(new ByteArrayInputStream(testBytes));
		response.SetBody(testBytes);
		try {
			JSONObject origin = JSONObject.fromObject(SlsClientTestData.TEST_RESPONSE_RAW_DATA);
			JSONObject result = logClientMock.ParserResponseMessage(response, "");
			
			assertEquals(result.getString("projectName"), origin.getString("projectName"));
			assertEquals(result.getString("projectDesc"), origin.getString("projectDesc"));
			assertEquals(result.getString("projectStatus"), origin.getString("projectStatus"));
			assertEquals(result.getJSONObject("quota").getInt("logStream"), origin.getJSONObject("quota").getInt("logStream"));
			assertEquals(result.getJSONObject("quota").getInt("shard"), origin.getJSONObject("quota").getInt("shard"));
			assertEquals(result.getInt("createTime"), origin.getInt("createTime"));
			assertEquals(result.getInt("lastModifyTime"), origin.getInt("lastModifyTime"));
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestParseResponseMessageToArray() {
		ResponseMessage response = new ResponseMessage();
		
		byte[] testBytes = null;
		try {
			testBytes = SlsClientTestData.TEST_RESPONSE_RAW_ARRAY.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		
		response.setContent(new ByteArrayInputStream(testBytes));
		response.SetBody(testBytes);
		try {
			JSONArray origin = JSONArray.fromObject(SlsClientTestData.TEST_RESPONSE_RAW_ARRAY);
			JSONArray result = logClientMock.ParseResponseMessageToArray(response, "");	
			assertEquals(origin.size(), result.size());

			for (int i = 0;i < origin.size();i++) {
				String shardStatusOrigin = origin.getJSONObject(i).getString("shardStatus");
				String shardStatusResult = result.getJSONObject(i).getString("shardStatus");
				int shardIdOrigin = origin.getJSONObject(i).getInt("shardID");
				int shardIdResult = result.getJSONObject(i).getInt("shardID");
				assertEquals(shardStatusOrigin, shardStatusResult);
				assertEquals(shardIdOrigin, shardIdResult);
			}
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	@Test
	public void TestCreateConfig() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testConfigName = "test_config";
		Config config = new Config(testConfigName);
		ConfigInputDetail inputDetail = new ConfigInputDetail();
		inputDetail.SetLogType("common_reg_log");
		inputDetail.SetLogPath("/var/log/httpd/");
		inputDetail.SetFilePattern("access.log");
		inputDetail.SetLocalStorage(true);
		inputDetail.SetTimeFormat("%H%m%S");
		inputDetail.SetLogBeginRegex("\\d+");
		inputDetail.SetRegex("(\\d+) (\\d+)");
		inputDetail.SetTopicFormat("none");
		
		ArrayList<String> key = new ArrayList<String>();
		key.add("number");
		key.add("seqno");
		inputDetail.SetKey(key);
		
		ArrayList<String> filterKey = new ArrayList<String>();
		filterKey.add("number1");
		filterKey.add("seqno1");

		
		ArrayList<String> filterRegex = new ArrayList<String>();
		filterRegex.add("num-123*");
		filterRegex.add("seq-abc*");
		inputDetail.SetFilterKeyRegex(filterKey, filterRegex);
		
		config.SetInputDetail(inputDetail);
		
		ConfigOutputDetail outputDetail = new ConfigOutputDetail();
		
		outputDetail.SetEndpoint("ay42");
		outputDetail.SetLogstoreName("perfcounter");
		config.SetOutputDetail(outputDetail);
		
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.CreateConfig(project, config);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.CreateConfig(project, config);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestUpdateConfig() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testConfigName = "test_config";
		
		Config config = new Config(testConfigName);
		JSONObject inputDetail = new JSONObject();
		
		inputDetail = new JSONObject();
		inputDetail.put("logType", "apsara_log");
		inputDetail.put("logPath", "/var/log/httpd1/");
		inputDetail.put("filePattern", "access1.log");
		inputDetail.put("localStorage", false);
		inputDetail.put("timeFormat", "%h");
		inputDetail.put("logBeginRegex", "\\w+");
		inputDetail.put("regex", "(\\w+) (\\w+)");
		inputDetail.put("topicFormat", "testlog");
		
		JSONArray key = new JSONArray();
		key.add("name3");
		key.add("seqno3");
		inputDetail.put("key", key);
		
		JSONArray filterKey = new JSONArray();
		inputDetail.put("filterKey", filterKey);
		
		JSONArray filterRegex = new JSONArray();
		inputDetail.put("filterRegex", filterRegex);
		
		try {
			config.SetInputDetail(inputDetail.toString());
		} catch (LogException e) {
			e.printStackTrace();
		}
		
		JSONObject outputDetail = new JSONObject();
		outputDetail.put("projectName", "ay421");
		outputDetail.put("logstoreName", "perfcounter1");
		
		String project = "test-project";
		
		try {
			config.SetOutputDetail(outputDetail.toString());
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		try {
			mock.ChangeResponse(response);
			mock.UpdateConfig(project, config);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.UpdateConfig(project, config);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestDeleteConfig() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testConfigName = "test_config";
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.DeleteConfig(project, testConfigName);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.DeleteConfig(project, testConfigName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestGetConfig() {
		String testConfigName = "test_config";
		Config config = new Config(testConfigName);
		ConfigInputDetail inputDetail = new ConfigInputDetail();
		inputDetail.SetLogType("common_reg_log");
		inputDetail.SetLogPath("/var/log/httpd/");
		inputDetail.SetFilePattern("access.log");
		inputDetail.SetLocalStorage(true);
		inputDetail.SetTimeFormat("%H%m%S");
		inputDetail.SetLogBeginRegex("\\d+");
		inputDetail.SetRegex("(\\d+) (\\d+)");
		inputDetail.SetTopicFormat("none");
		
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
		inputDetail.SetFilterKeyRegex(filterKey,filterRegex);
		
		config.SetInputDetail(inputDetail);
		
		ConfigOutputDetail outputDetail = new ConfigOutputDetail();
		
		outputDetail.SetEndpoint("ay42");
		outputDetail.SetLogstoreName("perfcounter");
		config.SetOutputDetail(outputDetail);
		
		JSONObject configDict = new JSONObject();
		configDict.put("configName", config.GetConfigName());
		configDict.put("inputDetail", ((ConfigInputDetail)(config.GetInputDetail())).ToJsonString());
		configDict.put("outputDetail", config.GetOutputDetail().ToJsonString());

		String jsonStr = configDict.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			GetConfigResponse res = mock.GetConfig(project, testConfigName);
			assertEquals("configName does not match", testConfigName, res.GetConfig().GetConfigName());
			
			assertEquals("logType does not match", inputDetail.GetLogType(), ((ConfigInputDetail)(((ConfigInputDetail)(res.GetConfig().GetInputDetail())))).GetLogType());
			assertEquals("logPath does not match", inputDetail.GetLogPath(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogPath());
			assertEquals("filePattern does not match", inputDetail.GetFilePattern(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetFilePattern());
			assertEquals("localStorage does not match", inputDetail.GetLocalStorage(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLocalStorage());
			assertEquals("timeFormat does not match", inputDetail.GetTimeFormat(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetTimeFormat());
			assertEquals("logBeginRegex does not match", inputDetail.GetLogBeginRegex(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetLogBeginRegex());
			assertEquals("regex does not match", inputDetail.GetRegex(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetRegex());
			assertEquals("topicFormat does not match", inputDetail.GetTopicFormat(), ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).GetTopicFormat());
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.GetConfig(project, testConfigName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.GetConfig(project, testConfigName);
		} catch (LogException e) {
			assertEquals("FailToGenerateConfig", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestListConfig() {
		String testConfigName1 = "test_config1";
		String testConfigName2 = "test_config2";
		
		JSONArray testConfigs = new JSONArray();
		testConfigs.add(testConfigName1);
		testConfigs.add(testConfigName2);
		
		JSONObject obj = new JSONObject();
		obj.put("total", 4);
		obj.put("count", 2);
		obj.put("configs", testConfigs);

		String jsonStr = obj.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		String project = "test-proeject";
		
		try {
			mock.ChangeResponse(response);
			mock.ListConfig(project, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			mock.ListConfig(project, testConfigName1, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			ListConfigResponse listRes = mock.ListConfig(project);
			assertEquals("total does not match", 4, listRes.GetTotal());
			assertEquals("size does not match", 2, listRes.GetCount());
			
			assertEquals("configNum does not match", testConfigs.size(), listRes.GetConfigs().size());
			for(int i = 0;i < testConfigs.size();i++) {
				assertEquals("config " + i + " does not match", testConfigs.getString(i), listRes.GetConfigs().get(i));
			}
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListConfig(project, testConfigName1, 0, 1);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListConfig(project, testConfigName1, 0, 1);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
		
		try {
			invalidBody = "{\"total\":\"4\",\"size\":\"2\",\"key\":\"invalid json\"}".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListConfig(project, testConfigName1, 0, 1);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestCreateMachineGroup() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testGroupName = "test_group";
		String machineIdentifyType = "uuid";
		String groupType = "Armory";
		String externalName = "testgroup";
		
		ArrayList<String> machineList = new ArrayList<String>();
		machineList.add("UUID1");
		machineList.add("UUID2");
		
		MachineGroup group = new MachineGroup(testGroupName, "userdefined",  machineList);
		group.SetMachineIdentifyType(machineIdentifyType);
		group.SetGroupType(groupType);
		group.SetExternalName(externalName);
		
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.CreateMachineGroup(project, group);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.CreateMachineGroup(project, group);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestCreateLogStore() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testLogStoreName = "test-logstore";
		int ttl = 1;
		int shardCount = 2;
		LogStore logStore = new LogStore(testLogStoreName, ttl, shardCount);
		
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.CreateLogStore(project, logStore);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.CreateLogStore(project, logStore);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestDeleteLogStore() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testLogStoreName = "test-logstore";
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.DeleteLogStore(project, testLogStoreName);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.DeleteLogStore(project, testLogStoreName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestUpdateMachineGroup() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testGroupName = "test_group";
		String machineIdentifyType = "uuid";
		String groupType = "";
		String externalName = "testgroup2";
		String groupTopic = "groupTopic2";
		
		ArrayList<String> machineList = new ArrayList<String>();
		machineList.add("UUID2");
		
		MachineGroup group = new MachineGroup(testGroupName, "userdefined", machineList);
		group.SetMachineIdentifyType(machineIdentifyType);
		group.SetGroupType(groupType);
		group.SetExternalName(externalName);
		group.SetGroupTopic(groupTopic);
		
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.UpdateMachineGroup(project, group);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.UpdateMachineGroup(project, group);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestDeleteMachineGroup() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String testGroupName = "test_group";
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.DeleteMachineGroup(project, testGroupName);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.DeleteMachineGroup(project, testGroupName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void ApplyConfigToMachineGroup() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String configName = "test_config";
		String testGroupName = "test_group";
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.ApplyConfigToMachineGroup(project, testGroupName, configName);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ApplyConfigToMachineGroup(project, testGroupName, configName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestRemoveConfigFromMachineGroup() {
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		String configName = "test_config";
		String testGroupName = "test_group";
		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			mock.RemoveConfigFromMachineGroup(project, testGroupName, configName);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.RemoveConfigFromMachineGroup(project, testGroupName, configName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestGetMachineGroup() {	
		String testGroupName = "test_group";
		String groupType = "Armory";
		String machineIdentifyType = "uuid";
		String externalName = "testgroup";
		String groupTopic = "groupTopic";
		
		JSONArray machineList = new JSONArray();
		machineList.add("UUID1");
		machineList.add("UUID2");
		
		MachineGroup group = new MachineGroup();
		group.SetMachineIdentifyType(machineIdentifyType);
		group.SetGroupName(testGroupName);
		group.SetGroupType(groupType);
		group.SetExternalName(externalName);
		group.SetGroupTopic(groupTopic);
		group.SetMachineList(machineList);
		
		int lastModifyTime = 12343513;
		int createTime = 13135041;
		group.SetLastModifyTime(lastModifyTime);
		group.SetCreateTime(createTime);
		
		JSONObject groupDict = new JSONObject();
		groupDict.put("groupName", group.GetGroupName());
		groupDict.put("groupType", group.GetGroupType());
		
		JSONObject groupAttributeDict = new JSONObject();
		groupAttributeDict.put("externalName", group.GetGroupAttribute().GetExternalName());
		groupAttributeDict.put("groupTopic", group.GetGroupAttribute().GetGroupTopic());
		groupDict.put("groupAttribute", groupAttributeDict);
		
		groupDict.put("machineIdentifyType", group.GetMachineIdentifyType());
		
		groupDict.put("machineList", machineList);
		
		groupDict.put("lastModifyTime", group.GetLastModifyTime());
		groupDict.put("createTime", group.GetCreateTime());
		
		String jsonStr = groupDict.toString();
		
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);

		String project = "test-project";
		
		try {
			mock.ChangeResponse(response);
			GetMachineGroupResponse res = mock.GetMachineGroup(project, testGroupName);
			assertEquals("group name does not match", testGroupName, res.GetMachineGroup().GetGroupName());
			assertEquals("group type does not match", groupType, res.GetMachineGroup().GetGroupType());
			assertEquals("machineIdentifyType does not match", machineIdentifyType, res.GetMachineGroup().GetMachineIdentifyType());
			assertEquals("external name does not match", externalName, res.GetMachineGroup().GetExternalName());
			assertEquals("group topic does not match", groupTopic, res.GetMachineGroup().GetGroupTopic());
			assertEquals("createTime does not match", createTime, res.GetMachineGroup().GetCreateTime());
			assertEquals("lastModifyTime does not match", lastModifyTime, res.GetMachineGroup().GetLastModifyTime());

			ArrayList<String> mlRes = res.GetMachineGroup().GetMachineList();
			assertEquals("machineList size does not match", machineList.size(), mlRes.size());
			for (int i = 0;i < mlRes.size();i++) {
				assertEquals("machine " + i + " does not match", machineList.getString(i), mlRes.get(i));
			}
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.GetMachineGroup(project, testGroupName);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.GetMachineGroup(project, testGroupName);
		} catch (LogException e) {
			assertEquals("FailToGenerateMachineGroup", e.GetErrorCode());
		}
	}
	@Test
	public void TestListMachineGroup() {
		String testGroupName1 = "test_group1";
		String testGroupName2 = "test_group2";
		
		JSONArray testGroups = new JSONArray();
		testGroups.add(testGroupName1);
		testGroups.add(testGroupName2);
		
		JSONObject obj = new JSONObject();
		obj.put("total", 4);
		obj.put("count", 2);
		obj.put("machinegroups", testGroups);

		String project = "test-project";
		
		String jsonStr = obj.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		
		try {
			mock.ChangeResponse(response);
			mock.ListMachineGroup(project, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			mock.ListMachineGroup(project, testGroupName1, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			ListMachineGroupResponse listRes = mock.ListMachineGroup(project);
			assertEquals("offset does not match", 4, listRes.GetTotal());
			assertEquals("size does not match", 2, listRes.GetCount());
			
			assertEquals("configNum does not match", testGroups.size(), listRes.GetMachineGroups().size());
			for(int i = 0;i < testGroups.size();i++) {
				assertEquals("config " + i + " does not match", testGroups.getString(i), listRes.GetMachineGroups().get(i));
			}
		} catch (LogException e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListMachineGroup(project, testGroupName1, 0, 1);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListMachineGroup(project, testGroupName1, 0, 1);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
		
		try {
			invalidBody = "{\"total\":\"4\",\"count\":\"2\",\"key\":\"invalid json\"}".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListMachineGroup(project, testGroupName1, 0, 1);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestPutLogs() {
		LogContent content1 = new LogContent();
		LogContent content2 = new LogContent();
		LogContent content3 = new LogContent();
		content1.mKey = "key1";
		content1.mValue = "value1";
		
		content2.mKey = "key2";
		content2.mValue = "value2";
		
		content3.mKey = "key3";
		content3.mValue = "value3";
		
		int time1 = 1123431;
		int time2 = 1123432;
		int time3 = 1123433;
		
		LogItem log1 = new LogItem(time1);
		log1.PushBack(content1);
		log1.PushBack(content3);
		
		LogItem log2 = new LogItem(time2);
		log2.PushBack(content1);
		
		LogItem log3 = new LogItem(time3);
		log3.PushBack(content1);
		log3.PushBack(content2);
		log3.PushBack(content3);
		
		List<LogItem> logs = new ArrayList<LogItem>();
		logs.add(log1);
		logs.add(log2);
		logs.add(log3);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		try {
			mock.ChangeResponse(response);
			mock.PutLogs("project", "logStore", "topic", logs, "source");
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		List<LogItem> maxLineLogs = new ArrayList<LogItem>();
		for (int i = 0;i < 4097;i++){
			maxLineLogs.add(log1);
		}
		
		try {
			mock.ChangeResponse(response);
			mock.PutLogs("project", "logStore", "topic", maxLineLogs, "source");		
		} catch (LogException e) {
			assertEquals("InvalidLogSize", e.GetErrorCode());
			assertEquals("logItems' length exceeds maximum limitation : " + String.valueOf(Consts.CONST_MAX_PUT_LINES) + " lines",
					e.GetErrorMessage());
		}
		
		LogItem overloadLog = new LogItem(time1);
		for(int i = 0;i < 512 * 1024;i++) {
			overloadLog.mContents.add(content1);
		}
		
		List<LogItem> overloadLogs = new ArrayList<LogItem>();
		overloadLogs.add(overloadLog);
		
		try {
			mock.ChangeResponse(response);
			mock.PutLogs("project", "logStore", "topic", overloadLogs, "source");
			
		} catch (LogException e) {
			assertEquals("InvalidLogSize", e.GetErrorCode());
			assertEquals("logItems' size exceeds maximum limitation : " + String.valueOf(Consts.CONST_MAX_PUT_SIZE) + " bytes",
					e.GetErrorMessage());
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.PutLogs("project", "logStore", "topic", logs, "source");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}

	@Test
	public void TestGetCursor() {
		JSONObject obj = new JSONObject();
		String cursor = "test_cursor";
		obj.put("cursor", cursor);
		
		String jsonStr = obj.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			GetCursorResponse res = null;
			
			mock.ChangeResponse(response);
			res = mock.GetCursor("project", "logStream", 0, 0);
			assertEquals(cursor, res.GetCursor());
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			res = mock.GetCursor("project", "logStream", 0, CursorMode.BEGIN);
			assertEquals(cursor, res.GetCursor());
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			res = mock.GetCursor("project", "logStream", 0, new Date());
			assertEquals(cursor, res.GetCursor());
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.GetCursor("project", "logStream", 0, new Date());
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.GetCursor("project", "logStream", 0, new Date());
		} catch (LogException e) {
			assertEquals("FailToCreateCursor", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestListShard() {
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		obj1.put("shardID", 0);
		obj1.put("status", "readwrite");
		obj1.put("inclusiveBeginKey", "e7000000000000000000000000000000");
		obj1.put("exclusiveEndKey", "f7000000000000000000000000000000");
		obj1.put("createTime", String.valueOf((new Date().getTime())/1000));
		obj2.put("shardID", 1);
		obj2.put("status", "readwrite");
		obj2.put("inclusiveBeginKey", "f7000000000000000000000000000000");
		obj2.put("exclusiveEndKey", "f8000000000000000000000000000000");
		obj2.put("createTime", String.valueOf((new Date().getTime())/1000));
		
		JSONArray array = new JSONArray();
		array.add(obj1);
		array.add(obj2);
		
		String jsonStr = array.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			ListShardResponse res = mock.ListShard("testProject", "testLogStore");
			
			assertEquals("shard num does not match", array.size(), res.GetShards().size());
			for (int i = 0;i < array.size();i++) {
				assertEquals("shard does not match", array.getJSONObject(i).getInt("shardID"), res.GetShards().get(i).GetShardId());
			}
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListShard("testProject", "testLogStore");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListShard("testProject", "testLogStore");
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
		
		invalidBody = null;
		try {
			invalidBody = "[\"XD\"]".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListShard("testProject", "testLogStore");
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestUpdateACL() {
		String project = "test-project";
		String logStore = "test-logstore";
		
		String principle = "ANONYMOUS";

		List<ACLPrivilege> privilegeList = new ArrayList<ACLPrivilege>();
		privilegeList.add(ACLPrivilege.READ);
		privilegeList.add(ACLPrivilege.WRITE);
		privilegeList.add(ACLPrivilege.WRITE);
		ACLPrivileges privileges = new ACLPrivileges(privilegeList);
		
		ACL acl = new ACL(principle, privileges, ACLAction.GRANT);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);

		try {
			mock.ChangeResponse(response);
			mock.UpdateACL(project, acl);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		
		try {
			mock.ChangeResponse(response);
			mock.UpdateACL(project, logStore, acl);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.UpdateACL(project, acl);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestListACL() {
		String project = "test-project";
		String logStore = "test-logstore";
		
		String principle = "ANONYMOUS";
		
		ACLPrivileges p1 = new ACLPrivileges();
		p1.AddPrivilege(ACLPrivilege.READ);
		ACLPrivileges p2 = new ACLPrivileges();
		p2.AddPrivilege(ACLPrivilege.WRITE);
		int createTime = 12343123;
		int lastModifyTime = 1234314;
		
		ACL acl1 = null;
		ACL acl2 = null;
		acl1 = new ACL(principle, p1, ACLAction.GRANT);
		acl1.SetCreateTime(createTime);
		acl1.SetLastModifyTime(lastModifyTime);
		
		acl2 = new ACL(principle, p2, ACLAction.GRANT);
		acl2.SetCreateTime(createTime);
		acl2.SetLastModifyTime(lastModifyTime);
		
		List<ACL> acls = new ArrayList<ACL>();
		acls.add(acl2);
		acls.add(acl1);
		
		JSONObject aclDict1 = new JSONObject();
		aclDict1.put("principle", acl2.GetPrinciple());
		aclDict1.put("privilege", JSONArray.fromObject(acl2.GetPrivilege().ToJsonString()));
		aclDict1.put("createTime", acl2.GetCreateTime());
		aclDict1.put("lastModifyTime", acl2.GetLastModifyTime());
		
		JSONObject aclDict2 = new JSONObject();
		aclDict2.put("principle", acl1.GetPrinciple());
		aclDict2.put("privilege", JSONArray.fromObject( acl1.GetPrivilege().ToJsonString()));
		aclDict2.put("createTime", acl1.GetCreateTime());
		aclDict2.put("lastModifyTime", acl1.GetLastModifyTime());
		
		JSONArray aclsArray = new JSONArray();
		aclsArray.add(aclDict1);
		aclsArray.add(aclDict2);
		
		JSONObject obj = new JSONObject();
		obj.put("total", 4);
		obj.put("count", 2);
		obj.put("acls", aclsArray);
		
		String jsonStr = obj.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			mock.ListACL(project, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			mock.ListACL(project, logStore, 0, 1);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			mock.ListACL(project, logStore);
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			ListACLResponse listRes = mock.ListACL(project);
			
			assertEquals("offset does not match", 4,listRes.GetTotal());
			assertEquals("count does not match", 2, listRes.GetCount());
			
			assertEquals("configNum does not match", acls.size(), listRes.GetACLs().size());
			for(int i = 0;i < acls.size();i++) {
				assertEquals(acls.get(i).GetPrinciple(), listRes.GetACLs().get(i).GetPrinciple());
				assertEquals(acls.get(i).GetPrivilege().ToJsonString(), listRes.GetACLs().get(i).GetPrivilege().ToJsonString());
				assertEquals(acls.get(i).GetCreateTime(), listRes.GetACLs().get(i).GetCreateTime());
				assertEquals(acls.get(i).GetLastModifyTime(), listRes.GetACLs().get(i).GetLastModifyTime());
			}
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListACL(project);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListACL(project);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
		
		try {
			invalidBody = "{\"total\":\"4\",\"size\":\"2\",\"key\":\"invalid json\"}".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.ListACL(project);
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestBatchGetLog() {
		LogContent content1 = new LogContent();
		LogContent content2 = new LogContent();
		LogContent content3 = new LogContent();
		content1.mKey = "key1";
		content1.mValue = "value1";
		
		content2.mKey = "key2";
		content2.mValue = "value2";
		
		content3.mKey = "key3";
		content3.mValue = "value3";
		
		int time1 = 1123431;
		int time2 = 1123432;
		int time3 = 1123433;
		
		LogItem log1 = new LogItem(time1);
		log1.PushBack(content1);
		log1.PushBack(content3);
		
		LogItem log2 = new LogItem(time2);
		log2.PushBack(content1);
		
		LogItem log3 = new LogItem(time3);
		log3.PushBack(content1);
		log3.PushBack(content2);
		log3.PushBack(content3);
		
		ArrayList<LogItem> logItems01 = new ArrayList<LogItem>();
		logItems01.add(log1);
		logItems01.add(log3);
		
		ArrayList<LogItem> logItems02 = new ArrayList<LogItem>();
		logItems02.add(log2);
		
		String reserved = "reserved";
		String topic = "topic";
		String source = "source";
		
		List<ArrayList<LogItem>> logGroups = new ArrayList<ArrayList<LogItem>>();
		logGroups.add(logItems01);
		logGroups.add(logItems02);
		
		LogGroupData logGroupData1 = new LogGroupData(reserved, topic, source, "", logItems01);
		LogGroupData logGroupData2 = new LogGroupData(logGroupData1);
		logGroupData2.SetAllLogs(logItems02);
		List<LogGroupData> logGroupDatas = new ArrayList<LogGroupData>();
		logGroupDatas.add(logGroupData1);
		logGroupDatas.add(logGroupData2);
		
		Logs.LogGroupList.Builder logGroupList = Logs.LogGroupList.newBuilder();
		for (ArrayList<LogItem> logItems:logGroups) {	
			Logs.LogGroup.Builder logs = Logs.LogGroup.newBuilder();
			logs.setCategory(reserved);
			logs.setTopic(topic);
			logs.setSource(source);
			for (int i = 0; i < logItems.size(); i++) {
				LogItem item = logItems.get(i);
				Logs.Log.Builder log = logs.addLogsBuilder();
				log.setTime(item.mLogTime);
				for (LogContent content : item.mContents) {
					Logs.Log.Content.Builder contentBuilder = log
							.addContentsBuilder();
					contentBuilder.setKey(content.mKey);
					contentBuilder.setValue(content.mValue);
				}
			}

			logGroupList.addLogGroupList(logs);
		}
		
		
		byte[] logBytes = logGroupList.build().toByteArray();
		int dataLength = logBytes.length;
		try {
			logBytes = LZ4Encoder.compressToLhLz4Chunk(logBytes.clone());
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(logBytes);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		resHeaders.put(Consts.CONST_X_SLS_CURSOR, "cursor");
		resHeaders.put(Consts.CONST_X_SLS_COUNT, "2");
		resHeaders.put(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(dataLength));
		
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(logBytes);
		BatchGetLogResponse res = null;
		try {
			mock.ChangeResponse(response);
			res = mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		try {
			res.GetLogGroup(100);
		} catch (LogException e) {
			assertEquals("GetLogGroupError", e.GetErrorCode());
			assertEquals("Invalid index", e.GetErrorMessage());
		}
		
		try {
			LogGroupData resLogGroup = res.GetLogGroup(0);
			assertEquals("reserved does not match", logGroupDatas.get(0).GetReserved(),
					resLogGroup.GetReserved());
			assertEquals("source does not match",  logGroupDatas.get(0).GetSource(),
					resLogGroup.GetSource());
			assertEquals("topic does not match",  logGroupDatas.get(0).GetTopic(),
					resLogGroup.GetTopic());
			
			List<LogItem> logItemsOri = logGroupDatas.get(0).GetAllLogs();
			List<LogItem> logItemsRes = resLogGroup.GetAllLogs();
			assertEquals("logItem num does not match", logItemsOri.size(), logItemsRes.size());
			for(int j = 0;j < logItemsOri.size();j++) {
				assertEquals("logTime does not match", logItemsOri.get(j).GetTime(), logItemsRes.get(j).GetTime());
				ArrayList<LogContent> logContentsOri = logItemsOri.get(j).GetLogContents();
				ArrayList<LogContent> logContentsRes = logItemsRes.get(j).GetLogContents();
				assertEquals("logContent num does not match", logContentsOri.size(), logContentsRes.size());
				for(int k = 0;k < logContentsOri.size();k++) {
					LogContent contentOri = logContentsOri.get(k);
					LogContent contentRes = logContentsRes.get(k);
					assertEquals("key does not match", contentOri.mKey, contentRes.mKey);
					assertEquals("value does not match", contentOri.mValue, contentRes.mValue);
				}
			}
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		try {
			res.GetLogGroups(100);
		} catch (LogException e) {
			assertEquals("GetLogGroupError", e.GetErrorCode());
			assertEquals("Invalid offset", e.GetErrorMessage());
		}
		
		try {
			List<LogGroupData> resLogGroups = res.GetLogGroups(1);
			assertEquals("reserved does not match", logGroupDatas.get(1).GetReserved(),
					resLogGroups.get(0).GetReserved());
			assertEquals("source does not match",  logGroupDatas.get(1).GetSource(),
					resLogGroups.get(0).GetSource());
			assertEquals("topic does not match",  logGroupDatas.get(1).GetTopic(),
					resLogGroups.get(0).GetTopic());
			
			List<LogItem> logItemsOri = logGroupDatas.get(1).GetAllLogs();
			List<LogItem> logItemsRes = resLogGroups.get(0).GetAllLogs();
			assertEquals("logItem num does not match", logItemsOri.size(), logItemsRes.size());
			for(int j = 0;j < logItemsOri.size();j++) {
				assertEquals("logTime does not match", logItemsOri.get(j).GetTime(), logItemsRes.get(j).GetTime());
				ArrayList<LogContent> logContentsOri = logItemsOri.get(j).GetLogContents();
				ArrayList<LogContent> logContentsRes = logItemsRes.get(j).GetLogContents();
				assertEquals("logContent num does not match", logContentsOri.size(), logContentsRes.size());
				for(int k = 0;k < logContentsOri.size();k++) {
					LogContent contentOri = logContentsOri.get(k);
					LogContent contentRes = logContentsRes.get(k);
					assertEquals("key does not match", contentOri.mKey, contentRes.mKey);
					assertEquals("value does not match", contentOri.mValue, contentRes.mValue);
				}
			}
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		try {
			assertEquals("cursor does not match", "cursor", res.GetNextCursor());
			assertEquals("count does not match", 2, res.GetCount());
			assertEquals("raw size does not match", dataLength, res.GetRawSize());
			
			List<LogGroupData> resLogGroups = res.GetLogGroups();
			assertEquals("log group num not match", logGroupDatas.size(), resLogGroups.size());	
			for(int i = 0;i < logGroupDatas.size();i++) {
				assertEquals("reserved does not match", logGroupDatas.get(i).GetReserved(),
						resLogGroups.get(i).GetReserved());
				assertEquals("source does not match",  logGroupDatas.get(i).GetSource(),
						resLogGroups.get(i).GetSource());
				assertEquals("topic does not match",  logGroupDatas.get(i).GetTopic(),
						resLogGroups.get(i).GetTopic());
				
				List<LogItem> logItemsOri = logGroupDatas.get(i).GetAllLogs();
				List<LogItem> logItemsRes = resLogGroups.get(i).GetAllLogs();
				assertEquals("logItem num does not match", logItemsOri.size(), logItemsRes.size());
				for(int j = 0;j < logItemsOri.size();j++) {
					assertEquals("logTime does not match", logItemsOri.get(j).GetTime(), logItemsRes.get(j).GetTime());
					ArrayList<LogContent> logContentsOri = logItemsOri.get(j).GetLogContents();
					ArrayList<LogContent> logContentsRes = logItemsRes.get(j).GetLogContents();
					assertEquals("logContent num does not match", logContentsOri.size(), logContentsRes.size());
					for(int k = 0;k < logContentsOri.size();k++) {
						LogContent contentOri = logContentsOri.get(k);
						LogContent contentRes = logContentsRes.get(k);
						assertEquals("key does not match", contentOri.mKey, contentRes.mKey);
						assertEquals("value does not match", contentOri.mValue, contentRes.mValue);
					}
				}
			}
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
		
		byte[] invalidBody = null;
		try {
			invalidBody = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream invalidContent = new ByteArrayInputStream(invalidBody);
		response.setStatusCode(200);
		response.setContent(invalidContent);
		mock.ChangeResponse(response);
		try {
			mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertEquals("DecompressException", e.GetErrorCode());
		}
		
		byte[] invalidCompressedBody = null;
		int rawInvalidSize = 0;
		try {
			byte[] invalid = SlsClientTestData.TEST_INVALID_JSON.getBytes("utf-8");
			rawInvalidSize = invalid.length;
			invalidCompressedBody = LZ4Encoder.compressToLhLz4Chunk(invalid);
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		resHeaders.put(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(rawInvalidSize));	
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(new ByteArrayInputStream(invalidCompressedBody));
		response.SetBody(invalidCompressedBody);
		mock.ChangeResponse(response);
		try {
			mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertEquals("InitLogGroupsError", e.GetErrorCode());
		}
		
		resHeaders.put(Consts.CONST_X_SLS_BODYRAWSIZE, "x");	
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(new ByteArrayInputStream(invalidCompressedBody));
		response.SetBody(invalidCompressedBody);
		mock.ChangeResponse(response);
		try {
			mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertEquals("ParseLogGroupListRawSizeError", e.GetErrorCode());
		}
		
		content = new ByteArrayInputStream(logBytes);
		
		response = new ResponseMessage();
		
		resHeaders.put(Consts.CONST_X_SLS_COUNT, "10");
		resHeaders.put(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(dataLength));
		
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(logBytes);
		mock.ChangeResponse(response);
		try {
			mock.BatchGetLog("project", "logStore", 0, 0, "cursor");
		} catch (LogException e) {
			assertEquals("LogGroupCountNotMatch", e.GetErrorCode());
		}
	}
	
	@Test
	public void TestGetHostURI() {
		String project = "testproject";
		URI uri = logClientMock.GetHostURI(project);
		String expectResult = "http://testproject.mock-sls.aliyun-inc.com";
		assertEquals(uri.toString(), expectResult);
	}
	
	@Test
	public void TestGetHistograms() {
		Histogram histogram1 = new Histogram(100, 200, 100, "Complete");
		Histogram histogram2 = new Histogram(200, 300, 200, "Complete");
		
		List<Histogram> histograms = new ArrayList<Histogram>();
		histograms.add(histogram1);
		histograms.add(histogram2);

		
		JSONArray histogramsArray = new JSONArray();
		for(Histogram histogram:histograms) {
			JSONObject obj = new JSONObject();
			obj.put("from", histogram.GetFrom());
			obj.put("to", histogram.GetTo());
			obj.put("count", histogram.GetCount());
			String complete = "Incomplete";
			if (histogram.IsCompleted()) {
				complete = "Complete";
			}
			obj.put("progress", complete);
			histogramsArray.add(obj);
		}
		
		
		String jsonStr = histogramsArray.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		resHeaders.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_INCOMPLETE);
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			GetHistogramsResponse res = mock.GetHistograms("project", "logStore", 0, 0, "topic", "query");
			
			/*
			String resComplete = "Incomplete";
			if (res.IsCompleted()) {
				resComplete = "Complete";
			}
			
			assertEquals("Complete", resComplete);
			assertEquals(300, res.GetTotalCount());
			*/
			assertEquals(histograms.size(), res.GetHistograms().size());
			for (int i = 0;i < histograms.size();i++) {
				assertEquals(histograms.get(i).GetCount(), res.GetHistograms().get(i).GetCount());
				assertEquals(histograms.get(i).GetFrom(), res.GetHistograms().get(i).GetFrom());
				assertEquals(histograms.get(i).GetTo(), res.GetHistograms().get(i).GetTo());
				assertEquals(histograms.get(i).IsCompleted(), res.GetHistograms().get(i).IsCompleted());
			}
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.GetHistograms("project", "logStore", 0, 0, "topic", "query");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestListTopic() {
		JSONArray topicArray = new JSONArray();
		topicArray.add("topic1");
		topicArray.add("topic2");
		topicArray.add("topic3");
		
		 
		String jsonStr = topicArray.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		resHeaders.put(Consts.CONST_X_SLS_COUNT, "3");
		resHeaders.put(Consts.CONST_X_SLS_NEXT_TOKEN, "nt");
		
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			ListTopicsResponse res = mock.ListTopics("project", "logStore", "token", 0);
			assertEquals("nt", res.GetNextToken());
			assertEquals(topicArray.size(), res.GetCount());
			
			for(int i = 0;i < res.GetCount();i++) {
				assertEquals(topicArray.get(i), res.GetTopics().get(i));
			}
			
		} catch (LogException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListTopics("project", "logStore", "token", 0);
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestListLogStores() {
		JSONArray logstoreArray = new JSONArray();
		logstoreArray.add("logstore1");
		logstoreArray.add("logstore2");
		
		JSONObject resObj = new JSONObject();

		resObj.put("logstores", logstoreArray);
		
		String jsonStr = resObj.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			ListLogStoresResponse res = mock.ListLogStores("project",0,500,"");
			
			assertEquals(logstoreArray.size(), res.GetCount());	
			for(int i = 0;i < res.GetCount();i++) {
				assertEquals(logstoreArray.get(i), res.GetLogStores().get(i));
			}
			
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.ListLogStores("project",0,500,"");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestGetLogs() {
		LogItem item1 = new LogItem();
		item1.SetTime(100);
		ArrayList<LogContent> contents1 = new ArrayList<LogContent>();
		contents1.add(new LogContent("key_1", "value_1"));
		contents1.add(new LogContent("key_2", "value_2"));
		item1.SetLogContents(contents1);
		
		QueriedLog log1 = new QueriedLog("10.10.10.10", item1);
		
		LogItem item2 = new LogItem();
		item1.SetTime(200);
		ArrayList<LogContent> contents2 = new ArrayList<LogContent>();
		contents2.add(new LogContent("key_3", "value_3"));
		contents2.add(new LogContent("key_4", "value_4"));
		item2.SetLogContents(contents2);
		
		QueriedLog log2 = new QueriedLog("20.20.20.20", item2);
		
		List<QueriedLog> queriedLogs = new ArrayList<QueriedLog>();
		queriedLogs.add(log1);
		queriedLogs.add(log2);
		
		JSONArray logsArray = new JSONArray();
		for (QueriedLog queriedLog:queriedLogs) {
			JSONObject log = new JSONObject();
			log.put("__time__", queriedLog.GetLogItem().GetTime());
			log.put("__source__", queriedLog.GetSource());
			
			for (LogContent content:queriedLog.GetLogItem().GetLogContents()) {
				log.put(content.GetKey(), content.GetValue());
			}
			
			logsArray.add(log);
		}
		
		String jsonStr = logsArray.toString();
		byte[] body = null;
		try {
			body = jsonStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream content = new ByteArrayInputStream(body);
		
		ResponseMessage response = new ResponseMessage();
		
		Map<String, String> resHeaders = new HashMap<String, String>();
		resHeaders.put(Consts.CONST_X_SLS_REQUESTID, "TESTREQID");
		resHeaders.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
		response.setHeaders(resHeaders);
		response.setStatusCode(200);
		response.setContent(content);
		response.SetBody(body);
		try {
			mock.ChangeResponse(response);
			GetLogsResponse res = mock.GetLogs("project", "logStore", 0, 0, "topic", "query");
			
			content = new ByteArrayInputStream(body);
			response.setContent(content);
			mock.ChangeResponse(response);
			mock.GetLogs("project", "logStore", 0, 0, "topic", "query", 0, 0, true);
			
			String resComplete = "Incomplete";
			if (res.IsCompleted()) {
				resComplete = "Complete";
			}
			
			assertEquals("Complete", resComplete);		
			assertEquals(queriedLogs.size(), res.GetCount());	
			for(int i = 0;i < res.GetCount();i++) {
				assertEquals(queriedLogs.get(i).GetLogItem().GetTime(), res.GetLogs().get(i).GetLogItem().GetTime());
				assertEquals(queriedLogs.get(i).GetSource(), res.GetLogs().get(i).GetSource());
				
				assertEquals(queriedLogs.get(i).GetLogItem().GetLogContents().size(), res.GetLogs().get(i).GetLogItem().GetLogContents().size());
				
				LogItem originLog = queriedLogs.get(i).GetLogItem();
				LogItem resultLog = res.GetLogs().get(i).GetLogItem();
				
				for (int j = 0;j < originLog.GetLogContents().size();j++) {
					assertEquals(originLog.GetLogContents().get(j).GetKey(), resultLog.GetLogContents().get(j).GetKey());
					assertEquals(originLog.GetLogContents().get(j).GetValue(), resultLog.GetLogContents().get(j).GetValue());
				}
				
			}
			
			
		} catch (LogException e) {
			assertTrue(e.getMessage(), false);
		}
		
		
		byte[] errorBody = null;
		try {
			errorBody = SlsClientTestData.TEST_STANDARD_ERROR.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			assertTrue(e.getMessage(), false);
		}
		InputStream errorContent = new ByteArrayInputStream(errorBody);
		response.setStatusCode(400);
		response.setContent(errorContent);
		mock.ChangeResponse(response);
		try {
			mock.GetLogs("project", "logStore", 0, 0, "topic", "query");
		} catch (LogException e) {
			assertEquals("code", e.GetErrorCode());
			assertEquals("message", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestLogItemString() {
		LogItem log = new LogItem(1);
		log.PushBack("key1", "value1");
		log.PushBack("key2", "value2");
		String res = log.ToJsonString();
		assertEquals("{\"logtime\":1,\"key1\":\"value1\",\"key2\":\"value2\"}", res);
	}
	
	@Test
	public void TestResponseMisc() {
		Response res1 = new Response(new HashMap<String, String>());
		assertEquals("", res1.GetHeader(""));
		assertEquals(0, res1.GetAllHeaders().size());
		Map<String,String> header =  new HashMap<String, String>();
		header.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
		GetLogsResponse res2 = new GetLogsResponse(header);
		LogItem log = new LogItem(1);
		QueriedLog queriedLog = new QueriedLog("", log);
		List<QueriedLog> logs = new ArrayList<QueriedLog>();
		logs.add(queriedLog);
		
		res2.SetLogs(logs);
		List<QueriedLog> resLogs = res2.GetLogs();
		
		assertEquals(logs.size(), resLogs.size());
		assertEquals(logs.get(0).GetSource(), resLogs.get(0).GetSource());
		LogItem resLog = resLogs.get(0).GetLogItem();
		assertEquals(log.GetTime(), resLog.GetTime());
		
		assertEquals(log.GetLogContents().size(), resLog.GetLogContents().size());
		for(int k = 0;k < log.GetLogContents().size();k++) {
			LogContent contentOri = log.GetLogContents().get(k);
			LogContent contentRes = resLog.GetLogContents().get(k);
			assertEquals(contentOri.mKey, contentRes.mKey);
			assertEquals(contentOri.mValue, contentRes.mValue);
		}
		
		GetHistogramsResponse res3 = new GetHistogramsResponse(header);
		res3.SetHistograms(new ArrayList<Histogram>());
		assertEquals(0, res3.GetHistograms().size());
		
		byte[] rawDataEmpty = {};
		Map<String, String> emptyHeader = new HashMap<String, String>();
		emptyHeader.put(Consts.CONST_X_SLS_COUNT, "0");
		emptyHeader.put(Consts.CONST_X_SLS_BODYRAWSIZE, "0");
		BatchGetLogResponse res5 = null;
		try {
			res5 = new BatchGetLogResponse(emptyHeader, rawDataEmpty);
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
		try {
			res5.GetLogGroup(0);
		} catch (LogException e) {
			assertEquals("GetLogGroupError", e.GetErrorCode());
			assertEquals("No LogGroups in response", e.GetErrorMessage());
		}
		
		try {
			res5.GetLogGroups(0);
		} catch (LogException e) {
			assertEquals("GetLogGroupError", e.GetErrorCode());
			assertEquals("No LogGroups in response", e.GetErrorMessage());
		}
		
		try {
			res5.GetLogGroups();
		} catch (LogException e) {
			assertEquals("GetLogGroupError", e.GetErrorCode());
			assertEquals("No LogGroups in response", e.GetErrorMessage());
		}
	}
	
	@Test
	public void TestRequestMisc() {
		Request req1 = new Request("");
		req1.SetParam("test", "value");
		assertEquals("value", req1.GetParam("test"));
		assertEquals("", req1.GetParam("test2"));
		
		RemoveConfigFromMachineGroupRequest req2 = new RemoveConfigFromMachineGroupRequest("", "", "");
		req2.SetGroupName("test1");
		req2.SetConfigName("test2");
		assertEquals("test1", req2.GetGroupName());
		assertEquals("test2", req2.GetConfigName());
		
		LogItem log = new LogItem(1);
		log.PushBack(new LogContent("key", "value"));
		List<LogItem> logItems = new ArrayList<LogItem>();
		logItems.add(log);
		PutLogsRequest req3 = new PutLogsRequest("pjt", "lst", "tpc", logItems);
		assertEquals("lst", req3.GetLogStore());
		assertEquals("tpc", req3.GetTopic());
		assertEquals(logItems.size(), req3.GetLogItems().size());
		
		LogItem resLog = req3.GetLogItems().get(0);
		assertEquals(log.GetTime(), resLog.GetTime());
		
		assertEquals(log.GetLogContents().size(), resLog.GetLogContents().size());
		for(int k = 0;k < log.GetLogContents().size();k++) {
			LogContent contentOri = log.GetLogContents().get(k);
			LogContent contentRes = resLog.GetLogContents().get(k);
			assertEquals(contentOri.mKey, contentRes.mKey);
			assertEquals(contentOri.mValue, contentRes.mValue);
		}
		
		req3.SetLogStore("lst1");
		req3.SetSource("src1");
		req3.SetTopic("tpc1");
		req3.SetlogItems(new ArrayList<LogItem>());
		
		assertEquals("lst1", req3.GetLogStore());
		assertEquals("src1", req3.GetSource());
		assertEquals("tpc1", req3.GetTopic());
		assertEquals(0, req3.GetLogItems().size());
		
		ListTopicsRequest req4 = new ListTopicsRequest("", "lst2");
		req4.SetToken("tk1");
		assertEquals("lst2", req4.GetLogStore());
		assertEquals("tk1", req4.GetToken());
		assertEquals(0, req4.GetLine());
		
		req4.SetLine(9);
		assertEquals(9, req4.GetLine());
		
		req4.SetLogStore("lst3");
		assertEquals("lst3", req4.GetLogStore());
		
		ListShardRequest req5 = new ListShardRequest("", "");
		req5.SetLogStore("lst4");
		assertEquals("lst4", req5.GetLogStore());
		
		ListMachineGroupRequest req6 = new ListMachineGroupRequest("logStore", "group", 1, 3);
		assertEquals("group", req6.GetGroupName());
		assertEquals(1, req6.GetOffset());
		assertEquals(3, req6.GetSize());
		
		ListConfigRequest req7 = new ListConfigRequest("logStore", "config", 2, 4);
		assertEquals("config", req7.GetConfigName());
		assertEquals(2, req7.GetOffset());
		assertEquals(4, req7.GetSize());
		
		ListACLRequest req8 = new ListACLRequest("project", "logStore", 3, 5);
		assertEquals("logStore", req8.GetLogStore());
		assertEquals(3, req8.GetOffset());
		assertEquals(5, req8.GetSize());
		
		GetMachineGroupRequest req10 = new GetMachineGroupRequest("project", "");
		req10.SetGroupName("group2");
		assertEquals("group2", req10.GetGroupName());
		
		GetLogsRequest req11 = new GetLogsRequest("", "", 1, 2, "tpc2", "qry1");
		req11.SetLogStore("lst3");
		assertEquals("lst3", req11.GetLogStore());
		assertEquals(1, req11.GetFromTime());
		assertEquals(2, req11.GetToTime());
		assertEquals("tpc2", req11.GetTopic());
		assertEquals("qry1", req11.GetQuery());
		assertEquals(0, req11.GetOffset());
		assertEquals(0, req11.GetLine());
		assertEquals(false, req11.GetReverse());
		
		req11.SetOffset(3);
		req11.SetLine(4);
		req11.SetReverse(true);
		assertEquals(3, req11.GetOffset());
		assertEquals(4, req11.GetLine());
		assertEquals(true, req11.GetReverse());
		
		GetHistogramsRequest req12 = new GetHistogramsRequest("", "", "tpc3", "qry2", 2, 4);
		req12.SetLogStore("lst4");
		assertEquals("lst4", req12.GetLogStore());
		assertEquals("tpc3", req12.GetTopic());
		assertEquals("qry2", req12.GetQuery());
		assertEquals(2, req12.GetFromTime());
		assertEquals(4, req12.GetToTime());
		
		
		GetCursorRequest req13 = new GetCursorRequest("", "", 0, CursorMode.END);
		req13.SetLogStore("lst5");
		req13.SetShardId(1);
		assertEquals("lst5", req13.GetLogStore());
		assertEquals(1, req13.GetShardId());
		assertEquals("end", req13.GetFrom());
		req13.SetFrom(1);
		assertEquals("1", req13.GetFrom());
		
		GetConfigRequest req14 = new GetConfigRequest("project", "");
		req14.SetConfigName("config2");
		assertEquals("config2", req14.GetConfigName());
		
		DeleteMachineGroupRequest req16 = new DeleteMachineGroupRequest("project", "");
		req16.SetGroupName("group3");
		assertEquals("group3", req16.GetGroupName());
		
		DeleteConfigRequest req17 = new DeleteConfigRequest("project", "");
		req17.SetConfigName("config3");
		assertEquals("config3", req17.GetConfigName());
		
		BatchGetLogRequest req19 = new BatchGetLogRequest("", "", 0, 10, "sdcset=");
		req19.SetLogStore("lst6");
		req19.SetShardId(5);
		assertEquals("lst6", req19.GetLogStore());
		assertEquals(5, req19.GetShardId());
		assertEquals(10, req19.GetCount());
		assertEquals("sdcset=", req19.GetCursor());
		
		ApplyConfigToMachineGroupRequest req20 = new ApplyConfigToMachineGroupRequest("project", "", "");
		req20.SetConfigName("config4");
		req20.SetGroupName("group4");
		assertEquals("group4", req20.GetGroupName());
		assertEquals("config4", req20.GetConfigName());
		
		PutLogsRequest req21 = new PutLogsRequest("", "", "", new ArrayList<LogItem>());
		req21.SetCompressType(CompressType.NONE);
		assertEquals(CompressType.NONE, req21.GetCompressType());
		
		DeleteLogStoreRequest req22 = new DeleteLogStoreRequest("", "");
		req22.SetLogStoreName("test-logstore");
		assertEquals("test-logstore", req22.GetLogStoreName());
	}
	
	@Test
	public void TestExceptionMisc() {
		LogException e = new LogException("", "", "test");
		assertEquals("test", e.GetRequestId());
	}
	
	@Test
	public void TestCommonMisc() {
		LogStore logStore = new LogStore();
		logStore.SetLogStoreName("1");
		logStore.SetShardCount(1);
		logStore.SetTtl(2);
		assertEquals("1", logStore.GetLogStoreName());
		assertEquals(1, logStore.GetShardCount());
		assertEquals(2, logStore.GetTtl());
		
		try {
			logStore.FromJsonString("abs");
		} catch (LogException e) {
			assertEquals("FailToGenerateLogStore", e.GetErrorCode());
		}
		
		try {
			logStore.FromJsonString(SlsClientTestData.TEST_INVALID_JSON);
		} catch (LogException e) {
			assertEquals("FailToGenerateLogStore", e.GetErrorCode());
		}
		
		String logStoreString = "{\"logstoreName\":\"2\",\"shardCount\":2,\"ttl\":4,\"createTime\":1988"
				+ ",\"lastModifyTime\":20132}";
		try {
			logStore.FromJsonString(logStoreString);
			assertEquals("2", logStore.GetLogStoreName());
			assertEquals(2, logStore.GetShardCount());
			assertEquals(4, logStore.GetTtl());
			assertEquals(1988, logStore.GetCreateTime());
			assertEquals(20132, logStore.GetLastModifyTime());
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
	
		String logStoreJsonString = logStore.ToJsonString();
		JSONObject logStoreJson = JSONObject.fromObject(logStoreJsonString);
		LogStore another = new LogStore();
		try {
			another.FromJsonObject(logStoreJson);
			assertEquals(another.GetLogStoreName(), logStore.GetLogStoreName());
			assertEquals(another.GetShardCount(), logStore.GetShardCount());
			assertEquals(another.GetTtl(), logStore.GetTtl());
			assertEquals(another.GetCreateTime(), logStore.GetCreateTime());
			assertEquals(another.GetLastModifyTime(), logStore.GetLastModifyTime());
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
		
		
		LogItem log = new LogItem(12);
		log.PushBack("key", "value");
		ArrayList<LogItem> logs = new ArrayList<LogItem>();
		logs.add(log);
		LogGroupData logGroup = new LogGroupData("", "", "", "", logs);
		try {
			LogItem resLog = logGroup.GetLogByIndex(0);
			assertEquals(log.GetTime(), resLog.GetTime());

			assertEquals(log.GetLogContents().size(), resLog.GetLogContents().size());
			for (int k = 0; k < log.GetLogContents().size(); k++) {
				LogContent contentOri = log.GetLogContents().get(k);
				LogContent contentRes = resLog.GetLogContents().get(k);
				assertEquals(contentOri.mKey, contentRes.mKey);
				assertEquals(contentOri.mValue, contentRes.mValue);
			}
		} catch (LogException e) {
			e.printStackTrace();
		}
		
		ConfigOutputDetail outputDetail = new ConfigOutputDetail("endpoint", "logStore");
		assertEquals("endpoint", outputDetail.GetEndpoint());
		assertEquals("logStore", outputDetail.GetLogstoreName());
		
		try {
			ConfigOutputDetail errorOutput = new ConfigOutputDetail();
			errorOutput.FromJsonString("");
		} catch (LogException e) {
			assertEquals("FailToGenerateOutputDetail", e.GetErrorCode());
		}
		
		try {
			ConfigOutputDetail errorOutput = new ConfigOutputDetail();
			errorOutput.FromJsonObject(new JSONObject());
		} catch (LogException e) {
			assertEquals("FailToGenerateOutputDetail", e.GetErrorCode());
		}
		
		ArrayList<String> key = new ArrayList<String>();
		key.add("key");
		ConfigInputDetail inputDetail = new ConfigInputDetail("logPath", "filePattern", "logType",
				"logBeginRegex", "regex", key, "timeFormat", false);
		
		assertEquals("filePattern", inputDetail.GetFilePattern());
		
		assertEquals(1, inputDetail.GetKey().size());
		assertEquals("key", inputDetail.GetKey().get(0));
		
		assertEquals(false, inputDetail.GetLocalStorage());
		assertEquals("logBeginRegex", inputDetail.GetLogBeginRegex());
		assertEquals("logPath", inputDetail.GetLogPath());
		assertEquals("logType", inputDetail.GetLogType());
		assertEquals("regex", inputDetail.GetRegex());
		assertEquals("timeFormat", inputDetail.GetTimeFormat());
		
		try {
			ConfigInputDetail errorInput = new ConfigInputDetail();
			errorInput.FromJsonString("");
		} catch (LogException e) {
			assertEquals("FailToGenerateInputDetail", e.GetErrorCode());
		}
		
		try {
			ConfigInputDetail errorInput = new ConfigInputDetail();
			errorInput.FromJsonObject(new JSONObject());
		} catch (LogException e) {
			assertEquals("FailToGenerateInputDetail", e.GetErrorCode());
		}
		
		Config config = new Config();
		config.SetConfigName("config");
		
		assertEquals("config", config.GetConfigName());
		
		byte[] logBytes = {1, 2, 4, 6, 7, 9, 11, 22};
		Deflater compresser = new Deflater();
		compresser.setInput(logBytes);
		compresser.finish();
		int size = logBytes.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buf = new byte[10240];
		while (compresser.finished() == false) {
			int count = compresser.deflate(buf);
			out.write(buf, 0, count);
		}
		logBytes = out.toByteArray();
		
		ACL acl = new ACL();
		acl.SetPrinciple("ID");
		assertEquals("ID", acl.GetPrinciple());
		
		GroupAttribute attribute = new GroupAttribute("externalName1", "groupTopic1");
		assertEquals("externalName1", attribute.GetExternalName());
		assertEquals("groupTopic1", attribute.GetGroupTopic());
		
		String fromAttributeStr = "{\"externalName\":\"externalName2\",\"groupTopic\":\"groupTopic2\"}";
		try {
			attribute.FromJsonString(fromAttributeStr);
			assertEquals("externalName2", attribute.GetExternalName());
			assertEquals("groupTopic2", attribute.GetGroupTopic());
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
		try {
			JSONObject errorInfoObj = new JSONObject();
			attribute.FromJsonObject(errorInfoObj);
		} catch (LogException e) {
			assertEquals("FailToGenerateGroupAttribute", e.GetErrorCode());
		}
		
		try {
			attribute.FromJsonString("af");
		} catch (LogException e) {
			assertEquals("FailToGenerateGroupAttribute", e.GetErrorCode());
		}
		
		ArrayList<String> machineList = new ArrayList<String>();
		machineList.add("uuid1");
		MachineGroup group1 = new MachineGroup("groupName", "userdefined", machineList);
		group1.SetGroupAttribute(attribute);
		group1.SetGroupType("type1");
		group1.SetCreateTime(543211);
		group1.SetLastModifyTime(543212);
		
		String groupJsonStr = group1.ToJsonString();
		JSONObject groupJsonObj = JSONObject.fromObject(groupJsonStr);
		
		assertEquals("groupName", groupJsonObj.getString("groupName"));
		assertEquals("type1", groupJsonObj.getString("groupType"));
		assertEquals(543211, groupJsonObj.getInt("createTime"));
		assertEquals(543212, groupJsonObj.getInt("lastModifyTime"));
		
		assertEquals("externalName2", groupJsonObj.getJSONObject("groupAttribute").getString("externalName"));
		assertEquals("groupTopic2", groupJsonObj.getJSONObject("groupAttribute").getString("groupTopic"));
		
		assertEquals(1, groupJsonObj.getJSONArray("machineList").size());
		String machine1 = groupJsonObj.getJSONArray("machineList").getString(0);
		assertEquals("uuid1", machine1);
		
		try {
			group1.FromJsonString("af");
		} catch (LogException e) {
			assertEquals("FailToGenerateMachineGroup", e.GetErrorCode());
		}
		
		Config config1 = new Config("configName");
		config1.SetCreateTime(32321);
		config1.SetLastModifyTime(32322);
		JSONObject configObj = JSONObject.fromObject(config1.ToJsonString());
		assertEquals("configName", configObj.getString("configName"));
		assertEquals(32321, configObj.getInt("createTime"));
		assertEquals(32322, configObj.getInt("lastModifyTime"));
		
		try {
			config1.FromJsonString("af");
		} catch (LogException e) {
			assertEquals("FailToGenerateConfig", e.GetErrorCode());
		}
		
		ACLPrivileges privileges = new ACLPrivileges();
		String privilegesStr = "[\"READ\",\"WRITE\",\"READ\"]";
		try {
			privileges.FromJsonString(privilegesStr);
			assertEquals(2, privileges.GetPrivileges().size());
			assertEquals(ACLPrivilege.READ, privileges.GetPrivileges().get(0));
			assertEquals(ACLPrivilege.WRITE, privileges.GetPrivileges().get(1));
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
		try {
			privileges.FromJsonString("fsad");
		} catch (LogException e) {
			assertEquals("FailToGenerateACLPrivileges", e.GetErrorCode());
		}
		
		ACL acl1 = new ACL();
		try {
			acl1.FromJsonString("fsad");
		} catch (LogException e) {
			assertEquals("FailToGenerateACL", e.GetErrorCode());
		}
		
		acl1.SetCreateTime(3321);
		acl1.SetLastModifyTime(3322);

		acl1.SetPrinciple("principle");
		acl1.SetPrivilege(privileges);
		
		try {
			JSONObject aclDict = JSONObject.fromObject(acl1.ToJsonString());
			assertEquals(3321, aclDict.getInt("createTime"));
			assertEquals(3322, aclDict.getInt("lastModifyTime"));
			assertEquals("principle", aclDict.getString("principle"));
			
			JSONArray privilegesArray = aclDict.getJSONArray("privilege");
			assertEquals(2, privilegesArray.size());
			assertEquals("READ", privilegesArray.getString(0));
			assertEquals("WRITE", privilegesArray.getString(1));
		} catch (LogException e) {
			assertTrue(e.GetErrorMessage(), false);
		}
		
		try {
			acl1.SetPrivilege(new ACLPrivileges());
			acl1.ToRequestJson();
		} catch (LogException e) {
			assertEquals("BadResponse", e.GetErrorCode());
			assertEquals("ACL privilege must have at least one value", e.GetErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param str
	 *            hexadecimal string without leading "0x"
	 * @return byte array: group each two contiguous characters as a byte
	 */
	private byte[] Hex2Byte(String str) {
		if (str == null) {
			return null;
		}

		int len = str.length();

		if (len == 0) {
			return null;
		}
		if (len % 2 == 1) {
			str = "0" + str;
			++len;
		}

		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer
						.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}

}

class SlsClientMockSend extends Client {
	private ResponseMessage response = new ResponseMessage();
	public SlsClientMockSend(String endpoint, String accessId, String accessKey) {
		super(endpoint, accessId, accessKey);
	}
	protected ResponseMessage SendData(String project, HttpMethod method,
			String resourceUri, Map<String, String> parameters,
			Map<String, String> headers, byte[] body) throws LogException {
		return response;
	}
	
	public void ChangeResponse(ResponseMessage response) {
		this.response = response;
	}
}

class SlsClientMock {
	private Client olsClient = new Client("http://mock-sls.aliyun-inc.com", 
			SlsClientTestData.TEST_ACCESS_KEY_ID,
			SlsClientTestData.TEST_ACCESS_KEY);

	public String GetSourceIp() {
		try {
			Field field = olsClient.getClass().getDeclaredField("sourceIp");
			field.setAccessible(true);
			return field.get(olsClient).toString();
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}

	public String GetMd5Value(byte[] bytes) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"GetMd5Value", byte[].class);
			method.setAccessible(true);
			return method.invoke(olsClient, bytes).toString();
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}

	public String BuildUrlParameter(Map<String, String> params) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"BuildUrlParameter", Map.class);
			method.setAccessible(true);
			return method.invoke(olsClient, params).toString();
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}

	public String GetSignature(String accesskey, String verb, String urlParas) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"GetSignature", String.class, String.class, String.class);
			method.setAccessible(true);
			return method.invoke(olsClient, accesskey, verb, urlParas)
					.toString();
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> ExtractJsonArray(String action, JSONObject object) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractJsonArray", String.class, JSONObject.class);
			method.setAccessible(true);
			return (ArrayList<String>) method.invoke(olsClient, action, object);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return new ArrayList<String>();
	}

	public void ExtractHistograms(GetHistogramsResponse response,
			JSONArray object) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractHistograms", GetHistogramsResponse.class,
					JSONArray.class);
			method.setAccessible(true);
			method.invoke(olsClient, response, object);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	public void ExtractLogs(GetLogsResponse response, JSONArray object) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractLogs", GetLogsResponse.class, JSONArray.class);
			method.setAccessible(true);
			method.invoke(olsClient, response, object);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}


	public void ErrorCheck(JSONObject object) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ErrorCheck", JSONObject.class, String.class);
			method.setAccessible(true);
			method.invoke(olsClient, object, "");
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				Throwable ex = ((InvocationTargetException) e)
						.getTargetException();
				if (ex instanceof LogException)
					throw (LogException) ex;
			}
			assertTrue(e.getMessage(), false);
		}
	}

	public JSONObject ParserResponseMessage(ResponseMessage response)
			throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ParserResponseMessage", ResponseMessage.class, String.class);
			method.setAccessible(true);
			return (JSONObject) method.invoke(olsClient, response, "");
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				Throwable ex = ((InvocationTargetException) e)
						.getTargetException();
				if (ex instanceof LogException)
					throw (LogException) ex;
			}
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> ExtractConfigs(JSONObject object, String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractConfigs", JSONObject.class, String.class);
			method.setAccessible(true);
			return (List<String>) method.invoke(olsClient, object, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> ExtractMachineGroups(JSONObject object, String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractMachineGroups", JSONObject.class, String.class);
			method.setAccessible(true);
			return (List<String>) method.invoke(olsClient, object, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public Config ExtractConfigFromResponse(JSONObject dict,
			String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractConfigFromResponse", JSONObject.class, String.class);
			method.setAccessible(true);
			return (Config) method.invoke(olsClient, dict, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public MachineGroup ExtractMachineGroupFromResponse(
			JSONObject dict, String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractMachineGroupFromResponse", JSONObject.class, String.class);
			method.setAccessible(true);
			return (MachineGroup) method.invoke(olsClient, dict, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public JSONObject ParserResponseMessage(ResponseMessage response,
			String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ParserResponseMessage", ResponseMessage.class, String.class);
			method.setAccessible(true);
			return (JSONObject) method.invoke(olsClient, response, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public JSONArray ParseResponseMessageToArray(ResponseMessage response,
			String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ParseResponseMessageToArray", ResponseMessage.class, String.class);
			method.setAccessible(true);
			return (JSONArray) method.invoke(olsClient, response, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public ByteArrayOutputStream ParseResponseMessageToByteStream(ResponseMessage response,
			String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ParseResponseMessageToByteStream", ResponseMessage.class, String.class);
			method.setAccessible(true);
			return (ByteArrayOutputStream) method.invoke(olsClient, response, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Shard> ExtractShards(JSONArray array, String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractShards", JSONArray.class, String.class);
			method.setAccessible(true);
			return (ArrayList<Shard>) method.invoke(olsClient, array, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ACL> ExtractACLs(JSONObject object, String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractACLs", JSONObject.class, String.class);
			method.setAccessible(true);
			return (List<ACL>) method.invoke(olsClient, object, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public ACL ExtractACLFromResponse(JSONObject dict,
			String requestId) throws LogException {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"ExtractACLFromResponse", JSONObject.class, String.class);
			method.setAccessible(true);
			return (ACL) method.invoke(olsClient, dict, requestId);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
	
	public URI GetHostURI(String project) {
		try {
			Method method = olsClient.getClass().getDeclaredMethod(
					"GetHostURI", String.class);
			method.setAccessible(true);
			return (URI) method.invoke(olsClient, project);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
		return null;
	}
}
