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
	
	private int count = 0;
	private int total = 0;
	private ShipperTasksStatistic statistic = null;
	private List<ShipperTask> shipperTasks = null;

	public GetShipperTasksResponse(Map<String, String> headers, int count, int total, ShipperTasksStatistic statistic, List<ShipperTask> shipperTasks) {
		super(headers);
		this.count = count;
		this.total = total;
		this.statistic = statistic;
		this.shipperTasks = new ArrayList<ShipperTask>(shipperTasks);
	}
	
	public int GetCountTask()
	{
		return count;
	}
	
	public int GetTotalTask()
	{
		return total;
	}
	
	public int GetRunningTaskCount()
	{
		return statistic.GetRunningTaskCount();
	}
	public int GetSuccessTaskCount()
	{
		return statistic.GetSuccessTaskCount();
	}
	public int GetFailTaskCount()
	{
		return statistic.GetFailTaskCount();
	}
	public List<ShipperTask> GetShipperTasks()
	{
		return new ArrayList<ShipperTask>(shipperTasks);
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
		for (ShipperTask task : shipperTasks)
		{
			if (task.GetTaskStatus().equals(status))
			{
				res.add(task.GetTaskId());
			}
		}
		return res;
	}
}
