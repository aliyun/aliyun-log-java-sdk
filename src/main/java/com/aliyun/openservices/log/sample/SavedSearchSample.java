package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.SavedSearch;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateSavedSearchRequest;
import com.aliyun.openservices.log.request.DeleteSavedSearchRequest;
import com.aliyun.openservices.log.request.GetSavedSearchRequest;
import com.aliyun.openservices.log.request.ListSavedSearchRequest;
import com.aliyun.openservices.log.request.UpdateSavedSearchRequest;
import com.aliyun.openservices.log.response.GetSavedSearchResponse;
import com.aliyun.openservices.log.response.ListSavedSearchResponse;

public class SavedSearchSample {
	public static void main(String args[]) throws LogException {
		String accessId = "";
		String accessKey = "";

		String project = "ali-cn-devcommon-sls-admin";
		String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
		String logStore = "sls_quotaserver_log";
		String savedSearchName = "test-savedsearch";
		
		Client client = new Client(host, accessId, accessKey);
		SavedSearch savedSearch = new SavedSearch();
		savedSearch.setSavedSearchName(savedSearchName);
		savedSearch.setSearchQuery("");
		savedSearch.setLogstore(logStore);
		savedSearch.setTopic("");
		
		try {
			// create
			CreateSavedSearchRequest createReq = new CreateSavedSearchRequest(project, savedSearch);
			client.createSavedSearch(createReq);
			
			// get
			GetSavedSearchRequest getReq = new GetSavedSearchRequest(project, savedSearchName);
			GetSavedSearchResponse getRes = client.getSavedSearch(getReq);
			System.out.println(getRes.getSavedSearch().getSavedSearchName());
			System.out.println(getRes.getSavedSearch().getLogstore());
			System.out.println(getRes.getSavedSearch().getTopic());
			System.out.println(getRes.getSavedSearch().getSearchQuery());
			
			// update
			savedSearch.setSearchQuery("test");
			UpdateSavedSearchRequest updateReq = new UpdateSavedSearchRequest(project, savedSearch);
			client.updateSavedSearch(updateReq);
			
			// list 
			ListSavedSearchRequest listReq = new ListSavedSearchRequest(project);
			ListSavedSearchResponse listRes = client.listSavedSearch(listReq);
			for (SavedSearch returnSavedSearch:listRes.getSavedSearches()) {
				System.out.println(returnSavedSearch.getSavedSearchName());
			}
			
			// delete 
			DeleteSavedSearchRequest deleteReq = new DeleteSavedSearchRequest(project, savedSearchName);
			client.deleteSavedSearch(deleteReq);

		} catch (LogException ex) {
			System.out.println(ex.GetErrorCode());
			System.out.println(ex.GetErrorMessage());
		}
	}
}
