package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RandomDataTest extends FunctionTest {
    @Ignore
    @Test
    public void test() {
        while (true) {
            int begin;
            int end = getNowTimestamp() - 60;
            begin = end - 3600;
            int b = randomBetween(begin, end);
            if (b + 1 >= end - 1) {
                continue;
            }
            int e = randomBetween(b + 1, end - 1);
            if (b > e) {
                continue;
            }
            Response hres = getLogs("", "", b, e, "set session parallel_sql=false;");
            Response ares = getLogs("", "", b, e, "set session parallel_sql=true;");
            if (hres.complete && ares.complete && hres.res.size() != ares.res.size()) {
                System.out.println(JSONObject.toJSONString(hres.res) + "\n" + JSONObject.toJSONString(ares.res) + b + e);
                break;
            }
        }
    }

    private Response getLogs(String project, String logstore, int from, int to, String query) {
        String sql = "not " + randomBetween(0, 100000000) + " |" + query + " " +
                "select method as m,count(1) as pv ,avg(latency) as lat,count(latency),count(latency), max(from_unixtime(__time__)) , " +
                "min(from_unixtime(__time__)) group by m order by pv desc,lat desc, m desc";
        GetLogsRequest request = new GetLogsRequest(project, logstore, from, to, "", sql);
        GetLogsResponse response;
        try {
            response = client.GetLogs(request);
            if (response.GetCount() == 0) {
                return new Response();
            } else {
                List<String[]> res = new ArrayList<String[]>();
                for (QueriedLog log : response.GetLogs()) {
                    String[] str = new String[]{"", "", ""};
                    for (LogContent mContent : log.mLogItem.mContents) {
                        String mKey = mContent.mKey;
                        String mValue = mContent.mValue;
                        if ("m".equals(mKey)) {
                            str[0] = mValue;
                        } else if ("pv".equals(mKey)) {
                            str[1] = mValue;
                        } else if ("lat".equals(mKey)) {
                            str[2] = mValue;
                        }
                    }
                    res.add(str);
                }
                return new Response(res, response, true);
            }
        } catch (LogException e) {
            return new Response();
        }
    }

    private class Response {
        private GetLogsResponse response;
        private boolean complete;
        private List<String[]> res;

        public Response() {
        }

        public Response(List<String[]> res, GetLogsResponse response, boolean complete) {
            this.response = response;
            this.complete = complete;
            this.res = res;
        }
    }
}
