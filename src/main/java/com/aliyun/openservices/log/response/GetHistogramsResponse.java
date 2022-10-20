/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The response of the GetHistogram API from sls server
 *
 * @author sls_dev
 */
public class GetHistogramsResponse extends Response {

    private static final long serialVersionUID = 5169979404935069850L;
    private boolean mIsCompleted = false;
    private long mCount = 0;
    private ArrayList<Histogram> mHistogram = new ArrayList<Histogram>();

    /**
     * Construct a histogram response
     *
     * @param headers http headers
     */
    public GetHistogramsResponse(Map<String, String> headers) {
        super(headers);
        this.SetProcessStatus(headers.get(Consts.CONST_X_SLS_PROCESS));
    }

    /**
     * Get query hit count
     *
     * @return hit count
     */
    public long GetTotalCount() {
        return mCount;
    }

    /**
     * Set process status to the response
     *
     * @param processStatus process status(Complete/InComplete only)
     */
    public void SetProcessStatus(String processStatus) {
        if (processStatus.equals(Consts.CONST_RESULT_COMPLETE)) {
            mIsCompleted = true;
        } else {
            mIsCompleted = false;
        }
    }

    /**
     * Check if the GetHistogram is completed
     *
     * @return true if the query is complete in the sls server
     */
    public boolean IsCompleted() {
        return mIsCompleted;
    }

    /**
     * Add a time range histogram to the response
     *
     * @param histogram a time range histogram
     */
    public void AddHistogram(Histogram histogram) {
        addHistogram(histogram);
    }

    public void addHistogram(Histogram histogram) {
        mHistogram.add(histogram);
        mCount += histogram.GetCount();
    }

    /**
     * Set all the time range histogram to the response
     *
     * @param histograms all time range histogram
     */
    public void SetHistograms(List<Histogram> histograms) {
        mHistogram = new ArrayList<Histogram>(histograms);
    }

    /**
     * Get all the time range histogram from the response
     *
     * @return all time range histogram
     */
    public ArrayList<Histogram> GetHistograms() {
        return mHistogram;
    }

    public void fromJSON(JSONArray items) {
        mHistogram.clear();
        mCount = 0;
        if (items == null) {
            return;
        }
        try {
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                Histogram histogram = new Histogram(
                        item.getIntValue(Consts.CONST_FROM),
                        item.getIntValue(Consts.CONST_TO),
                        item.getLong(Consts.CONST_RESULT_COUNT),
                        item.getString(Consts.CONST_RESULT_PROCESS));
                addHistogram(histogram);
            }
        } catch (JSONException e) {
            // ignore
        }
    }
}
