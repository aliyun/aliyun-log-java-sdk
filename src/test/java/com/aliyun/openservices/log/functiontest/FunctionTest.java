package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;

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
                    + ", errorMessage=" + ex.GetErrorMessage()
                    + ", requestId=" + ex.GetRequestId());
            if (!ex.GetErrorCode().equals("LogStoreNotExist")) {
                fail("Delete logStore " + logStore + " failed");
            }
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
            Thread.currentThread().interrupt();
        }
    }
}
