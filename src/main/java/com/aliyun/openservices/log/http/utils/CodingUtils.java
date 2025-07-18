package com.aliyun.openservices.log.http.utils;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.util.regex.Pattern;

/**
 * Utils for common coding.
 *
 * @author xiaoming.yin
 */
public class CodingUtils {
    private static final Pattern PROJECT_PATTERN = Pattern.compile("^[0-9a-zA-Z-_]{1,128}$");
    private static final Pattern CONSUMER_GROUP_PATTERN = Pattern.compile("^[0-9a-zA-Z-_]{1,128}$");
    private static final Pattern DASHBOARD_PATTERN = Pattern.compile("^[0-9a-z][0-9a-z_-]{0,126}[0-9a-z]$");
    private static final Pattern LOGSTORE_PATTERN = Pattern.compile("^[0-9a-z][0-9a-z_-]{0,78}[0-9a-z]$");
    private static final Pattern LOGSTORE_SEARCH_PATTERN = Pattern.compile("^[A-Za-z0-9_\\*-]+$");
    private static final Pattern METASTORE_PATTERN = Pattern.compile("^[a-z][0-9a-z_.]{1,125}[0-9a-z]$");
    private static final Pattern SAVEDSEARCH_PATTERN = Pattern.compile("^[0-9a-z][0-9a-z_-]{0,62}[0-9a-z]$");
    private static final Pattern JOB_PATTERN = Pattern.compile("^[0-9a-zA-Z-_]{1,63}$");
    private static final Pattern CONFIG_PATTERN = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z_-]{0,126}[0-9a-zA-Z]$");
    private static final Pattern MACHINE_GROUP_PATTERN = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z_-]{0,126}[0-9a-zA-Z]$");
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("^[a-z][0-9a-z_.]{1,125}[0-9a-z]$");
    private static final Pattern RECORD_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_:.-]{0,63}$");
    private static final Pattern ETL_JOB_PATTERN = Pattern.compile("^[0-9a-z][0-9a-z_-]{0,126}[0-9a-z]$");
    private static final int MAX_OFFSET = Integer.MAX_VALUE;

    public static void assertParameterNotNull(Object param, String paramName) throws LogException {
        if (param == null) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, paramName + " is null", "");
        }
    }

    public static void assertStringNotNullOrEmpty(String param, String paramName) throws LogException {
        assertParameterNotNull(param, paramName);
        if (param.isEmpty()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, paramName + " is empty", "");
        }
    }

    public static void validateDashboardName(String dashboardName) throws LogException {
        assertParameterNotNull(dashboardName, "dashboardName");
        if (!DASHBOARD_PATTERN.matcher(dashboardName).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid dashboard: " + dashboardName, "");
        }
    }

    public static void validateLogstore(String logstore) throws LogException {
        assertParameterNotNull(logstore, "logstore");
        if (!LOGSTORE_PATTERN.matcher(logstore).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid logstore: " + logstore, "");
        }
    }
    public static void validateLogstoreSearch(String logstore) throws LogException {
        assertParameterNotNull(logstore, "logstore");
        if (!LOGSTORE_SEARCH_PATTERN.matcher(logstore).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid logstore: " + logstore, "");
        }
    }

    public static void validateMetastore(String metastore) throws LogException {
        assertParameterNotNull(metastore, "metastore");
        if (!METASTORE_PATTERN.matcher(metastore).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid metastore: " + metastore, "");
        }
    }

    public static void validateSavedSearch(String savedsearch) throws LogException {
        assertParameterNotNull(savedsearch, "savedsearch");
        if (!SAVEDSEARCH_PATTERN.matcher(savedsearch).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid savedsearch: " + savedsearch, "");
        }
    }

    public static void validateJob(String jobName) throws LogException {
        assertParameterNotNull(jobName, "jobName");
        if (!JOB_PATTERN.matcher(jobName).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid jobName: " + jobName, "");
        }
    }

    public static void validateProject(String project) throws LogException {
        assertParameterNotNull(project, "project");
        if (!PROJECT_PATTERN.matcher(project).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid project: " + project, "");
        }
    }

    public static void validateConsumerGroup(String consumerGroup) throws LogException {
        assertParameterNotNull(consumerGroup, "consumerGroup");
        if (!CONSUMER_GROUP_PATTERN.matcher(consumerGroup).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid consumerGroup: " + consumerGroup, "");
        }
    }

    public static void validateConfig(String config) throws LogException {
        assertParameterNotNull(config, "config");
        if (!CONFIG_PATTERN.matcher(config).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid configName: " + config, "");
        }
    }

    public static void validateMachineGroup(String machineGroup) throws LogException {
        assertParameterNotNull(machineGroup, "machineGroup");
        if (!MACHINE_GROUP_PATTERN.matcher(machineGroup).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid machineGroupName: " + machineGroup, "");
        }
    }

    public static void validateResource(String resourceName) throws LogException {
        assertParameterNotNull(resourceName, "resourceName");
        if (!RESOURCE_PATTERN.matcher(resourceName).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid resourceName: " + resourceName, "");
        }
    }

    public static void validateRecordId(String recordId) throws LogException {
        assertParameterNotNull(recordId, "recordId");
        if (!RECORD_ID_PATTERN.matcher(recordId).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid recordId: " + recordId, "");
        }
    }

    public static void validateETLJob(String etlJobName) throws LogException {
        assertParameterNotNull(etlJobName, "etlJobName");
        if (!ETL_JOB_PATTERN.matcher(etlJobName).matches()) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Invalid etlJobName: " + etlJobName, "");
        }
    }

    public static void validateOffset(long offset) throws LogException {
        assertParameterNotNull(offset, "offset");
        if (offset > MAX_OFFSET) {
            throw new LogException(ErrorCodes.INVALID_PARAMETER, "Up to " + MAX_OFFSET + " numbers of logs are supported to search", "");
        }
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
