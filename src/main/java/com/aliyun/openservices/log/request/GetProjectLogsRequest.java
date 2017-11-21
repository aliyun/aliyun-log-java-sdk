/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The Request used to get data of a query from sls server
 *
 * @author sls_dev
 *
 */
public class GetProjectLogsRequest extends Request {
    private static final long serialVersionUID = -484272901258629068L;


    /**
     * Construct a the request
     *
     * @param project
     *            project name
     * @param query
     *            user query
     */
    public GetProjectLogsRequest(String project, String query) {
        super(project);
        SetQuery(query);
    }


    /**
     * Set query
     *
     * @param query
     *            user define query
     */
    public void SetQuery(String query) {
        SetParam(Consts.CONST_QUERY, query);
    }

    /**
     * Get Query
     *
     * @return query
     */
    public String GetQuery() {
        return GetParam(Consts.CONST_QUERY);
    }

}
