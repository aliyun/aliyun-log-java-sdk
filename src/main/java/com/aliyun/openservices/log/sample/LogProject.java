package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetProjectResponse;

public class LogProject {

	public static void main(String args[]) {

		String endpoint = "";
		String accessKeyID = "test_accessKeyId";
		String accessKeySecret = "test_accessKey";

		String project = "test-project";
		// create a client
		Client client = new Client(endpoint, accessKeyID, accessKeySecret);

		try {
			client.CreateProject(project, "this is a sample project");

			GetProjectResponse r = client.GetProject(project);
			System.out.println(r.GetProjectOwner());
			System.out.println(r.GetProjectRegion());
			System.out.println(r.GetProjectStatus());
			System.out.println(r.GetProjectDescription());
		} catch (LogException e) {
			e.printStackTrace();
		}
	}

}
