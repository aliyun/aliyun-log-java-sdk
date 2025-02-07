package com.aliyun.openservices.log.sample;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.ConfigInputDetail;
import com.aliyun.openservices.log.common.ConfigOutputDetail;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.CreateConfigResponse;
import com.aliyun.openservices.log.response.DeleteConfigResponse;
import com.aliyun.openservices.log.response.GetConfigResponse;
import com.aliyun.openservices.log.response.ListConfigResponse;
import com.aliyun.openservices.log.response.UpdateConfigResponse;

public class LogtailConfigSample {
    private Client client;
    private String testConfigName = "test-config";
    private String project = "test-project";

    LogtailConfigSample() {
		String endpoint = "";
		String accessKeyID = "test_accessKeyId";
		String accessKeySecret = "test_accessKey";

        // create a client
		this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }

    public static void main(String[] args) {
        LogtailConfigSample sample = new LogtailConfigSample();
        sample.CreateConfig();
        sample.UpdateConfig();
        sample.GetConfig();
        sample.ListConfigs();
        sample.DeleteConfig();
    }


    public void CreateConfig() {
        Config config = new Config(testConfigName);
        //construct config type 1: using ConfigInputDetail and ConfigOutputDetail

        ConfigInputDetail inputDetail = new ConfigInputDetail();
        inputDetail.SetLogType("common_reg_log");
        inputDetail.SetLogPath("/var/log/httpd/");
        inputDetail.SetFilePattern("access.log");
        inputDetail.SetLocalStorage(true);
        inputDetail.SetTimeFormat("%H%m%S");
        inputDetail.SetLogBeginRegex("\\d+");
        inputDetail.SetRegex("(\\d+) (\\d+)");
        //TopicFormat:none, group_topic, default, using regex
        inputDetail.SetTopicFormat("group_topic"); // using group topic

        ArrayList<String> key = new ArrayList<String>();
        key.add("number");
        key.add("seqno");
        inputDetail.SetKey(key);

        ArrayList<String> filterKey = new ArrayList<String>();
        filterKey.add("number1");
        filterKey.add("seqno1");

        ArrayList<String> filterRegex = new ArrayList<String>();
        filterRegex.add("123-*");
        filterRegex.add("abc-*");

        inputDetail.SetFilterKeyRegex(filterKey, filterRegex);

        config.SetInputDetail(inputDetail);

        ConfigOutputDetail outputDetail = new ConfigOutputDetail();

        outputDetail.SetEndpoint("cn-hangzhou-for-sample.sls.aliyuncs.com");
        outputDetail.SetLogstoreName("perfcounter");
        config.SetOutputDetail(outputDetail);

        try {
            CreateConfigResponse res = client.CreateConfig(project, config);

            System.out.println(res.GetAllHeaders().toString());
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void UpdateConfig() {
        Config config = new Config(testConfigName);
        JSONObject inputDetail = new JSONObject();
        inputDetail.put("logType", "apsara_log");
        inputDetail.put("logPath", "/var/log/httpd1/");
        inputDetail.put("filePattern", "access1.log");
        inputDetail.put("localStorage", false);
        inputDetail.put("timeFormat", "%h");
        inputDetail.put("logBeginRegex", "\\w+");
        inputDetail.put("regex", "(\\w+) (\\w+)");
        inputDetail.put("topicFormat", "none");

        JSONArray key = new JSONArray();
        key.add("name3");
        key.add("seqno3");
        inputDetail.put("key", key);

        JSONArray filterKey = new JSONArray();
        inputDetail.put("filterKey", filterKey);

        JSONArray filterRegex = new JSONArray();
        inputDetail.put("filterRegex", filterRegex);

        try {
            config.SetInputDetail(inputDetail);
        } catch (LogException e) {
            e.printStackTrace();
        }

        JSONObject outputDetail = new JSONObject();
        outputDetail.put("projectName", "ay421");
        outputDetail.put("logstoreName", "perfcounter1");
        try {
            config.SetOutputDetail(outputDetail);
        } catch (LogException e) {
            e.printStackTrace();
        }

        try {
            UpdateConfigResponse res = client.UpdateConfig(project, config);
            System.out.println("RequestId:" + res.GetRequestId());
            Thread.sleep(100);
        } catch (LogException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void GetConfig() {
        try {
            GetConfigResponse res = client.GetConfig(project, testConfigName);
            System.out.println("RequestId:" + res.GetRequestId());
            Config config = res.GetConfig();
            System.out.println("ConfigName:" + config.GetConfigName());

            //Optional get inputDetail by json object
            //JSONObject inputDetail = ((ConfigInputDetail)(res.GetConfig().GetInputDetail())).ToJson();

            System.out.println("logType:" + ((ConfigInputDetail) res.GetConfig().GetInputDetail()).GetLogType());
            System.out.println("logPath:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetLogPath());
            System.out.println("filePattern:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetFilePattern());
            System.out.println("localStorage:" + res.GetConfig().GetInputDetail().GetLocalStorage());
            System.out.println("timeFormat:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetTimeFormat());
            System.out.println("logBeginRegex:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetLogBeginRegex());
            System.out.println("regex:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetRegex());
            System.out.println("topicFormat:" + ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetTopicFormat());

            List<String> keyRes = ((ConfigInputDetail) (res.GetConfig().GetInputDetail())).GetKey();
            System.out.println("key");
            for (String key : keyRes) {
                System.out.println(key);
            }

            List<String> filterKeyRes = res.GetConfig().GetInputDetail().GetFilterKey();
            System.out.println("filterKey");
            for (String filterKey : filterKeyRes) {
                System.out.println(filterKey);
            }

            List<String> filterRegexRes = res.GetConfig().GetInputDetail().GetFilterRegex();
            System.out.println("filterRegex");
            for (String filterRegex : filterRegexRes) {
                System.out.println(filterRegex);
            }

            //Optional get outputDetail by json object
            //JSONObject outputDetail = res.GetConfig().GetOutputDetail().ToJson();


            System.out.println("OutputDetail Endpoint:" + config.GetOutputDetail().GetEndpoint());
            System.out.println("OutputDetail LogStoreName:" + config.GetOutputDetail().GetLogstoreName());

            //System.out.println("CreateTime:" + config.GetCreateTime());
            //System.out.println("LastModifyTime:" + config.GetLastModifyTime());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void DeleteConfig() {

        try {
            DeleteConfigResponse res = client.DeleteConfig(project, testConfigName);
            System.out.println("RequestId:" + res.GetRequestId());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void ListConfigs() {
        try {
            long s_t = System.currentTimeMillis();
            ListConfigResponse res = client.ListConfig(project);
            long e_t = System.currentTimeMillis();
            System.out.print("ms:" + (e_t - s_t));
            System.out.println("RequestId:" + res.GetRequestId());
            int total = res.GetTotal();
            int cout = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("Count:" + cout);
            System.out.println("ConfigNames:" + res.GetConfigs().toString());
            // add config name filter
            res = client.ListConfig(project, "nonexist", 0, 100);
            e_t = System.currentTimeMillis();
            System.out.print("ms:" + (e_t - s_t));
            System.out.println("RequestId:" + res.GetRequestId());
            total = res.GetTotal();
            cout = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("Count:" + cout);
            System.out.println("ConfigNames:" + res.GetConfigs().toString());
            res = client.ListConfig(project, testConfigName, 0, 100);
            e_t = System.currentTimeMillis();
            System.out.print("ms:" + (e_t - s_t));
            System.out.println("RequestId:" + res.GetRequestId());
            total = res.GetTotal();
            cout = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("Count:" + cout);
            System.out.println("ConfigNames:" + res.GetConfigs().toString());
            res = client.ListConfig(project, testConfigName, "perfcounter", 0, 100);
            e_t = System.currentTimeMillis();
            System.out.print("ms:" + (e_t - s_t));
            System.out.println("RequestId:" + res.GetRequestId());
            total = res.GetTotal();
            cout = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("Count:" + cout);
            System.out.println("ConfigNames:" + res.GetConfigs().toString());
            res = client.ListConfig(project, testConfigName, "perfcounter1", 0, 100);
            e_t = System.currentTimeMillis();
            System.out.print("ms:" + (e_t - s_t));
            System.out.println("RequestId:" + res.GetRequestId());
            total = res.GetTotal();
            cout = res.GetCount();
            System.out.println("total:" + total);
            System.out.println("Count:" + cout);
            System.out.println("ConfigNames:" + res.GetConfigs().toString());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }
}
