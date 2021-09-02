package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.UpdateProjectRequest;
import com.aliyun.openservices.log.response.GetProjectResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProjectFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = makeProjectName();

    @Test
    public void testUpdateProjectList() throws LogException {
        String project = TEST_PROJECT;
        client.CreateProject(project, "abc");

        GetProjectResponse response = client.GetProject(project);
        assertEquals(response.GetProjectDescription(), "abc");

        ListProjectResponse response1 = client.ListProject(project, 0, 100);
        for (Project project1 : response1.getProjects()) {
            assertEquals(project1.getProjectDesc(), "abc");
        }
        client.updateProject(new UpdateProjectRequest(project, "124"));
        response1 = client.ListProject(project, 0, 100);
        for (Project project1 : response1.getProjects()) {
            assertEquals(project1.getProjectDesc(), "124");
        }
        safeDeleteProjectWithoutSleep(project);
    }

    private void verifyUpdate(final String description,
                              final String expected) throws LogException {
        UpdateProjectRequest request = new UpdateProjectRequest(TEST_PROJECT, description);
        client.updateProject(request);

        GetProjectResponse response = client.GetProject(TEST_PROJECT);
        // description max length is 64
        assertTrue(expected.startsWith(response.GetProjectDescription()));
    }

    private void shouldFails(final String description,
                             String errorMessage,
                             String errorCode,
                             int httpCode) {
        UpdateProjectRequest request = new UpdateProjectRequest(TEST_PROJECT, description);
        try {
            client.updateProject(request);
            fail();
        } catch (LogException ex) {
            assertEquals(errorMessage, ex.GetErrorMessage());
            assertEquals(errorCode, ex.GetErrorCode());
            assertEquals(httpCode, ex.GetHttpCode());
        }
    }

    @Test
    public void testCreateProject() throws Exception {
        String desc = randomString();
        String project = makeProjectName();
        client.CreateProject(project, desc);
        GetProjectResponse response = client.GetProject(project);
        assertEquals(response.GetProjectDescription(), desc);
        assertEquals(response.GetProjectStatus(), "Normal");
        safeDeleteProjectWithoutSleep(project);
        try {
            client.GetProject(project);
            fail();
        } catch (LogException ex) {
            assertEquals("ProjectNotExist", ex.GetErrorCode());
        }
    }

    @Test
    public void testUpdateProject() throws Exception {
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
        client.CreateProject(TEST_PROJECT, "xxx");

        GetProjectResponse response = client.GetProject(TEST_PROJECT);
        assertEquals("xxx", response.GetProjectDescription());

        verifyUpdate("", "");
        verifyUpdate("test", "test");
        verifyUpdate(null, "");

        String longDesc = "@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11" +
                "xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xx" +
                "xxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxx" +
                "xxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxx" +
                "x@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@" +
                "@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@" +
                "@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxx";

        verifyUpdate(longDesc, longDesc);

        String tooLongDesc = "@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11" +
                "xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xx" +
                "xxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxx" +
                "xxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxx" +
                "x@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@" +
                "@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@" +
                "@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxxx@@@@@111xx11xxxxxx@@@@@@";
        shouldFails(tooLongDesc,
                "Invalid project description: '" + tooLongDesc + "'",
                "ParameterInvalid",
                400);

        StringBuilder chineseBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            chineseBuilder.append("中文中文中文中文中文");
        }
        final String chinese = chineseBuilder.toString();
        verifyUpdate(chinese, chinese);
    }

    @After
    public void tearDown() {
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
    }
}
