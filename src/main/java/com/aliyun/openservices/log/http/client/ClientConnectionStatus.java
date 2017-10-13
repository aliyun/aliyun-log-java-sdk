package com.aliyun.openservices.log.http.client;

public class ClientConnectionStatus {
	final String mIpAddress;
	long mCreateTimeNano;
	long mLastUsedTimeNano;
	long mSendDataSize;
	long mPullDataSize;
	boolean mIsValid;
	public ClientConnectionStatus(String ip)
	{
		mIpAddress = ip;
		mCreateTimeNano = System.nanoTime();
		mLastUsedTimeNano = 0;
		mSendDataSize = 0;
		mPullDataSize = 0;
		mIsValid = true;
	}
	public String GetIpAddress()
	{
		return mIpAddress;
	}
	public void AddSendDataSize(long data_size)
	{
		mSendDataSize += data_size;
	}
	public long GetSendDataSize()
	{
		return mSendDataSize;
	}
	public void AddPullDataSize(long data_size)
	{
		mPullDataSize += data_size;
	}
	public long GetPullDataSize()
	{
		return mPullDataSize;
	}
	public long GetCreateTime()
	{
		return mCreateTimeNano;
	}
	public boolean IsValidConnection()
	{
		return mIsValid && mIpAddress != null && mIpAddress.isEmpty() == false;
	}
	public void DisableConnection()
	{
		mIsValid = false;
	}
	public void UpdateLastUsedTime(long cur_time)
	{
		mLastUsedTimeNano = cur_time;
	}
	public long GetLastUsedTime()
	{
		return mLastUsedTimeNano;
	}
}
