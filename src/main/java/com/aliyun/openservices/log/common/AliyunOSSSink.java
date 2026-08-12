package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.annotation.InternalApi;

public class AliyunOSSSink extends DataSink {

    private String roleArn;
    private String bucket;
    private String prefix;
    private String suffix;
    private String pathFormat;
    private String pathFormatType;  // default is "time"
    private int bufferSize;
    private int bufferInterval;
    private String timeZone;        // +(-)xxxx style, otherwise +8000
    private String contentType;
    private String compressionType;
    private ExportContentDetail contentDetail;
    private String endpoint;

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; };

    private int delaySeconds;

    public AliyunOSSSink() { super(DataSinkType.ALIYUN_OSS); }

    public AliyunOSSSink(String roleArn, String bucket, String prefix,
                         String suffix, String pathFormat, String pathFormatType, int bufferSize, int bufferInterval,
                         String timeZone, String contentType, String compressionType, ExportContentDetail contentDetail) {
        super(DataSinkType.ALIYUN_OSS);
        this.roleArn = roleArn;
        this.bucket = bucket;
        this.prefix = prefix;
        this.suffix = suffix;
        this.pathFormat = pathFormat;
        this.pathFormatType = pathFormatType;
        this.bufferSize = bufferSize;
        this.bufferInterval = bufferInterval;
        this.timeZone = timeZone;
        this.contentType = contentType;
        this.compressionType = compressionType;
        this.contentDetail = contentDetail;
    }


    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPathFormat() {
        return pathFormat;
    }

    public void setPathFormat(String pathFormat) {
        this.pathFormat = pathFormat;
    }

    public String getPathFormatType() {
        return pathFormatType;
    }

    public void setPathFormatType(String pathFormatType) {
        this.pathFormatType = pathFormatType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBufferInterval() {
        return bufferInterval;
    }

    public void setBufferInterval(int bufferInterval) {
        this.bufferInterval = bufferInterval;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public ExportContentDetail getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(ExportContentDetail contentDetail) {
        this.contentDetail = contentDetail;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        value.put("type", getType().toString());
        put(value, "roleArn", roleArn);
        put(value, "bucket", bucket);
        put(value, "prefix", prefix);
        put(value, "suffix", suffix);
        put(value, "pathFormat", pathFormat);
        put(value, "pathFormatType", pathFormatType);
        value.put("bufferSize", bufferSize);
        value.put("bufferInterval", bufferInterval);
        value.put("delaySeconds", delaySeconds);
        put(value, "endpoint", endpoint);
        put(value, "timeZone", timeZone);
        put(value, "contentType", contentType);
        put(value, "compressionType", compressionType);
        if (contentDetail != null) {
            value.put("contentDetail", contentDetail.toJsonObject());
        }
        return value;
    }

    private static void put(JSONObject value, String key, String item) {
        if (item != null) {
            value.put(key, item);
        }
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        roleArn = value.getString("roleArn");
        bucket = value.getString("bucket");
        prefix = value.getString("prefix");
        suffix = value.getString("suffix");
        pathFormat = value.getString("pathFormat");
        pathFormatType = JsonUtils.readOptionalString(value, "pathFormatType", "time");
        bufferSize = value.getIntValue("bufferSize");
        bufferInterval = value.getIntValue("bufferInterval");
        delaySeconds = value.getIntValue("delaySeconds");
        endpoint = value.getString("endpoint");
        timeZone = value.getString("timeZone");
        compressionType = value.getString("compressionType");
        contentType = value.getString("contentType");
        JSONObject obj = value.getJSONObject("contentDetail");
        if ("csv".equals(contentType)) {
            contentDetail = new ExportContentCsvDetail();
        } else if ("parquet".equals(contentType) || "orc".equals(contentType)) {
            contentDetail = new ExportContentColumnStorageDetail();
        } else if ("json".equals(contentType)) {
            contentDetail = new ExportContentJsonDetail();
        } else {
            throw new RuntimeException("ContentType should be json/csv/parquet/orc");
        }
        contentDetail.fromJsonObject(obj);
    }
}
