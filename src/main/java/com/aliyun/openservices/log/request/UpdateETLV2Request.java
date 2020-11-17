package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateETLV2Request extends JobRequest {

    private static final long serialVersionUID = -6902986158715725606L;

    private ETLV2 etlv2;

    public UpdateETLV2Request(String project, ETLV2 etlv2) {
        super(project);
        this.etlv2 = etlv2;
        Args.notNull(etlv2, "ETL");
        setName(etlv2.getName());
    }

    public ETLV2 getEtl() {
        return etlv2;
    }

    public void setEtl(ETLV2 etlv2) {
        this.etlv2 = etlv2;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return etlv2;
    }
}
