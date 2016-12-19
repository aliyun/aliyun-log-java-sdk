package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.common.Logs.Log;
import com.aliyun.openservices.log.common.Logs.LogGroup;
import com.aliyun.openservices.log.common.Logs.Log.Content;
import com.aliyun.openservices.log.exception.LogException;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * The compressed log group data
 * @author log-service-dev
 *
 */
public class CompressedLogGroupData implements Serializable {

	private static final long serialVersionUID = 5482386722654903391L;
	protected int mUncompressedSize;
	protected byte[] mCompressedData = null;
	
	public CompressedLogGroupData() {
		mUncompressedSize = 0;
		mCompressedData = null;
	}
	
	public CompressedLogGroupData(int uncompressedSize, byte[] compressedData) {
		mUncompressedSize = uncompressedSize;
		mCompressedData = compressedData.clone();
	}
	
	public CompressedLogGroupData(CompressedLogGroupData compressedLogDataInfo) {
		mUncompressedSize = compressedLogDataInfo.GetUncompressedSize();
		mCompressedData = compressedLogDataInfo.GetCompressedData().clone();
	}

	public int GetUncompressedSize() {
		return mUncompressedSize;
	}

	public void SetUncompressedSize(int uncompressedSize) {
		this.mUncompressedSize = uncompressedSize;
	}

	public byte[] GetCompressedData() {
		return mCompressedData;
	}

	public void SetCompressedData(byte[] compressedData) {
		this.mCompressedData = compressedData;
	}
	
	public LogGroupData DecompressLogGroup() throws LogException {
		/*
		Inflater decompresser = new Inflater();
		decompresser.setInput(mCompressedData, 0, mCompressedData.length);
		byte[] result = new byte[mUncompressedSize];
		try {
			decompresser.inflate(result);
		} catch (DataFormatException e) {
			throw new LogException("DecompressError", e.getMessage(), e, "");
		} finally {
			decompresser.end();
		}
		*/
		byte[] result = LZ4Encoder.decompressFromLhLz4Chunk(mCompressedData, mUncompressedSize);
		
		LogGroupData data = new LogGroupData();
		
		try {
			LogGroup logGroup = LogGroup.parseFrom(result);
			if (logGroup.hasTopic()) {
				data.SetTopic(logGroup.getTopic());
			}
			if (logGroup.hasSource()) {
				data.SetSource(logGroup.getSource());
			}
			List<Log> logs = logGroup.getLogsList();
			ArrayList<LogItem> logItems = new ArrayList<LogItem>();
			for (Log log:logs) {
				ArrayList<LogContent> logContents = new ArrayList<LogContent>();
				for(Content content:log.getContentsList()) {
					logContents.add(new LogContent(content.getKey(), content.getValue()));
				}
				logItems.add(new LogItem(log.getTime(), logContents));
			}
			
			data.SetAllLogs(logItems);
		} catch (InvalidProtocolBufferException e) {
			throw new LogException("InitLogGroupsError", e.getMessage(), e, "");
		}
		
		return data;
	}
}
