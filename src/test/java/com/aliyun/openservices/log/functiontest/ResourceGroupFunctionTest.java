package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.Assert;
import org.junit.Test;

public class ResourceGroupFunctionTest extends FunctionTest {
    static String testProjectId = makeProjectName();
    static String defaultResourceGroupId = "xxxx";
    static String newResourceGroupId = "xxxx";

    @Test
    public void testCreateProject() throws LogException {
        // test create project in default resource group
        try {
            client.CreateProject(testProjectId, "");
            Assert.assertTrue(!client.GetProject(testProjectId).getResourceGroupId().isEmpty());
        } catch (LogException e) {
            e.printStackTrace();
            Assert.fail("create project should not fail");
        } finally {
            client.DeleteProject(testProjectId);
        }

        // test create project in new resource group
        try {
            client.createProject(testProjectId, "", newResourceGroupId);
            Assert.assertEquals(newResourceGroupId, client.GetProject(testProjectId).getResourceGroupId());
        } catch (LogException e) {
            e.printStackTrace();
            Assert.fail("create project should not fail");
        } finally {
            client.DeleteProject(testProjectId);
        }

        // test create project in wrong resource group
        try {
            client.createProject(testProjectId, "", "wrongResourceGroupId");
            Assert.fail("create project should fail");
        } catch (LogException e) {
            Assert.assertEquals("ParameterInvalid", e.GetErrorCode());
        }
    }

    @Test
    public void testChangeResourceGroup() throws LogException {
        // test change wrong resource type
        try {
            client.CreateProject(testProjectId, "");
            client.changeResourceGroup("wrongType", testProjectId, newResourceGroupId);
            Assert.fail("change resource group id should fail");
        } catch (LogException e) {
            Assert.assertEquals("ParameterInvalid" , e.GetErrorCode());
        } finally {
            client.DeleteProject(testProjectId);
        }
        // test change resource group
        try {
            client.CreateProject(testProjectId, "");
            client.changeResourceGroup("PROJECT", testProjectId, newResourceGroupId);
            Assert.assertEquals(newResourceGroupId, client.GetProject(testProjectId).getResourceGroupId());
        } catch (LogException e) {
            Assert.fail("change resource group id should not fail");
        } finally {
            client.DeleteProject(testProjectId);
        }
    }

    @Test
    public void testListResourceGroup() throws LogException {
        try{
            client.createProject(testProjectId, "", newResourceGroupId);
            ListProjectResponse response =  client.ListProject(new ListProjectRequest("", 0, 5, newResourceGroupId));
            for (Project p : response.getProjects()) {
                Assert.assertEquals(newResourceGroupId, p.getResourceGroupId());
            }
        } catch (LogException e) {
            e.printStackTrace();
            Assert.fail("should not fail");
        } finally {
            client.DeleteProject(testProjectId);
        }
    }
}
