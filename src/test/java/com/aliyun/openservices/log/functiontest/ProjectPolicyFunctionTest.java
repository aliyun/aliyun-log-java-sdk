package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetProjectPolicyReponse;
import com.aliyun.openservices.log.response.ListShardResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ProjectPolicyFunctionTest extends MetaAPIBaseFunctionTest {
    String projectNotExist = "ProjectNotExist";
    String invalidPolicy = "ParameterInvalid";

    String notExistProjectName = "asfsdfsfsadfasd";
    String projectWithOwnerShip = TEST_PROJECT;
    String policyText = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"log:Post*\"],\"Resource\":\"acs:log:*:*:project/" + projectWithOwnerShip + "/*\",\"Effect\":\"Allow\"}]}";

    @Test
    public void testNormalPolicy() throws LogException {
        try {
            System.out.println(projectWithOwnerShip);
            client.setProjectPolicy(projectWithOwnerShip, policyText);
            GetProjectPolicyReponse response = client.getProjectPolicy(projectWithOwnerShip);
            Assert.assertEquals(policyText, response.getPolicyText());
        } catch (LogException e) {
            Assert.fail(e.getMessage());
        } finally {
            client.deleteProjectPolicy(projectWithOwnerShip);
        }
        GetProjectPolicyReponse response = client.getProjectPolicy(projectWithOwnerShip);
        Assert.assertEquals("", response.getPolicyText());

        try {
            client.setProjectPolicy(projectWithOwnerShip, policyText);
            policyText = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"log:Post*\"],\"Resource\":\"acs:log:*:*:project/" + projectWithOwnerShip + "/*\",\"Effect\":\"Deny\"}]}";
            client.setProjectPolicy(projectWithOwnerShip, policyText);
            response = client.getProjectPolicy(projectWithOwnerShip);
            Assert.assertEquals(policyText, response.getPolicyText());
        } catch (LogException e) {
            Assert.fail(e.getMessage());
        } finally {
            client.deleteProjectPolicy(projectWithOwnerShip);
        }
    }

    @Test
    public void testUnnormalSetPolicy() {

        // set non-existent project
        try {
            client.setProjectPolicy(notExistProjectName, policyText);
            Assert.fail("Set project policy should not be successful");
        } catch (LogException e) {
            Assert.assertEquals(projectNotExist, e.GetErrorCode());
        }

        // set project with unnormal policy text
        String unnormalPolicyText = "{unnormal-policy-text}";
        try {
            client.setProjectPolicy(projectWithOwnerShip, unnormalPolicyText);
            Assert.fail("Set project policy should not be successful");
        } catch (LogException e) {
            Assert.assertEquals(invalidPolicy, e.getErrorCode());
        }
    }

    @Test
    public void testUnnormalGetPolicy() throws LogException {
        // get non-existent project
        try {
            GetProjectPolicyReponse response = client.getProjectPolicy(notExistProjectName);
            Assert.fail("Get project policy should not be successful");
        } catch (LogException e) {
            Assert.assertEquals(projectNotExist, e.getErrorCode());
        }

        // get non-exist policy
        GetProjectPolicyReponse response = client.getProjectPolicy(projectWithOwnerShip);
        Assert.assertEquals(response.getPolicyText(), "");

    }

    @Test
    public void testUnnormalDeletePolicy() {

        // delete non-existent project
        try {
            client.deleteProjectPolicy(notExistProjectName);
            Assert.fail("Delete project policy should not be successful");
        } catch (LogException e) {
            Assert.assertEquals(projectNotExist, e.getErrorCode());
        }
        // delete non-exist policy
        try {
            client.deleteProjectPolicy(projectWithOwnerShip);
        } catch (LogException e) {
            Assert.fail("DeleteProjectPolicy err" + e.getMessage());
        }

    }

    @Test
    public void testProjectPolicyReject() throws Exception {
        String projectName = makeProjectName();
        client.CreateProject(projectName, "");
        try {
            try {
                client.GetProject(projectName);
            } catch (LogException e) {
                Assert.fail("should not fail : " + e.GetErrorCode());
            }
            String policyText = "{" +
                    "   \"Version\": \"1\",\n" +
                    "   \"Statement\": [{" +
                    "       \"Action\": [\"log:GetProject\",\"log:GetCursorOrData\"]," +
                    "       \"Resource\": \"*\",\n" +
                    "       \"Condition\": {" +
                    "          \"StringNotLike\": {" +
                    "            \"acs:SourceVpc\":[\"vpc-*\"]" +
                    "           }" +
                    "       }," +
                    "       \"Effect\": \"Deny\"" +
                    "   }]" +
                    " }";
            client.setProjectPolicy(projectName, policyText);
            try {
                client.GetProject(projectName);
                Assert.fail("should fail");
            } catch (LogException e) {
                Assert.assertEquals("Unauthorized", e.getErrorCode());
                Assert.assertEquals("Access denied by project policy.", e.getMessage());
            }
            LogStore logStore = new LogStore("logstore1", 1, 1);
            client.CreateLogStore(projectName, logStore);
            waitOneMinutes();
            try {
                client.GetCursor(projectName, logStore.GetLogStoreName(), 0, Consts.CursorMode.BEGIN);
                Assert.fail("GetCursor should fail");
            } catch (LogException e) {
                Assert.assertEquals("Unauthorized", e.getErrorCode());
                Assert.assertEquals("Access denied by project policy.", e.getMessage());
            }
            // ListShards is ok
            ListShardResponse response = client.ListShard(projectName, logStore.GetLogStoreName());
            Assert.assertEquals(1, response.GetShards().size());
            client.deleteProjectPolicy(projectName);
        } finally {
            client.DeleteProject(projectName);
        }
    }
}
