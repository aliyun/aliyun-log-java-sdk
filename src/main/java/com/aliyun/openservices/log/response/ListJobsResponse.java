package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONArray;

import java.util.Map;

public class ListJobsResponse extends ResponseList<Job> {

    private static final long serialVersionUID = -1867693972138681156L;

    public ListJobsResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<Job> unmarshaller() {
        return new Unmarshaller<Job>() {
            @Override
            public Job unmarshal(JSONArray value, int index) {
                Job job = new Job();
                job.deserialize(value.getJSONObject(index));
                return job;
            }
        };
    }
}
