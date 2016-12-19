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
	 * @param bufferInteval
	 *            the time(seconds) to buffer before save to oss
	 * @param bufferMb
	 *            the data size(MB) to buffer before save to oss
	 * @param compressType
	 *            the compress type, only support 'snappy' or 'none'
	 */
	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInteval, int bufferMb, String compressType) {
		mOssBucket = ossBucket;
		mOssPrefix = ossPrefix;
		mRoleArn = roleArn;
		mBufferInterval = bufferInteval;
		mBufferMb = bufferMb;
		mCompressType = compressType;
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
		} catch (JSONException e) {
			throw new LogException("FailToParseOssShipperConfig",
					e.getMessage(), e, "");
		}
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
		JSONObject obj = new JSONObject();
		obj.put("ossBucket", this.mOssBucket);
		obj.put("ossPrefix", this.mOssPrefix);
		obj.put("roleArn", this.mRoleArn);
		obj.put("bufferInterval", this.mBufferInterval);
		obj.put("bufferSize", this.mBufferMb);
		obj.put("compressType", this.mCompressType);
		return obj;
	}
}
