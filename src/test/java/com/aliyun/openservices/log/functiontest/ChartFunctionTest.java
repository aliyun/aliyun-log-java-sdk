package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateChartRequest;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.DeleteChartRequest;
import com.aliyun.openservices.log.request.GetChartRequest;
import com.aliyun.openservices.log.request.UpdateChartRequest;
import com.aliyun.openservices.log.response.GetChartResponse;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ChartFunctionTest extends MetaAPIBaseFunctionTest {
    private static final String DASHBOARD_NAME = "test_dashboard";

    @Test
    public void testCRUD() throws LogException {
        createDashboard();
        client.createChart(new CreateChartRequest(TEST_PROJECT, DASHBOARD_NAME, createChart("chart-0")));
        try {
            client.createChart(new CreateChartRequest(TEST_PROJECT, DASHBOARD_NAME, createChart("chart-0")));
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
            assertEquals("specified chart already exists", e.GetErrorMessage());
        }
        //update
        Chart chart = createChart("chart-0");
        chart.setLogstore("logstore-2");
        client.updateChart(new UpdateChartRequest(TEST_PROJECT, DASHBOARD_NAME, "chart-0", chart));
        //get
        GetChartResponse getResponse = client.getChart(new GetChartRequest(TEST_PROJECT, DASHBOARD_NAME, "chart-0"));
        Chart getChart = getResponse.getChart();
        assertEquals("logstore-2", getChart.getLogstore());
        assertEquals("*", getChart.getQuery());
        assertEquals("chart-0", getChart.getTitle());
        assertEquals("table", getChart.getType());
        assertEquals("", getChart.getTopic());
        assertEquals(5L, getChart.getHeight());
        assertEquals(5L, getChart.getWidth());
        assertEquals("-60s", getChart.getStart());
        assertEquals("now", getChart.getEnd());
        assertEquals(0L, getChart.getxPosition());
        assertEquals(-1L, getChart.getyPosition());
        String rawSearchAttr = getChart.getRawSearchAttr();
        JSONObject object = JSONObject.parseObject(rawSearchAttr);
        assertEquals("logstore-2", object.getString("logstore"));
        assertEquals("-60s", object.getString("start"));
        assertEquals("now", object.getString("end"));
        assertEquals("", object.getString("topic"));
        assertEquals("*", object.getString("query"));
        assertEquals("custom", object.getString("timeSpanType"));
        //list
        //No

        //delete
        client.deleteChart(new DeleteChartRequest(TEST_PROJECT, DASHBOARD_NAME, "chart-0"));
        try {
            client.deleteChart(new DeleteChartRequest(TEST_PROJECT, DASHBOARD_NAME, "chart-0"));
        } catch (LogException e) {
            assertEquals("ChartNotExist", e.GetErrorCode());
            assertEquals("specified chart does not exist", e.GetErrorMessage());
        }
    }

    private void createDashboard() throws LogException {
        Dashboard dashboard = new Dashboard();
        dashboard.setChartList(new ArrayList<Chart>());
        dashboard.setDashboardName(DASHBOARD_NAME);
        dashboard.setDisplayName("test-chart-dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        client.createDashboard(new CreateDashboardRequest(TEST_PROJECT, dashboard));
    }

    private Chart createChart(String chartTitle) {
        Chart chart = new Chart();
        chart.setDisplayName(chartTitle);
        chart.setQuery("*");
        chart.setLogstore("logstore-1");
        chart.setTitle(chartTitle);
        chart.setType("table");
        chart.setTopic("");
        chart.setHeight(5);
        chart.setWidth(5);
        chart.setStart("-60s");
        chart.setEnd("now");
        chart.setxPosition(0);
        chart.setyPosition(-1);
        JSONObject searchAttr = new JSONObject();
        searchAttr.put("logstore", "logstore-1");
        searchAttr.put("start", "-60s");
        searchAttr.put("end", "now");
        searchAttr.put("topic", "");
        searchAttr.put("query", chart.getQuery());
        searchAttr.put("timeSpanType", "custom");
        chart.setRawSearchAttr(searchAttr.toString());
        return chart;
    }
}
