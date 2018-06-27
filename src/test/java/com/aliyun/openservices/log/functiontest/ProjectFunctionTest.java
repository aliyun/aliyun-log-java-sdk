package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.request.UpdateProjectRequest;
import com.aliyun.openservices.log.response.GetProjectResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ProjectFunctionTest extends FunctionTest {

    // For testing environment, please make sure the endpoint
    // project1.<endpoint> is accessible.
    private static final String TEST_PROJECT = "project1";


    private void verifyUpdate(final String description,
                              final String expected) throws LogException {
        UpdateProjectRequest request = new UpdateProjectRequest(TEST_PROJECT, description);
        client.updateProject(request);

        GetProjectResponse response = client.GetProject(TEST_PROJECT);
        assertEquals(expected, response.GetProjectDescription());
    }

    private void shouldFails(final String description,
                             String errorMessage,
                             String errorCode) {
        UpdateProjectRequest request = new UpdateProjectRequest(TEST_PROJECT, description);
        try {
            client.updateProject(request);
            fail();
        } catch (LogException ex) {
            assertEquals(errorMessage, ex.GetErrorMessage());
            assertEquals(errorCode, ex.GetErrorCode());
        }
    }

    @Test
    public void testUpdateProject() throws Exception {
        safeDeleteProject(TEST_PROJECT);
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
        shouldFails(tooLongDesc, "Invalid project description", "ParameterInvalid");

        StringBuilder chineseBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            chineseBuilder.append("中文中文中文中文中文");
        }
        final String chinese = chineseBuilder.toString();
        verifyUpdate(chinese, chinese);
    }

    @Test
    public void testListProject() throws Exception {
        ListProjectRequest request = new ListProjectRequest("", 0, 100);
        ListProjectResponse response = client.ListProject(request);

        List<Project> projects = response.getProjects();
        assertTrue(projects.size() < 100);
        for (int i = 1; i < projects.size(); i++) {
            // Check projects were ordered by create time in descending order
            final String createTime = projects.get(i).getCreateTime();
            final String lastOneCreateTime = projects.get(i - 1).getCreateTime();
            assertTrue(createTime.compareTo(lastOneCreateTime) <= 0);
        }
    }


    @After
    public void tearDown() {
        try {
            client.DeleteProject(TEST_PROJECT);
        } catch (Exception ex) {
            // swallow it
        }
    }
}
