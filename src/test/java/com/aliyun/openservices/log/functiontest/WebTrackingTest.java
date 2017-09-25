package com.aliyun.openservices.log.functiontest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;

public class WebTrackingTest {
	static private String accessId = "access key id";
	static private String accessKey = "access key";

	static private String project = "";
	static private String host = "";
	static private String logStore = "";

	static private Client client = new Client(host, accessId, accessKey);
	public boolean TestPutLogs()
	{
		HttpClient httpClient = new DefaultHttpClient();
		String params = "APIVersion=0.6.0";
		for(int i = 0; i < 100; ++i)
		{
			params += "&key" + i + "=" + "value" + i;
		}
		HttpGet httpGet = new HttpGet("http://" + project + "." + host + "/logstores/" + logStore + "/track?" + params);
		int len = httpGet.toString().length();
		try {
			HttpResponse res = httpClient.execute(httpGet);
			
			if(res.getStatusLine().getStatusCode() / 200 != 1)
			{
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		return true;
	}
	@Test
	public void TestWebTracking() throws InterruptedException {
		try {
			client.CreateProject(project, "test web tracking");
		} catch (LogException e) {
			assertTrue(e.GetErrorCode().compareTo("ProjectAlreadyExist") == 0);
		}
		Thread.sleep(60 * 1000);
		try {
			client.DeleteLogStore(project, logStore);
		} catch (LogException e) {
		}
		Thread.sleep(60 * 1000);
		try {
			client.CreateLogStore(project, new LogStore(logStore, 1, 2, true));
		} catch (LogException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		Thread.sleep(60 * 1000);
		assertTrue(TestPutLogs());
		try 
		{
			client.UpdateLogStore(project, new LogStore(logStore, 1, 2, true));
			assertTrue(false);
		}
		catch (LogException e) 
		{
			assertTrue(e.GetErrorCode().compareTo("ParameterInvalid") == 0);
		}
		try {
			client.UpdateLogStore(project, new LogStore(logStore, 1, 2, false));
		} catch (LogException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		Thread.sleep(60 * 1000);
		assertTrue(!TestPutLogs());
		try {
			client.UpdateLogStore(project, new LogStore(logStore, 1, 2, true));
		} catch (LogException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		Thread.sleep(60 * 1000);
		assertTrue(TestPutLogs());
	}
}
