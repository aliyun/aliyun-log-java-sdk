package com.aliyun.openservices.log.functiontest;

import net.sf.json.JSONObject;

import java.io.File;
import java.util.Scanner;


public final class Credentials {
    private static final String CONFIG_FILE = "credentials.json";

    private String endpoint;
    private String accessKeyId;
    private String accessKey;

    public Credentials(String endpoint, String accessKeyId, String accessKey) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKey = accessKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public static Credentials load() {
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
            return new Credentials(endpoint, accessKeyId, accessKey);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
