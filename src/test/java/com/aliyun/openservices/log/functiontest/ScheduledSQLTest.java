package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.common.ScheduledSQLConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ScheduledSQLTest extends FunctionTest {
    private final String endpoint = credentials.getEndpoint();
    private final String accessKeyId = credentials.getAccessKeyId();
    private final String accessKeySecret = credentials.getAccessKey();
    private final String aliUid = credentials.getAliuid();
    private final String roleArn = "acs:ram::" + aliUid + ":role/aliyunlogscheduledsqlrole";
    private final String script = "select min(id) as min_id from test";
    private final String project = makeProjectName();
    private final String sourceLogstore = "test";
    private final String destEndpoint = "cn-hangzhou.log.aliyuncs.com";
    private final String destProject = makeProjectName() + "-dest";
    private final String destLogstore = "dest";
    private final String fromTimeExpr = "-1hour";
    private final String toTimeExpr = "-0s";
    private final Integer maxRunTimeInSeconds = 1800;
    private final Integer maxRetries = 10;
    private final Long fromTime = getLastHourTime(25);
    private final Long toTime = getLastHourTime(-2);
    private final String sqlTaskName = "sql-153203245";
    private final int delay = 10;
    private final String interval = "60s";
    private final Client client = new Client(endpoint, accessKeyId, accessKeySecret);
    private ScheduledSQL scheduledSql = generateScheduledSQL();
    private String instanceId = "";

    public ScheduledSQLTest() {
        super(1800000);
    }

    @Before
    public void setUp() {
        safeCreateProject(project, "ScheduledSQLTest");
        safeCreateProject(destProject, "ScheduledSQLTest dest");
    }

    @After
    public void clearUp() {
        safeDeleteProjectWithoutSleep(project);
        safeDeleteProjectWithoutSleep(destProject);
    }

    @Test
    public void testCrud() throws LogException, InterruptedException {
        new ScheduledSQLTest();
        testCreateScheduledSQL();
        testGetScheduledSQL();
        testListScheduledSQL();
        testUpdateScheduledSQL();
        testInvalidTimeRange();
        // jobInstance测试
        testListJobInstance();
        testGetJobInstance();
        testRerunJobInstance();
        // 删除scheduledSQL 任务
        testDeleteScheduledSQL();
    }

    @Test
    public void testInvalidTimeRange() throws LogException {
        System.out.println("testInvalidTimeRange ready to start.......");
        ScheduledSQLConfiguration configuration = scheduledSql.getConfiguration();
        configuration.setFromTime(0L);
        configuration.setToTime(0L);
        scheduledSql.setConfiguration(configuration);
        try {
            client.createScheduledSQL(new CreateScheduledSQLRequest(project, scheduledSql));
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid fromTime: 0 toTime: 0, please ensure fromTime more than 1451577600.", e.getMessage());
        }
        configuration.setFromTime(0L);
        configuration.setToTime(toTime);
        scheduledSql.setConfiguration(configuration);
        try {
            client.createScheduledSQL(new CreateScheduledSQLRequest(project, scheduledSql));
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid fromTime: 0 toTime: " + toTime + ", please ensure fromTime more than 1451577600.", e.getMessage());
        }
        configuration.setFromTime(toTime);
        configuration.setToTime(toTime);
        scheduledSql.setConfiguration(configuration);
        try {
            client.createScheduledSQL(new CreateScheduledSQLRequest(project, scheduledSql));
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid fromTime: " + toTime + " toTime: " + toTime + ", please ensure fromTime more than 1451577600.", e.getMessage());
        }
        configuration.setFromTime(fromTime);
        configuration.setToTime(-1L);
        scheduledSql.setConfiguration(configuration);
        try {
            client.createScheduledSQL(new CreateScheduledSQLRequest(project, scheduledSql));
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid fromTime: " + fromTime + " toTime: -1, please ensure fromTime more than 1451577600.", e.getMessage());
        }
    }

    private void testCreateScheduledSQL() throws LogException {
        System.out.println("Create ScheduledSQL ready to start.......");
        // Create
        CreateScheduledSQLResponse createScheduledSQLResponse = client.createScheduledSQL(new CreateScheduledSQLRequest(project, scheduledSql));
    }

    private void testDeleteScheduledSQL() throws LogException {
        System.out.println("Delete ScheduledSQL ready to start.......");
        // Delete
        DeleteScheduledSQLResponse deleteScheduledSQLResponse = client.deleteScheduledSQL(new DeleteScheduledSQLRequest(project, sqlTaskName));
    }

    private void testGetScheduledSQL() throws LogException {
        GetScheduledSQLResponse scheduledSQLResponse = getScheduledSQL();
        System.out.println(JSONObject.toJSONString(scheduledSQLResponse));
        assertEquals(sqlTaskName, scheduledSQLResponse.getScheduledSQL().getName());
        assertEquals("ScheduledSqlTask", scheduledSQLResponse.getScheduledSQL().getDisplayName());
        assertFalse(scheduledSQLResponse.getScheduledSQL().getRecyclable());
        assertNotNull(scheduledSQLResponse.getScheduledSQL().getScheduleId());
        assertEquals("ENABLED", scheduledSQLResponse.getScheduledSQL().getStatus());
        assertEquals("ScheduledSQL", scheduledSQLResponse.getScheduledSQL().getType().toString());
        // configuration
        ScheduledSQLConfiguration configuration = scheduledSQLResponse.getScheduledSQL().getConfiguration();
        assertEquals("searchQuery", configuration.getSqlType());
        assertEquals("enhanced", configuration.getResourcePool());
        assertEquals(destProject, configuration.getDestProject());
        assertEquals(destLogstore, configuration.getDestLogstore());
        assertEquals(roleArn, configuration.getRoleArn());
        assertEquals(roleArn, configuration.getDestRoleArn());
        assertEquals(fromTimeExpr, configuration.getFromTimeExpr());
        assertEquals(maxRetries, configuration.getMaxRetries());
        assertEquals(maxRunTimeInSeconds, configuration.getMaxRunTimeInSeconds());
        assertEquals(script, configuration.getScript());
        assertEquals(sourceLogstore, configuration.getSourceLogstore());
        assertEquals(toTimeExpr, configuration.getToTimeExpr());
        assertEquals(fromTime, configuration.getFromTime());
        assertEquals(toTime, configuration.getToTime());
        // scheduledSQL
        JobSchedule schedule = scheduledSQLResponse.getScheduledSQL().getSchedule();
        assertEquals("FixedRate", schedule.getType().toString());
        assertEquals(interval, schedule.getInterval());
        assertEquals(String.valueOf(delay), schedule.getDelay().toString());
    }

    private void testListScheduledSQL() throws LogException {
        System.out.println("List ScheduledSQL ready to start.......");
        // List ScheduledSQL
        ListScheduledSQLResponse listScheduledSQLResponse = client.listScheduledSQL(new ListScheduledSQLRequest(project));
        assertEquals(sqlTaskName, listScheduledSQLResponse.getResults().get(0).getName());
        assertEquals("ScheduledSqlTask", listScheduledSQLResponse.getResults().get(0).getDisplayName());
        assertFalse(listScheduledSQLResponse.getResults().get(0).getRecyclable());
        assertNotNull(listScheduledSQLResponse.getResults().get(0).getScheduleId());
        assertEquals("ENABLED", listScheduledSQLResponse.getResults().get(0).getStatus());
        assertEquals("ScheduledSQL", listScheduledSQLResponse.getResults().get(0).getType().toString());
        // configuration
        ScheduledSQLConfiguration configuration = listScheduledSQLResponse.getResults().get(0).getConfiguration();
        assertEquals(destProject, configuration.getDestProject());
        assertEquals(destLogstore, configuration.getDestLogstore());
        assertEquals(fromTimeExpr, configuration.getFromTimeExpr());
        assertEquals(maxRetries, configuration.getMaxRetries());
        assertEquals(maxRunTimeInSeconds, configuration.getMaxRunTimeInSeconds());
        assertEquals(script, configuration.getScript());
        assertEquals(sourceLogstore, configuration.getSourceLogstore());
        assertEquals(toTimeExpr, configuration.getToTimeExpr());
        // scheduledSQL
        JobSchedule schedule = listScheduledSQLResponse.getResults().get(0).getSchedule();
        assertEquals("FixedRate", schedule.getType().toString());
        assertEquals(interval, schedule.getInterval());
        assertEquals(String.valueOf(delay), schedule.getDelay().toString());
    }

    private void testUpdateScheduledSQL() throws LogException {
        System.out.println("Update ScheduledSQL ready to start.......");
        ScheduledSQL scheduledSql = getScheduledSQL().getScheduledSQL();
        // Update
        scheduledSql.setDisplayName("UpdateTest");
        ScheduledSQLConfiguration scheduledSQLConfiguration = generateConfig();
        scheduledSQLConfiguration.setScript("select min(Id) as min_id, max(Id) as max_id from test");
        // scheduledSQLConfiguration
        scheduledSql.setConfiguration(scheduledSQLConfiguration);
        scheduledSQLConfiguration.setToTimeExpr("");
        // Update
        UpdateScheduledSQLResponse scheduledSQLResponse = client.updateScheduledSQL(new UpdateScheduledSQLRequest(project, scheduledSql));
        // Proof Update
        GetScheduledSQLResponse getScheduledSQLResponse = getScheduledSQL();
        System.out.println(JSONObject.toJSONString(getScheduledSQLResponse));
        assertEquals(sqlTaskName, getScheduledSQLResponse.getScheduledSQL().getName());
        assertEquals("UpdateTest", getScheduledSQLResponse.getScheduledSQL().getDisplayName());
        assertEquals("select min(Id) as min_id, max(Id) as max_id from test", getScheduledSQLResponse.getScheduledSQL().getConfiguration().getScript());
    }


    private void testGetJobInstance() throws LogException {
        // JobInstances
        GetJobInstanceResponse getJobInstanceResponse = getJobInstance();
        assertEquals(sqlTaskName, getJobInstanceResponse.getJobInstance().getJobName());
        assertNotNull(getJobInstanceResponse.getJobInstance().getJobScheduleId());
        System.out.println("getJobInstance: " + JSONObject.toJSONString(getJobInstanceResponse));
    }

    private void testListJobInstance() throws LogException, InterruptedException {
        // List jobInstance
        System.out.println("Wait for start jobInstance...");
        TimeUnit.MINUTES.sleep(5);
        ListJobInstancesResponse listJobInstancesResponse = client.listJobInstances(new ListJobInstancesRequest(project, sqlTaskName, fromTime, toTime));
        if (listJobInstancesResponse.getResults().size() > 0) {
            instanceId = listJobInstancesResponse.getResults().get(0).getInstanceId();
        } else {
            throw new LogException("NoJobInstance", "JobInstances have not start, please wait.", "");
        }
        System.out.println("list JobInstances: " + JSONObject.toJSONString(listJobInstancesResponse));
    }

    private void testRerunJobInstance() throws LogException, InterruptedException {
        System.out.println("Rerun jobInstance ready to start.......");
        // Stop jobInstance
        GetJobInstanceResponse getJobInstanceResponse = getJobInstance();
        String state = getJobInstanceResponse.getJobInstance().getState();
        if ("SUCCEEDED".equals(state) || "FAILED".equals(state)) {
            client.modifyJobInstanceState(new ModifyJobInstanceStateRequest(project, sqlTaskName, instanceId, "RUNNING"));
        }
        TimeUnit.SECONDS.sleep(3);
        GetJobInstanceResponse getJobInstanceResponse2 = getJobInstance();
        String afterModifyState = getJobInstanceResponse2.getJobInstance().getState();
        List<String> stateList = Arrays.asList("RUNNING", "SUCCEEDED", "FAILED");
        assertTrue(stateList.contains(afterModifyState));
    }

    private void testStopJobInstance() throws LogException {
        System.out.println("Stop jobInstance ready to start.......");
        // Start jobInstance
        ModifyJobInstanceStateResponse modifyJobInstanceStateResponse = client.modifyJobInstanceState(new ModifyJobInstanceStateRequest(project, sqlTaskName, "c7f6e01fc67ecdcb-5bd121c38cc93-5645bf9", "STOPPED"));
    }

    private ScheduledSQL generateScheduledSQL() {
        ScheduledSQL scheduledSQLStructure = new ScheduledSQL();
        scheduledSQLStructure.setName(sqlTaskName);
        scheduledSQLStructure.setDisplayName("ScheduledSqlTask");
        scheduledSQLStructure.setDescription("sql description");
        ScheduledSQLConfiguration scheduledSQLConfiguration = generateConfig();
        scheduledSQLStructure.setConfiguration(scheduledSQLConfiguration);
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.FIXED_RATE);
        jobSchedule.setInterval(interval);
        jobSchedule.setDelay(delay);
        scheduledSQLStructure.setSchedule(jobSchedule);
        return scheduledSQLStructure;
    }

    private ScheduledSQLConfiguration generateConfig() {
        ScheduledSQLConfiguration scheduledSQLConfiguration = new ScheduledSQLConfiguration();
        scheduledSQLConfiguration.setScript(script);
        scheduledSQLConfiguration.setSqlType("searchQuery");
        scheduledSQLConfiguration.setResourcePool("enhanced");
        scheduledSQLConfiguration.setRoleArn(roleArn);
        scheduledSQLConfiguration.setDestRoleArn(roleArn);
        scheduledSQLConfiguration.setSourceLogstore(sourceLogstore);
        scheduledSQLConfiguration.setDestEndpoint(destEndpoint);
        scheduledSQLConfiguration.setDestProject(destProject);
        scheduledSQLConfiguration.setDestLogstore(destLogstore);
        scheduledSQLConfiguration.setFromTimeExpr(fromTimeExpr);
        scheduledSQLConfiguration.setToTimeExpr(toTimeExpr);
        scheduledSQLConfiguration.setMaxRetries(maxRetries);
        scheduledSQLConfiguration.setMaxRunTimeInSeconds(maxRunTimeInSeconds);
        scheduledSQLConfiguration.setFromTime(fromTime);
        scheduledSQLConfiguration.setToTime(toTime);
        return scheduledSQLConfiguration;
    }

    private GetScheduledSQLResponse getScheduledSQL() throws LogException {
        System.out.println("Get ScheduledSQL ready to start.......");
        return client.getScheduledSQL(new GetScheduledSQLRequest(project, sqlTaskName));
    }

    private GetJobInstanceResponse getJobInstance() throws LogException {
        System.out.println("Get JobInstance ready to start.....");
        return client.getJobInstance(new GetJobInstanceRequest(project, sqlTaskName, instanceId));
    }

    private Long getLastHourTime(int n) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
        return ca.getTimeInMillis() / 1000;
    }
}