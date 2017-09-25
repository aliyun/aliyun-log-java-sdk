/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.LZ4Encoder;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.VarintUtil;


public class BatchGetLogResponse extends Response{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1682292008312952493L;
	protected List<LogGroupData> mLogGroups = new ArrayList<LogGroupData>();
	protected int mRawSize;
	
	/**
	 * @return the mRawSize
	 */
	public int GetRawSize() {
		return mRawSize;
	}

	/**
	 * parse LogGroupList using fast deserialize methold
	 * @param uncompressedData is LogGroupList bytes
	 * @throws LogException
	 */
	public void ParseFastLogGroupList(byte[] uncompressedData) throws LogException {
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
					mLogGroups.add(new LogGroupData(uncompressedData, value[2], value[1], GetRequestId()));
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
	 * default consutructor for unittest
	 * @param headers
	 */
	public BatchGetLogResponse(Map<String, String> headers) {
		super(headers);
	}

	/**
	 * Construct the response with http headers
	 * @param headers http headers
	 * @param rawData the response byte array data
	 * @throws LogException if any error occurs in generating compressed log data
	 */
	public BatchGetLogResponse(Map<String, String> headers, byte[] rawData) throws LogException {
		super(headers);
		try {
			mRawSize = Integer.parseInt(headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
			if (mRawSize > 0) {
				byte[] uncompressedData = LZ4Encoder.decompressFromLhLz4Chunk(rawData, mRawSize);
				ParseFastLogGroupList(uncompressedData);
			}
		} catch (NumberFormatException e) {
			throw new LogException("ParseLogGroupListRawSizeError", e.getMessage(), e, GetRequestId());
		}

		if (mLogGroups.size() != GetCount()) {
			throw new LogException("LogGroupCountNotMatch", "Loggroup count does match with the count in header message",
					GetRequestId());
		}
	}
	
	public String GetNextCursor() {
		return GetHeader(Consts.CONST_X_SLS_CURSOR);
	}
	
	public int GetCount() {
		return Integer.parseInt(GetHeader(Consts.CONST_X_SLS_COUNT));
	}
	
	/**
	 * get one uncompressed log group by index
	 * @param index the index of log group array
	 * @throws LogException if any error occurs in getting uncompressed log group
	 * @return one uncompressed log group
	 */
	public LogGroupData GetLogGroup(int index) throws LogException {
		if (GetCount() <= 0) {
			throw new LogException("GetLogGroupError", "No LogGroups in response", GetRequestId());
		}
		
		if (index >= 0 && index < GetCount()) {
			return mLogGroups.get(index);
		} else {
			throw new LogException("GetLogGroupError", "Invalid index", GetRequestId());
		}
	}

	/**
	 * get uncompressed log groups with offset
	 * @param offset the offset to get log groups, starts with 0
	 * @throws LogException if any error occurs in getting uncompressed log groups with offset
	 * @return uncompressed log groups
	 */
	public List<LogGroupData> GetLogGroups(int offset) throws LogException {
		if (GetCount() <= 0) {
			throw new LogException("GetLogGroupError", "No LogGroups in response", GetRequestId());
		}
		
		if (offset < 0 || offset >= GetCount()) {
			throw new LogException("GetLogGroupError", "Invalid offset", GetRequestId());
		}
		
		return mLogGroups.subList(offset, GetCount());
	}
	
	/**
	 * get all uncompressed log groups
	 * @throws LogException if any error occurs in getting uncompressed log groups
	 * @return all uncompressed log groups
	 */
	public List<LogGroupData> GetLogGroups() throws LogException {
		return mLogGroups;
	}
}
