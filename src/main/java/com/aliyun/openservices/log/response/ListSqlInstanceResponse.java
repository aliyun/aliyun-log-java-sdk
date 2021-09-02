package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.SqlInstance;

import java.util.List;
import java.util.Map;

public class ListSqlInstanceResponse extends Response {
    List<SqlInstance>  sqlInstances;

    public ListSqlInstanceResponse(Map<String, String> headers, List<SqlInstance> sqlInstances) {
        super(headers);
        this.sqlInstances = sqlInstances;
    }

    public List<SqlInstance> getSqlInstances() {
        return sqlInstances;
    }
}
