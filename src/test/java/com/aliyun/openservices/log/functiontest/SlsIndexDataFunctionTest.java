package com.aliyun.openservices.log.functiontest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.common.IndexLine;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListTopicsResponse;

public class SlsIndexDataFunctionTest {

	static private String accessId = "";
	static private String accessKey = "";

	static private String project = "ali-cn-yunlei-sls-admin";
	static private String host = "";
	static private String logStore = "byls-one-3";

	private int startTime = (int) (new Date().getTime() / 1000);
	private String topic_prefix = "sls_java_topic_" + String.valueOf(startTime)
			+ "_";

	static private Client client = new Client(host, accessId, accessKey);

	public SlsIndexDataFunctionTest() {

	}

	@BeforeClass
	public static void SetupOnce() {
		try {
			client.DeleteIndex(project, logStore);
			Thread.sleep(5000);
		} catch (LogException e) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		LogStore logStoreRes = new LogStore(logStore, 1, 1);
		
		try {
			client.CreateLogStore(project, logStoreRes);
			Thread.sleep(10000);
		} catch (LogException e) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void CleanUpOnce() {
		try {
			client.DeleteLogStore(project, logStore);
			Thread.sleep(1000);
		} catch (LogException e) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void TestCreateIndex() {
		Index index = new Index();
		index.SetTtl(7);

		IndexLine line = new IndexLine();
		line.SetCaseSensitive(false);
		List<String> token = new ArrayList<String>();
		token.add(";");
		line.SetToken(token);
		
		List<String> includeKeys = new ArrayList<String>();
		includeKeys.add("ID");
		line.SetIncludeKeys(includeKeys);

		index.SetLine(line);
		
		try {
			client.CreateIndex(project, logStore, index);
			Thread.sleep(60 * 1000);
			
			Index res = client.GetIndex(project, logStore).GetIndex();
			org.junit.Assert.assertEquals(index.GetTtl(), res.GetTtl());

			org.junit.Assert.assertEquals(index.GetLine().GetCaseSensitive(), res.GetLine().GetCaseSensitive());
			org.junit.Assert.assertEquals(index.GetLine().GetToken().size(), res.GetLine().GetToken().size());
			for (int i = 0;i < index.GetLine().GetToken().size();i++) {
				org.junit.Assert.assertEquals(index.GetLine().GetToken().get(i), res.GetLine().GetToken().get(i));
			}
			
			org.junit.Assert.assertEquals(index.GetLine().GetIncludeKeys().size(), res.GetLine().GetIncludeKeys().size());
			for (int i = 0;i < index.GetLine().GetIncludeKeys().size();i++) {
				org.junit.Assert.assertEquals(index.GetLine().GetIncludeKeys().get(i), res.GetLine().GetIncludeKeys().get(i));
			}
			
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void TestDeleteIndex() {

		try {
			client.DeleteIndex(project, logStore);
			
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
		
		try {
			client.GetIndex(project, logStore);
			org.junit.Assert.assertTrue("should get index error", false);
		} catch (LogException e) {
			org.junit.Assert.assertTrue("should get index error"+e.GetErrorCode(), e.GetErrorCode()!="IndexConfigNotExist");
		}
	}
	
	public void TestUpdateIndex() {
		Index index = new Index();
		index.SetTtl(7);
		IndexKeys keys = new IndexKeys();
		IndexKey keyContent = new IndexKey();
		keyContent.SetCaseSensitive(false);
		List<String> token = new ArrayList<String>();
		token.add(";");
		keyContent.SetToken(token);
		String keyName = "test";
		keys.AddKey(keyName, keyContent);
		index.SetKeys(keys);
		
		try {
			client.UpdateIndex(project, logStore, index);
			
			Index res = client.GetIndex(project, logStore).GetIndex();
			org.junit.Assert.assertEquals(index.GetTtl(), res.GetTtl());
			IndexKeys resKeys = res.GetKeys();
			org.junit.Assert.assertEquals(1, resKeys.GetKeys().size());
			org.junit.Assert.assertTrue(resKeys.GetKeys().containsKey(keyName));
			IndexKey resKey = resKeys.GetKeys().get(keyName);
			org.junit.Assert.assertEquals(keyContent.GetCaseSensitive(), resKey.GetCaseSensitive());
			org.junit.Assert.assertEquals(keyContent.GetToken().size(), resKey.GetToken().size());
			for (int i = 0;i < keyContent.GetToken().size();i++) {
				org.junit.Assert.assertEquals(keyContent.GetToken().get(i), resKey.GetToken().get(i));
			}
			
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
	}

	public void TestPutData() {
		for (int i = 0; i < 10; i++) {
			Vector<LogItem> logGroup = new Vector<LogItem>();
			for (int j = 0; j < 600; j++) {
				LogItem logItem = new LogItem(
						(int) (new Date().getTime() / 1000));
				logItem.PushBack("ID", "id_" + String.valueOf(i * 600 + j));
				logGroup.add(logItem);
			}
			String topic = topic_prefix + String.valueOf(i);
			try {
				client.PutLogs(project, logStore, topic, logGroup, "");
			} catch (LogException e) {
				org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
			}
		}
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void TestGetLogStore() {
		try {
			ListLogStoresResponse res = client.ListLogStores(project,0,500,"");
			org.junit.Assert.assertTrue(res.GetLogStores().contains(logStore));
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
	}

	public void TestListTopics() {
		try {
			ArrayList<String> returnTopics = new ArrayList<String>();
			String token = topic_prefix;
			ListTopicsResponse res = client.ListTopics(project, logStore,
					token, 5);
			returnTopics.addAll(res.GetTopics());
			org.junit.Assert.assertEquals(res.GetNextToken(), topic_prefix
					+ String.valueOf(5));
			token = res.GetNextToken();
			res = client.ListTopics(project, logStore, token, 5);
			returnTopics.addAll(res.GetTopics());
			org.junit.Assert.assertTrue(res.GetNextToken().startsWith(
					topic_prefix) == false);
			org.junit.Assert.assertEquals(returnTopics.size(), 10);
			for (int i = 0; i < returnTopics.size(); i++) {
				org.junit.Assert.assertEquals(returnTopics.get(i), topic_prefix
						+ String.valueOf(i));
			}
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
	}

	public void TestGetHistogram() {
		try {
			GetHistogramsResponse res = client.GetHistograms(project, logStore,
					this.startTime, this.startTime + 3600, 
					topic_prefix + String.valueOf(0), "ID");
			org.junit.Assert.assertEquals(res.GetTotalCount(), 600);
			ArrayList<Histogram> histograms = res.GetHistograms();
			int total = 0;
			for (Histogram histogram : histograms) {
				org.junit.Assert.assertEquals(histogram.IsCompleted(), true);
				total += histogram.GetCount();
			}
			org.junit.Assert.assertEquals(total, 600);
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
	}

	public void TestGetLogs() {
		int topic_index = 3;
		String topic = topic_prefix + String.valueOf(topic_index);
		try {
			GetLogsResponse res = client.GetLogs(project, logStore,
					this.startTime, this.startTime + 3600, topic, "ID", 20, 0, false);
			org.junit.Assert.assertEquals(res.GetCount(), 20);
			res = client.GetLogs(project, logStore,
					this.startTime, this.startTime + 3600, topic, "ID", 100, 50, false);
			org.junit.Assert.assertEquals(res.GetCount(), 100);
			org.junit.Assert.assertEquals(res.IsCompleted(), true);
			ArrayList<QueriedLog> queriedLogs = res.GetLogs();
			int index = 0;
			for (QueriedLog log : queriedLogs) {
				LogItem item = log.GetLogItem();
				org.junit.Assert.assertEquals(item.GetLogContents().get(0)
						.GetValue(),
						"id_" + String.valueOf(topic_index * 600 + 50 + index));
				index++;
			}
		} catch (LogException e) {
			org.junit.Assert.assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
		}
	}

	@Test
	public void TestAll() {
		TestCreateIndex();
		TestPutData();
		try {
			Thread.sleep(45 * 1000);
		} catch (InterruptedException e) {

		}
		TestGetLogStore();
		TestListTopics();
		TestGetHistogram();
		TestGetLogs();
		TestUpdateIndex();
		TestDeleteIndex();
	}
}
