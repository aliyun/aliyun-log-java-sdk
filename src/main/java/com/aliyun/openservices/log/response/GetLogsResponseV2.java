/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.QueryResult;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.util.GzipUtils;
import com.aliyun.openservices.log.util.LZ4Encoder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GetLogsResponseV2 extends Response {

    private static final long serialVersionUID = 2137561838992842919L;
    private final String rawQueryResult;

    private QueryResult result;

    public GetLogsResponseV2(Map<String, String> headers, String rawQueryResult) {
        super(headers);
        this.rawQueryResult = rawQueryResult;
    }

    public QueryResult getResult() throws LogException {
        if (result == null) {
            result = new QueryResult();
            result.deserializeFrom(rawQueryResult, GetRequestId());
        }
        return result;
    }

    public String getQueryResultAsString() {
        // for console
        return rawQueryResult;
    }

    public static GetLogsResponseV2 deserializeFrom(ResponseMessage response) throws LogException {
        byte[] rawData = response.GetRawBody();
        Map<String, String> headers = response.getHeaders();
        String compressType = headers.get(Consts.CONST_X_SLS_COMPRESSTYPE);
        String rawSizeStr = headers.get(Consts.CONST_X_SLS_BODYRAWSIZE);
        if (compressType != null && rawSizeStr != null) {
            int rawSize = Integer.parseInt(rawSizeStr);
            Consts.CompressType type = Consts.CompressType.fromString(compressType);
            switch (type) {
                case LZ4:
                    rawData = LZ4Encoder.decompressFromLhLz4Chunk(rawData, rawSize);
                    break;
                case GZIP:
                    try {
                        rawData = GzipUtils.uncompress(rawData);
                    } catch (Exception ex) {
                        throw new LogException("InvalidResponse", "Fail to uncompress GZIP data", response.getRequestId());
                    }
                    break;
                default:
                    break;
            }
        }
        try {
            String data = new String(rawData, Consts.UTF_8_ENCODING);
            return new GetLogsResponseV2(headers, data);
        } catch (UnsupportedEncodingException ex) {
            throw new LogException(ErrorCodes.ENCODING_EXCEPTION, ex.getMessage(), response.getRequestId());
        }
    }
}
