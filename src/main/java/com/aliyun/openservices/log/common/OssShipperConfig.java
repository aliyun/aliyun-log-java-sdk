package com.aliyun.openservices.log.common;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class OssShipperConfig implements ShipperConfig {

	private String mOssBucket;
	private String mOssPrefix;
	private String mRoleArn;
	private int mBufferInterval;
	private int mBufferMb;
	private String mCompressType;
	private String mPathFormat;
	private OssShipperStorageDetail storageDetail;

	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInteval, int bufferMb) {
		this(ossBucket, ossPrefix, roleArn, bufferInteval, bufferMb, "snappy");
	}

	/**
	 * create a oss shipper config
	 * 
	 * @param ossBucket
	 *            oss bucket name
	 * @param ossPrefix
	 *            the prefix path in oss where to save the log data
	 * @param roleArn
	 *            the ram arn used to get the temporary write permission to the
	 *            oss bucket
	 * @param bufferInterval
	 *            the time(seconds) to buffer before save to oss
	 * @param bufferMb
	 *            the data size(MB) to buffer before save to oss
	 * @param compressType
	 *            the compress type, only support 'snappy' or 'none'
	 */
	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMb, String compressType) {
		this(ossBucket, ossPrefix, roleArn, bufferInterval, bufferMb, compressType, "%Y/%m/%d/%H/%M", "json");
	}
	
	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMb, String compressType, String pathFormat) {
		this(ossBucket, ossPrefix, roleArn, bufferInterval, bufferMb, compressType, pathFormat, "json");
	}
	

	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInteval, int bufferMb, String compressType, String pathFormat, String storageFormat) {
		mOssBucket = ossBucket;
		mOssPrefix = ossPrefix;
		mRoleArn = roleArn;
		mBufferInterval = bufferInteval;
		mBufferMb = bufferMb;
		mCompressType = compressType;
		mPathFormat = pathFormat;
		if (storageFormat.equals("parquet")) {
			storageDetail = new OssShipperParquetStorageDetail();
		} else if (storageFormat.equals("csv")) {
			storageDetail = new OssShipperCsvStorageDetail();
		} else {
			storageDetail = new OssShipperJsonStorageDetail();
		}
	}
	
	public OssShipperConfig() {

	}

	public void FromJsonObj(JSONObject obj) throws LogException {
		try {
			this.mOssBucket = obj.getString("ossBucket");
			this.mOssPrefix = obj.getString("ossPrefix");
			this.mRoleArn = obj.getString("roleArn");
			this.mBufferInterval = obj.getInt("bufferInterval");
			this.mBufferMb = obj.getInt("bufferSize");
			this.mCompressType = obj.getString("compressType");
			this.mPathFormat = obj.getString("pathFormat");
			JSONObject storage = obj.getJSONObject("storage");
			String storageFormat = storage.getString("format");
			
			if (storageFormat.equals("parquet")) {
				storageDetail = new OssShipperParquetStorageDetail();
			} else if (storageFormat.equals("csv")) {
				storageDetail = new OssShipperCsvStorageDetail();
			} else {
				storageDetail = new OssShipperJsonStorageDetail();
			}
			
			storageDetail.FromJsonObject(obj);
			
		} catch (JSONException e) {
			throw new LogException("FailToParseOssShipperConfig",
					e.getMessage(), e, "");
		}
	}

	public OssShipperStorageDetail GetStorageDetail() {
		return storageDetail;
	}
	
	public String GetPathFormat() {
		return mPathFormat;
	}
	
	public String GetOssBucket() {
		return mOssBucket;
	}

	public String GetOssPrefix() {
		return mOssPrefix;
	}

	public String GetRoleArm() {
		return mRoleArn;
	}

	public int GetBufferInterval() {
		return mBufferInterval;
	}

	public int GetBufferMb() {
		return mBufferMb;
	}

	public String GetCompressType() {
		return mCompressType;
	}
	
	@Override
	public String GetShipperType() {
		return "oss";
	}

	public JSONObject GetJsonObj() {
		JSONObject obj = storageDetail.ToJsonObject();
		obj.put("ossBucket", this.mOssBucket);
		obj.put("ossPrefix", this.mOssPrefix);
		obj.put("roleArn", this.mRoleArn);
		obj.put("bufferInterval", this.mBufferInterval);
		obj.put("bufferSize", this.mBufferMb);
		obj.put("compressType", this.mCompressType);
		obj.put("pathFormat", this.mPathFormat);
		
		return obj;
	}
}
