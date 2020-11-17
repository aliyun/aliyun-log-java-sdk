package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;

import java.util.*;

public class ProjectConsumerGroupCheckPointResponse extends Response {
    private static final long serialVersionUID = -5446935677776563121L;
    private Map<String, List<ConsumerGroupShardCheckPoint>> checkPoints;

    public ProjectConsumerGroupCheckPointResponse(Map<String, String> headers, JSONObject checkPointsMap) {
        super(headers);
        checkPoints = new HashMap<String, List<ConsumerGroupShardCheckPoint>>();

        for (String logStore : checkPointsMap.keySet()) {
            List<ConsumerGroupShardCheckPoint> cpList = new ArrayList<ConsumerGroupShardCheckPoint>();
            JSONArray cpJsonArray = checkPointsMap.getJSONArray(logStore);

            for (int i = 0; i < cpJsonArray.size(); ++i) {
                ConsumerGroupShardCheckPoint cp = new ConsumerGroupShardCheckPoint();
                cp.Deserialize(cpJsonArray.getJSONObject(i));
                cpList.add(cp);
            }
            checkPoints.put(logStore, cpList);
        }
    }

    public Map<String, List<ConsumerGroupShardCheckPoint>> getCheckPoints() {
        return checkPoints;
    }

    public void setCheckPoints(Map<String, List<ConsumerGroupShardCheckPoint>> checkPoints) {
        this.checkPoints = checkPoints;
    }
}
