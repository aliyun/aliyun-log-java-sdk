package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.LZ4Encoder;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.VarintUtil;
import com.aliyun.openservices.log.util.ZSTDEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PullLogsResponse extends Response {

    private static final long serialVersionUID = -2027711570684362279L;
    private List<LogGroupData> logGroups;
    private int rawSize;
    private int count;
    private long rawDataSize = -1;
    private int rawDataCount = -1;
    private final byte[] rawData;
    private final String compressType;

    /**
     * Construct the response with http headers
     *
     * @param headers http headers
     * @param rawData the response byte array data
     * @throws LogException if any error occurs in generating compressed log data
     */
    public PullLogsResponse(Map<String, String> headers, byte[] rawData) throws LogException {
        super(headers);
        this.rawData = rawData;
        try {
            rawSize = Integer.parseInt(headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
            count = Integer.parseInt(GetHeader(Consts.CONST_X_SLS_COUNT));
            if (headers.containsKey(Consts.CONST_X_SLS_RAWDATASIZE)) {
                rawDataSize = Long.parseLong(headers.get(Consts.CONST_X_SLS_RAWDATASIZE));
            }
            if (headers.containsKey(Consts.CONST_X_SLS_RAWDATACOUNT)) {
                rawDataCount = Integer.parseInt(headers.get(Consts.CONST_X_SLS_RAWDATACOUNT));
            }
            compressType = headers.get(Consts.CONST_X_SLS_COMPRESSTYPE);
        } catch (NumberFormatException e) {
            throw new LogException("ParseLogGroupListRawSizeError", e.getMessage(), e, GetRequestId());
        }
    }

    /**
     * @return The raw size of data responded.
     */
    public int getRawSize() {
        return rawSize;
    }

    public long getRawDataSize() {
        return rawDataSize;
    }

    public int getRawDataCount() {
        return rawDataCount;
    }

    private void parseLogGroupsIfNeeded() throws LogException {
        if (logGroups != null) {
            return;
        }
        logGroups = new ArrayList<LogGroupData>();
        if (rawSize > 0) {
            Consts.CompressType type = Consts.CompressType.fromString(compressType);
            byte[] uncompressedData;
            switch (type) {
                case LZ4:
                    uncompressedData = LZ4Encoder.decompressFromLhLz4Chunk(rawData, rawSize);
                    parseFastLogGroupList(uncompressedData);
                    break;
                case ZSTD:
                    uncompressedData = ZSTDEncoder.decompress(rawData, rawSize);
                    parseFastLogGroupList(uncompressedData);
                    break;
                default:
                    throw new LogException("DecompressException", "The compress type is invalid: " + type, GetRequestId());
            }
        }
        if (logGroups.size() != count) {
            throw new LogException("LogGroupCountNotMatch",
                    "LogGroup count does match with the count in header message", GetRequestId());
        }
    }

    /**
     * Parse LogGroupList using fast deserialize method.
     *
     * @param uncompressedData is LogGroupList bytes
     * @throws LogException if parse fails
     */
    private void parseFastLogGroupList(byte[] uncompressedData) throws LogException {
        int pos = 0;
        int rawSize = uncompressedData.length;
        int mode, index;
        while (pos < rawSize) {
            int[] value = VarintUtil.DecodeVarInt32(uncompressedData, pos, rawSize);
            if (value[0] == 0) {
                throw new LogException("InitLogGroupsError", "decode varint32 error", GetRequestId());
            }
            pos = value[2];
            mode = value[1] & 0x7;
            index = value[1] >> 3;
            if (mode == 0) {
                value = VarintUtil.DecodeVarInt32(uncompressedData, pos, rawSize);
                if (value[0] == 0) {
                    throw new LogException("InitLogGroupsError", "decode varint32 error", GetRequestId());
                }
                pos = value[2];
            } else if (mode == 1) {
                pos += 8;
            } else if (mode == 2) {
                value = VarintUtil.DecodeVarInt32(uncompressedData, pos, rawSize);
                if (value[0] == 0) {
                    throw new LogException("InitLogGroupsError", "decode varint32 error", GetRequestId());
                }
                if (index == 1) {
                    logGroups.add(new LogGroupData(uncompressedData, value[2], value[1], GetRequestId()));
                }
                pos = value[1] + value[2];
            } else if (mode == 5) {
                pos += 4;
            } else {
                throw new LogException("InitLogGroupsError", "mode: " + mode, GetRequestId());
            }
        }
        if (pos != rawSize) {
            throw new LogException("InitLogGroupsError", "parse LogGroupList fail", GetRequestId());
        }
    }

    /**
     * @return The next cursor
     */
    public String getNextCursor() {
        return GetHeader(Consts.CONST_X_SLS_CURSOR);
    }

    /**
     * @return The count of LogGroup in response.
     */
    public int getCount() {
        return count;
    }

    /**
     * get one uncompressed log group by index
     *
     * @param index the index of log group array
     * @return one uncompressed log group
     */
    public LogGroupData getLogGroup(int index) throws LogException {
        Args.check(count > 0, "No LogGroups in response");
        Args.check(index >= 0 && index < count, "index out of range [0, " + count + ")");
        parseLogGroupsIfNeeded();
        return logGroups.get(index);
    }

    /**
     * get uncompressed log groups with offset
     *
     * @param offset the offset to get log groups, starts with 0
     * @return uncompressed log groups
     */
    public List<LogGroupData> getLogGroups(int offset) throws LogException {
        Args.check(count > 0, "No LogGroups in response");
        Args.check(offset >= 0 && offset < count, "offset out of range [0, " + count + ")");
        parseLogGroupsIfNeeded();
        return logGroups.subList(offset, count);
    }

    /**
     * get all uncompressed log groups
     *
     * @return all uncompressed log groups
     */
    public List<LogGroupData> getLogGroups() throws LogException {
        parseLogGroupsIfNeeded();
        return logGroups;
    }

    public byte[] getRawData() {
        return rawData;
    }

}
