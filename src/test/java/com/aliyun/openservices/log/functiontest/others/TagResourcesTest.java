package com.aliyun.openservices.log.functiontest.others;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.ListTagResourcesRequest;
import com.aliyun.openservices.log.request.TagResourcesRequest;
import com.aliyun.openservices.log.request.UntagResourcesRequest;
import com.aliyun.openservices.log.response.ListTagResourcesResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TagResourcesTest extends MetaAPIBaseFunctionTest {
    private String logstoreName = "test-logstore";
    private String dashboardName = "test-dashboard";
    private String machineGroupName = "test-machineGroup";
    private String logtailConfigName = "test-logtailConfig";
    private Map<String, String> supportedTypeAndResourceId = new HashMap<String, String>();
    private Map<String, String> resourceNotExistErrorCode = new HashMap<String, String>();

    @Before
    public void setup() throws LogException {
        supportedTypeAndResourceId.put("project", TEST_PROJECT);
        supportedTypeAndResourceId.put("logstore", TagResourcesRequest.genResourceId(TEST_PROJECT, logstoreName));
        supportedTypeAndResourceId.put("dashboard", TagResourcesRequest.genResourceId(TEST_PROJECT, dashboardName));
        supportedTypeAndResourceId.put("machinegroup", TagResourcesRequest.genResourceId(TEST_PROJECT, machineGroupName));
        supportedTypeAndResourceId.put("logtailconfig", TagResourcesRequest.genResourceId(TEST_PROJECT, logtailConfigName));
        resourceNotExistErrorCode.put("project", "ProjectNotExist");
        resourceNotExistErrorCode.put("logstore", "LogStoreNotExist");
        resourceNotExistErrorCode.put("dashboard", "DashboardNotExist");
        resourceNotExistErrorCode.put("machinegroup", "MachineGroupNotExist");
        resourceNotExistErrorCode.put("logtailconfig", "ConfigNotExist");

        client.CreateLogStore(TEST_PROJECT, new LogStore(logstoreName, 3, 2));
        client.CreateMachineGroup(TEST_PROJECT, new MachineGroup(machineGroupName, "ip", new ArrayList<String>(Arrays.asList("127.0.0.1"))));
        String str = "{\"createTime\":1565005860,\"configName\":\"" + logtailConfigName + "\",\"inputDetail\":{\"delayAlarmBytes\":0,\"filterRegex\":[],\"logBeginRegex\":\".*\",\"timeFormat\":\"\",\"dockerIncludeLabel\":{},\"preserve\":true,\"maxSendRate\":-1,\"discardUnmatch\":false,\"preserveDepth\":1,\"priority\":0,\"mergeType\":\"topic\",\"enableTag\":false,\"advanced\":{\"force_multiconfig\":false},\"localStorage\":true,\"key\":[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\",\"i\",\"j\",\"k\"],\"logTimezone\":\"\",\"topicFormat\":\"none\",\"plugin\": {\"processors\": [{\"type\":\"processor_default\", \"detail\":{}},{\"type\":\"processor_default\", \"detail\":{}}]},\"adjustTimezone\":false,\"logPath\":\"/root/test/regex-parse-apache\",\"regex\":\"([0-9\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s(\\\\[[^\\\\[\\\\]]+\\\\]|\\\\-)\\\\s\\\\\\\"(\\\\S+)\\\\s(\\\\S+)\\\\s(\\\\S+)\\\\\\\"\\\\s(\\\\d{3}|\\\\-)\\\\s(\\\\d+|\\\\-)\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\",\"fileEncoding\":\"utf8\",\"tailExisted\":false,\"sendRateExpire\":0,\"dockerExcludeEnv\":{},\"delaySkipBytes\":0,\"filePattern\":\"*.log\",\"maxDepth\":100,\"enableRawLog\":false,\"dockerFile\":false,\"sensitive_keys\":[],\"discardNonUtf8\":false,\"filterKey\":[],\"shardHashKey\":[],\"dockerIncludeEnv\":{},\"logType\":\"common_reg_log\",\"dockerExcludeLabel\":{}},\"inputType\":\"file\",\"outputType\":\"LogService\",\"outputDetail\":{\"logstoreName\":\"logstore1\",\"endpoint\":\"cn-hangzhou-intranet.log.aliyuncs.com\"},\"logSample\":\"192.168.1.9 - - [05/May/2019:19:26:12 +0800] \\\"POST /favicon.ico HTTP/1.1\\\" 200 209 \\\"http://localhost/x0.html\\\" \\\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36\\\"\",\"lastModifyTime\":1565005860}";
        Config config = new Config();
        config.FromJsonString(str);
        client.CreateConfig(TEST_PROJECT, config);
        Dashboard dashboard = new Dashboard(dashboardName, "Dashboard", new ArrayList<Chart>());
        client.createDashboard(new CreateDashboardRequest(TEST_PROJECT, dashboard));
    }
    @Test
    public void testTagAndUntag() throws Exception {
        testTagSources();
        testUnTagSources();
    }

    public void tagSources(String resourceType, String resourceId) throws LogException {
        String wrongResourceType = resourceType + "-1";
        String wrongResourceId = resourceId + "-1";
        //test invalid resourceType
        try {
            client.tagResources(createTagSources(wrongResourceType, resourceId, 2, false));
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
            assertEquals("The resource type is not supported: " + wrongResourceType, e.GetErrorMessage());
        }
        //test invalid resourceId
        try {
            client.tagResources(createTagSources(resourceType, wrongResourceId, 2, false));
            fail();
        } catch (LogException e) {
            assertEquals(resourceNotExistErrorCode.get(resourceType), e.GetErrorCode());
        }
        //test invalid tags
        try {
            client.tagResources(createTagSources(resourceType, resourceId, 0, false));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("request tag key value pair size must little than 20", e.GetErrorMessage());
        }
        try {
            client.tagResources(createTagSources(resourceType, resourceId, 21, false));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("request tag key value pair size must little than 20", e.GetErrorMessage());
        }
        //created correctly
        client.tagResources(createTagSources(resourceType, resourceId, 3, false));

        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("t1", "k1"));
        TagResourcesRequest request = new TagResourcesRequest(resourceType,
                Collections.singletonList(resourceId), tags);
        client.tagResources(request);

        List<String> tagKeys = new ArrayList<String>();
        for (Tag t : tags) {
            tagKeys.add(t.getKey());
        }
        UntagResourcesRequest untagResourcesRequest = new UntagResourcesRequest(
                resourceType,
                Collections.singletonList(resourceId),
                tagKeys);
        client.untagResources(untagResourcesRequest);
        untagResourcesRequest = new UntagResourcesRequest(resourceType,
                Collections.singletonList(resourceId));
        client.untagResources(untagResourcesRequest);
    }

    public void testTagSources() throws LogException {
        for (String resourceType : supportedTypeAndResourceId.keySet()) {
            tagSources(resourceType, supportedTypeAndResourceId.get(resourceType));
        }
    }

    public void unTagSources(String resourceType, String resourceId) throws LogException {
        String wrongResourceType = resourceType + "-1";
        String wrongResourceId = resourceId + "-1";
        //invalid delete
        try {
            client.untagResources(createTagSources(wrongResourceType, TEST_PROJECT, 2, true));
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
            assertEquals("The resource type is not supported: " + wrongResourceType, e.GetErrorMessage());
        }
        try {
            client.untagResources(createTagSources(resourceType, wrongResourceId, 2, true));
            fail();
        } catch (LogException e) {
            assertEquals(resourceNotExistErrorCode.get(resourceType), e.GetErrorCode());
        }
        //empty delete
        try {
            client.untagResources(createTagSources(resourceType, resourceId, 0, true));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        //exceed delete
        try {
            client.untagResources(createTagSources(resourceType, resourceId, 21, true));
            fail();
        } catch (LogException e) {
            assertEquals("PostBodyInvalid", e.GetErrorCode());
            assertEquals("tag key value pair count exceed", e.GetErrorMessage());
        }
        client.tagResources(createTagSources(resourceType, resourceId, 3, false));

        //delete & list
        client.untagResources(createTagSources(resourceType, resourceId, 2, true));
        ListTagResourcesResponse resources = client.listTagResources(new ListTagResourcesRequest(resourceType,
                Arrays.asList(resourceId), Collections.singletonMap("tag-key-" + 2, "tag-value-" + 2)));
        assertEquals(1, resources.getTagResources().size());
        TagResource tagResource = resources.getTagResources().get(0);
        assertEquals(resourceType, tagResource.getResourceType());
        assertEquals(resourceId, tagResource.getResourceId());
        assertEquals("tag-key-2", tagResource.getTagKey());
        assertEquals("tag-value-2", tagResource.getTagValue());

    }

    public void testUnTagSources() throws LogException {
        for (String resourceType : supportedTypeAndResourceId.keySet()) {
            unTagSources(resourceType, supportedTypeAndResourceId.get(resourceType));
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
