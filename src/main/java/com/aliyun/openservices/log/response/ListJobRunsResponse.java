package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.JobRun;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListJobRunsResponse extends ResponseList<JobRun> implements Serializable {

    private static final long serialVersionUID = 2584488640627126067L;

    public ListJobRunsResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<JobRun> unmarshaller() {
        return new Unmarshaller<JobRun>() {
            @Override
            public JobRun unmarshal(JSONArray value, int index) {
                JobRun jobRun = new JobRun();
                jobRun.deserialize(value.getJSONObject(index));
                return jobRun;
            }
        };
    }
}
