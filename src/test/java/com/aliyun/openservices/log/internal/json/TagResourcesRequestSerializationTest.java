package com.aliyun.openservices.log.internal.json;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.aliyun.openservices.log.common.Tag;
import com.aliyun.openservices.log.request.TagResourcesRequest;
import com.aliyun.openservices.log.request.TagResourcesSystemTagsRequest;
import com.aliyun.openservices.log.request.UntagResourcesRequest;
import com.aliyun.openservices.log.request.UntagResourcesSystemTagsRequest;
import com.aliyun.openservices.log.util.JsonUtils;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;

/** Request bodies must follow the resource tag protocol and omit routing-only project. */
public class TagResourcesRequestSerializationTest {

    private static final String RESOURCE_TYPE = "logstore";

    private static final java.util.List<String> RESOURCE_IDS = Arrays.asList(
            "project#resource-a", "project#resource-b");

    @Test
    public void testTagResourcesRequestOmitsRoutingProject() {
        TagResourcesRequest request = new TagResourcesRequest(
                RESOURCE_TYPE, RESOURCE_IDS, Arrays.asList(
                        new Tag("env", "prod"), new Tag("special", "中文<>&=\"\\line")));

        assertEquals("project", request.getProject());
        assertJsonEquals("{\"resourceId\":[\"project#resource-a\",\"project#resource-b\"],"
                        + "\"resourceType\":\"logstore\","
                        + "\"tags\":[{\"key\":\"env\",\"value\":\"prod\"},"
                        + "{\"key\":\"special\",\"value\":\"中文<>&=\\\"\\\\line\"}]}",
                JsonUtils.serialize(request));
    }

    @Test
    public void testUntagResourcesRequestOmitsRoutingProject() {
        UntagResourcesRequest request = new UntagResourcesRequest(RESOURCE_TYPE, RESOURCE_IDS);

        assertEquals("project", request.getProject());
        assertJsonEquals("{\"all\":true,"
                        + "\"resourceId\":[\"project#resource-a\",\"project#resource-b\"],"
                        + "\"resourceType\":\"logstore\"}",
                JsonUtils.serialize(request));
    }

    @Test
    public void testTagResourcesSystemTagsRequestOmitsRoutingProject() {
        TagResourcesSystemTagsRequest request = new TagResourcesSystemTagsRequest(
                RESOURCE_TYPE, RESOURCE_IDS, Collections.singletonList(new Tag("env", "prod")),
                "123456789", "ALL");

        assertJsonEquals("{\"resourceId\":[\"project#resource-a\",\"project#resource-b\"],"
                        + "\"resourceType\":\"logstore\",\"scope\":\"ALL\","
                        + "\"tagOwnerUid\":\"123456789\","
                        + "\"tags\":[{\"key\":\"env\",\"value\":\"prod\"}]}",
                JsonUtils.serialize(request));
    }

    @Test
    public void testUntagResourcesSystemTagsRequestOmitsRoutingProject() {
        UntagResourcesSystemTagsRequest request = new UntagResourcesSystemTagsRequest(
                RESOURCE_TYPE, RESOURCE_IDS, Collections.singletonList("env"), "123456789");

        assertJsonEquals("{\"all\":false,"
                        + "\"resourceId\":[\"project#resource-a\",\"project#resource-b\"],"
                        + "\"resourceType\":\"logstore\",\"tagOwnerUid\":\"123456789\","
                        + "\"tags\":[\"env\"]}",
                JsonUtils.serialize(request));
    }
}
