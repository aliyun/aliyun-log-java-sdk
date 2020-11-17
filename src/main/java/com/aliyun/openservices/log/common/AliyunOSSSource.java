package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;

public class AliyunOSSSource extends DataSource {

    private String bucket;

    private String endpoint;

    private String roleARN;

    private String prefix;

    private String pattern;

    private String compressionCodec;

    private String encoding;

    private DataFormat format;

    private boolean restoreObjectEnabled;

    /**
     * Whether use object last modified time as log time.
     */
    private boolean lastModifyTimeAsLogTime = false;

    public AliyunOSSSource() {
        super(DataSourceType.ALIYUN_OSS);
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRoleARN() {
        return roleARN;
    }

    public void setRoleARN(String roleARN) {
        this.roleARN = roleARN;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getCompressionCodec() {
        return compressionCodec;
    }

    public void setCompressionCodec(String compressionCodec) {
        this.compressionCodec = compressionCodec;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public DataFormat getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format;
    }

    public boolean isRestoreObjectEnabled() {
        return restoreObjectEnabled;
    }

    public void setRestoreObjectEnabled(boolean restoreObjectEnabled) {
        this.restoreObjectEnabled = restoreObjectEnabled;
    }

    public boolean isLastModifyTimeAsLogTime() {
        return lastModifyTimeAsLogTime;
    }

    public void setLastModifyTimeAsLogTime(boolean lastModifyTimeAsLogTime) {
        this.lastModifyTimeAsLogTime = lastModifyTimeAsLogTime;
    }

    private static DataFormat createFormat(String type) {
        if ("DelimitedText".equals(type)) {
            return new DelimitedTextFormat();
        } else if ("JSON".equals(type)) {
            return new JSONFormat();
        } else if ("Multiline".equals(type)) {
            return new MultilineFormat();
        } else if ("Parquet".equals(type)) {
            return new ParquetFormat();
        } else if ("Line".equals(type)) {
            return new LineFormat();
        } else {
            return null;
        }
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        bucket = jsonObject.getString("bucket");
        endpoint = jsonObject.getString("endpoint");
        roleARN = jsonObject.getString("roleARN");
        prefix = JsonUtils.readOptionalString(jsonObject, "prefix");
        pattern = JsonUtils.readOptionalString(jsonObject, "pattern");
        compressionCodec = JsonUtils.readOptionalString(jsonObject, "compressionCodec");
        encoding = JsonUtils.readOptionalString(jsonObject, "encoding");
        JSONObject formatObject = jsonObject.getJSONObject("format");
        restoreObjectEnabled = JsonUtils.readBool(jsonObject, "restoreObjectEnabled", false);
        lastModifyTimeAsLogTime = JsonUtils.readBool(jsonObject, "lastModifyTimeAsLogTime", false);
        if (formatObject != null && !formatObject.isEmpty()) {
            String type = JsonUtils.readOptionalString(formatObject, "type");
            if (type != null) {
                format = createFormat(type);
                if (format != null) {
                    format.deserialize(formatObject);
                }
            }
        }
    }
}
