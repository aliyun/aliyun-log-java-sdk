package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EtlJobTest {

    private static final long STARTING_UNIXTIME = 1234567890L;

    @Test
    public void testStartingUnixtimeDeserializeAndRoundTrip() throws Exception {
        String body = "{"
                + "\"etlJobName\":\"etl-job\","
                + "\"sourceConfig\":{\"logstoreName\":\"source-logstore\"},"
                + "\"triggerConfig\":{"
                + "\"roleArn\":\"acs:ram::1234567890123456:role/etl-role\","
                + "\"triggerInterval\":60,"
                + "\"maxRetryTime\":3,"
                + "\"startingPosition\":\"at-unixtime\","
                + "\"startingUnixtime\":" + STARTING_UNIXTIME
                + "},"
                + "\"functionConfig\":{\"functionProvider\":\"CloudProdLogDispatch\"},"
                + "\"functionParameter\":{},"
                + "\"logConfig\":{"
                + "\"endpoint\":\"cn-hangzhou.log.aliyuncs.com\","
                + "\"projectName\":\"project\","
                + "\"logstoreName\":\"result-logstore\""
                + "},"
                + "\"enable\":true"
                + "}";

        EtlJob job = parse(body);
        assertTrigger(job);

        String serialized = job.toJsonString(true, true);
        JSONObject trigger = JSONObject.parseObject(serialized)
                .getJSONObject(Consts.ETL_JOB_TRIGGER_CONFIG);
        assertEquals(Consts.ETL_JOB_TRIGGER_STARTING_POSITION_AT_UNIXTIME,
                trigger.getString(Consts.ETL_JOB_TRIGGER_STARTING_POSITION));
        assertEquals(STARTING_UNIXTIME,
                trigger.getLongValue(Consts.ETL_JOB_TRIGGER_STARTING_UNIXTIME));

        assertTrigger(parse(serialized));
    }

    private static EtlJob parse(String body) throws Exception {
        EtlJob job = new EtlJob();
        job.fromJsonObject(JSONObject.parseObject(body));
        return job;
    }

    private static void assertTrigger(EtlJob job) {
        assertEquals(Consts.ETL_JOB_TRIGGER_STARTING_POSITION_AT_UNIXTIME,
                job.getTriggerConfig().getStartingPosition());
        assertEquals(STARTING_UNIXTIME, job.getTriggerConfig().getStartingUnixtime());
    }
}
