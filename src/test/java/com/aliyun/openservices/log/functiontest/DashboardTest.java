package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetDashboardResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DashboardTest extends FunctionTest {

    private static final String TEST_PROJECT = "test-project-to-dashboard" + getNowTimestamp();

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "dashboardtest");
        waitForSeconds(5);
    }

    @After
    public void tearDown() {
        safeDeleteProject(TEST_PROJECT);
    }

    @Test
    public void testCRUD() throws LogException {
        /*delete dashboard*/
        String dashboardName = "dashboardtest";
        try {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboardName));
        } catch (LogException ex) {
            Assert.assertEquals("specified dashboard does not exist", ex.getMessage());
        }

        /*create dashboard*/
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName(dashboardName);
        dashboard.setDescription("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);
        try {
            client.createDashboard(createDashboardRequest);
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "specified dashboard already exists");
            assertEquals(ex.GetErrorCode(), "ParameterInvalid");
        }

        /*get dashboard*/
        GetDashboardResponse getDashboardResponse = client.getDashboard(new GetDashboardRequest(TEST_PROJECT, dashboardName));
        Dashboard getDashboard = getDashboardResponse.getDashboard();
        assertEquals(0, getDashboard.getChartList().size());
        assertEquals("Dashboard", getDashboard.getDescription());
        assertEquals("dashboardtest", getDashboard.getDashboardName());

        /*list dashboard*/
        ListDashboardResponse listDashboard = client.listDashboard(new ListDashboardRequest(TEST_PROJECT));
        assertEquals(1, listDashboard.getTotal());
        assertEquals(1, listDashboard.getCount());
        Dashboard listOne = listDashboard.getDashboards().get(0);
        assertEquals(0, listOne.getChartList().size());
        //assertEquals("Dashboard", listOne.getDescription());//can not get description
        assertEquals("dashboardtest", listOne.getDashboardName());

        /*update dashboard*/
        ArrayList<Chart> charts = new ArrayList<Chart>();
        Chart chart1 = createChart("chart-111");
        charts.add(chart1);
        dashboard.setChartList(charts);
        client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));

        Chart chart2 = createChart("chart-111");
        charts.add(chart2);
        dashboard.setChartList(charts);
        try {
            client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Duplicate chart title: " + chart1.getTitle());
            assertEquals(ex.GetErrorCode(), "PostBodyInvalid");
        }
        charts.clear();
        for (int i = 0; i < 100; i++) {
            charts.add(createChart("chart-" + i));
        }
        dashboard.setChartList(charts);
        client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
        try {
            charts.add(createChart("chart-100"));
            dashboard.setChartList(charts);
            client.updateDashboard(new UpdateDashboardRequest(TEST_PROJECT, dashboard));
            fail();
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "chart quota exceed");
            assertEquals(ex.GetErrorCode(), "ExceedQuota");
        }
        for (int i = 0; i < 100; i++) {
            client.deleteChart(new DeleteChartRequest(TEST_PROJECT, dashboardName, "chart-" + i));
        }
        try {
            client.deleteChart(new DeleteChartRequest(TEST_PROJECT, dashboardName, "chart-100"));
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
