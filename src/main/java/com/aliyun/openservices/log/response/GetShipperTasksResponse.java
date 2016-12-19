package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.ShipperTask;
import com.aliyun.openservices.log.common.ShipperTasksStatistic;

public class GetShipperTasksResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4081210336385900737L;
	
	private int mCount = 0;
	private int mTotal = 0;
	private ShipperTasksStatistic mStatistic = null;
	private List<ShipperTask> mShipperTasks = null;

	public GetShipperTasksResponse(Map<String, String> headers, int count, int total, ShipperTasksStatistic statistic, List<ShipperTask> shipperTasks) {
		super(headers);
		mCount = count;
		mTotal = total;
		mStatistic = statistic;
		mShipperTasks = new ArrayList<ShipperTask>(shipperTasks);
	}
	
	public int GetCountTask()
	{
		return mCount;
	}
	
	public int GetTotalTask()
	{
		return mTotal;
	}
	
	public int GetRunningTaskCount()
	{
		return mStatistic.GetRunningTaskCount();
	}
	public int GetSuccessTaskCount()
	{
		return mStatistic.GetSuccessTaskCount();
	}
	public int GetFailTaskCount()
	{
		return mStatistic.GetFailTaskCount();
	}
	public List<ShipperTask> GetShipperTasks()
	{
		return new ArrayList<ShipperTask>(mShipperTasks);
	}
	
	public List<String> GetSuccessTaskIds()
	{
		return GetTaskIds("success");
	}
	
	public List<String> GetFailTaskIds()
	{
		return GetTaskIds("fail");
	}
	
	public List<String> GetRunningTaskIds()
	{
		return GetTaskIds("running");
	}
	
	private List<String> GetTaskIds(String status)
	{
		List<String> res = new ArrayList<String>();
		for (ShipperTask task : mShipperTasks)
		{
			if (task.GetTaskStatus().equals(status))
			{
				res.add(task.GetTaskId());
			}
		}
		return res;
	}
}
