package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.DeleteChartRequest;
import com.aliyun.openservices.log.request.DeleteDashboardRequest;
import com.aliyun.openservices.log.request.GetDashboardRequest;
import com.aliyun.openservices.log.request.ListDashboardRequest;
import com.aliyun.openservices.log.request.UpdateDashboardRequest;
import com.aliyun.openservices.log.response.GetDashboardResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DashboardTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testListDashboard() throws Exception {
        ListDashboardResponse response = client.listDashboard(new ListDashboardRequest(TEST_PROJECT));
        assertEquals(0, response.getCount());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getDashboards().size());

        int x = randomInt(10);
        for (int i = 0; i < x; i++) {
            Dashboard dashboard = new Dashboard();
            dashboard.setDashboardName("dash-" + i);
            dashboard.setDescription("Dashboard");
            dashboard.setChartList(new ArrayList<Chart>());
            CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
            client.createDashboard(createDashboardRequest);
        }
        response = client.listDashboard(new ListDashboardRequest(TEST_PROJECT));
        assertEquals(x, response.getCount());
        assertEquals(x, response.getTotal());
        assertEquals(x, response.getDashboards().size());
        for (int i = 0; i < x; i++) {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, "dash-" + i));
        }
        response = client.listDashboard(new ListDashboardRequest(TEST_PROJECT));
        assertEquals(0, response.getCount());
        assertEquals(0, response.getTotal());
        assertEquals(0, response.getDashboards().size());
    }

    @Test
    public void testCRUD() throws LogException {
        String dashboardName = "dashboardtest";
        try {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboardName));
        } catch (LogException ex) {
            assertEquals("DashboardNotExist", ex.GetErrorCode());
            assertEquals("specified dashboard does not exist", ex.getMessage());
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName(dashboardName);
        dashboard.setDescription("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);
        try {
            client.createDashboard(createDashboardRequest);
            fail();
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "specified dashboard already exists");
            assertEquals(ex.GetErrorCode(), "ParameterInvalid");
        }

        GetDashboardResponse getDashboardResponse = client.getDashboard(new GetDashboardRequest(TEST_PROJECT, dashboardName));
        Dashboard getDashboard = getDashboardResponse.getDashboard();
        assertEquals(0, getDashboard.getChartList().size());
        assertEquals("Dashboard", getDashboard.getDescription());
        assertEquals("dashboardtest", getDashboard.getDashboardName());

        ListDashboardResponse listDashboard = client.listDashboard(new ListDashboardRequest(TEST_PROJECT));
        assertEquals(1, listDashboard.getTotal());
        assertEquals(1, listDashboard.getCount());
        Dashboard listOne = listDashboard.getDashboards().get(0);
        assertEquals(0, listOne.getChartList().size());
        assertEquals("dashboardtest", listOne.getDashboardName());

        ArrayList<Chart> charts = new ArrayList<Chart>();
        Chart chart1 = createChart("chart-111");
        charts.add(chart1);
        dashboard.setChartList(charts);
        client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));

        GetDashboardResponse response = client.getDashboard(new GetDashboardRequest(TEST_PROJECT, dashboardName));
        Dashboard dashboard1 = response.getDashboard();
        List<Chart> charts1 = dashboard1.getChartList();
        assertEquals(1, charts1.size());
        Chart chartRes = charts1.get(0);
        assertEquals(chart1.getQuery(), chartRes.getQuery());
        assertEquals(chart1.getTitle(), chartRes.getTitle());
        assertEquals(chart1.getStart(), chartRes.getStart());
        assertEquals(chart1.getEnd(), chartRes.getEnd());
        assertEquals(chart1.getLogstore(), chartRes.getLogstore());
        assertEquals(chart1.getTopic(), chartRes.getTopic());

        Chart chart2 = createChart("chart-111");
        charts.add(chart2);
        dashboard.setChartList(charts);
        try {
            client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
            fail();
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Duplicate chart title: " + chart1.getTitle());
            assertEquals(ex.GetErrorCode(), "PostBodyInvalid");
        }
        charts.clear();
        int chartQuota = 200;
        for (int i = 0; i < chartQuota; i++) {
            charts.add(createChart("chart-" + i));
        }
        dashboard.setChartList(charts);
        client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
        try {
            charts.add(createChart("chart-" + chartQuota));
            dashboard.setChartList(charts);
            client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
            fail();
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "You have too many charts in your dashboard");
            assertEquals(ex.GetErrorCode(), "ExceedQuota");
        }
        for (int i = 0; i < chartQuota; i++) {
            client.deleteChart(new DeleteChartRequest(TEST_PROJECT, dashboardName, "chart-" + i));
        }
        try {
            client.deleteChart(new DeleteChartRequest(TEST_PROJECT, dashboardName, "chart-" + chartQuota));
            fail();
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "specified chart does not exist");
            assertEquals(ex.GetErrorCode(), "ChartNotExist");
        }
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
