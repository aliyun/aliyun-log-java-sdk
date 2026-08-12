package com.aliyun.openservices.log.request;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;

public class RequestBodyCompatibilityTest {

    private static final String SPECIAL = "中文<>&=\"\\line";

    @Test
    public void getLogsV2PreservesLargeIntegersFlagsAndSpecialCharacters() {
        GetLogsRequestV2 request = new GetLogsRequestV2(
                "project", "logstore", 1700000000, 1700003600, "topic-" + SPECIAL,
                "* | select count(*) where latency>=10", 123456789L, 100, true);
        request.setPowerSql(false);
        request.setForward(true);
        request.setSession("session-" + SPECIAL);
        request.SetAccurate(true);
        request.SetNeedHighlight(true);

        assertJsonEquals("{\"from\":1700000000,\"to\":1700003600,\"line\":100,"
                        + "\"offset\":123456789,\"reverse\":true,\"powerSql\":false,"
                        + "\"session\":\"session-中文<>&=\\\"\\\\line\","
                        + "\"topic\":\"topic-中文<>&=\\\"\\\\line\","
                        + "\"query\":\"* | select count(*) where latency>=10\","
                        + "\"forward\":true,\"accurate\":true,\"highlight\":true}",
                request.getRequestBody());
    }

    @Test
    public void submitAsyncSqlPreservesNestedExtensionsAndExplicitFalse() {
        SubmitAsyncSqlRequest request = new SubmitAsyncSqlRequest(
                "project", "logstore", "* | select '<>&=' as special, 123456789",
                1700000000, 1700003600);
        request.setMaxRunMillis(123456789L);
        request.setPowerSqlEnabled(false);
        request.addSession("x-sls-session", SPECIAL);

        assertJsonEquals("{\"logstore\":\"logstore\","
                        + "\"query\":\"* | select '<>&=' as special, 123456789\","
                        + "\"from\":1700000000,\"to\":1700003600,"
                        + "\"extensions\":{\"maxRunTime\":123456789,"
                        + "\"powerSql\":false,"
                        + "\"sessions\":{\"x-sls-session\":\"中文<>&=\\\"\\\\line\"}}}",
                new String(request.getRequestBody(), StandardCharsets.UTF_8));
    }
}
