package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.LogGroup;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.Logs.SlsCompressType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Utils;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;

public class BatchPutLogsRequest extends Request {
    private static final long serialVersionUID = 3221252831212912821L;
    private final List<LogGroup> logGroups = new ArrayList<LogGroup>();
    private String logStore;
    private String hashKey;
    private CompressType compressType = CompressType.LZ4;

    public BatchPutLogsRequest(String project, String logStore, List<LogGroup> logGroups, String hashKey) {
        super(project);
        this.logStore = logStore;
        this.hashKey = hashKey;
        this.logGroups.addAll(logGroups);
    }

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public List<LogGroup> getLogGroups() {
        return logGroups;
    }

    public void setLogGroups(List<LogGroup> logGroups) {
        this.logGroups.clear();
        this.logGroups.addAll(logGroups);
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public byte[] serializeToPb() throws LogException {
        SlsCompressType compressType = SlsCompressType.SLS_CMP_NONE;
        switch (getCompressType()) {
            case LZ4:
                compressType = SlsCompressType.SLS_CMP_LZ4;
                break;
            case ZSTD:
                compressType = SlsCompressType.SLS_CMP_ZSTD;
                break;
            case GZIP:
                compressType = SlsCompressType.SLS_CMP_DEFLATE;
                break;
            default:
                break;
        }

        Logs.SlsLogPackageList.Builder builder = Logs.SlsLogPackageList.newBuilder();
        for (LogGroup logGroup : logGroups) {
            byte[] logGroupBytes = logGroup.serializeToPb();
            Logs.SlsLogPackage.Builder packageBuilder = builder.addPackagesBuilder();
            packageBuilder.setUncompressSize(logGroupBytes.length);
            packageBuilder.setCompressType(compressType);
            packageBuilder.setData(ByteString.copyFrom(Utils.compressLogBytes(logGroupBytes, getCompressType())));
        }
        return builder.build().toByteArray();
    }
}
