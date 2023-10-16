package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.util.Map;

public class ListShipperMigrationRequest extends Request {
    private Integer offset;
    private Integer size;
    /**
     * Construct the base request
     *
     * @param project project name
     */
    public ListShipperMigrationRequest(String project, int offset, int size) {
        super(project);
        this.offset = offset;
        this.size = size;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }
        return super.GetAllParams();
    }
}
