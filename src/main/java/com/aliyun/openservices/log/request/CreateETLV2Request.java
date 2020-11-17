package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateETLV2Request extends JobRequest {

    private static final long serialVersionUID = 3346010323520068092L;

    private ETLV2 etlv2;

    public CreateETLV2Request(String project, ETLV2 etlv2) {
        super(project);
        Args.notNull(etlv2, "ETL");
        this.etlv2 = etlv2;
    }

    public ETLV2 getEtl() {
        return etlv2;
    }

    public void setEtl(ETLV2 etlv2) {
        this.etlv2 = etlv2;
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
        return etlv2;
    }
}
