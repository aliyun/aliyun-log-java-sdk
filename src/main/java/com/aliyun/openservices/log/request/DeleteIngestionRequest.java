package com.aliyun.openservices.log.request;

public class DeleteIngestionRequest extends DeleteJobRequest {

    private static final long serialVersionUID = 8861818102457211066L;

    public DeleteIngestionRequest(String project, String name) {
        super(project, name);
    }
}
