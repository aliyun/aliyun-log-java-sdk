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
    private final List<LogGroup> mLogGroups = new ArrayList<LogGroup>();
    private String mLogStore;
    private String mHashKey;
    private CompressType mCompressType = CompressType.LZ4;

    public BatchPutLogsRequest(String project, String logStore, List<LogGroup> logGroups, String hashKey) {
        super(project);
        mLogStore = logStore;
        mHashKey = hashKey;
        mLogGroups.addAll(logGroups);
    }

    public String getLogStore() {
        return mLogStore;
    }

    public void setLogStore(String logStore) {
        mLogStore = logStore;
    }

    public String getHashKey() {
        return mHashKey;
    }

    public void setHashKey(String hashKey) {
        mHashKey = hashKey;
    }

    public List<LogGroup> getLogGroups() {
        return mLogGroups;
    }

    public void setLogGroups(List<LogGroup> logGroups) {
        mLogGroups.clear();
        mLogGroups.addAll(logGroups);
    }

    public CompressType getCompressType() {
        return mCompressType;
    }

    public void setCompressType(CompressType compressType) {
        mCompressType = compressType;
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
        for (LogGroup logGroup : mLogGroups) {
            byte[] logGroupBytes = logGroup.serializeToPb();
            Logs.SlsLogPackage.Builder packageBuilder = builder.addPackagesBuilder();
            packageBuilder.setUncompressSize(logGroupBytes.length);
            packageBuilder.setCompressType(compressType);
            packageBuilder.setData(ByteString.copyFrom(Utils.compressLogBytes(logGroupBytes, getCompressType())));
        }
        return builder.build().toByteArray();
    }
}
