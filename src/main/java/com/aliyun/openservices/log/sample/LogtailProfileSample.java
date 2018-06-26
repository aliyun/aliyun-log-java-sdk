package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogtailProfile;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogtailProfileResponse;

public class LogtailProfileSample {

	public static void main(String[] args) throws LogException {
		String akId = "";
		String ak = "=";
		String project = "";
		String host = "";
		String logstore = "";
		String ip = "";
		Client client = new Client(host, akId, ak);
		GetLogtailProfileResponse response = client.GetLogtailProfile(project, logstore, ip, 100, 0);
		System.out.println(response.getCount());
		System.out.println(response.getTotal());
		for (LogtailProfile logtailProfile: response.getLogtailProfiles()) {
			System.out.println(logtailProfile.getTime()); // occur time
			System.out.println(logtailProfile.getIp()); // logtail machine ip
			System.out.println(logtailProfile.getMachineOS()); // logtail machine os
			System.out.println(logtailProfile.getAlarmCount()); // every 5min report error
			System.out.println(logtailProfile.getAlarmType());
			System.out.println(logtailProfile.getAlarmMessage());
		}
	}

}
