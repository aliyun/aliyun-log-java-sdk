package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import com.aliyun.openservices.log.util.Args;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class FunctionTest {

    static final Random RANDOM = new Random();
    static final String PROJECT_NAME_PREFIX = "sls-sdk-testp-";
    static final Credentials credentials = Credentials.load();

    static final String TEST_ENDPOINT = credentials.getEndpoint();

    static Client client = new Client(
            TEST_ENDPOINT,
            credentials.getAccessKeyId(),
            credentials.getAccessKey());
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    public FunctionTest() {
    }

    public FunctionTest(int time) {
        this.testTimeout = new Timeout(time);
    }

    public void setTestTimeout(int testTimeout) {
        this.testTimeout = new Timeout(testTimeout);
    }

    static String makeProjectName() {
        return PROJECT_NAME_PREFIX + randomBetween(0, 10000) + "-" + getNowTimestamp();
    }

    static int getNowTimestamp() {
        return (int) (new Date().getTime() / 1000);
    }

    static <T> T randomFrom(final T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    static <T> T randomFrom(final List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    static int randomBetween(int low, int high) {
        Args.check(low <= high, "low > high!");
        if (low == high) {
            return low;
        }
        return low + RANDOM.nextInt(high - low);
    }

    static int randomInt(int upperBound) {
        Args.check(upperBound > 0, "upperBound <= 0");
        return RANDOM.nextInt(upperBound);
    }

    static String randomString() {
        int len = randomBetween(10, 20);
        StringBuilder builder = new StringBuilder(len);
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < len; i++) {
            builder.append(CHARS.charAt(randomInt(CHARS.length())));
        }
        return builder.toString();
    }

    static int randomInt() {
        return RANDOM.nextInt(100000);
    }

    static long randomLong() {
        return RANDOM.nextLong();
    }

    static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    static void safeDeleteProjectWithoutSleep(String project) {
        if (project != null && project.startsWith(PROJECT_NAME_PREFIX)) {
            try {
                client.DeleteProject(project);
            } catch (LogException ex) {
                if (!ex.GetErrorCode().equals("ProjectNotExist")) {
                    fail("Delete project failed: " + ex.GetErrorMessage());
                }
            }
        }
    }

    static void safeDeleteProject(String project) {
        safeDeleteProjectWithoutSleep(project);
        // Wait cache refresh completed
        waitOneMinutes();
    }

    static boolean safeDeleteLogStore(String project, String logStore) {
        try {
            client.DeleteLogStore(project, logStore);
            return true;
        } catch (LogException ex) {
            System.out.println("ERROR: errorCode=" + ex.GetErrorCode()
                    + ", httpCode=" + ex.getHttpCode()
                    + ", errorMessage=" + ex.getMessage()
                    + ", requestId=" + ex.getRequestId());
            assertEquals(ex.getHttpCode(), 404);
        }
        return false;
    }

    static void createOrUpdateLogStore(String project, LogStore logStore) {
        safeCreateProject(project, "");
        try {
            client.CreateLogStore(project, logStore);
            waitOneMinutes();
            return;
        } catch (LogException ex) {
            if (!ex.getErrorCode().equals("LogStoreAlreadyExist")) {
                throw new IllegalStateException(ex);
            }
        }
        try {
            client.UpdateLogStore(project, logStore);
            waitOneMinutes();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void createOrUpdateLogStoreNoWait(String project, LogStore logStore) {
        try {
            client.CreateLogStore(project, logStore);
            return;
        } catch (LogException ex) {
            if (!ex.getErrorCode().equals("LogStoreAlreadyExist")) {
                throw new IllegalStateException(ex);
            }
        }
        try {
            client.UpdateLogStore(project, logStore);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void createOrUpdateLogStoreSimple(String project, LogStore logStore) throws Exception {
        safeCreateProject(project, "");
        try {
            client.CreateLogStore(project, logStore);
        } catch (LogException ex) {
            if (!ex.getErrorCode().equals("LogStoreAlreadyExist")) {
                throw new IllegalStateException(ex);
            }
            client.UpdateLogStore(project, logStore);
        }
    }

    static void reCreateLogStore(String project, LogStore logStore) {
        safeCreateProject(project, "");
        safeDeleteLogStore(project, logStore.GetLogStoreName());
        safeCreateLogStore(project, logStore);
        waitOneMinutes();
    }

    static boolean safeCreateProject(String project, String desc) {
        try {
            client.CreateProject(project, desc);
            return true;
        } catch (LogException e) {
            assertEquals("ProjectAlreadyExist", e.GetErrorCode());
        }
        return false;
    }

    static void createProjectIfNotExist(String project, String desc) {
        if (safeCreateProject(project, desc)) {
            waitOneMinutes();
        }
    }

    protected static boolean safeCreateLogStore(String project, LogStore logStore) {
        try {
            client.CreateLogStore(project, logStore);
            return true;
        } catch (LogException e) {
            assertEquals("LogStoreAlreadyExist", e.GetErrorCode());
        }
        return false;
    }

    static void waitOneMinutes() {
        waitForSeconds(60);
    }

    static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    static String exceptionToString(Exception ex) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    static void failOnError(Exception ex) {
        ex.printStackTrace();
        fail(exceptionToString(ex));
    }

    protected int writeData(Client client, String project, String logStore) {
        int round = randomBetween(1, 10);
        int totalLines = 0;
        for (int i = 0; i < round; i++) {
            List<LogItem> logGroup = new ArrayList<LogItem>(10);
            for (int j = 0; j < 10; j++) {
                LogItem logItem = new LogItem(getNowTimestamp());
                logItem.PushBack("ID", "id_" + (i * 10 + j));
                logGroup.add(logItem);
                ++totalLines;
            }
            String topic = "topic_" + i;
            try {
                client.PutLogs(project, logStore, topic, logGroup, "");
            } catch (LogException e) {
                fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
            }
        }
        return totalLines;
    }

    protected List<FastLogGroup> pullAllLogGroups(Client client, String project, String logStore, int shardNum) throws LogException {
        List<FastLogGroup> groups = new ArrayList<FastLogGroup>();
        for (int i = 0; i < shardNum; i++) {
            pullForShard(client, project, logStore, i, groups);
        }
        return groups;
    }

    private void pullForShard(Client client, String project, String logStore, int shard, List<FastLogGroup> results) throws LogException {
        GetCursorResponse cursorResponse = client.GetCursor(project, logStore, shard, Consts.CursorMode.BEGIN);
        String cursor = cursorResponse.GetCursor();
        while (true) {
            PullLogsRequest request = new PullLogsRequest(project, logStore, shard, 1000, cursor);
            PullLogsResponse response = client.pullLogs(request);
            for (LogGroupData data : response.getLogGroups()) {
                results.add(data.GetFastLogGroup());
            }
            final String nextCursor = response.getNextCursor();
            if (cursor.equals(nextCursor)) {
                break;
            }
            cursor = nextCursor;
        }
    }
}
