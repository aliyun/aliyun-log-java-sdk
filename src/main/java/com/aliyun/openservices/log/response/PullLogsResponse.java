package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LZ4Encoder;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.VarintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PullLogsResponse extends Response {

    private static final long serialVersionUID = -2027711570684362279L;
    private List<LogGroupData> logGroups;
    private int rawSize;
    private int count;

    public PullLogsResponse(Map<String, String> headers) {
        super(headers);
        logGroups = new ArrayList<LogGroupData>();
    }

    /**
     * Construct the response with http headers
     *
     * @param headers http headers
     * @param rawData the response byte array data
     * @throws LogException if any error occurs in generating compressed log data
     */
    public PullLogsResponse(Map<String, String> headers, byte[] rawData) throws LogException {
        this(headers);
        try {
            rawSize = Integer.parseInt(headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
            if (rawSize > 0) {
                byte[] uncompressedData = LZ4Encoder.decompressFromLhLz4Chunk(rawData, rawSize);
                parseFastLogGroupList(uncompressedData);
            }
            count = Integer.parseInt(GetHeader(Consts.CONST_X_SLS_COUNT));
        } catch (NumberFormatException e) {
            throw new LogException("ParseLogGroupListRawSizeError", e.getMessage(), e, GetRequestId());
        }
        if (logGroups.size() != count) {
            throw new LogException("LogGroupCountNotMatch", "LogGroup count does match with the count in header message", GetRequestId());
        }
    }

    /**
     * @return The raw size of data responded.
     */
    public int getRawSize() {
        return rawSize;
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
    public LogGroupData getLogGroup(int index) {
        Args.check(count > 0, "No LogGroups in response");
        Args.check(index >= 0 && index < count, "index out of range [0, " + count + ")");
        return logGroups.get(index);
    }

    /**
     * get uncompressed log groups with offset
     *
     * @param offset the offset to get log groups, starts with 0
     * @return uncompressed log groups
     */
    public List<LogGroupData> getLogGroups(int offset) {
        Args.check(count > 0, "No LogGroups in response");
        Args.check(offset >= 0 && offset < count, "offset out of range [0, " + count + ")");
        return logGroups.subList(offset, count);
    }

    /**
     * get all uncompressed log groups
     *
     * @return all uncompressed log groups
     */
    public List<LogGroupData> getLogGroups() {
        return logGroups;
    }
}
