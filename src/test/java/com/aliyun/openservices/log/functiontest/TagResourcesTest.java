package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Tag;
import com.aliyun.openservices.log.common.TagResource;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ListTagResourcesRequest;
import com.aliyun.openservices.log.request.TagResourcesRequest;
import com.aliyun.openservices.log.request.UntagResourcesRequest;
import com.aliyun.openservices.log.response.ListTagResourcesResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TagResourcesTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testTagSources() throws LogException {
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
        client.tagResources(createTagSources("project", TEST_PROJECT, 3, false));

        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("t1", "k1"));
        TagResourcesRequest request = new TagResourcesRequest("project",
                Collections.singletonList(TEST_PROJECT), tags);
        client.tagResources(request);

        List<String> tagKeys = new ArrayList<String>();
        for (Tag t : tags) {
            tagKeys.add(t.getKey());
        }
        UntagResourcesRequest untagResourcesRequest = new UntagResourcesRequest(
                "project",
                Collections.singletonList(TEST_PROJECT),
                tagKeys);
        client.untagResources(untagResourcesRequest);
        untagResourcesRequest = new UntagResourcesRequest("project",
                Collections.singletonList(TEST_PROJECT));
        client.untagResources(untagResourcesRequest);
    }
//
//    @Test
//    public void testDispatchTagRequest() throws LogException {
//        client.tagResources(createTagSources("project", "project-test-zone", 3, false));
//        client.untagResources(createTagSources("project", "project-test-zone", 2, true));
//    }

    @Test
    public void testUnTagSources() throws LogException {
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
        client.tagResources(createTagSources("project", TEST_PROJECT, 3, false));

        //delete & list
        client.untagResources(createTagSources("project", TEST_PROJECT, 2, true));
        ListTagResourcesResponse resources = client.listTagResources(new ListTagResourcesRequest("project",
                Arrays.asList(TEST_PROJECT), Collections.singletonMap("tag-key-" + 2, "tag-value-" + 2)));
        assertEquals(1, resources.getTagResources().size());
        TagResource tagResource = resources.getTagResources().get(0);
        assertEquals("project", tagResource.getResourceType());
        assertEquals(TEST_PROJECT, tagResource.getResourceId());
        assertEquals("tag-key-2", tagResource.getTagKey());
        assertEquals("tag-value-2", tagResource.getTagValue());
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
