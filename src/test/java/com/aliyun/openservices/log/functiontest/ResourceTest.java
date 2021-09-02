package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.ResourceName;
import com.aliyun.openservices.log.common.ResourceRecord;
import com.aliyun.openservices.log.common.ResourceUser;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.DeleteResourceRecordResponse;
import com.aliyun.openservices.log.response.GetResourceRecordResponse;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ResourceTest extends FunctionTest {
    private static final String userId = "test_user_1";
    private static final String userName = "test user name";
    private static final String userNewName = "test new user name";

    @Test
    public void testCrud() throws LogException {
        testDeleteResourceUser();
        testCreateResourceUser();
        testUpsertResourceUser();
        testGetResourceUser();
        testUpdateResourceUser();
        testDeleteResourceUser();
    }

    public void testCreateResourceUser() throws LogException {
        ResourceUser resourceUser = new ResourceUser();
        resourceUser.setUserId(userId);
        resourceUser.setUserName(userName);
        resourceUser.setCountryCode("86");
        resourceUser.setPhone("13888888888");
        resourceUser.setEmail(new ArrayList<String>() {{
            add("test_a@test.com");
            add("test_b@test.com");
        }});
        resourceUser.setEnabled(true);
        resourceUser.setVoiceEnabled(false);
        resourceUser.setSmsEnabled(true);

        String resourceUserJsonString = JSONObject.toJSONString(resourceUser);
        ResourceRecord resourceRecord = new ResourceRecord();
        resourceRecord.setId(userId);
        resourceRecord.setTag(userName);
        resourceRecord.setValue(resourceUserJsonString);

        CreateResourceRecordRequest createResourceRecordRequest = new CreateResourceRecordRequest(ResourceName.USER.toString(), resourceRecord);
        client.createResourceRecord(createResourceRecordRequest);
    }

    public void testUpsertResourceUser() throws LogException {
        ResourceUser resourceUser = new ResourceUser();
        resourceUser.setUserId(userId);
        resourceUser.setUserName(userName);
        resourceUser.setCountryCode("86");
        resourceUser.setPhone("13888888888");
        resourceUser.setEmail(new ArrayList<String>() {{
            add("test_a@test.com");
            add("test_b@test.com");
        }});
        resourceUser.setEnabled(true);
        resourceUser.setVoiceEnabled(false);
        resourceUser.setSmsEnabled(true);

        String resourceUserJsonString = JSONObject.toJSONString(resourceUser);
        ResourceRecord resourceRecord = new ResourceRecord();
        resourceRecord.setId(userId);
        resourceRecord.setTag(userName);
        resourceRecord.setValue(resourceUserJsonString);

        UpsertResourceRecordRequest upsertResourceRecordRequest = new UpsertResourceRecordRequest(ResourceName.USER.toString(), resourceRecord);
        client.upsertResourceRecord(upsertResourceRecordRequest);
    }

    public void testGetResourceUser() throws LogException {
        GetResourceRecordRequest getResourceRecordRequest = new GetResourceRecordRequest(ResourceName.USER.toString(), userId);
        GetResourceRecordResponse response = client.getResourceRecord(getResourceRecordRequest);
        ResourceRecord record = response.getRecord();
        assertEquals(record.getId(), userId);
        assertEquals(record.getTag(), userName);
        String userJsonString = record.getValue();
        assertNotNull(userJsonString);
        ResourceUser resourceUser = JSONObject.parseObject(userJsonString, ResourceUser.class);
        assertTrue(resourceUser.isEnabled());
        assertEquals(resourceUser.getUserId(), userId);
        assertEquals(resourceUser.getUserName(), userName);
        assertEquals(resourceUser.getPhone(), "13888888888");
    }


    public void testUpdateResourceUser() throws LogException {
        GetResourceRecordRequest getResourceRecordRequest = new GetResourceRecordRequest(ResourceName.USER.toString(), userId);
        GetResourceRecordResponse response = client.getResourceRecord(getResourceRecordRequest);
        ResourceRecord record = response.getRecord();
        assertEquals(record.getId(), userId);
        assertEquals(record.getTag(), userName);
        String userJsonString = record.getValue();
        assertNotNull(userJsonString);
        ResourceUser resourceUser = JSONObject.parseObject(userJsonString, ResourceUser.class);
        resourceUser.setUserName(userNewName);
        record.setValue(JSONObject.toJSONString(resourceUser));

        // update user name
        UpdateResourceRecordRequest updateResourceRecordRequest = new UpdateResourceRecordRequest(ResourceName.USER.toString(), record);
        client.updateResourceRecord(updateResourceRecordRequest);

        // get new user
        response = client.getResourceRecord(getResourceRecordRequest);
        record = response.getRecord();
        assertEquals(record.getId(), userId);
        assertEquals(record.getTag(), userName);
        userJsonString = record.getValue();
        assertNotNull(userJsonString);
        resourceUser = JSONObject.parseObject(userJsonString, ResourceUser.class);
        assertEquals(resourceUser.getUserName(), userNewName);
    }

    public void testDeleteResourceUser() throws LogException {
        DeleteResourceRecordRequest deleteResourceRecordRequest = new DeleteResourceRecordRequest(ResourceName.USER.toString(), userId);
        try {
            DeleteResourceRecordResponse response = client.deleteResourceRecord(deleteResourceRecordRequest);
        } catch (LogException e) {
            if (!"ResourceRecordNotExist".equals(e.GetErrorCode())) {
                throw e;
            }
        }
    }

}
