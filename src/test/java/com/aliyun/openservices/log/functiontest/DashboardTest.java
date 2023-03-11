package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetDashboardResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;
import org.junit.Assert;
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
    public void testValidateDashboardName() throws Exception {
        Dashboard dashboard = new Dashboard();
        StringBuilder dashboardName = new StringBuilder();
        dashboard.setDescription("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        for (int i = 0; i < 146; ++i) {
            dashboardName.append((char) (randomBetween(0, 26) + 'a'));
            String tmpName = dashboardName.toString();
            dashboard.setDashboardName(tmpName);
            if (dashboardName.length() < 2 || dashboardName.length() > 128) {
                try {
                    client.createDashboard(createDashboardRequest);
                    fail("should fail");
                } catch (LogException ex) {
                    Assert.assertEquals(tmpName, "InvalidDashboardName", ex.getErrorCode());
                    if (dashboardName.length() > 128) {
                        Assert.assertEquals("The length of dashboard name must be less than or equal to 128", ex.getMessage());
                    } else {
                        Assert.assertEquals("The dashboard name is invalid: " + tmpName, ex.getMessage());
                    }
                }
                continue;
            }
            client.createDashboard(createDashboardRequest);
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, tmpName));
        }
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

    @Test
    public void testSpecialCharacters() throws Exception {
        Chart chart = createChart("test-chart-1");
        JSONObject displayAttrObj = JSONObject.parseObject("{\"yPos\":9,\"filterData\":[{\"list\":[\"\uD83D\uDE13❤\"],\"type\":\"filter\",\"listAlias\":[\"\uD83D\uDE13❤\"],\"key\":\"key\",\"listDefault\":[true]}],\"displayName\":\"测试\",\"showTitle\":false,\"bindQuery\":false,\"showBackground\":false,\"width\":6,\"logic\":\"and\",\"xPos\":0,\"showBorder\":false,\"height\":1,\"zIndex\":10}");
        chart.setRawDisplayAttr(displayAttrObj.toString());
        String dashboardName = "dashboardtest-special";
        try {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboardName));
        } catch (LogException ex) {
            assertEquals("DashboardNotExist", ex.GetErrorCode());
            assertEquals("specified dashboard does not exist", ex.getMessage());
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName(dashboardName);
        dashboard.setDescription("Dashboard");
        ArrayList<Chart> charts = new ArrayList<Chart>();
        charts.add(chart);
        dashboard.setChartList(charts);
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);
        Dashboard dashboard1 = client.getDashboard(new GetDashboardRequest(TEST_PROJECT, dashboardName)).getDashboard();
        Assert.assertEquals(1, dashboard1.getChartList().size());
        Chart chart1 = dashboard1.getChartList().get(0);
        Assert.assertEquals(chart1.getTitle(), chart.getTitle());
        String displayAttr = chart1.getRawDisplayAttr();
        JSONObject displayObj = JSONObject.parseObject(displayAttr);
        Assert.assertEquals(9, displayObj.getIntValue("yPos"));
        Assert.assertEquals(0, displayObj.getIntValue("xPos"));
        Assert.assertEquals(1, displayObj.getIntValue("height"));
        Assert.assertEquals(6, displayObj.getIntValue("width"));
        Assert.assertEquals(10, displayObj.getIntValue("zIndex"));
        Assert.assertEquals("测试", displayObj.getString("displayName"));
        Assert.assertEquals("and", displayObj.getString("logic"));
        Assert.assertFalse(displayObj.getBoolean("showTitle"));
        Assert.assertFalse(displayObj.getBoolean("bindQuery"));
        Assert.assertFalse(displayObj.getBoolean("showBackground"));
        Assert.assertFalse(displayObj.getBoolean("showBorder"));
        JSONArray filterData = displayObj.getJSONArray("filterData");
        Assert.assertEquals(1, filterData.size());
        JSONObject first = filterData.getJSONObject(0);
        JSONArray list = first.getJSONArray("list");
        Assert.assertEquals("\uD83D\uDE13❤", list.getString(0));
        Assert.assertEquals("filter", first.getString("type"));
        Assert.assertEquals("key", first.getString("key"));
        JSONArray listDefault = first.getJSONArray("listDefault");
        Assert.assertEquals(1, listDefault.size());
        Assert.assertTrue(listDefault.getBoolean(0));
        JSONArray listAlias = first.getJSONArray("listAlias");
        Assert.assertEquals("\uD83D\uDE13❤", listAlias.getString(0));
    }

    @Test
    public void testAttributeNotMissing() throws Exception {
        Chart chart = createChart("test-chart-1");
        JSONObject displayAttrObj = JSONObject.parseObject("{\"yPos\":9,\"filterData\":[{\"list\":[\"\uD83D\uDE13❤\"],\"type\":\"filter\",\"listAlias\":[\"\uD83D\uDE13❤\"],\"key\":\"key\",\"listDefault\":[true]}],\"displayName\":\"测试\",\"showTitle\":false,\"bindQuery\":false,\"showBackground\":false,\"width\":6,\"logic\":\"and\",\"xPos\":0,\"showBorder\":false,\"height\":1,\"zIndex\":10}");
        chart.setRawDisplayAttr(displayAttrObj.toString());
        String dashboardName = "dashboardtest-attribute-not-missing";
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName(dashboardName);
        dashboard.setDescription("Dashboard");
        dashboard.setAttribute("{\"key\":\"xxxxx\"}");
        ArrayList<Chart> charts = new ArrayList<Chart>();
        charts.add(chart);
        dashboard.setChartList(charts);
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);
        Dashboard dashboard1 = client.getDashboard(new GetDashboardRequest(TEST_PROJECT, dashboardName)).getDashboard();
        Assert.assertEquals(dashboard1.getAttribute(), dashboard.getAttribute());
    }
}
