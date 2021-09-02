package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricAggRules implements Serializable {

    private static final long serialVersionUID = 7119605382626844897L;
    private String id;
    private String name;
    private String desc;

    private String srcStore;
    private String SrcAccessKeyID;
    private String SrcAccessKeySecret;

    private String destEndpoint;
    private String destProject;
    private String destStore;
    private String destAccessKeyID;
    private String destAccessKeySecret;

    private MetricAggRuleItem[] aggRules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSrcStore() {
        return srcStore;
    }

    public void setSrcStore(String srcStore) {
        this.srcStore = srcStore;
    }

    public String getSrcAccessKeyID() {
        return SrcAccessKeyID;
    }

    public void setSrcAccessKeyID(String srcAccessKeyID) {
        SrcAccessKeyID = srcAccessKeyID;
    }

    public String getSrcAccessKeySecret() {
        return SrcAccessKeySecret;
    }

    public void setSrcAccessKeySecret(String srcAccessKeySecret) {
        SrcAccessKeySecret = srcAccessKeySecret;
    }

    public String getDestEndpoint() {
        return destEndpoint;
    }

    public void setDestEndpoint(String destEndpoint) {
        this.destEndpoint = destEndpoint;
    }

    public String getDestProject() {
        return destProject;
    }

    public void setDestProject(String destProject) {
        this.destProject = destProject;
    }

    public String getDestStore() {
        return destStore;
    }

    public void setDestStore(String destStore) {
        this.destStore = destStore;
    }

    public String getDestAccessKeyID() {
        return destAccessKeyID;
    }

    public void setDestAccessKeyID(String destAccessKeyID) {
        this.destAccessKeyID = destAccessKeyID;
    }

    public String getDestAccessKeySecret() {
        return destAccessKeySecret;
    }

    public void setDestAccessKeySecret(String destAccessKeySecret) {
        this.destAccessKeySecret = destAccessKeySecret;
    }

    public MetricAggRuleItem[] getAggRules() {
        return aggRules;
    }

    public void setAggRules(MetricAggRuleItem[] aggRules) {
        this.aggRules = aggRules;
    }

    private Map<String, String> createScheduledSQLParameters(MetricAggRuleItem[] aggRulesItems) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Map<String, String> jobMode = new HashMap<String, String>();
        jobMode.put("type", "ml");
        jobMode.put("source", "ScheduledSQL");
        String jobModeStr = JSONObject.toJSONString(jobMode);
        parameterMap.put("sls.config.job_mode", jobModeStr);

        Map<String, Object> scheduledSql = new HashMap<String, Object>();
        List<Object> aggRuleJsons = new ArrayList<Object>();
        for (MetricAggRuleItem aggRulesItem : aggRulesItems) {
            Map<String, Object> aggRuleMap = new HashMap<String, Object>();

            aggRuleMap.put("rule_name", aggRulesItem.getName());

            Map<String, Object> advancedQueryMap = new HashMap<String, Object>();
            advancedQueryMap.put("type", aggRulesItem.getQueryType());
            advancedQueryMap.put("query", aggRulesItem.getQuery());
            advancedQueryMap.put("time_name", aggRulesItem.getTimeName());
            advancedQueryMap.put("metric_names", aggRulesItem.getMetricNames());
            advancedQueryMap.put("labels", aggRulesItem.getLabelNames());
            aggRuleMap.put("advanced_query", advancedQueryMap);

            Map<String, Object> scheduleControlMap = new HashMap<String, Object>();
            scheduleControlMap.put("from_unixtime", aggRulesItem.getBeginUnixTime());
            scheduleControlMap.put("to_unixtime", aggRulesItem.getEndUnixTime());
            scheduleControlMap.put("granularity", aggRulesItem.getInterval());
            scheduleControlMap.put("delay", aggRulesItem.getDelaySeconds());
            aggRuleMap.put("schedule_control", scheduleControlMap);

            aggRuleJsons.add(aggRuleMap);
        }
        scheduledSql.put("agg_rules", aggRuleJsons);
        String scheduledSqlJson = JSONObject.toJSONString(scheduledSql);
        parameterMap.put("config.ml.scheduled_sql", scheduledSqlJson);
        return parameterMap;
    }

    public ETLV2 createScheduledETL(MetricAggRules metricAggRules) {
        ETLV2 etl = new ETLV2();
        etl.setName(metricAggRules.getId());
        etl.setDisplayName(metricAggRules.getName());
        etl.setDescription(metricAggRules.getDesc());
        ETLConfiguration configuration = new ETLConfiguration();
        configuration.setLogstore(metricAggRules.getSrcStore());
        configuration.setScript("");
        configuration.setAccessKeyId(metricAggRules.getSrcAccessKeyID());
        configuration.setAccessKeySecret(metricAggRules.getSrcAccessKeySecret());
        configuration.setParameters(createScheduledSQLParameters(metricAggRules.getAggRules()));
        List<AliyunLOGSink> sinks = new ArrayList<AliyunLOGSink>();
        AliyunLOGSink sink = new AliyunLOGSink(DataSinkType.ALIYUN_LOG, "sls-convert-metric", metricAggRules.getDestEndpoint(), metricAggRules.getDestProject(), metricAggRules.getDestStore(), metricAggRules.getDestAccessKeyID(), metricAggRules.getDestAccessKeySecret());
        sinks.add(sink);
        configuration.setSinks(sinks);
        etl.setConfiguration(configuration);
        return etl;
    }

    public void deserialize(ETLV2 etl) {
        this.setId(etl.getScheduleId());
        this.setName(etl.getName());
        this.setDesc(etl.getDescription());

        ETLConfiguration configuration = etl.getConfiguration();
        if (configuration != null) {
            this.setSrcAccessKeyID(configuration.getAccessKeyId());
            this.setSrcAccessKeySecret(configuration.getAccessKeySecret());
            this.setSrcStore(configuration.getLogstore());

            Map<String, String> parameters = configuration.getParameters();
            String scheduledSqlJson = parameters.get("config.ml.scheduled_sql");

            JSONObject aggRuleJson = JSONObject.parseObject(scheduledSqlJson);
            JSONArray aggRuleMaps = aggRuleJson.getJSONArray("agg_rules");
            MetricAggRuleItem[] aggRuleItems = new MetricAggRuleItem[aggRuleMaps.size()];
            for (int i = 0; i < aggRuleMaps.size(); i++) {
                MetricAggRuleItem metricAggRuleItem = new MetricAggRuleItem();
                JSONObject aggRuleMap = aggRuleMaps.getJSONObject(i);
                metricAggRuleItem.setName(aggRuleMap.getString("rule_name"));

                JSONObject advancedQuery = aggRuleMap.getJSONObject("advanced_query");
                metricAggRuleItem.setQueryType(advancedQuery.getString("type"));
                metricAggRuleItem.setQuery(advancedQuery.getString("query"));
                metricAggRuleItem.setTimeName(advancedQuery.getString("time_name"));

                JSONArray metricNamesJson = advancedQuery.getJSONArray("metric_names");
                String[] metricNames = metricNamesJson.toArray(new String[metricNamesJson.size()]);
                metricAggRuleItem.setMetricNames(metricNames);

                JSONObject labelsJson = advancedQuery.getJSONObject("labels");
                Map labels = JSONObject.toJavaObject(labelsJson, Map.class);
                metricAggRuleItem.setLabelNames(labels);

                JSONObject scheduleControl = aggRuleMap.getJSONObject("schedule_control");
                metricAggRuleItem.setBeginUnixTime(scheduleControl.getIntValue("from_unixtime"));
                metricAggRuleItem.setEndUnixTime(scheduleControl.getIntValue("to_unixtime"));
                metricAggRuleItem.setInterval(scheduleControl.getIntValue("granularity"));
                metricAggRuleItem.setDelaySeconds(scheduleControl.getIntValue("delay"));
                aggRuleItems[i] = metricAggRuleItem;
            }
            this.setAggRules(aggRuleItems);

            List<AliyunLOGSink> sinks = configuration.getSinks();
            for (AliyunLOGSink sink : sinks) {
                this.setDestEndpoint(sink.getEndpoint());
                this.setDestAccessKeyID(sink.getAccessKeyId());
                this.setDestAccessKeySecret(sink.getAccessKeySecret());
                this.setDestProject(sink.getProject());
                this.setDestStore(sink.getLogstore());
            }
        }
    }
}
