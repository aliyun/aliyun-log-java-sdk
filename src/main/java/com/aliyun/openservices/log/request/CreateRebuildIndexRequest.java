package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class CreateRebuildIndexRequest extends JobRequest {

    private static final long serialVersionUID = -289518691743568346L;

    private final RebuildIndex rebuildIndex;

    public CreateRebuildIndexRequest(String project, RebuildIndex rebuildIndex) {
        super(project);
        this.rebuildIndex = rebuildIndex;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI;
    }

    @Override
    public Object getBody() {
        return rebuildIndex;
    }
}
