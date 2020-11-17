package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class FunctionTest {

    static final Random RANDOM = new Random();
    static final Credentials credentials = Credentials.load();
    static final Client client = new Client(credentials.getEndpoint(), credentials.getAccessKeyId(), credentials.getAccessKey());

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
        return RANDOM.nextInt();
    }

    static long randomLong() {
        return RANDOM.nextLong();
    }

    static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    static void safeDeleteProject(String project) {
        try {
            client.DeleteProject(project);
            // Wait cache refresh completed
            waitOneMinutes();
        } catch (LogException ex) {
            if (!ex.GetErrorCode().equals("ProjectNotExist")) {
                fail("Delete project failed: " + ex.GetErrorMessage());
            }
        }
    }

    static boolean safeDeleteLogStore(String project, String logStore) {
        try {
            client.DeleteLogStore(project, logStore);
            return true;
        } catch (LogException ex) {
            System.out.println("ERROR: errorCode=" + ex.GetErrorCode()
                    + ", httpCode=" + ex.GetHttpCode()
                    + ", errorMessage=" + ex.GetErrorMessage()
                    + ", requestId=" + ex.GetRequestId());
            assertEquals(ex.GetHttpCode(), 404);
        }
        return false;
    }

    static void createOrUpdateLogStore(String project, LogStore logStore) {
        createProjectIfNotExist(project, "");
        try {
            client.CreateLogStore(project, logStore);
            waitOneMinutes();
            return;
        } catch (LogException ex) {
            if (!ex.GetErrorCode().equals("LogStoreAlreadyExist")) {
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

    static void reCreateLogStore(String project, LogStore logStore) {
        createProjectIfNotExist(project, "");
        if (safeDeleteLogStore(project, logStore.GetLogStoreName())) {
            waitOneMinutes();
        }
        if (safeCreateLogStore(project, logStore)) {
            waitOneMinutes();
        }
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

    private static boolean safeCreateLogStore(String project, LogStore logStore) {
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
            TimeUnit.SECONDS.sleep(seconds);
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
}
