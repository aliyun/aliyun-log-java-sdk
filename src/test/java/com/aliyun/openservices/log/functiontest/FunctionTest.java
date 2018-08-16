package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.NetworkUtils;
import net.sf.json.JSONObject;
import org.junit.Before;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class FunctionTest {

    static final Random RANDOM = new Random();

    private static final String CONFIG_FILE = "credentials.json";
    final static ClientWrapper client = ClientWrapper.createFromConfig();

    @Before
    public void setUp() throws Exception {
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

    static void reCreateLogStore(String project, LogStore logStore) {
        if (safeCreateProject(project, "")) {
            waitOneMinutes();
        }
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

    static class ClientWrapper extends Client {
        private String endpoint;

        ClientWrapper(String accessKey, String accessKeyId, String endpoint) {
            super(endpoint, accessKeyId, accessKey, NetworkUtils.getLocalMachineIP(),
                    false, 30000, 30000, 30000);
            this.endpoint = endpoint;
        }

        String getEndpoint() {
            return endpoint;
        }

        static ClientWrapper createFromConfig() {
            final File file = new File(System.getProperty("user.home"), CONFIG_FILE);
            if (!file.exists()) {
                throw new IllegalStateException(String.format("[%s] does not exist!", file.getAbsolutePath()));
            }
            try {
                final String text = new Scanner(file).useDelimiter("\\A").next();
                JSONObject object = JSONObject.fromObject(text);
                String endpoint = object.getString("endpoint");
                String accessKeyId = object.getString("accessKeyId");
                String accessKey = object.getString("accessKey");
                return new ClientWrapper(accessKey, accessKeyId, endpoint);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
