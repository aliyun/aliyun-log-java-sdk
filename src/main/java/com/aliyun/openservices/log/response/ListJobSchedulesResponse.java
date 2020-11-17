package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListJobSchedulesResponse extends ResponseList<JobSchedule> implements Serializable {

    private static final long serialVersionUID = 2584488640627126067L;

    public ListJobSchedulesResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<JobSchedule> unmarshaller() {
        return new Unmarshaller<JobSchedule>() {
            @Override
            public JobSchedule unmarshal(JSONArray value, int index) {
                JobSchedule model = new JobSchedule();
                model.deserialize(value.getJSONObject(index));
                return model;
            }
        };
    }
}
