package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.GetProjectResponse;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonModelCompatibilityTest {

    @Test
    public void etlMetaReadsObjectWrittenByItsEncoder() throws Exception {
        Map<String, Object> metaValue = new LinkedHashMap<String, Object>();
        metaValue.put("endpoint", "example.com");
        metaValue.put("enabled", true);
        EtlMeta original = new EtlMeta("meta", "key", "tag", metaValue, true);

        EtlMeta decoded = new EtlMeta();
        decoded.fromJsonObject(original.toJsonObject());

        assertEquals("example.com", decoded.getMetaValue().get("endpoint"));
        assertEquals(Boolean.TRUE, decoded.getMetaValue().get("enabled"));
    }

    @Test
    public void etlMetaStillReadsLegacyStringValue() throws Exception {
        JSONObject value = JSONObject.parseObject("{"
                + "\"etlMetaName\":\"meta\",\"etlMetaKey\":\"key\",\"etlMetaTag\":\"tag\","
                + "\"etlMetaValue\":\"{\\\"endpoint\\\":\\\"example.com\\\"}\\n\"}");
        EtlMeta decoded = new EtlMeta();
        decoded.fromJsonObject(value);
        assertEquals("example.com", decoded.getMetaValue().get("endpoint"));
    }

    @Test
    public void customizedFieldsReadsObjectWrittenByItsEncoder() throws Exception {
        ConfigInputDetail original = new ConfigInputDetail(
                "/var/log", "*.log", "common_reg_log", ".*", "(.*)",
                new ArrayList<String>(Arrays.asList("content")), "", false,
                "{\"env\":\"prod\"}");

        ConfigInputDetail decoded = new ConfigInputDetail();
        decoded.fromJsonObject(original.toJsonObject());

        assertJsonEquals("{\"env\":\"prod\"}", decoded.GetCustomizedFields());
    }

    @Test
    public void queryInfoReadsNativeAndLegacyScalarRepresentations() {
        assertQueryInfo("{\"limited\":7,\"phraseQueryInfo\":{\"scanAll\":true,"
                + "\"beginOffset\":11,\"endOffset\":22,\"endTime\":33}}");
        assertQueryInfo("{\"limited\":\"7\",\"phraseQueryInfo\":{\"scanAll\":\"true\","
                + "\"beginOffset\":\"11\",\"endOffset\":\"22\",\"endTime\":\"33\"}}");
    }

    @Test
    public void projectTimesReadStringAndLegacyNumberRepresentations() throws Exception {
        Project project = new Project();
        project.fromJsonObject(projectJson(100, 200));
        assertEquals("100", project.getCreateTime());
        assertEquals("200", project.getLastModifyTime());

        GetProjectResponse response = new GetProjectResponse(Collections.<String, String>emptyMap());
        response.fromJsonObject(projectJson("100", "200"));
        assertEquals("100", response.getCreateTime());
        assertEquals("200", response.getLastModifyTime());
    }

    @Test
    public void configReadsResponseOnlyOutputType() throws Exception {
        Config config = new Config();
        config.fromJsonObject(JSONObject.parseObject("{"
                + "\"configName\":\"config\","
                + "\"inputType\":\"file\","
                + "\"outputType\":\"OtherOutput\""
                + "}"));
        assertEquals("OtherOutput", config.getOutputType());
    }

    @Test
    public void shipperFactoryOwnsTargetTypeDispatch() throws Exception {
        ShipperConfig config = ShipperConfig.fromJsonObject("oss", JSONObject.parseObject("{"
                + "\"ossBucket\":\"bucket\","
                + "\"ossPrefix\":\"prefix\","
                + "\"roleArn\":\"role\","
                + "\"bufferInterval\":60,"
                + "\"bufferSize\":10,"
                + "\"compressType\":\"none\","
                + "\"pathFormat\":\"%Y/%m/%d\","
                + "\"storage\":{\"format\":\"json\",\"detail\":{\"enableTag\":true}}"
                + "}"));
        assertTrue(config instanceof OssShipperConfig);
        assertEquals("bucket", ((OssShipperConfig) config).getOssBucket());
    }

    private static void assertQueryInfo(String queryInfo) {
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
        headers.put(Consts.CONST_X_LOG_QUERY_INFO, queryInfo);
        GetLogsResponse response = new GetLogsResponse(headers);
        assertEquals(7, response.getLimited());
        assertTrue(response.IsScanAll());
        assertEquals(11, response.GetBeginOffset());
        assertEquals(22, response.GetEndOffset());
        assertEquals(33, response.GetEndTime());
        assertFalse(response.IsPhraseQuery());
    }

    private static JSONObject projectJson(Number createTime, Number lastModifyTime) {
        JSONObject value = baseProjectJson();
        value.put(Consts.CONST_CREATTIME, createTime);
        value.put(Consts.CONST_LASTMODIFYTIME, lastModifyTime);
        return value;
    }

    private static JSONObject projectJson(String createTime, String lastModifyTime) {
        JSONObject value = baseProjectJson();
        value.put(Consts.CONST_CREATTIME, createTime);
        value.put(Consts.CONST_LASTMODIFYTIME, lastModifyTime);
        return value;
    }

    private static JSONObject baseProjectJson() {
        JSONObject value = new JSONObject();
        value.put(Consts.CONST_PROJECTNAME, "project");
        value.put(Consts.CONST_PROJECTDESC, "description");
        value.put(Consts.CONST_PROJECTOWNER, "owner");
        value.put(Consts.CONST_PROJECTSTATUS, "Normal");
        value.put(Consts.CONST_PROJECTREGION, "cn-hangzhou");
        value.put(Consts.CONST_RESOURCEGROUPID, "rg-acfm");
        value.put("dataRedundancyType", "LRS");
        value.put("transferAcceleration", "Disabled");
        value.put("deletionProtection", false);
        return value;
    }
}
