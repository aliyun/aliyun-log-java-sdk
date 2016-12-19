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
import com.aliyun.openservices.log.common.Logs.LogGroup;
import com.aliyun.openservices.log.common.Logs.LogGroupList;
import com.aliyun.openservices.log.exception.LogException;
import com.google.protobuf.InvalidProtocolBufferException;


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
				LogGroupList logGroupList = LogGroupList.parseFrom(uncompressedData);
				for (LogGroup logGroup:logGroupList.getLogGroupListList()) {
					mLogGroups.add(new LogGroupData(logGroup));
				}
			}
		} catch (InvalidProtocolBufferException e) {
			throw new LogException("InitLogGroupsError", e.getMessage(), e, GetRequestId());
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
