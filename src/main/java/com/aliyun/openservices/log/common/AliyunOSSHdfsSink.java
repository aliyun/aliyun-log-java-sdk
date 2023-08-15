package com.aliyun.openservices.log.common;

public class AliyunOSSHdfsSink extends AliyunOSSSink {
    public AliyunOSSHdfsSink() {
        setType(DataSinkType.ALIYUN_OSSHDFS);
    }

    public AliyunOSSHdfsSink(String roleArn, String bucket, String prefix,
                             String suffix, String pathFormat, String pathFormatType, int bufferSize, int bufferInterval,
                             String timeZone, String contentType, String compressionType, ExportContentDetail contentDetail) {
        super(roleArn, bucket, prefix, suffix,
                pathFormat, pathFormatType, bufferSize, bufferInterval,
                timeZone, contentType, compressionType, contentDetail
        );
        setType(DataSinkType.ALIYUN_OSSHDFS);
    }
}
