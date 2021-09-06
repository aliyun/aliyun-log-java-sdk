package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class OssShipperConfig implements ShipperConfig {

	private String ossBucket;
	private String ossPrefix;
	private String roleArn;
	private int bufferInterval;
	private int bufferMB;
	private String compressType;
	private String pathFormat;
	private String timeZone;
	private OssShipperStorageDetail storageDetail;

	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMB) {
		this(ossBucket, ossPrefix, roleArn, bufferInterval, bufferMB, "snappy");
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
	 * @param bufferMB
	 *            the data size(MB) to buffer before save to oss
	 * @param compressType
	 *            the compress type, only support 'snappy' or 'none'
	 */
	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMB, String compressType) {
		this(ossBucket, ossPrefix, roleArn, bufferInterval, bufferMB, compressType, "%Y/%m/%d/%H/%M", "json", "");
	}
	
	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMB, String compressType, String pathFormat) {
		this(ossBucket, ossPrefix, roleArn, bufferInterval, bufferMB, compressType, pathFormat, "json", "");
	}
	

	public OssShipperConfig(String ossBucket, String ossPrefix, String roleArn,
			int bufferInterval, int bufferMB, String compressType, String pathFormat, String storageFormat, String timezone) {
		this.ossBucket = ossBucket;
		this.ossPrefix = ossPrefix;
		this.roleArn = roleArn;
		this.bufferInterval = bufferInterval;
		this.bufferMB = bufferMB;
		this.compressType = compressType;
		this.pathFormat = pathFormat;
		if (storageFormat.equals("parquet")) {
			this.storageDetail = new OssShipperParquetStorageDetail();
		} else if (storageFormat.equals("csv")) {
			this.storageDetail = new OssShipperCsvStorageDetail();
		} else {
			this.storageDetail = new OssShipperJsonStorageDetail();
		}
		this.timeZone = timezone;
	}
	
	public OssShipperConfig() {

	}

	public void FromJsonObj(JSONObject obj) throws LogException {
		try {
			this.ossBucket = obj.getString("ossBucket");
			this.ossPrefix = obj.getString("ossPrefix");
			this.roleArn = obj.getString("roleArn");
			this.bufferInterval = obj.getIntValue("bufferInterval");
			this.bufferMB = obj.getIntValue("bufferSize");
			this.compressType = obj.getString("compressType");
			this.pathFormat = obj.getString("pathFormat");
			if (obj.containsKey("timeZone")) {
				this.timeZone = obj.getString("timeZone");
			} else {
				this.timeZone = "";
			}
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
		return pathFormat;
	}
	
	public String GetOssBucket() {
		return ossBucket;
	}

	public String GetOssPrefix() {
		return ossPrefix;
	}

	public String GetRoleArm() {
		return roleArn;
	}

	public int GetBufferInterval() {
		return bufferInterval;
	}

	public int GetBufferMB() {
		return bufferMB;
	}

	public String GetCompressType() {
		return compressType;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setOssBucket(String ossBucket) {
		this.ossBucket = ossBucket;
	}

	public void setOssPrefix(String ossPrefix) {
		this.ossPrefix = ossPrefix;
	}

	public void setRoleArn(String roleArn) {
		this.roleArn = roleArn;
	}

	public void setBufferInterval(int bufferInterval) {
		this.bufferInterval = bufferInterval;
	}

	public void setBufferMB(int bufferMB) {
		this.bufferMB = bufferMB;
	}

	public void setCompressType(String compressType) {
		this.compressType = compressType;
	}

	public void setPathFormat(String pathFormat) {
		this.pathFormat = pathFormat;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public void setStorageDetail(OssShipperStorageDetail storageDetail) {
		this.storageDetail = storageDetail;
	}

	@Override
	public String GetShipperType() {
		return "oss";
	}

	public JSONObject GetJsonObj() {
		JSONObject obj = storageDetail.ToJsonObject();
		obj.put("ossBucket", this.ossBucket);
		obj.put("ossPrefix", this.ossPrefix);
		obj.put("roleArn", this.roleArn);
		obj.put("bufferInterval", this.bufferInterval);
		obj.put("bufferSize", this.bufferMB);
		obj.put("compressType", this.compressType);
		obj.put("pathFormat", this.pathFormat);
		obj.put("timeZone", this.timeZone);
		
		return obj;
	}

}
