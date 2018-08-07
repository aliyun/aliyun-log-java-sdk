package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.NetworkUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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

    private static final Random RANDOM = new Random();

    private static final String CONFIG_FILE = "credentials.json";

    // Overwrite configuration in CONFIG_FILE
    private static final String ENDPOINT = "";
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY = "";


    protected Client client;


    protected static class Config {
        private final String accessKeyID;
        private final String accessKey;
        private final String endpoint;

        Config(String accessKey, String accessKeyID, String endpoint) {
            this.accessKeyID = accessKeyID;
            this.accessKey = accessKey;
            this.endpoint = endpoint;
        }

        public String getAccessKeyID() {
            return accessKeyID;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }

    static Config readConfig() {
        if (StringUtils.isNotBlank(ENDPOINT)
                && StringUtils.isNotBlank(ACCESS_KEY_ID)
                && StringUtils.isNotBlank(ACCESS_KEY)) {
            return new Config(ACCESS_KEY, ACCESS_KEY_ID, ENDPOINT);
        }
        final File file = new File(System.getProperty("user.home"), CONFIG_FILE);
        if (!file.exists()) {
            throw new IllegalStateException(String.format("[%s] doest not exist!", file.getAbsolutePath()));
        }
        try {
            final String text = new Scanner(file).useDelimiter("\\A").next();
            JSONObject object = JSONObject.fromObject(text);
            String endpoint = object.getString("endpoint");
            String accessKeyID = object.getString("accessKeyID");
            String accessKey = object.getString("accessKey");
            return new Config(accessKey, accessKeyID, endpoint);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Before
    public void setUp() throws Exception {
        final Config config = readConfig();
        client = new Client(config.endpoint, config.accessKeyID, config.accessKey, NetworkUtils.getLocalMachineIP(),
                false, 30000, 30000, 30000);
    }

    static int timestampNow() {
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

    static boolean randomBoolean() {
        return randomBetween(1, 10) % 2 == 1;
    }

    void safeDeleteProject(String project) {
        try {
            client.DeleteProject(project);
            // Wait cache refresh completed
            waitForSeconds(60);
        } catch (LogException ex) {
            if (!ex.GetErrorCode().equals("ProjectNotExist")) {
                fail("Delete project failed: " + ex.GetErrorMessage());
            }
        }
    }

    boolean safeDeleteLogStore(String project, String logStore) {
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

    void reCreateLogStore(String project, LogStore logStore) {
        if (safeCreateProject(project, "")) {
            waitForSeconds(60);
        }
        if (safeDeleteLogStore(project, logStore.GetLogStoreName())) {
            waitForSeconds(60);
        }
        if (safeCreateLogStore(project, logStore)) {
            waitForSeconds(60);
        }
    }

    boolean safeCreateProject(String project, String desc) {
        try {
            client.CreateProject(project, desc);
            return true;
        } catch (LogException e) {
            assertEquals("ProjectAlreadyExist", e.GetErrorCode());
        }
        return false;
    }

    boolean safeCreateLogStore(String project, LogStore logStore) {
        try {
            client.CreateLogStore(project, logStore);
            return true;
        } catch (LogException e) {
            assertEquals("LogStoreAlreadyExist", e.GetErrorCode());
        }
        return false;
    }

    void waitForSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
