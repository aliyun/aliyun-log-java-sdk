package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateAlertRequest;
import com.aliyun.openservices.log.request.DeleteAlertRequest;
import com.aliyun.openservices.log.request.GetAlertRequest;
import com.aliyun.openservices.log.request.ListAlertRequest;
import com.aliyun.openservices.log.request.UpdateAlertRequest;
import com.aliyun.openservices.log.response.GetAlertResponse;
import com.aliyun.openservices.log.response.ListAlertResponse;

public class AlertSample {
	public static void main(String args[]) {
		String accessId = "";
		String accessKey = "";

		String project = "ali-cn-devcommon-sls-admin";
		String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
		String alertName = "test-alert";
		
		Client client = new Client(host, accessId, accessKey);
		Alert alert = new Alert();
		alert.setAlertName(alertName);
		alert.setSavedSearchName("test-savedsearch");
		alert.setRoleArn("test-rolearn");
		alert.setFrom("-1h");
		alert.setTo("now");
		alert.setCheckInterval(5); // 5min
		alert.setCount(1);
		alert.setAlertKey("ProjectName");
		alert.setComparator("like");
		alert.setAlertValue("test");
		alert.setActionType("sms");
		alert.setPhoneNumber("12345678");
		
		try {
			// create
			CreateAlertRequest createReq = new CreateAlertRequest(project, alert);
			client.createAlert(createReq);
			
			// get 
			GetAlertRequest getReq = new GetAlertRequest(project, alertName);
			GetAlertResponse getRes = client.getAlert(getReq);
			System.out.println(getRes.getAlert().getAlertName());
			System.out.println(getRes.getAlert().getSavedSearchName());
			System.out.println(getRes.getAlert().getRoleArn());
			System.out.println(getRes.getAlert().getFrom());
			System.out.println(getRes.getAlert().getTo());
			System.out.println(getRes.getAlert().getCheckInterval());
			System.out.println(getRes.getAlert().getCheckInterval());
			System.out.println(getRes.getAlert().getAlertKey());
			System.out.println(getRes.getAlert().getComparator());
			System.out.println(getRes.getAlert().getAlertValue());
			System.out.println(getRes.getAlert().getActionType());
			System.out.println(getRes.getAlert().getPhoneNumber());
			
			// update
			alert.setPhoneNumber("1382122");
			UpdateAlertRequest updateReq = new UpdateAlertRequest(project, alert);
			client.updateAlert(updateReq);
			
			// list
			ListAlertRequest listReq = new ListAlertRequest(project);
			ListAlertResponse listRes = client.listAlert(listReq);
			for (Alert returnAlert:listRes.getAlerts()) {
				System.out.println(returnAlert.getAlertName());
			}
			
			// delete 
			DeleteAlertRequest deleteReq = new DeleteAlertRequest(project, alertName);
			client.deleteAlert(deleteReq);

		} catch (LogException ex) {
			System.out.println(ex.GetErrorCode());
			System.out.println(ex.GetErrorMessage());
		}
	}
}
