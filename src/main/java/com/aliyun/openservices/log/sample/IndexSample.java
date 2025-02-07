package com.aliyun.openservices.log.sample;

import java.util.ArrayList;
import java.util.Arrays;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.common.IndexLine;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.CreateIndexResponse;
import com.aliyun.openservices.log.response.DeleteIndexResponse;
import com.aliyun.openservices.log.response.GetIndexResponse;
import com.aliyun.openservices.log.response.UpdateIndexResponse;

public class IndexSample {
    private Client client;
    IndexSample() {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";

        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }

    public static void main(String[] args) {
        IndexSample sample = new IndexSample();

        // create index for an exsiting logstore,
        // to create a logstore, see file LogStoreSample.java
        // to create a project, see file LogProject.java
        String project = "test-project";
        String logStoreName = "test-logstore";

        sample.CreateIndex(project, logStoreName);
        sample.GetIndex(project, logStoreName);
        sample.UpdateIndex(project, logStoreName);
        sample.DeleteIndex(project, logStoreName);
    }

    public void CreateIndex(String project, String logStoreName) {
        try {
            IndexKeys keys = new IndexKeys();
            IndexKey key = new IndexKey(new ArrayList<String>(Arrays.asList(
                    "  ", " ", ",")), false);
            keys.AddKey("key_1", key);
            IndexLine line = new IndexLine(new ArrayList<String>(Arrays.asList(
                    "\t", "\n")), true);
            line.SetIncludeKeys(new ArrayList<String>(Arrays.asList("key_3", "key_4")));
            Index index = new Index(7, keys, line);
            CreateIndexResponse res = client.CreateIndex(project, logStoreName, index);
            System.out.println(res.GetRequestId());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void UpdateIndex(String project, String logStoreName) {
        try {
            IndexKeys keys = new IndexKeys();
            IndexKey key = new IndexKey(new ArrayList<String>(Arrays.asList(
                    "  ", " ", ",")), false);
            keys.AddKey("key_1", key);
            IndexLine line = new IndexLine(new ArrayList<String>(Arrays.asList(
                    "\t", "\n")), true);
            line.SetIncludeKeys(new ArrayList<String>(Arrays.asList("key_3", "key_4")));
            Index index = new Index(7, keys, line);
            UpdateIndexResponse res = client.UpdateIndex(project, logStoreName, index);
            System.out.println(res.GetRequestId());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void GetIndex(String project, String logStoreName) {
        try {
            GetIndexResponse res = client.GetIndex(project, logStoreName);
            System.out.println(res.GetRequestId());
            System.out.println("index config :" + res.GetIndex().ToJsonString());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void DeleteIndex(String project, String logStoreName) {
        try {
            DeleteIndexResponse res = client.DeleteIndex(project, logStoreName);
            System.out.println(res.GetRequestId());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }
}
