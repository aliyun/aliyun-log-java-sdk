package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.FastLog;
import com.aliyun.openservices.log.common.FastLogContent;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.FastLogTag;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.Logs.Log;
import com.aliyun.openservices.log.common.Logs.LogGroup;
import com.aliyun.openservices.log.common.Logs.LogGroupList;
import com.aliyun.openservices.log.common.Logs.LogTag;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListTopicsResponse;
import com.aliyun.openservices.log.response.PutLogsResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SlsPerformanceTest extends FunctionTest {

    private String project = "ali-cn-yunlei-sls-admin";
    private String logStore = "data-ft";

    private int logItemLine = 30;
    private int runTimes = 100;
    private int lastMaxTime = 5;

    private int fromTime;
    private int toTime;

    private String randomString(int minLength, int maxLength) {
        int length = randomBetween(minLength, maxLength);
        StringBuilder randString = new StringBuilder();
        int i = 0;
        while (i < length) {
            randString.append(Character.toString((char) (randomBetween(33, 126))));
            i++;
        }
        return " " + randString;
    }

    private void VerifyFastPbDeserialize(byte[][] testDataSet, int logGroupCount, int logCount, int tagCount, int contentCount, int categoryCount,
                                         int sourceCount, int topicCount, int uuidCount) throws InvalidProtocolBufferException, LogException {
        int vLogGroupCount = 0;
        int vLogCount = 0;
        int vTagCount = 0;
        int vContentCount = 0;
        int vCategoryCount = 0;
        int vSourceCount = 0;
        int vTopicCount = 0;
        int vUuidCount = 0;
        for (int i = 0; i < testDataSet.length; ++i) {
            BatchGetLogResponse bglResponse = new BatchGetLogResponse(new HashMap<String, String>());
            bglResponse.ParseFastLogGroupList(testDataSet[i]);
            List<LogGroupData> fastLogGroupDatas = bglResponse.GetLogGroups();
            LogGroupList logGroupList = LogGroupList.parseFrom(testDataSet[i]);
            assertEquals(fastLogGroupDatas.size(), logGroupList.getLogGroupListCount());
            for (int j = 0; j < logGroupList.getLogGroupListCount(); ++j) {
                vLogGroupCount++;
                LogGroupData logGroupData = new LogGroupData(logGroupList.getLogGroupList(j));
                LogGroup logGroup = logGroupData.GetLogGroup();
                FastLogGroup fastLogGroup = fastLogGroupDatas.get(j).GetFastLogGroup();
                LogGroup copyLogGroup = LogGroup.parseFrom(fastLogGroup.getBytes());
                assertTrue(!copyLogGroup.hasCategory());
                assertEquals(copyLogGroup.hasSource(), fastLogGroup.hasSource());
                assertEquals(copyLogGroup.hasTopic(), fastLogGroup.hasTopic());
                assertEquals(copyLogGroup.hasMachineUUID(), fastLogGroup.hasMachineUUID());
                assertEquals(logGroup.hasCategory(), fastLogGroup.hasCategory());
                assertEquals(logGroup.hasTopic(), fastLogGroup.hasTopic());
                assertEquals(logGroup.hasSource(), fastLogGroup.hasSource());
                assertEquals(logGroup.hasMachineUUID(), fastLogGroup.hasMachineUUID());
                if (logGroup.hasCategory()) {
                    vCategoryCount++;
                    assertEquals(logGroup.getCategory(), fastLogGroup.getCategory());
                    assertEquals(new String(logGroup.getCategoryBytes().toByteArray()), new String(fastLogGroup.getCategoryBytes()));
                } else {
                    assertNull(fastLogGroup.getCategory());
                    assertNull(fastLogGroup.getCategoryBytes());
                }
                if (logGroup.hasTopic()) {
                    vTopicCount++;
                    assertEquals(copyLogGroup.getTopic(), fastLogGroup.getTopic());
                    assertEquals(new String(copyLogGroup.getTopicBytes().toByteArray()), new String(fastLogGroup.getTopicBytes()));
                    assertEquals(logGroup.getTopic(), fastLogGroup.getTopic());
                    assertEquals(new String(logGroup.getTopicBytes().toByteArray()), new String(fastLogGroup.getTopicBytes()));
                } else {
                    assertNull(fastLogGroup.getTopic());
                    assertNull(fastLogGroup.getTopicBytes());
                }
                if (logGroup.hasSource()) {
                    vSourceCount++;
                    assertEquals(copyLogGroup.getSource(), fastLogGroup.getSource());
                    assertEquals(new String(copyLogGroup.getSourceBytes().toByteArray()), new String(fastLogGroup.getSourceBytes()));
                    assertEquals(logGroup.getSource(), fastLogGroup.getSource());
                    assertEquals(new String(logGroup.getSourceBytes().toByteArray()), new String(fastLogGroup.getSourceBytes()));
                } else {
                    assertNull(fastLogGroup.getSource());
                    assertNull(fastLogGroup.getSourceBytes());
                }
                if (logGroup.hasMachineUUID()) {
                    vUuidCount++;
                    assertEquals(copyLogGroup.getMachineUUID(), fastLogGroup.getMachineUUID());
                    assertEquals(new String(copyLogGroup.getMachineUUIDBytes().toByteArray()), new String(fastLogGroup.getMachineUUIDBytes()));
                    assertEquals(logGroup.getMachineUUID(), fastLogGroup.getMachineUUID());
                    assertEquals(new String(logGroup.getMachineUUIDBytes().toByteArray()), new String(fastLogGroup.getMachineUUIDBytes()));
                } else {
                    assertNull(fastLogGroup.getMachineUUID());
                    assertNull(fastLogGroup.getMachineUUIDBytes());
                }
                assertEquals(copyLogGroup.getLogTagsCount(), fastLogGroup.getLogTagsCount());
                assertEquals(logGroup.getLogTagsCount(), fastLogGroup.getLogTagsCount());
                for (int k = 0; k < logGroup.getLogTagsCount(); ++k) {
                    LogTag tag = logGroup.getLogTags(k);
                    vTagCount++;
                    FastLogTag fastTag = fastLogGroup.getLogTags(k);
                    LogTag copyTag = copyLogGroup.getLogTags(k);
                    assertEquals(copyTag.getKey(), fastTag.getKey());
                    assertEquals(new String(copyTag.getKeyBytes().toByteArray()), new String(fastTag.getKeyBytes()));
                    assertEquals(copyTag.getValue(), fastTag.getValue());
                    assertEquals(new String(copyTag.getValueBytes().toByteArray()), new String(fastTag.getValueBytes()));
                    assertEquals(tag.getKey(), fastTag.getKey());
                    assertEquals(new String(tag.getKeyBytes().toByteArray()), new String(fastTag.getKeyBytes()));
                    assertEquals(tag.getValue(), fastTag.getValue());
                    assertEquals(new String(tag.getValueBytes().toByteArray()), new String(fastTag.getValueBytes()));
                }
                assertEquals(logGroup.getLogsCount(), fastLogGroup.getLogsCount());
                for (int k = 0; k < logGroup.getLogsCount(); ++k) {
                    vLogCount++;
                    Log log = logGroup.getLogs(k);
                    FastLog fastLog = fastLogGroup.getLogs(k);
                    Log copyLog = copyLogGroup.getLogs(k);
                    assertEquals(copyLog.getTime(), fastLog.getTime());
                    assertEquals(log.getTime(), fastLog.getTime());
                    assertEquals(copyLog.getContentsCount(), fastLog.getContentsCount());
                    assertEquals(log.getContentsCount(), fastLog.getContentsCount());
                    for (int l = 0; l < log.getContentsCount(); ++l) {
                        vContentCount++;
                        Log.Content logContent = log.getContents(l);
                        Log.Content copyLogContent = copyLog.getContents(l);
                        FastLogContent fastLogContent = fastLog.getContents(l);
                        assertEquals(copyLogContent.getKey(), fastLogContent.getKey());
                        assertEquals(new String(copyLogContent.getKeyBytes().toByteArray()), new String(fastLogContent.getKeyBytes()));
                        assertEquals(copyLogContent.getValue(), fastLogContent.getValue());
                        assertEquals(new String(copyLogContent.getValueBytes().toByteArray()), new String(fastLogContent.getValueBytes()));
                        assertEquals(logContent.getKey(), fastLogContent.getKey());
                        assertEquals(new String(logContent.getKeyBytes().toByteArray()), new String(fastLogContent.getKeyBytes()));
                        assertEquals(logContent.getValue(), fastLogContent.getValue());
                        assertEquals(new String(logContent.getValueBytes().toByteArray()), new String(fastLogContent.getValueBytes()));
                    }
                }
            }
        }
        assertEquals(logGroupCount, vLogGroupCount);
        assertEquals(logCount, vLogCount);
        assertEquals(tagCount, vTagCount);
        assertEquals(contentCount, vContentCount);
        assertEquals(categoryCount, vCategoryCount);
        assertEquals(sourceCount, vSourceCount);
        assertEquals(topicCount, vTopicCount);
        assertEquals(uuidCount, vUuidCount);
    }

    private int BenchPbDeserialize(byte[][] testDataSet, int sampleCount, boolean bytes, int logGroupCount, int logCount, int tagCount, int contentCount) {
        int vLogGroupCount = 0;
        int vLogCount = 0;
        int vContentCount = 0;
        int vTagCount = 0;
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < testDataSet.length; ++i) {
            try {
                LogGroupList logGroupList = LogGroupList.parseFrom(testDataSet[i]);
                for (int j = 0; j < logGroupList.getLogGroupListCount(); ++j) {
                    vLogGroupCount++;
                    LogGroupData logGroupData = new LogGroupData(logGroupList.getLogGroupList(j));
                    LogGroup logGroup = logGroupData.GetLogGroup();
                    if (vLogGroupCount % sampleCount == 0) {
                        if (bytes) {
                            logGroup.getCategoryBytes();
                            logGroup.getTopicBytes();
                            logGroup.getSourceBytes();
                            logGroup.getMachineUUIDBytes();
                        } else {
                            logGroup.getCategoryBytes();
                            logGroup.getTopicBytes();
                            logGroup.getSourceBytes();
                            logGroup.getMachineUUIDBytes();
                        }
                    }
                    for (int k = 0; k < logGroup.getLogTagsCount(); ++k) {
                        vTagCount++;
                        LogTag tag = logGroup.getLogTags(k);
                        if (vLogGroupCount % sampleCount == 0) {
                            if (bytes) {
                                tag.getKeyBytes();
                                tag.getValueBytes();
                            } else {
                                tag.getKey();
                                tag.getValue();
                            }
                        }
                    }
                    for (int k = 0; k < logGroup.getLogsCount(); ++k) {
                        Log log = logGroup.getLogs(k);
                        vLogCount++;
                        log.getTime();
                        for (int l = 0; l < log.getContentsCount(); ++l) {
                            Log.Content logContent = log.getContents(l);
                            vContentCount++;
                            if (vLogGroupCount % sampleCount == 0) {
                                if (bytes) {
                                    logContent.getKeyBytes();
                                    logContent.getValueBytes();
                                } else {
                                    logContent.getKey();
                                    logContent.getValue();
                                }
                            }
                        }
                    }

                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } catch (LogException e) {
                System.err.println((e.getStackTrace()));
            }
        }
        int cost = (int) (System.currentTimeMillis() - beginTime);
        assertEquals(logGroupCount, vLogGroupCount);
        assertEquals(logCount, vLogCount);
        assertEquals(tagCount, vTagCount);
        assertEquals(contentCount, vContentCount);
        return cost;
    }

    private int BenchFastPbDeserialize(byte[][] testDataSet, int sampleCount, boolean bytes, int logGroupCount, int logCount, int tagCount, int contentCount) throws LogException {
        int vLogGroupCount = 0;
        int vLogCount = 0;
        int vContentCount = 0;
        int vTagCount = 0;
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < testDataSet.length; ++i) {
            BatchGetLogResponse bglResponse = new BatchGetLogResponse(new HashMap<String, String>());
            bglResponse.ParseFastLogGroupList(testDataSet[i]);
            List<LogGroupData> logGroups = bglResponse.GetLogGroups();
            for (int j = 0; j < logGroups.size(); ++j) {
                vLogGroupCount++;
                FastLogGroup fastLogGroup = logGroups.get(j).GetFastLogGroup();
                if (vLogGroupCount % sampleCount == 0) {
                    if (bytes) {
                        fastLogGroup.getCategoryBytes();
                        fastLogGroup.getSourceBytes();
                        fastLogGroup.getTopicBytes();
                        fastLogGroup.getMachineUUIDBytes();
                    } else {
                        fastLogGroup.getCategory();
                        fastLogGroup.getSource();
                        fastLogGroup.getTopic();
                        fastLogGroup.getMachineUUID();
                    }
                }
                for (int k = 0; k < fastLogGroup.getLogTagsCount(); ++k) {
                    FastLogTag tag = fastLogGroup.getLogTags(k);
                    vTagCount++;
                    if (vLogGroupCount % sampleCount == 0) {
                        if (bytes) {
                            tag.getKeyBytes();
                            tag.getValueBytes();
                        } else {
                            tag.getKey();
                            tag.getValue();
                        }
                    }
                }
                for (int k = 0; k < fastLogGroup.getLogsCount(); ++k) {
                    FastLog fastLog = fastLogGroup.getLogs(k);
                    fastLog.getTime();
                    vLogCount++;
                    for (int l = 0; l < fastLog.getContentsCount(); ++l) {
                        FastLogContent fastLogContent = fastLog.getContents(l);
                        vContentCount++;
                        if (vLogGroupCount % sampleCount == 0) {
                            if (bytes) {
                                byte[] keyBytes = fastLogContent.getKeyBytes();
                                byte[] valueBytes = fastLogContent.getValueBytes();
                            } else {
                                String key = fastLogContent.getKey();
                                String value = fastLogContent.getValue();
                            }
                        }
                    }
                }
            }
        }
        int cost = (int) (System.currentTimeMillis() - beginTime);
        assertEquals(logGroupCount, vLogGroupCount);
        assertEquals(logCount, vLogCount);
        assertEquals(tagCount, vTagCount);
        assertEquals(contentCount, vContentCount);
        return cost;
    }

    @Test
    public void TestLogGroupListDeserialize() throws LogException, InvalidProtocolBufferException {
        //mock LogGroupList for benchmark
        final int LOGGROUP_LIST_COUNT = 30;
        byte[][] testDataSet = new byte[LOGGROUP_LIST_COUNT][];
        int minTime = (int) (System.currentTimeMillis() / (long) 1000) - 86400;
        int maxTime = minTime + 86400;
        long beginTime = System.currentTimeMillis();
        long testDataBytes = 0;
        int logGroupCount = 0;
        int logCount = 0;
        int contentCount = 0;
        int tagCount = 0;
        int categoryCount = 0;
        int sourceCount = 0;
        int topicCount = 0;
        int uuidCount = 0;
        for (int i = 0; i < LOGGROUP_LIST_COUNT; ++i) {
            LogGroupList.Builder logGroupListBuilder = LogGroupList.newBuilder();
            int LOGGROUP_COUNT = randomInt(30) + 1;
            logGroupCount += LOGGROUP_COUNT;
            for (int j = 0; j < LOGGROUP_COUNT; ++j) {
                LogGroup.Builder logGroupBuilder = LogGroup.newBuilder();
                if (randomInt(3) != 0) {
                    logGroupBuilder.setCategory(randomString(0, 64));
                    categoryCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setTopic(randomString(0, 64));
                    topicCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setSource(randomString(0, 64));
                    sourceCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setMachineUUID(randomString(0, 64));
                    uuidCount++;
                }
                int TAG_COUNT = randomInt(3);
                tagCount += TAG_COUNT;
                for (int k = 0; k < TAG_COUNT; ++k) {
                    LogTag.Builder tagBuilder = LogTag.newBuilder();
                    tagBuilder.setKey(randomString(0, 8));
                    tagBuilder.setValue(randomString(0, 64));
                    logGroupBuilder.addLogTags(tagBuilder.build());
                }
                int LOG_COUNT = randomInt(2000);
                logCount += LOG_COUNT;
                for (int k = 0; k < LOG_COUNT; ++k) {
                    Log.Builder logBuilder = Log.newBuilder();
                    int CONTENT_COUNT = randomInt(30) + 1;
                    contentCount += CONTENT_COUNT;
                    for (int l = 0; l < CONTENT_COUNT; ++l) {
                        Log.Content.Builder contentBuilder = Log.Content.newBuilder();
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
            testDataBytes += testDataSet[i].length;
        }
        long endTime = System.currentTimeMillis();
        double testDataMegaBytes = testDataBytes / 1024.0 / 1024.0;
        System.out.println(String.format("# dataset\n\n## desc\n\n* construct dataset cost: %d millis\n" +
                        "* dataset serialized bytes: %f MB\n* LogGroupList count: %d\n* LogGroup count: %d\n* LogTag count: %d\n" +
                        "* Log count: %d\n* LogContent count: %d\n* Category count: %d\n* Topic count: %d\n* Source count: %d\n* MachineUUID count: %d\n",
                endTime - beginTime, testDataMegaBytes, LOGGROUP_LIST_COUNT, logGroupCount, tagCount, logCount, contentCount,
                categoryCount, topicCount, sourceCount, uuidCount));

        VerifyFastPbDeserialize(testDataSet, logGroupCount, logCount, tagCount, contentCount, categoryCount, sourceCount, topicCount, uuidCount);

        int TEST_ROUND = 5;
        int SAMPLE_COUNT[] = {1, 3, 9};
        ArrayList<String> pbStringResult = new ArrayList<String>();
        ArrayList<String> pbBytesResult = new ArrayList<String>();
        ArrayList<String> fastpbStringResult = new ArrayList<String>();
        ArrayList<String> fastpbBytesResult = new ArrayList<String>();
        for (int sampleId = 0; sampleId < SAMPLE_COUNT.length; ++sampleId) {
            int sampleCount = SAMPLE_COUNT[sampleId];
            for (int caseId = 0; caseId < TEST_ROUND * 4; ++caseId) {
                try {
                    System.gc();
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    System.err.println(e.getStackTrace());
                }
                if (caseId % 4 == 0) {
                    int cost = BenchPbDeserialize(testDataSet, sampleCount, false, logGroupCount, logCount, tagCount, contentCount);
                    pbStringResult.add(String.format("| 1/%d | %d | %s | %d | %f |", sampleCount, pbStringResult.size(), "PB, getString", cost, testDataMegaBytes * 1.0 / cost * 1000));
                } else if (caseId % 4 == 1) {
                    int cost = BenchFastPbDeserialize(testDataSet, sampleCount, false, logGroupCount, logCount, tagCount, contentCount);
                    fastpbStringResult.add(String.format("| 1/%d | %d | %s | %d | %f |", sampleCount, fastpbStringResult.size(), "FastPB, getString", cost, testDataMegaBytes * 1.0 / cost * 1000));
                } else if (caseId % 4 == 2) {
                    int cost = BenchPbDeserialize(testDataSet, sampleCount, true, logGroupCount, logCount, tagCount, contentCount);
                    pbBytesResult.add(String.format("| 1/%d | %d | %s | %d | %f |", sampleCount, pbBytesResult.size(), "PB, getBytes", cost, testDataMegaBytes * 1.0 / cost * 1000));
                } else {
                    int cost = BenchFastPbDeserialize(testDataSet, sampleCount, true, logGroupCount, logCount, tagCount, contentCount);
                    fastpbBytesResult.add(String.format("| 1/%d | %d | %s | %d | %f |", sampleCount, fastpbBytesResult.size(), "FastPB, getBytes", cost, testDataMegaBytes * 1.0 / cost * 1000));
                }
            }
        }
        System.out.println("## benchmark\n\n| getter调用覆盖率 | 测试轮次 | 方法 | cost mills | throughput (MB/s) |\n|---|---|---|---|---|");
        for (String res : pbStringResult) {
            System.out.println(res);
        }
        for (String res : fastpbStringResult) {
            System.out.println(res);
        }
        for (String res : pbBytesResult) {
            System.out.println(res);
        }
        for (String res : fastpbBytesResult) {
            System.out.println(res);
        }
    }


    @Test
    public void TestMockLogGroupListDeserialize() throws LogException, InvalidProtocolBufferException {
        //mock LogGroupList for benchmark
        final int LOGGROUP_LIST_COUNT = 10;
        int minTime = (int) (System.currentTimeMillis() / (long) 1000) - 86400;
        int maxTime = minTime + 86400;
        byte[][] testDataSet = new byte[LOGGROUP_LIST_COUNT][];
        int logGroupCount = 0;
        int logCount = 0;
        int contentCount = 0;
        int tagCount = 0;
        int categoryCount = 0;
        int sourceCount = 0;
        int topicCount = 0;
        int uuidCount = 0;
        for (int i = 0; i < LOGGROUP_LIST_COUNT; ++i) {
            MockLogs.MockLogGroupList.Builder logGroupListBuilder = MockLogs.MockLogGroupList.newBuilder();
            if (randomInt(3) == 0) {
                logGroupListBuilder.setLgl1(randomInt(1000000));
            }
            if (randomInt(3) == 0) {
                logGroupListBuilder.setLgl2(randomString(0, 8));
            }
            int lgl3Count = randomInt(5);
            for (int j = 0; j < lgl3Count; ++j) {
                logGroupListBuilder.addLgl3(randomLong());
            }
            int LOGGROUP_COUNT = randomInt(30) + 1;
            logGroupCount += LOGGROUP_COUNT;
            for (int j = 0; j < LOGGROUP_COUNT; ++j) {
                MockLogs.MockLogGroup.Builder logGroupBuilder = MockLogs.MockLogGroup.newBuilder();
                if (randomInt(3) != 0) {
                    logGroupBuilder.setCategory(randomString(0, 64));
                    categoryCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setTopic(randomString(0, 64));
                    topicCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setSource(randomString(0, 64));
                    sourceCount++;
                }
                if (randomInt(3) != 0) {
                    logGroupBuilder.setMachineUUID(randomString(0, 64));
                    uuidCount++;
                }
                if (randomInt(3) == 0) {
                    logGroupBuilder.setLg1(ByteString.copyFromUtf8(randomString(0, 128)));
                }
                if (randomInt(3) == 0) {
                    logGroupBuilder.setLg2(randomBoolean());
                }
                if (randomInt(3) == 0) {
                    logGroupBuilder.setLg3(RANDOM.nextDouble());
                }
                int TAG_COUNT = randomInt(3);
                tagCount += TAG_COUNT;
                for (int k = 0; k < TAG_COUNT; ++k) {
                    MockLogs.MockLogTag.Builder tagBuilder = MockLogs.MockLogTag.newBuilder();
                    tagBuilder.setKey(randomString(0, 8));
                    tagBuilder.setValue(randomString(0, 64));
                    if (randomInt(3) == 0) {
                        tagBuilder.setT2(ByteString.copyFromUtf8(randomString(0, 128)));
                    }
                    int t2Count = randomInt(5);
                    for (int l = 0; l < t2Count; ++l) {
                        tagBuilder.addT1(RANDOM.nextFloat());
                    }
                    logGroupBuilder.addLogTags(tagBuilder.build());
                }
                int LOG_COUNT = randomInt(2000);
                logCount += LOG_COUNT;
                for (int k = 0; k < LOG_COUNT; ++k) {
                    MockLogs.MockLog.Builder logBuilder = MockLogs.MockLog.newBuilder();
                    if (randomInt(3) == 0) {
                        logBuilder.setL1(RANDOM.nextDouble());
                    }
                    int CONTENT_COUNT = randomInt(30) + 1;
                    contentCount += CONTENT_COUNT;
                    for (int l = 0; l < CONTENT_COUNT; ++l) {
                        MockLogs.MockLog.MockContent.Builder contentBuilder = MockLogs.MockLog.MockContent.newBuilder();
                        contentBuilder.setKey(randomString(0, 8));
                        contentBuilder.setValue(randomString(0, 128));
                        if (randomInt(3) == 0) {
                            contentBuilder.setC1(randomString(0, 128));
                        }
                        if (randomInt(3) == 0) {
                            contentBuilder.setC2(randomInt());
                        }
                        if (randomInt(3) == 0) {
                            contentBuilder.setC3(randomLong());
                        }
                        logBuilder.addContents(contentBuilder.build());
                    }
                    logBuilder.setTime(randomBetween(minTime, maxTime));
                    logGroupBuilder.addLogs(logBuilder.build());
                }
                logGroupListBuilder.addLogGroupList(logGroupBuilder.build());
            }
            testDataSet[i] = logGroupListBuilder.build().toByteArray();
        }
        System.out.println(String.format("# TestMockLogGroupListDeserialize dataset\n\n## desc\n\n" +
                        "* LogGroupList count: %d\n* LogGroup count: %d\n* LogTag count: %d\n" +
                        "* Log count: %d\n* LogContent count: %d\n* Category count: %d\n* Topic count: %d\n* Source count: %d\n* MachineUUID count: %d\n",
                LOGGROUP_LIST_COUNT, logGroupCount, tagCount, logCount, contentCount,
                categoryCount, topicCount, sourceCount, uuidCount));

        VerifyFastPbDeserialize(testDataSet, logGroupCount, logCount, tagCount, contentCount, categoryCount, sourceCount, topicCount, uuidCount);
    }

    public void TestPutData(int logLine, String topic) throws LogException {
        Vector<LogItem> logGroup = new Vector<LogItem>();
        for (int i = 0; i < logLine; i++) {
            LogItem logItem = new LogItem(fromTime);
            for (int j = 0; j < logItemLine; ++j) {
                logItem.PushBack("ID", "id_" + String.valueOf(i * logItemLine + j));
            }
            logGroup.add(logItem);
        }
        double maxTime = 0;
        double minTime = 100000;
        double totalTime = 0;

        for (int i = 0; i < runTimes; ++i) {
            double st = new Date().getTime() / 1000.0;
            PutLogsResponse response = client.PutLogs(project, logStore, topic, logGroup, "");
            double last = new Date().getTime() / 1000.0 - st;

            if (last > lastMaxTime) {
                System.out.println("last time: " + last + ", request id: " + response.GetRequestId());
            }

            maxTime = Math.max(maxTime, last);
            minTime = Math.min(minTime, last);
            totalTime += last;
        }
        System.out.println("TestPutData: ");
        System.out.println("max time: " + maxTime);
        System.out.println("min time: " + minTime);
        System.out.println("ave time: " + totalTime / runTimes);
    }


    public void TestGetLogStore() {
        try {
            double maxTime = 0;
            double minTime = 100000;
            double totalTime = 0;

            for (int i = 0; i < runTimes; ++i) {
                double st = new Date().getTime() / 1000.0;
                ListLogStoresResponse response = client.ListLogStores(project, 0, 500, "");
                double last = new Date().getTime() / 1000.0 - st;

                if (last > lastMaxTime) {
                    System.out.println("last time: " + last + ", request id: " + response.GetRequestId());
                }

                maxTime = Math.max(maxTime, last);
                minTime = Math.min(minTime, last);
                totalTime += last;
            }
            System.out.println("TestGetLogStore: ");
            System.out.println("max time: " + maxTime);
            System.out.println("min time: " + minTime);
            System.out.println("ave time: " + totalTime / runTimes);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode() + ":" + e.GetErrorMessage());
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }


    public double TestListTopics(String topic) {
        try {
            double maxTime = 0;
            double minTime = 100000;
            double totalTime = 0;

            for (int i = 0; i < runTimes; ++i) {
                String token = topic;
                double st = new Date().getTime() / 1000.0;
                ListTopicsResponse response = client.ListTopics(project, logStore, token, 100);
                double last = new Date().getTime() / 1000.0 - st;

                if (last > lastMaxTime) {
                    System.out.println("last time: " + last + ", request id: " + response.GetRequestId());
                }

                maxTime = Math.max(maxTime, last);
                minTime = Math.min(minTime, last);
                totalTime += last;
            }
            System.out.println("TestListTopics: ");
            System.out.println("max time: " + maxTime);
            System.out.println("min time: " + minTime);
            System.out.println("ave time: " + totalTime / runTimes);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode() + ":" + e.GetErrorMessage());
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
        return -1;
    }


    public double TestGetHistogram(String topic) {
        try {
            double maxTime = 0;
            double minTime = 100000;
            double totalTime = 0;
            int times = 0;

            for (int i = 0; i < runTimes; ++i) {
                double st = new Date().getTime() / 1000.0;
                GetHistogramsResponse response = client.GetHistograms(project, logStore, fromTime, toTime, topic, "ID");
                double last = new Date().getTime() / 1000.0 - st;

                if (last > lastMaxTime) {
                    System.out.println("last time: " + last + ", request id: " + response.GetRequestId());
                }

                if (response.GetTotalCount() == 0) {
                    break;
                }

                maxTime = Math.max(maxTime, last);
                minTime = Math.min(minTime, last);
                totalTime += last;

                times = i;
            }

            System.out.println("TestGetHistogram: ");
            System.out.println("max time: " + maxTime);
            System.out.println("min time: " + minTime);
            System.out.println("ave time: " + totalTime / times);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode() + ":" + e.GetErrorMessage());
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
        return -1;
    }


    public double TestGetLogs(String topic) {
        try {
            double maxTime = 0;
            double minTime = 100000;
            double totalTime = 0;

            for (int i = 0; i < runTimes; ++i) {
                double st = new Date().getTime() / 1000.0;
                GetLogsResponse response = client.GetLogs(project, logStore,
                        fromTime, toTime, topic, "ID", 100, 0, false);
                double last = new Date().getTime() / 1000.0 - st;

                if (last > lastMaxTime) {
                    System.out.println("last time: " + last + ", request id: " + response.GetRequestId());
                }

                maxTime = Math.max(maxTime, last);
                minTime = Math.min(minTime, last);
                totalTime += last;
            }
            System.out.println("TestGetLogs: ");
            System.out.println("max time: " + maxTime);
            System.out.println("min time: " + minTime);
            System.out.println("ave time: " + totalTime / runTimes);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode() + ":" + e.GetErrorMessage());
            assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
        }
        return -1;
    }

    @Test
    public void TestAll() throws LogException, InvalidProtocolBufferException {

        /*
        int logLineArray[] = {100, 500, 1000, 2000, 4000};
        for (int logLine : logLineArray) {
            System.out.println("Case size: "+logLine);
            fromTime = (int) (new Date().getTime() / 1000);
            toTime = fromTime + 100;
            
            String topic = "sls_java_topic_" + String.valueOf(logLine) + "_" + 
                    String.valueOf(fromTime);
            
            try {
                Thread.sleep(30 * 1000);
            }catch(Exception ex) {
            }
            
            TestPutData(logLine, topic);
            TestGetLogStore();
            TestListTopics(topic);
            TestGetHistogram(topic);
            TestGetLogs(topic);
        }
        */
    }
}

