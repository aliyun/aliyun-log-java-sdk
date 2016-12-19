package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

import net.sf.json.JSONArray;

import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;

public class ConsumerGroupCheckPointResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8040754126341011292L;
	private ArrayList<ConsumerGroupShardCheckPoint> checkPoints;
	public ConsumerGroupCheckPointResponse(Map<String, String> headers, JSONArray checkPointsArray) {
		super(headers);
		checkPoints = new ArrayList<ConsumerGroupShardCheckPoint>();
		for(int i = 0; i < checkPointsArray.size(); ++i)
		{
			ConsumerGroupShardCheckPoint checkpoint = new ConsumerGroupShardCheckPoint();
			checkpoint.Deserialize(checkPointsArray.getJSONObject(i));
			checkPoints.add(checkpoint);
		}
	}
	/**
	 * @return
	 * 		shard checkpoint
	 */
	public ArrayList<ConsumerGroupShardCheckPoint> GetCheckPoints() {
		return checkPoints;
	}
	public void SetCheckPoints(ArrayList<ConsumerGroupShardCheckPoint> checkPoints) {
		this.checkPoints = checkPoints;
	}
	
}
