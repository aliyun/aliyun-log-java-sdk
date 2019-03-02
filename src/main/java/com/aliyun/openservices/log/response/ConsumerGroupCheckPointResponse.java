package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;

public class ConsumerGroupCheckPointResponse extends Response {
    /**
     *
     */
    private static final long serialVersionUID = 8040754126341011292L;
    private List<ConsumerGroupShardCheckPoint> checkPoints;

    public ConsumerGroupCheckPointResponse(Map<String, String> headers, JSONArray checkPointsArray) {
        super(headers);
        checkPoints = new ArrayList<ConsumerGroupShardCheckPoint>();
        for (int i = 0; i < checkPointsArray.size(); ++i) {
            ConsumerGroupShardCheckPoint checkpoint = new ConsumerGroupShardCheckPoint();
            checkpoint.Deserialize(checkPointsArray.getJSONObject(i));
            checkPoints.add(checkpoint);
        }
    }

    /**
     * @return shard checkpoint
     * @deprecated Use {@link #getCheckPoints()} instead.
     */
    @Deprecated
    public ArrayList<ConsumerGroupShardCheckPoint> GetCheckPoints() {
        return new ArrayList<ConsumerGroupShardCheckPoint>(checkPoints);
    }

    /**
     * @deprecated Use {@link #setCheckPoints(List)} instead.
     */
    @Deprecated
    public void SetCheckPoints(ArrayList<ConsumerGroupShardCheckPoint> checkPoints) {
        this.checkPoints = checkPoints;
    }

    public List<ConsumerGroupShardCheckPoint> getCheckPoints() {
        return checkPoints;
    }

    public void setCheckPoints(List<ConsumerGroupShardCheckPoint> checkPoints) {
        this.checkPoints = checkPoints;
    }
}
