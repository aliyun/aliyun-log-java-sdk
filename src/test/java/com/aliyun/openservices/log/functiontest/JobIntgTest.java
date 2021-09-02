package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateDashboardRequest;

import java.util.ArrayList;
import java.util.Arrays;

abstract class JobIntgTest extends MetaAPIBaseFunctionTest {

    static final String TEST_DASHBOARD = "dashboardtest";

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

    private static JobScheduleType randomScheduleType(boolean scheduled) {
        if (scheduled) {
            return randomFrom(Arrays.asList(JobScheduleType.DAILY,
                    JobScheduleType.HOURLY,
                    JobScheduleType.WEEKLY,
                    JobScheduleType.FIXED_RATE,
                    JobScheduleType.CRON));
        }
        return randomFrom(JobScheduleType.values());
    }

    protected JobSchedule createSchedule(boolean scheduled) {
        JobSchedule schedule = new JobSchedule();
        schedule.setType(randomScheduleType(scheduled));
        schedule.setRunImmediately(randomBoolean());
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
                schedule.setCronExpression("0 12 * * ?");
                break;
        }
        schedule.setDelay(0);
        return schedule;
    }
}
