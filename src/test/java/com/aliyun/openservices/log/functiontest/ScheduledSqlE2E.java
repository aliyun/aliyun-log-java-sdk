package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.Client;

import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.GetScheduledSQLResponse;
import com.aliyun.openservices.log.response.ListJobInstancesResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScheduledSqlE2E extends FunctionTest {
    private final String sourceProject = "scheduled-sql-pub-test";
    private final String roleArn = "acs:ram::" + credentials.getAliuid() + ":role/aliyunlogscheduledsqlrole";
    private final String sourceLogstoreIndex = "{\"line\": {\"token\": [\",\", \" \", \"'\", \"\\\"\", \";\", \"=\", \"(\", \")\", \"[\", \"]\", \"{\", \"}\", \"?\", \"@\", \"&\", \"<\", \">\", \"/\", \":\", \"\\n\", \"\\t\", \"\\r\"], \"caseSensitive\": false, \"chn\": false}, \"keys\": {\"dev\": {\"type\": \"text\", \"token\": [\",\", \" \", \"'\", \"\\\"\", \";\", \"=\", \"(\", \")\", \"[\", \"]\", \"{\", \"}\", \"?\", \"@\", \"&\", \"<\", \">\", \"/\", \":\", \"\\n\", \"\\t\", \"\\r\"], \"caseSensitive\": false, \"alias\": \"\", \"doc_value\": true, \"chn\": false}, \"id\": {\"type\": \"long\", \"alias\": \"\", \"doc_value\": true}}, \"log_reduce\": false, \"max_text_len\": 2048}";
    private final String destLogstoreIndex = "{\"keys\":{\"__tag__:__schedule_time__\":{\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"doc_value\":true,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"],\"type\":\"text\"},\"number_id\":{\"alias\":\"\",\"doc_value\":true,\"type\":\"long\"}},\"line\":{\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"log_reduce\":false,\"max_text_len\":16384}";
    private final long mockLogsTime = 60 * 70L;
    private final String sumNumberIdQuery = "*|SELECT sum(number_id) as sum_id";
    private final String sumScheduledTimeQuery = "* | SELECT sum(if (try_cast(\"__tag__:__schedule_time__\" as bigint) % 60 = 0, 0, 1)) from dest-sql-log where number_id!=0";

    public ScheduledSqlE2E() {
        super(10800 * 1000);
    }
    @Before
    public void setup() {
        new ScheduledSqlE2E();
        // 删除初始化project
        safeDeleteProject(sourceProject);
        // 原始资源初始化
        safeCreateProject(sourceProject, "");
        waitOneMinutes();
        System.out.println("setup finish....");
    }

    /**
     * 同区域场景，时间范围设置为持续性
     *
     * @throws LogException
     */
    @Test
    public void testScheduledSqlInTheSameRegion() throws LogException, InterruptedException {
        new ScheduledSqlE2E();
        String testDescription = "时间范围设置为持续性: ";
        System.out.println(testDescription + "Ready start test the same region...");
        // 创建源Logstore
        String sourceLogstore = "test-sql-log";
        createLogstoreAndIndex(sourceProject, sourceLogstore, sourceLogstoreIndex);
        // 创建目标LogStore
        String destLogstore = "dest-sql-log";
        createLogstoreAndIndex(sourceProject, destLogstore, destLogstoreIndex);

        // 创建ScheduledSQL任务
        System.out.println(testDescription + "Ready create ScheduledSQL...");
        String sqlTaskName = "sql-1532032452";
        String displayName = "TestFullOneMin";
        String destEndpoint = credentials.getEndpoint();
        // schedule
        String fromTimeExpr = "@m - 1m";
        String toTimeExpr = "@m";
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.CRON);
        jobSchedule.setCronExpression("0/1 * * * *");
        jobSchedule.setTimeZone("+0800");
        createScheduledSql(sqlTaskName, sourceLogstore,
                destEndpoint, sourceProject, destLogstore,
                getLastTimeInSecond(10 * 60), 0,
                displayName, jobSchedule, fromTimeExpr,
                toTimeExpr);

        // 执行Mock logs
        System.out.println(testDescription + "Ready mock logs");

        int mockLogsNumbers = putMockLogsTask(sourceLogstore);
        System.out.println(testDescription + "mockLogsNumbers: " + mockLogsNumbers);
        // List JobInstances
        System.out.println(testDescription + "Ready list jobInstance....");
        listJobInstance(sqlTaskName);

        // 校验数据
        System.out.println(testDescription + "Ready get target result from dest logstore...");
        checkTargetLogs(sourceProject, destLogstore, sumNumberIdQuery, mockLogsNumbers, client);
        checkTargetLogs(sourceProject, destLogstore, sumScheduledTimeQuery, 0, client);

    }

    /**
     * 同区域场景，时间范围设置为固定时间范围
     *
     * @throws LogException
     */
    @Test
    public void testScheduledSqlInTheSameRegionForTimeRange() throws LogException, InterruptedException {
        new ScheduledSqlE2E();
        String testDescription = "时间范围为固定范围: ";
        System.out.println(testDescription + "Ready start test the same region for time range...");
        // 创建源Logstore
        String sourceLogstore = "test-sql-log-time-range";
        createLogstoreAndIndex(sourceProject, sourceLogstore, sourceLogstoreIndex);
        // 创建目标LogStore
        String destLogstore = "dest-sql-log-time-range";
        createLogstoreAndIndex(sourceProject, destLogstore, destLogstoreIndex);

        // 创建ScheduledSQL任务
        System.out.println(testDescription + "Ready create ScheduledSQL...");
        String sqlTaskName = "sql-1532032453";
        String displayName = "TestTimeRange";
        String destEndpoint = credentials.getEndpoint();
        // schedule
        String fromTimeExpr = "@m - 1m";
        String toTimeExpr = "@m";
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.CRON);
        jobSchedule.setCronExpression("0/1 * * * *");
        jobSchedule.setTimeZone("+0800");
        createScheduledSql(sqlTaskName, sourceLogstore,
                destEndpoint, sourceProject, destLogstore,
                getLastTimeInSecond(10 * 60),
                getLastHourTime(-2), displayName,
                jobSchedule, fromTimeExpr, toTimeExpr);

        // 执行Mock logs
        System.out.println(testDescription + "Ready mock logs");

        int mockLogsNumbers = putMockLogsTask(sourceLogstore);
        System.out.println(testDescription + "mockLogsNumbers: " + mockLogsNumbers);
        // List JobInstances
        System.out.println(testDescription + "Ready list jobInstance....");
        listJobInstance(sqlTaskName);

        // 校验数据
        System.out.println(testDescription + "Ready get target result from dest logstore...");
        checkTargetLogs(sourceProject, destLogstore, sumNumberIdQuery, mockLogsNumbers, client);
        checkTargetLogs(sourceProject, destLogstore, sumScheduledTimeQuery, 0, client);
    }

    /**
     * 同区域场景，整分每10min调度一次
     *
     * @throws LogException
     */
    @Test
    public void testScheduledSqlInTheSameRegionFullTenMines() throws LogException, InterruptedException {
        new ScheduledSqlE2E();
        String testDescription = "整分每10min调度一次: ";
        System.out.println(testDescription + "Ready start test the same region for time range...");
        // 创建源Logstore
        String sourceLogstore = "test-sql-log-full-ten-mines";
        createLogstoreAndIndex(sourceProject, sourceLogstore, sourceLogstoreIndex);
        // 创建目标LogStore
        String destLogstore = "dest-sql-log-full-ten-mines";
        createLogstoreAndIndex(sourceProject, destLogstore, destLogstoreIndex);

        // 创建ScheduledSQL任务
        System.out.println(testDescription + "Ready create ScheduledSQL...");
        String sqlTaskName = "sql-1632032453";
        String displayName = "TestFullTenMines";
        String destEndpoint = credentials.getEndpoint();
        // schedule
        String fromTimeExpr = "@m - 10m";
        String toTimeExpr = "@m";
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.CRON);
        jobSchedule.setCronExpression("0/10 * * * *");
        jobSchedule.setTimeZone("+0800");
        createScheduledSql(sqlTaskName, sourceLogstore,
                destEndpoint, sourceProject, destLogstore,
                getLastTimeInSecond(10 * 60),
                0, displayName,
                jobSchedule, fromTimeExpr, toTimeExpr);

        // 执行Mock logs
        System.out.println(testDescription + "Ready mock logs");

        int mockLogsNumbers = putMockLogsTask(sourceLogstore);
        System.out.println(testDescription + "mockLogsNumbers: " + mockLogsNumbers);
        // List JobInstances
        System.out.println(testDescription + "Ready list jobInstance....");
        listJobInstance(sqlTaskName);

        // 校验数据
        System.out.println(testDescription + "Ready get target result from dest logstore...");
        checkTargetLogs(sourceProject, destLogstore, sumNumberIdQuery, mockLogsNumbers, client);
        checkTargetLogs(sourceProject, destLogstore, sumScheduledTimeQuery, 0, client);
    }

    /**
     * 同区域场景，相对每10min调度一次
     *
     * @throws LogException
     */
    @Test
    public void testScheduledSqlInTheSameRegionTenMines() throws LogException, InterruptedException {
        new ScheduledSqlE2E();
        String testDescription = "相对每10min调度一次: ";
        System.out.println(testDescription + "Ready start test the same region for time range...");
        // 创建源Logstore
        String sourceLogstore = "test-sql-log-ten-mines";
        createLogstoreAndIndex(sourceProject, sourceLogstore, sourceLogstoreIndex);
        // 创建目标LogStore
        String destLogstore = "dest-sql-log-ten-mines";
        createLogstoreAndIndex(sourceProject, destLogstore, destLogstoreIndex);

        // 创建ScheduledSQL任务
        System.out.println(testDescription + "Ready create ScheduledSQL...");
        String sqlTaskName = "sql-1732032453";
        String displayName = "TestFullTenMines";
        String destEndpoint = credentials.getEndpoint();
        // schedule
        String fromTimeExpr = "- 10m";
        String toTimeExpr = "";
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.FIXED_RATE);
        jobSchedule.setInterval("10m");
        createScheduledSql(sqlTaskName, sourceLogstore,
                destEndpoint, sourceProject, destLogstore,
                getLastTimeInSecond(10 * 60),
                0, displayName,
                jobSchedule, fromTimeExpr, toTimeExpr);

        // 执行Mock logs
        System.out.println(testDescription + "Ready mock logs");

        int mockLogsNumbers = putMockLogsTask(sourceLogstore);
        System.out.println(testDescription + "mockLogsNumbers: " + mockLogsNumbers);
        // List JobInstances
        System.out.println(testDescription + "Ready list jobInstance....");
        listJobInstance(sqlTaskName);

        // 校验数据
        System.out.println(testDescription + "Ready get target result from dest logstore...");
        checkTargetLogs(sourceProject, destLogstore, sumNumberIdQuery, mockLogsNumbers, client);
    }

    /**
     * 跨区域场景
     *
     * @throws LogException
     */
    @Test
    @Ignore
    public void testScheduledSqlInTheDifferentRegion() throws LogException, InterruptedException {
        new ScheduledSqlE2E();
        String testDescription = "跨区域场景: ";
        System.out.println(testDescription + "Ready start test cross region...");
        // 创建源Logstore
        String sourceLogstore = "test-sql-log-cross-region";
        createLogstoreAndIndex(sourceProject, sourceLogstore, sourceLogstoreIndex);
        // 创建目标LogStore
        String targetEndpoint = "cn-hangzhou-b.log.aliyuncs.com";
        String targetProject = "scheduled-sql-dest";
        String destLogstore = "dest-sql-log-cross-region";
        Client destClient = new Client(targetEndpoint, credentials.getAccessKeyId(), credentials.getAccessKey());
        createDifferentRegionProjectAndLogstore(targetProject, destLogstore, destClient);

        // 创建ScheduledSQL任务
        System.out.println(testDescription + "Ready create ScheduledSQL...");
        String sqlTaskName = "sql-3532032456";
        String displayName = "TestCrossRegion";
        // schedule
        String fromTimeExpr = "@m - 1m";
        String toTimeExpr = "@m";
        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setType(JobScheduleType.CRON);
        jobSchedule.setCronExpression("0/1 * * * *");
        jobSchedule.setTimeZone("+0800");
        createScheduledSql(sqlTaskName, sourceLogstore,
                targetEndpoint, targetProject, destLogstore,
                getLastTimeInSecond(10 * 60), 0,
                displayName, jobSchedule, fromTimeExpr, toTimeExpr);


        // 执行Mock logs
        System.out.println(testDescription + "Ready mock logs");

        int mockLogsNumbers = putMockLogsTask(sourceLogstore);
        System.out.println(testDescription + "mockLogsNumbers: " + mockLogsNumbers);
        // List JobInstances
        System.out.println(testDescription + "Ready list jobInstance....");
        listJobInstance(sqlTaskName);

        // 校验数据
        System.out.println(testDescription + "Ready get target result from dest logstore...");
        checkTargetLogs(targetProject, destLogstore, sumNumberIdQuery, mockLogsNumbers, destClient);
        checkTargetLogs(targetProject, destLogstore, sumScheduledTimeQuery, 0, destClient);

    }


    /**
     * LogStore Resource
     *
     * @param logStoreName
     * @return
     */
    private LogStore structureLogStore(String logStoreName) {
        LogStore logStore = new LogStore();
        logStore.SetTtl(30);
        logStore.SetShardCount(2);
        logStore.SetLogStoreName(logStoreName);
        return logStore;
    }

    private void createIndex(String project, String logStore, String indexConfig, Client destClient) throws LogException {
        Index index = new Index();
        index.FromJsonString(indexConfig);
        destClient.CreateIndex(new CreateIndexRequest(project, logStore, index));
    }

    public int putMockLogsTask(String logStoreName) {
        int logNumbers = 0;
        long startMockLogsTime = System.currentTimeMillis() / 1000;
        while (true) {
            List<LogItem> logGroup = new ArrayList<LogItem>();
            for (int j = 0; j < 100; ++j) {
                LogItem logItem = new LogItem();
                logItem.PushBack("id", String.valueOf(logNumbers));
                logItem.PushBack("dev", "slog3_6");
                logGroup.add(logItem);
                logNumbers++;
            }
            try {
                client.PutLogs(sourceProject, logStoreName, "", logGroup, "");
            } catch (LogException e) {
                e.printStackTrace();
            }
            waitForSeconds(1);
            long currentMockLogsTime = System.currentTimeMillis() / 1000;
            if (currentMockLogsTime - startMockLogsTime > mockLogsTime) {
                waitForSeconds(2 * 60);
                break;
            }
        }
        return logNumbers;
    }

    private void createLogstoreAndIndex(String project, String logstoreName, String index) throws LogException {
        LogStore logStore = structureLogStore(logstoreName);
        reCreateLogStore(project, logStore);
        waitOneMinutes();
        createIndex(project, logstoreName, index, client);
        waitForSeconds(30);
    }

    private void createDifferentRegionProjectAndLogstore(String project, String logstore, Client destClient) throws LogException {
        try {
            destClient.CreateProject(project, "desc");
        } catch (LogException e) {
            assertEquals("ProjectAlreadyExist", e.GetErrorCode());
        }

        try {
            destClient.DeleteLogStore(project, logstore);
        } catch (LogException ex) {
            System.out.println("ERROR: errorCode=" + ex.GetErrorCode()
                    + ", httpCode=" + ex.GetHttpCode()
                    + ", errorMessage=" + ex.GetErrorMessage()
                    + ", requestId=" + ex.GetRequestId());
            assertEquals(ex.GetHttpCode(), 404);
        }
        destClient.CreateLogStore(project, structureLogStore(logstore));
        createIndex(project, logstore, destLogstoreIndex, destClient);
    }

    private void checkTargetLogs(String project, String destLogstore, String query, int targetLogNumbers, Client client) throws LogException, InterruptedException {
        String recordLog = String.format("checkTargetLogs project: %s, destLogstore: %s, targetLogNumbers: %s, query: %s",
                project, destLogstore, targetLogNumbers, query);
        System.out.println(recordLog);
        long readyGetLogsTime = System.currentTimeMillis() / 1000;
        int targetLogResult = 0;
        while (true) {
            long handleTime = System.currentTimeMillis() / 1000;
            String targetResult = getDestLogsIdNumbers(project, destLogstore, getLastHourTime(1), getLastHourTime(-1), query, client);
            System.out.println("targetLogResult: " + targetLogResult);
            if (targetResult != null && !"".equals(targetResult) && !"null".equals(targetResult)) {
                targetLogResult = Integer.parseInt(targetResult);
                if (targetLogResult >= targetLogNumbers) {
                    assertEquals(String.valueOf(targetLogNumbers), targetResult);
                    break;
                }
            }

            if (handleTime - readyGetLogsTime > 1200) {
                System.out.println("checkTargetLogs failed...");
                break;
            }

            waitOneMinutes();
        }
        System.out.println("Finished check target logs...");
    }

    /**
     * Scheduled SQL
     *
     * @return
     */
    private void createScheduledSql(String sqlTaskName, String sourceLogstore,
                                    String destEndpoint, String destProject, String destLogstore,
                                    int fromTime, int toTime, String displayName,
                                    JobSchedule jobSchedule, String fromTimeExpr, String toTimeExpr) throws LogException {
        try {
            client.deleteScheduledSQL(new DeleteScheduledSQLRequest(sourceProject, sqlTaskName));
        } catch (LogException e) {
            assertEquals("JobNotExist", e.GetErrorCode());
        }
        waitOneMinutes();
        ScheduledSQL scheduledSQL = generateScheduledSQL(sqlTaskName, sourceLogstore,
                destEndpoint, destProject, destLogstore,
                fromTime, toTime, displayName, jobSchedule, fromTimeExpr, toTimeExpr);
        client.createScheduledSQL(new CreateScheduledSQLRequest(sourceProject, scheduledSQL));
        GetScheduledSQLResponse getScheduledSQLResponse = client.getScheduledSQL(new GetScheduledSQLRequest(sourceProject, sqlTaskName));
        assertEquals(sqlTaskName, getScheduledSQLResponse.getScheduledSQL().getName());
        waitOneMinutes();
    }

    private void listJobInstance(String sqlTaskName) throws LogException {
        try {
            Integer startTime = getLastHourTime(2);
            Integer endTime = getLastHourTime(-2);
            ListJobInstancesResponse listJobInstancesResponse = client.listJobInstances(new ListJobInstancesRequest(sourceProject, sqlTaskName, startTime, endTime));
            assertTrue(listJobInstancesResponse.getResults().size() > 0);
        } catch (LogException e) {
            assertEquals("InternalServerError", e.GetErrorCode());
            waitOneMinutes();
        }
    }

    private ScheduledSQL generateScheduledSQL(String sqlTaskName, String sourceLogstore,
                                              String destEndpoint, String destProject, String destLogStore,
                                              long fromTime, long toTime, String displayName, JobSchedule jobSchedule,
                                              String fromTimeExpr, String toTimeExpr) {
        ScheduledSQL scheduledSQLStructure = new ScheduledSQL();
        scheduledSQLStructure.setName(sqlTaskName);
        scheduledSQLStructure.setDisplayName(displayName);
        scheduledSQLStructure.setDescription("sql description");
        ScheduledSQLConfiguration scheduledSQLConfiguration = generateConfig(sourceLogstore, destEndpoint,
                destProject, destLogStore, fromTime, toTime, fromTimeExpr, toTimeExpr);
        scheduledSQLStructure.setConfiguration(scheduledSQLConfiguration);

        jobSchedule.setDelay(30);
        scheduledSQLStructure.setSchedule(jobSchedule);
        return scheduledSQLStructure;
    }

    private ScheduledSQLConfiguration generateConfig(String sourceLogstore, String destEndpoint,
                                                     String destProject, String destLogStore,
                                                     long fromTime, long toTime,
                                                     String fromTimeExpr, String toTimeExpr) {
        String script = "SELECT COUNT(DISTINCT(id)) as number_id from " + sourceLogstore + " where dev='slog3_6'";
        int maxRetries = 10;
        int maxRunTimeInSeconds = 1800;

        ScheduledSQLConfiguration scheduledSQLConfiguration = new ScheduledSQLConfiguration();
        scheduledSQLConfiguration.setScript(script);
        scheduledSQLConfiguration.setSqlType("searchQuery");
        scheduledSQLConfiguration.setResourcePool("enhanced");
        scheduledSQLConfiguration.setRoleArn(roleArn);
        scheduledSQLConfiguration.setDestRoleArn(roleArn);
        scheduledSQLConfiguration.setSourceLogstore(sourceLogstore);
        scheduledSQLConfiguration.setDestEndpoint(destEndpoint);
        scheduledSQLConfiguration.setDestProject(destProject);
        scheduledSQLConfiguration.setDestLogstore(destLogStore);
        scheduledSQLConfiguration.setFromTimeExpr(fromTimeExpr);
        scheduledSQLConfiguration.setToTimeExpr(toTimeExpr);
        scheduledSQLConfiguration.setMaxRetries(maxRetries);
        scheduledSQLConfiguration.setMaxRunTimeInSeconds(maxRunTimeInSeconds);
        scheduledSQLConfiguration.setFromTime(fromTime);
        scheduledSQLConfiguration.setToTime(toTime);
        return scheduledSQLConfiguration;
    }

    private Integer getLastHourTime(int n) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
        return (int) (ca.getTimeInMillis() / 1000);
    }

    private String getDestLogsIdNumbers(String project, String logStore, int fromTime, int toTime, String query, Client client) throws LogException {
        GetLogsResponse getLogsResponse = client.GetLogs(project, logStore, fromTime, toTime, "", query);
        return getLogsResponse.GetLogs().get(0).GetLogItem().mContents.get(0).GetValue();
    }

    private Integer getLastTimeInSecond(int n) {
        long current = System.currentTimeMillis() / 1000;
        return (int) current - n;
    }

}
