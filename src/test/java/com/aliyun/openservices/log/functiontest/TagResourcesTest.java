package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ListTagResourcesRequest;
import com.aliyun.openservices.log.response.ListTagResourcesResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TagResourcesTest extends FunctionTest {
    private static final String TEST_PROJECT = "test-project-to-tagsources-" + getNowTimestamp();

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "test tag sources");
        waitForSeconds(10);
    }

    @After
    public void clearDown() {
        safeDeleteProject(TEST_PROJECT);
    }

    @Test
    public void testTagSources() {
        //test invalid resourceType
        try {
            client.tagResources(createTagSources("project-1", TEST_PROJECT, 2, false));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("only support resource type: project", e.GetErrorMessage());
        }
        //test invalid resourceId
        try {
            client.tagResources(createTagSources("project", TEST_PROJECT.substring(0, 10), 2, false));
            fail();
        } catch (LogException e) {
            assertEquals("ProjectNotExist", e.GetErrorCode());
            assertEquals("The Project does not exist : " + TEST_PROJECT.substring(0, 10), e.GetErrorMessage());
        }
        //test invalid tags
        try {
            client.tagResources(createTagSources("project", TEST_PROJECT, 0, false));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("request tag key value pair size must little than 20", e.GetErrorMessage());
        }
        try {
            client.tagResources(createTagSources("project", TEST_PROJECT, 21, false));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("request tag key value pair size must little than 20", e.GetErrorMessage());
        }
        //created correctly
        try {
            client.tagResources(createTagSources("project", TEST_PROJECT, 3, false));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
    }

    @Test
    public void testUnTagSources() {
        //invalid delete
        try {
            client.untagResources(createTagSources("project-1", TEST_PROJECT, 2, true));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("only support resource type: project", e.GetErrorMessage());
        }
        try {
            client.untagResources(createTagSources("project", TEST_PROJECT.substring(0, 10), 2, true));
            fail();
        } catch (LogException e) {
            assertEquals("ProjectNotExist", e.GetErrorCode());
            assertEquals("The Project does not exist : " + TEST_PROJECT.substring(0, 10), e.GetErrorMessage());
        }
        //empty delete
        try {
            client.untagResources(createTagSources("project", TEST_PROJECT, 0, true));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //exceed delete
        try {
            client.untagResources(createTagSources("project", TEST_PROJECT, 21, true));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("tag key value pair count exceed", e.GetErrorMessage());
        }
        try {
            client.tagResources(createTagSources("project", TEST_PROJECT, 3, false));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        //delete & list
        try {
            client.untagResources(createTagSources("project", TEST_PROJECT, 2, true));
            ListTagResourcesResponse resources = client.listTagResources(new ListTagResourcesRequest("project",
                    Arrays.asList(TEST_PROJECT), Collections.singletonMap("tag-key-" + 2, "tag-value-" + 2)));
            JSONObject parseObject = JSONObject.parseObject(resources.getTagList());
            List<String> array = JSONArray.parseArray(parseObject.getString("tagResources"), String.class);
            assertEquals(1, array.size());
            JSONObject res = JSONObject.parseObject(array.get(0));
            assertEquals("project", res.getString("resourceType"));
            assertEquals(TEST_PROJECT, res.getString("resourceId"));
            assertEquals("tag-key-2", res.getString("tagKey"));
            assertEquals("tag-value-2", res.getString("tagValue"));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
    }

    private String createTagSources(String type, String id, int length, boolean untag) {
        JSONObject object = new JSONObject();
        object.put("resourceType", type);
        object.put("resourceId", Arrays.asList(id));
        JSONArray array = new JSONArray();
        for (int i = 0; i < length; i++) {
            if (untag) {
                array.add("tag-key-" + i);
            } else {
                Map<String, String> tags = new HashMap<String, String>(2);
                tags.put("key", "tag-key-" + i);
                tags.put("value", "tag-value-" + i);
                array.add(tags);
            }
        }
        object.put("tags", array);
        return object.toJSONString();
    }
}
