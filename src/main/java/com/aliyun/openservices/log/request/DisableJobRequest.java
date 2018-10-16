package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Utils;

import java.util.Date;
import java.util.Map;

public class DisableJobRequest extends JobRequest {

    // Disable for a while
    private Date disableUntil;

    public DisableJobRequest(String project, String jobName) {
        super(project, jobName);
        SetParam(Consts.ACTION, Consts.DISABLE);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    public Date getDisableUntil() {
        return disableUntil;
    }

    public void setDisableUntil(Date disableUntil) {
        this.disableUntil = disableUntil;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (disableUntil != null) {
            SetParam(Consts.DISABLE_UNTIL, String.valueOf(Utils.getTimestamp(disableUntil)));
        }
        return super.GetAllParams();
    }
}
