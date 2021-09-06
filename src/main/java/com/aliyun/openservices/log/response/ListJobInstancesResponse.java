package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.log.common.JobInstance;
import com.aliyun.openservices.log.internal.Unmarshaller;
import java.io.Serializable;
import java.util.Map;

/**
 * ScheduledSQL list JonInstances Response
 */
public class ListJobInstancesResponse extends ResponseList<JobInstance> implements Serializable {
    private static final long serialVersionUID = -6158196540776923094L;
    public ListJobInstancesResponse(Map<String, String> headers) {
        super(headers);
    }
    @Override
    public Unmarshaller<JobInstance> unmarshaller() {
        return new Unmarshaller<JobInstance>() {
            @Override
            public JobInstance unmarshal(JSONArray value, int index) {
                JobInstance jobInstance = new JobInstance();
                jobInstance.deserialize(value.getJSONObject(index));
                return jobInstance;
            }
        };
    }
}