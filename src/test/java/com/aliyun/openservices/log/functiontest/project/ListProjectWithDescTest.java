package com.aliyun.openservices.log.functiontest.project;


import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.functiontest.logstore.FunctionTest;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.request.UpdateProjectRequest;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListProjectWithDescTest extends FunctionTest {

    private static final String project = makeProjectName();
    private static final String desc = "test-desc-for-sdk";

    @Before
    public void setUp() throws Exception {
        client.CreateProject(project, desc);
    }

    @Test
    public void testListProjectWithDesc() throws Exception {
        boolean found = false;
        ListProjectRequest listProjectRequest = new ListProjectRequest(project, 0, 100);
        listProjectRequest.setDescription("-for-sdk");
        ListProjectResponse listProjectResponse = client.ListProject(listProjectRequest);
        for (Project item : listProjectResponse.getProjects()) {
            assertTrue(item.getProjectDesc().toLowerCase().contains(listProjectRequest.getDescription()));
            if (item.getProjectName().equals(project)) {
                assertEquals(item.getProjectDesc(), desc);
                assertEquals(item.getProjectStatus(), "Normal");
                assertNull(item.getQuota());
                found = true;
                break;
            }
        }
        assertTrue(found);

        found = false;
        listProjectRequest.setDescription("-FOR-sdk");
        listProjectResponse = client.ListProject(listProjectRequest);
        for (Project item : listProjectResponse.getProjects()) {
            assertTrue(item.getProjectDesc().toLowerCase().contains(listProjectRequest.getDescription().toLowerCase()));
            if (item.getProjectName().equals(project)) {
                assertEquals(item.getProjectDesc(), desc);
                assertEquals(item.getProjectStatus(), "Normal");
                assertNull(item.getQuota());
                found = true;
                break;
            }
        }
        assertTrue(found);

        String descCn = "test中文desc";
        client.updateProject(new UpdateProjectRequest(project, descCn));
        found = false;
        listProjectRequest.setDescription("中文desc");
        listProjectResponse = client.ListProject(listProjectRequest);
        for (Project item : listProjectResponse.getProjects()) {
            assertTrue(item.getProjectDesc().toLowerCase().contains(listProjectRequest.getDescription()));
            if (item.getProjectName().equals(project)) {
                assertEquals(item.getProjectDesc(), descCn);
                assertEquals(item.getProjectStatus(), "Normal");
                assertNull(item.getQuota());
                found = true;
                break;
            }
        }
        assertTrue(found);

        found = false;
        listProjectRequest.setDescription("中文DESC");
        listProjectResponse = client.ListProject(listProjectRequest);
        for (Project item : listProjectResponse.getProjects()) {
            assertTrue(item.getProjectDesc().toLowerCase().contains(listProjectRequest.getDescription().toLowerCase()));
            if (item.getProjectName().equals(project)) {
                assertEquals(item.getProjectDesc(), descCn);
                assertEquals(item.getProjectStatus(), "Normal");
                assertNull(item.getQuota());
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @After
    public void tearDown() {
        safeDeleteProjectWithoutSleep(project);
    }
}
