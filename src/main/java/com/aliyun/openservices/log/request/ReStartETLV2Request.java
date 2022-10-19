
package com.aliyun.openservices.log.request;
import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class ReStartETLV2Request extends JobRequest {
    private static final long serialVersionUID = -6812128854811645686L;

    private ETLV2 etlv2;

    public ReStartETLV2Request(String project, ETLV2 etlv2) {
        super(project, etlv2.getName());
        this.etlv2 = etlv2;
        SetParam(Consts.ACTION, Consts.RESTART);
    }

    public ETLV2 getEtl() {
        return etlv2;
    }

    public void setEtl(ETLV2 etlv2) {
        this.etlv2 = etlv2;
    }

    @Override
    public Object getBody() {
        return etlv2;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
