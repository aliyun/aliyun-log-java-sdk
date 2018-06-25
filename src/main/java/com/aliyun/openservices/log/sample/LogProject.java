package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetProjectResponse;

public class LogProject {

	public static void main(String args[]) throws LogException {
 
		//String accessId = "";
		//String accessKey = "=";

		String accessId = "";
		String accessKey = "";
		
		String project = "";
		String host = "";
		String logStore = "";
		String topic = "";

		int shardId = 0;

		/*
		 * 构建一个client
		 */
		Client client = new Client(host, accessId, accessKey);
		//client.CreateProject(project, "sample_");
		//client.DeleteProject(project);
		client.CreateProject(project, "sample_");
		GetProjectResponse r = client.GetProject(project);
		System.out.println(r.GetProjectOwner());
		System.out.println(r.GetProjectRegion());
		System.out.println(r.GetProjectStatus());
		System.out.println(r.GetProjectDescription());
	}

}
