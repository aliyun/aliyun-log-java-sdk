/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.ListTopicsResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class SlsSample {

	public static void main(String args[]) throws LogException,
			InterruptedException {

		String accessId = "your_access_id";
		String accessKey = "your_access_key";

		String project = "your_project_name";
		String host = "";
		String logStore = "your_logstore";
		String topic = "TestTopic_2";

		int shardId = 0;
		
		/*
		 * 构建一个client
		 */
		Client client = new Client(host, accessId, accessKey);

		String cursor = client.GetCursor(project, logStore, shardId, CursorMode.END).GetCursor();
		System.out.println("cursor = " +cursor);
		try {
			while (true) {
				PullLogsRequest request = new PullLogsRequest(project, logStore, shardId, 1000, cursor);
				PullLogsResponse response = client.pullLogs(request);
				System.out.println(response.getCount());
				System.out.println("cursor = " + cursor + " next_cursor = " + response.getNextCursor());
				if (cursor.equals(response.getNextCursor())) {
				    break;
                }
				cursor = response.getNextCursor();
				Thread.sleep(200);
			}
		}
		catch(LogException e) {
			System.out.println(e.GetRequestId() + e.GetErrorMessage());
		}
	
		if (shardId > 0)
		{
			return;
		}
		
		
		int from_t = (int) (new Date().getTime() / 1000) - 36000;
		int to_t  = (int) (new Date().getTime() / 1000) - 100;
		GetLogsResponse res = client.GetLogs(project, logStore, from_t, to_t, topic, "", 10, 0, false);

		System.out.println("Returned log data count:" + res.GetCount());
		for (QueriedLog log : res.GetLogs()) {
			System.out.println("source : " + log.GetSource());
			LogItem item = log.GetLogItem();
			System.out.println("time : " + item.mLogTime);
			for (LogContent content : item.mContents) {
				System.out.println(content.mKey + ":" + content.mValue);
			}
		}
		
		
		// generate two logs
		int log_group_num = 10;
		/**
		 * 向SLS发送一个日志包，每个日志包中，有2行日志
		 */
		for (int i = 0; i < log_group_num; i++) {
			Vector<LogItem> logGroup = new Vector<LogItem>();
			LogItem logItem = new LogItem((int) (new Date().getTime() / 1000));
			logItem.PushBack("level", "info");
			logItem.PushBack("name", String.valueOf(i));
			logItem.PushBack("message", "it's a test message");

			logGroup.add(logItem);

			LogItem logItem2 = new LogItem((int) (new Date().getTime() / 1000));
			logItem2.PushBack("level", "error");
			logItem2.PushBack("name", String.valueOf(i));
			logItem2.PushBack("message", "it's a test message");
			logGroup.add(logItem2);

			try {
				client.PutLogs(project, logStore, topic, logGroup, "");
			} catch (LogException e) {
				System.out.println("error code :" + e.GetErrorCode());
				System.out.println("error message :" + e.GetErrorMessage());
				System.out.println("error requestId :" + e.GetRequestId());
				throw e;
			}

		}
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {

		}
		try {
			/**
			 * 查询project中所有logStore的名字
			 */
			ArrayList<String> logStores = client.ListLogStores(project,0,500,"")
					.GetLogStores();
			System.out.println("ListLogs:" + logStores.toString() + "\n");
		} catch (LogException e) {
			System.out.print(e.getCause());
			System.out.println("error code :" + e.GetErrorCode());
			System.out.println("error message :" + e.GetErrorMessage());
			System.out.println("error requestId :" + e.GetRequestId());
			throw e;
		}

		/**
		 * 查询logstore中的topic的名字
		 */
		try {
			String token = "";
			ListTopicsResponse listTopicResponse = client.ListTopics(project,
					logStore, token, 10);
			ArrayList<String> topics = listTopicResponse.GetTopics();
			System.out.println("ListTopics:" + topics.toString());
			System.out.println("NextTopic:" + listTopicResponse.GetNextToken() + "\n");
		} catch (LogException e) {
			System.out.println("error code :" + e.GetErrorCode());
			System.out.println("error message :" + e.GetErrorMessage());
			System.out.println("error requestId :" + e.GetRequestId());
			throw e;
		}

		/**
		 * 查询logstore的histogram信息
		 */
		try {
			String query = "";
			int from = (int) (new Date().getTime() / 1000 - 10000);
			int to = (int) (new Date().getTime() / 1000 + 10);
			GetHistogramsResponse histogramsResponse = client.GetHistograms(
					project, logStore, from, to, topic, query);
			System.out.println("histogram result: " + histogramsResponse.GetTotalCount());
			System.out.println("is_completed : "
					+ histogramsResponse.IsCompleted());
			for (Histogram histogram : histogramsResponse.GetHistograms()) {
				System.out.println("beginTime:" + histogram.mFromTime
						+ " endTime:" + histogram.mToTime + " logCount:"
						+ histogram.mCount + " is_completed:"
						+ histogram.mIsCompleted);
			}
			System.out.println("");
		} catch (LogException e) {
			System.out.println("error code :" + e.GetErrorCode());
			System.out.println("error message :" + e.GetErrorMessage());
			System.out.println("error requestId :" + e.GetRequestId());
			throw e;
		}

	
		/**
		 * 查询logstore的内容
		 */
		try {
			String query = "error";
			int from = (int) (new Date().getTime() / 1000 - 10000);
			int to = (int) (new Date().getTime() / 1000 + 10);
			GetLogsResponse logsResponse = client.GetLogs(project, logStore,
					from, to, topic, query, 10, 0, false);
			System.out.println("Returned log data count:" + logsResponse.GetCount());
			for (QueriedLog log : logsResponse.GetLogs()) {
				System.out.println("source : " + log.GetSource());
				LogItem item = log.GetLogItem();
				System.out.println("time : " + item.mLogTime);
				for (LogContent content : item.mContents) {
					System.out.println(content.mKey + ":" + content.mValue);
				}
			}
		} catch (LogException e) {
			System.out.println("error code :" + e.GetErrorCode());
			System.out.println("error message :" + e.GetErrorMessage());
			throw e;
		}

	}
}
