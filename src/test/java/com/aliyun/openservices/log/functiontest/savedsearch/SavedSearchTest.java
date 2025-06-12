package com.aliyun.openservices.log.functiontest.savedsearch;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.SavedSearch;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetSavedSearchResponse;
import com.aliyun.openservices.log.response.ListSavedSearchResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

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

    @Test
    public void testSpecialCharacters() throws Exception {
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setSavedSearchName("savedsearch-special");
        savedSearch.setLogstore("logstore-1");
        savedSearch.setDisplayName("savedsearch-special");
        savedSearch.setSearchQuery("\uD83D\uDE13❤");
        savedSearch.setTopic("test-topic111");

        client.createSavedSearch(new CreateSavedSearchRequest(TEST_PROJECT, savedSearch));
        SavedSearch savedSearch1 = client.getSavedSearch(new GetSavedSearchRequest(TEST_PROJECT, savedSearch.getSavedSearchName()))
                .getSavedSearch();
        Assert.assertEquals(savedSearch.getSavedSearchName(), savedSearch1.getSavedSearchName());
        Assert.assertEquals(savedSearch.getSearchQuery(), savedSearch1.getSearchQuery());
        Assert.assertEquals(savedSearch.getDisplayName(), savedSearch1.getDisplayName());
        Assert.assertEquals(savedSearch.getLogstore(), savedSearch1.getLogstore());
        Assert.assertEquals(savedSearch.getTopic(), savedSearch1.getTopic());
        String rawJson = savedSearch1.getRawSavedSearchAttr();
        JSONObject object = JSONObject.parseObject(rawJson);
        Assert.assertEquals(5, object.size());
        Assert.assertEquals(savedSearch.getSavedSearchName(), object.getString(Consts.CONST_SAVEDSEARCH_NAME));
        Assert.assertEquals(savedSearch.getSearchQuery(), object.getString(Consts.CONST_SAVEDSEARCH_QUERY));
        Assert.assertEquals(savedSearch.getDisplayName(), object.getString(Consts.CONST_SAVEDSEARCH_DISPLAYNAME));
        Assert.assertEquals(savedSearch.getLogstore(), object.getString(Consts.LOGSTORE_KEY));
        Assert.assertEquals(savedSearch.getTopic(), object.getString(Consts.CONST_TOPIC));
        client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedSearch.getSavedSearchName()));
    }


    static String randomString(int maxSize) {
        StringBuilder builder = new StringBuilder(maxSize);
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < maxSize; i++) {
            builder.append(CHARS.charAt(randomInt(CHARS.length())));
        }
        return builder.toString();
    }

    @Test
    public void testPayloadLengthBeyondLimit() throws Exception {
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setSavedSearchName("savedsearch-special");
        savedSearch.setLogstore("logstore-1");
        savedSearch.setDisplayName("savedsearch-special");
        String bigQuery = randomString(65535);
        savedSearch.setSearchQuery(bigQuery);
        savedSearch.setTopic("test-topic111");

        try {
            client.createSavedSearch(new CreateSavedSearchRequest(TEST_PROJECT, savedSearch));
            Assert.fail("Should fail as too long");
        } catch (LogException ex) {
            Assert.assertEquals(400, ex.getHttpCode());
            Assert.assertEquals("PostBodyInvalid", ex.getErrorCode());
            Assert.assertEquals("The savedsearch configuration is too long.", ex.GetErrorMessage());
        }
        savedSearch.setSearchQuery(randomString(100));
        // OK
        client.createSavedSearch(new CreateSavedSearchRequest(TEST_PROJECT, savedSearch));
        savedSearch.setSearchQuery(bigQuery);
        try {
            client.updateSavedSearch(new UpdateSavedSearchRequest(TEST_PROJECT, savedSearch));
            Assert.fail("Should fail as too long");
        } catch (LogException ex) {
            Assert.assertEquals(400, ex.getHttpCode());
            Assert.assertEquals("PostBodyInvalid", ex.getErrorCode());
            Assert.assertEquals("The savedsearch configuration is too long.", ex.GetErrorMessage());
        }
        client.deleteSavedSearch(new DeleteSavedSearchRequest(TEST_PROJECT, savedSearch.getSavedSearchName()));
    }
}
