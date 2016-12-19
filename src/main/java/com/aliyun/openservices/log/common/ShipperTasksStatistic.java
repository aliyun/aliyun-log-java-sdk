package com.aliyun.openservices.log.common;

public class ShipperTasksStatistic {

	private int mRunningTaskCount;

	private int mSuccessTaskCount;

	private int mFailTaskCount;

	public ShipperTasksStatistic(int runningTaskCount, int successTaskCount,
			int failTaskCount) {
		mRunningTaskCount = runningTaskCount;
		mSuccessTaskCount = successTaskCount;
		mFailTaskCount = failTaskCount;
	}

	public int GetRunningTaskCount() {
		return mRunningTaskCount;
	}

	public int GetSuccessTaskCount() {
		return mSuccessTaskCount;
	}

	public int GetFailTaskCount() {
		return mFailTaskCount;
	}
	
	
}
