package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;

abstract class JobIntgTest extends FunctionTest {

    static final String TEST_PROJECT = "project-intg-" + getNowTimestamp();
    static final String TEST_DASHBOARD = "dashboardtest";

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "");
    }

    protected void createDashboard() throws LogException {
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName(TEST_DASHBOARD);
        dashboard.setDescription("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        try {
            client.createDashboard(createDashboardRequest);
        } catch (LogException ex) {
            if (!ex.GetErrorMessage().equals("specified dashboard already exists")) {
                throw ex;
            }
        }
    }

    protected JobSchedule createSchedule() {
        JobSchedule schedule = new JobSchedule();
        schedule.setType(randomFrom(JobScheduleType.values()));
        switch (schedule.getType()) {
            case DAILY:
                schedule.setHour(0);
                break;
            case WEEKLY:
                schedule.setDayOfWeek(0);
                schedule.setHour(0);
                break;
            case FIXED_RATE:
                schedule.setInterval("60s");
                break;
            case CRON:
                schedule.setCronExpression("0 0 12 * * ?");
                break;
        }
        schedule.setDelay(0);
        return schedule;
    }

    @After
    public void tearDown() throws Exception {
        client.DeleteProject(TEST_PROJECT);
    }
}
