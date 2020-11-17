package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.SavedSearch;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetSavedSearchResponse;
import com.aliyun.openservices.log.response.ListSavedSearchResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SavedSearchTest extends FunctionTest {

    private static final String TEST_PROJECT = "test-project-to-savedsearch-" + getNowTimestamp();

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "savedsearch test");
        waitForSeconds(5);
    }

    @Test
    public void testCRUD() throws LogException {
        String savedsearchName = "savedsearchtest";
        //create
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
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "savedsearch quota exceed");
            assertEquals(ex.GetErrorCode(), "ExceedQuota");
        }
        try {
            GetSavedSearchResponse getSavedSearchResponse = client.getSavedSearch(new GetSavedSearchRequest(TEST_PROJECT, savedsearchName + 0));
            SavedSearch getSavedSearch = getSavedSearchResponse.getSavedSearch();
            assertEquals(getSavedSearch.getSavedSearchName(), savedsearchName + 0);
            assertEquals(getSavedSearch.getDisplayName(), savedsearchName + 0);
            assertEquals(getSavedSearch.getSearchQuery(), "*");
            assertEquals(getSavedSearch.getLogstore(), "logstore-1");
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //list
        try {
            ListSavedSearchResponse listSavedSearch = client.listSavedSearch(new ListSavedSearchRequest(TEST_PROJECT));
            assertEquals(listSavedSearch.getCount(), 100);
            assertEquals(listSavedSearch.getTotal(), 100);
            List<SavedSearch> savedSearches = listSavedSearch.getSavedSearches();
            for (SavedSearch search : savedSearches) {
                assertTrue(search.getSavedSearchName().startsWith(savedsearchName));
                assertTrue(search.getDisplayName().startsWith(savedsearchName));
            }
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //update
        try {
            SavedSearch updateSaveSearch = new SavedSearch();
            updateSaveSearch.setSavedSearchName(savedsearchName + 0);
            updateSaveSearch.setLogstore("logstore-2");
            updateSaveSearch.setDisplayName(savedsearchName + "update");
            updateSaveSearch.setSearchQuery("*");
            client.updateSavedSearch(new UpdateSavedSearchRequest(TEST_PROJECT, updateSaveSearch));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //get
        try {
            GetSavedSearchResponse getSavedSearchResponse = client.getSavedSearch(new GetSavedSearchRequest(TEST_PROJECT, savedsearchName + 0));
            SavedSearch getSavedSearch = getSavedSearchResponse.getSavedSearch();
            assertEquals(getSavedSearch.getSavedSearchName(), savedsearchName + 0);
            assertEquals(getSavedSearch.getDisplayName(), savedsearchName + "update");
            assertEquals(getSavedSearch.getSearchQuery(), "*");
            assertEquals(getSavedSearch.getLogstore(), "logstore-2");
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //delete
        for (int i = 0; i < 100; i++) {
            client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedsearchName + i));
        }
        try {
            client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedsearchName + "100"));
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "specified savedsearch does not exist");
            assertEquals(ex.GetErrorCode(), "SavedSearchNotExist");
        }
    }

    @After
    public void tearDown() throws Exception {
        client.DeleteProject(TEST_PROJECT);
    }
}
