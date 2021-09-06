package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.SavedSearch;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateSavedSearchRequest;
import com.aliyun.openservices.log.request.DeleteSavedSearchRequest;
import com.aliyun.openservices.log.request.GetSavedSearchRequest;
import com.aliyun.openservices.log.request.ListSavedSearchRequest;
import com.aliyun.openservices.log.request.UpdateSavedSearchRequest;
import com.aliyun.openservices.log.response.GetSavedSearchResponse;
import com.aliyun.openservices.log.response.ListSavedSearchResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SavedSearchTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testCRUD() throws LogException {
        String savedsearchName = "savedsearchtest";
        for (int i = 0; i < 100; i++) {
            SavedSearch savedSearch = new SavedSearch();
            savedSearch.setSavedSearchName(savedsearchName + i);
            savedSearch.setLogstore("logstore-1");
            savedSearch.setDisplayName(savedsearchName + i);
            savedSearch.setSearchQuery("*");
            client.createSavedSearch(new CreateSavedSearchRequest(TEST_PROJECT, savedSearch));
        }
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setSavedSearchName(savedsearchName + "100");
        savedSearch.setLogstore("logstore-1");
        savedSearch.setDisplayName(savedsearchName);
        savedSearch.setSearchQuery("*");
        try {
            client.createSavedSearch(new CreateSavedSearchRequest(TEST_PROJECT, savedSearch));
            fail("createSavedSearch should fail");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "savedsearch quota exceed");
            assertEquals(ex.GetErrorCode(), "ExceedQuota");
        }
        for (int i = 0; i < 100; i++) {
            String name = savedsearchName + i;
            for (int j = 0; j < 10; j++) {
                // Test cache hit
                GetSavedSearchResponse getSavedSearchResponse = client.getSavedSearch(new GetSavedSearchRequest(TEST_PROJECT, name));
                SavedSearch getSavedSearch = getSavedSearchResponse.getSavedSearch();
                assertEquals(getSavedSearch.getSavedSearchName(), name);
                assertEquals(getSavedSearch.getDisplayName(), name);
                assertEquals(getSavedSearch.getSearchQuery(), "*");
                assertEquals(getSavedSearch.getLogstore(), "logstore-1");
            }
        }
        for (int idx = 0; idx < 10; idx++) {
            // Test cache hit
            ListSavedSearchResponse listSavedSearch = client.listSavedSearch(new ListSavedSearchRequest(TEST_PROJECT));
            assertEquals(listSavedSearch.getCount(), 100);
            assertEquals(listSavedSearch.getTotal(), 100);
            List<SavedSearch> savedSearches = listSavedSearch.getSavedSearches();
            for (SavedSearch search : savedSearches) {
                assertTrue(search.getSavedSearchName().startsWith(savedsearchName));
                assertTrue(search.getDisplayName().startsWith(savedsearchName));
            }
        }

        //update
        SavedSearch updateSaveSearch = new SavedSearch();
        updateSaveSearch.setSavedSearchName(savedsearchName + 0);
        updateSaveSearch.setLogstore("logstore-2");
        updateSaveSearch.setDisplayName(savedsearchName + "update");
        updateSaveSearch.setSearchQuery("*");
        client.updateSavedSearch(new UpdateSavedSearchRequest(TEST_PROJECT, updateSaveSearch));

        //get
        GetSavedSearchResponse getSavedSearchResponse = client.getSavedSearch(new GetSavedSearchRequest(TEST_PROJECT, savedsearchName + 0));
        SavedSearch getSavedSearch = getSavedSearchResponse.getSavedSearch();
        assertEquals(getSavedSearch.getSavedSearchName(), savedsearchName + 0);
        assertEquals(getSavedSearch.getDisplayName(), savedsearchName + "update");
        assertEquals(getSavedSearch.getSearchQuery(), "*");
        assertEquals(getSavedSearch.getLogstore(), "logstore-2");

        //delete
        for (int i = 0; i < 100; i++) {
            client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedsearchName + i));
            try {
                client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedsearchName + i));
                fail("saved search should not exist");
            } catch (LogException ex) {
                assertEquals(ex.GetErrorCode(), "SavedSearchNotExist");
                assertEquals(ex.GetErrorMessage(), "specified savedsearch does not exist");
            }
        }
        try {
            client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedsearchName + "100"));
            fail("deleteSavedSearch not exist savedsearch");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "specified savedsearch does not exist");
            assertEquals(ex.GetErrorCode(), "SavedSearchNotExist");
        }
    }
}
