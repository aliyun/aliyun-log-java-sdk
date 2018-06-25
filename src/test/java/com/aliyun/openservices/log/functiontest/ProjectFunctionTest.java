package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.UpdateProjectRequest;
import com.aliyun.openservices.log.response.CreateProjectResponse;
import com.aliyun.openservices.log.response.GetProjectResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProjectFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        client.DeleteProject(TEST_PROJECT);
    }

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
        CreateProjectResponse cpr = client.CreateProject(TEST_PROJECT, "xxx");
        System.out.println(cpr.GetAllHeaders());

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
