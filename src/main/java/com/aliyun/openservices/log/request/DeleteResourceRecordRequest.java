package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeleteResourceRecordRequest extends RecordRequest {
    private List<String> recordIds;

    public DeleteResourceRecordRequest(String resourceName, List<String> recordIds) {
        super(resourceName);
        this.recordIds = recordIds;
    }

    public DeleteResourceRecordRequest(String resourceName, String recordId) {
        this(resourceName, Collections.singletonList(recordId));
    }

    @Override
    public Map<String, String> GetAllParams() {
        SetParam(Consts.RESOURCE_RECORD_IDS, Utils.join(",", recordIds));
        return super.GetAllParams();
    }

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }
}
