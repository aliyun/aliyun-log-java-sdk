package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MethodTest extends FunctionTest {

    @Test
    public void testGetByte() {
        byte[][] testDate = getTestDate();
        assertEquals(getProtoLength(testDate), getLength(testDate));
        assertEquals(getProtoLogsLength(testDate), getLogsLength(testDate));
    }

    private int getLength(byte[][] testDataSet) {
        int count = 0;
        for (byte[] bytes : testDataSet) {
            try {
                BatchGetLogResponse logResponse = new BatchGetLogResponse(new HashMap<String, String>());
                logResponse.ParseFastLogGroupList(bytes);
                List<LogGroupData> logGroups = logResponse.GetLogGroups();
                for (LogGroupData logGroup : logGroups) {
                    FastLogGroup fastLogGroup = logGroup.GetFastLogGroup();
                    count += fastLogGroup.getByteSize();
                }
            } catch (Exception e) {
                failOnError(e);
            }
        }
        return count;
    }

    private int getProtoLength(byte[][] testDataSet) {
        int count = 0;
        for (byte[] bytes : testDataSet) {
            try {
                Logs.LogGroupList logGroupList = Logs.LogGroupList.parseFrom(bytes);
                for (int j = 0; j < logGroupList.getLogGroupListCount(); j++) {
                    LogGroupData logGroupData = new LogGroupData(logGroupList.getLogGroupList(j));
                    Logs.LogGroup logGroup = logGroupData.GetLogGroup();
                    count += logGroup.getSerializedSize();
                }
            } catch (InvalidProtocolBufferException e) {
                failOnError(e);
            } catch (LogException ex) {
                failOnError(ex);
            }
        }
        return count;
    }

    private int getLogsLength(byte[][] testDataSet) {
        int count = 0;
        for (byte[] bytes : testDataSet) {
            try {
                BatchGetLogResponse logResponse = new BatchGetLogResponse(new HashMap<String, String>());
                logResponse.ParseFastLogGroupList(bytes);
                List<LogGroupData> logGroups = logResponse.GetLogGroups();
                for (LogGroupData logGroup : logGroups) {
                    FastLogGroup fastLogGroup = logGroup.GetFastLogGroup();
                    for (int k = 0; k < fastLogGroup.getLogsCount(); k++) {
                        count += fastLogGroup.getLogs(k).getByteSize();
                    }
                }
            } catch (Exception e) {
                failOnError(e);
            }
        }
        return count;
    }

    private int getProtoLogsLength(byte[][] testDataSet) {
        int count = 0;
        for (byte[] bytes : testDataSet) {
            try {
                Logs.LogGroupList logGroupList = Logs.LogGroupList.parseFrom(bytes);
                for (int j = 0; j < logGroupList.getLogGroupListCount(); j++) {
                    LogGroupData logGroupData = new LogGroupData(logGroupList.getLogGroupList(j));
                    Logs.LogGroup logGroup = logGroupData.GetLogGroup();
                    for (int k = 0; k < logGroup.getLogsCount(); k++) {
                        count += logGroup.getLogs(k).getSerializedSize();
                    }
                }
            } catch (Exception e) {
                failOnError(e);
            }
        }
        return count;
    }

    private String randomString(int minLength, int maxLength) {
        int length = randomBetween(minLength, maxLength);
        StringBuilder randString = new StringBuilder();
        int i = 0;
        while (i < length) {
            randString.append((char) (randomBetween(33, 126)));
            i++;
        }
        return " " + randString;
    }

    private byte[][] getTestDate() {
        final int logGroupListCount = 5;
        byte[][] testDataSet = new byte[logGroupListCount][];
        int minTime = (int) (System.currentTimeMillis() / (long) 1000) - 86400;
        int maxTime = minTime + 86400;
        for (int i = 0; i < logGroupListCount; ++i) {
            Logs.LogGroupList.Builder logGroupListBuilder = Logs.LogGroupList.newBuilder();
            int logGroupCount = randomInt(30) + 1;
            for (int j = 0; j < logGroupCount; ++j) {
                Logs.LogGroup.Builder logGroupBuilder = Logs.LogGroup.newBuilder();
                if (randomInt(3) != 0) {
                    logGroupBuilder.setCategory(randomString(0, 64));
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setTopic(randomString(0, 64));
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setSource(randomString(0, 64));
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setMachineUUID(randomString(0, 64));
                }
                int tagCount = randomInt(3);
                for (int k = 0; k < tagCount; ++k) {
                    Logs.LogTag.Builder tagBuilder = Logs.LogTag.newBuilder();
                    tagBuilder.setKey(randomString(0, 8));
                    tagBuilder.setValue(randomString(0, 64));
                    logGroupBuilder.addLogTags(tagBuilder.build());
                }
                int logCount = randomInt(2000);
                for (int k = 0; k < logCount; ++k) {
                    Logs.Log.Builder logBuilder = Logs.Log.newBuilder();
                    int contentCount = randomInt(30) + 1;
                    for (int l = 0; l < contentCount; ++l) {
                        Logs.Log.Content.Builder contentBuilder = Logs.Log.Content.newBuilder();
                        contentBuilder.setKey(randomString(0, 8));
                        contentBuilder.setValue(randomString(0, 128));
                        logBuilder.addContents(contentBuilder.build());
                    }
                    logBuilder.setTime(randomBetween(minTime, maxTime));
                    logGroupBuilder.addLogs(logBuilder.build());
                }
                logGroupListBuilder.addLogGroupList(logGroupBuilder.build());
            }
            testDataSet[i] = logGroupListBuilder.build().toByteArray();
        }
        return testDataSet;
    }
}
