package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import net.sf.json.JSONObject;
import org.junit.Before;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class FunctionTest {

    private static final Random RANDOM = new Random();

    private static final String CONFIG_FILE = "credentials.json";
    private static final String ACCESS_KEY_ID = "accessKeyID";
    private static final String ACCESS_KEY = "accessKey";
    private static final String ENDPOINT = "endpoint";

    protected Client client;


    private static class Config {
        private final String accessKeyID;
        private final String accessKey;
        private final String endpoint;

        Config(String accessKey, String accessKeyID, String endpoint) {
            this.accessKeyID = accessKeyID;
            this.accessKey = accessKey;
            this.endpoint = endpoint;
        }
    }

    private static Config readConfig() {
        final File file = new File(System.getProperty("user.home"), CONFIG_FILE);
        if (!file.exists()) {
            throw new IllegalStateException(String.format("[%s] doest not exist!", file.getAbsolutePath()));
        }
        try {
            final String text = new Scanner(file).useDelimiter("\\A").next();
            JSONObject object = JSONObject.fromObject(text);
            String endpoint = object.getString(ENDPOINT);
            String accessKeyID = object.getString(ACCESS_KEY_ID);
            String accessKey = object.getString(ACCESS_KEY);
            return new Config(accessKey, accessKeyID, endpoint);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Before
    public void setUp() throws Exception {
        final Config config = readConfig();
        client = new Client(config.endpoint, config.accessKeyID, config.accessKey);
    }

    static <T> T randomFrom(final T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    static <T> T randomFrom(final List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}
