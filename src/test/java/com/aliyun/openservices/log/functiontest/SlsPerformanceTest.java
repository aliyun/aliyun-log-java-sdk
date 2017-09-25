package com.aliyun.openservices.log.functiontest;

import java.util.*;

import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.response.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.common.Logs.*;

public class SlsPerformanceTest {

    private String accessId = "x";
    private String accessKey = "x";

    private String project = "ali-cn-yunlei-sls-admin";
    private String host = "x";
    private String logStore = "data-ft";

    private int logItemLine = 30;
    private int runTimes = 100;
    private int lastMaxTime = 5;

    private int fromTime;
    private int toTime;
    private Random rand = new Random();

    private Client client = new Client(host, accessId, accessKey);

    public SlsPerformanceTest() {
    }

    @BeforeClass
    public static void SetupOnce() {
    }

    @AfterClass
    public static void CleanUpOnce() {
    }

    private String RandomString(int minLength, int maxLength) {
        int length = minLength + this.rand.nextInt(maxLength - minLength);
        String randString = new String();
        int i = 0;
        while (i < length) {
            randString += Character.toString((char) (33 + this.rand.nextInt(126 - 33)));
            i++;
        }
        return " " + randString;
    }

    private int RandomInt(int min, int max) {
        return min + this.rand.nextInt(max - min);
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
                    assertEquals(fastLogGroup.getCategory(), null);
                    assertEquals(fastLogGroup.getCategoryBytes(), null);
                }
                if (logGroup.hasTopic()) {
                    vTopicCount++;
                    assertEquals(copyLogGroup.getTopic(), fastLogGroup.getTopic());
                    assertEquals(new String(copyLogGroup.getTopicBytes().toByteArray()), new String(fastLogGroup.getTopicBytes()));
                    assertEquals(logGroup.getTopic(), fastLogGroup.getTopic());
                    assertEquals(new String(logGroup.getTopicBytes().toByteArray()), new String(fastLogGroup.getTopicBytes()));
                } else {
                    assertEquals(fastLogGroup.getTopic(), null);
                    assertEquals(fastLogGroup.getTopicBytes(), null);
                }
                if (logGroup.hasSource()) {
                    vSourceCount++;
                    assertEquals(copyLogGroup.getSource(), fastLogGroup.getSource());
                    assertEquals(new String(copyLogGroup.getSourceBytes().toByteArray()), new String(fastLogGroup.getSourceBytes()));
                    assertEquals(logGroup.getSource(), fastLogGroup.getSource());
                    assertEquals(new String(logGroup.getSourceBytes().toByteArray()), new String(fastLogGroup.getSourceBytes()));
                } else {
                    assertEquals(fastLogGroup.getSource(), null);
                    assertEquals(fastLogGroup.getSourceBytes(), null);
                }
                if (logGroup.hasMachineUUID()) {
                    vUuidCount++;
                    assertEquals(copyLogGroup.getMachineUUID(), fastLogGroup.getMachineUUID());
                    assertEquals(new String(copyLogGroup.getMachineUUIDBytes().toByteArray()), new String(fastLogGroup.getMachineUUIDBytes()));
                    assertEquals(logGroup.getMachineUUID(), fastLogGroup.getMachineUUID());
                    assertEquals(new String(logGroup.getMachineUUIDBytes().toByteArray()), new String(fastLogGroup.getMachineUUIDBytes()));
                } else {
                    assertEquals(fastLogGroup.getMachineUUID(),null);
                    assertEquals(fastLogGroup.getMachineUUIDBytes(),null);
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
                System.err.println(e.getStackTrace());
            } catch (LogException e) {
                System.err.println((e.getStackTrace()));
            }
        }
        int cost = (int)(System.currentTimeMillis() - beginTime);
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
                        if (vLogGroupCount% sampleCount == 0) {
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
        int cost = (int)(System.currentTimeMillis() - beginTime);
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
            int LOGGROUP_COUNT = this.rand.nextInt(30) + 1;
            logGroupCount += LOGGROUP_COUNT;
            for (int j = 0; j < LOGGROUP_COUNT; ++j) {
                LogGroup.Builder logGroupBuilder = LogGroup.newBuilder();
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setCategory(RandomString(0, 64));
                    categoryCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setTopic(RandomString(0, 64));
                    topicCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setSource(RandomString(0, 64));
                    sourceCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setMachineUUID(RandomString(0, 64));
                    uuidCount++;
                }
                int TAG_COUNT = this.rand.nextInt(3);
                tagCount += TAG_COUNT;
                for (int k = 0; k < TAG_COUNT; ++k) {
                    LogTag.Builder tagBuilder = LogTag.newBuilder();
                    tagBuilder.setKey(RandomString(0, 8));
                    tagBuilder.setValue(RandomString(0, 64));
                    logGroupBuilder.addLogTags(tagBuilder.build());
                }
                int LOG_COUNT = this.rand.nextInt(2000);
                logCount += LOG_COUNT;
                for (int k = 0; k < LOG_COUNT; ++k) {
                    Log.Builder logBuilder = Log.newBuilder();
                    int CONTENT_COUNT = this.rand.nextInt(30) + 1;
                    contentCount += CONTENT_COUNT;
                    for (int l = 0; l < CONTENT_COUNT; ++l) {
                        Log.Content.Builder contentBuilder = Log.Content.newBuilder();
                        contentBuilder.setKey(RandomString(0, 8));
                        contentBuilder.setValue(RandomString(0, 128));
                        logBuilder.addContents(contentBuilder.build());
                    }
                    logBuilder.setTime(RandomInt(minTime, maxTime));
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
            if (this.rand.nextInt(3) == 0) {
                logGroupListBuilder.setLgl1(this.rand.nextInt(1000000));
            }
            if (this.rand.nextInt(3) == 0) {
                logGroupListBuilder.setLgl2(RandomString(0, 8));
            }
            int lgl3Count = this.rand.nextInt(5);
            for (int j = 0; j < lgl3Count; ++j) {
                logGroupListBuilder.addLgl3(this.rand.nextLong());
            }
            int LOGGROUP_COUNT = this.rand.nextInt(30) + 1;
            logGroupCount += LOGGROUP_COUNT;
            for (int j = 0; j < LOGGROUP_COUNT; ++j) {
                MockLogs.MockLogGroup.Builder logGroupBuilder = MockLogs.MockLogGroup.newBuilder();
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setCategory(RandomString(0, 64));
                    categoryCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setTopic(RandomString(0, 64));
                    topicCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setSource(RandomString(0, 64));
                    sourceCount++;
                }
                if (this.rand.nextInt(3) != 0) {
                    logGroupBuilder.setMachineUUID(RandomString(0, 64));
                    uuidCount++;
                }
                if (this.rand.nextInt(3) == 0) {
                    logGroupBuilder.setLg1(ByteString.copyFromUtf8(RandomString(0, 128)));
                }
                if (this.rand.nextInt(3) == 0) {
                    logGroupBuilder.setLg2(this.rand.nextBoolean());
                }
                if (this.rand.nextInt(3) == 0) {
                    logGroupBuilder.setLg3(this.rand.nextDouble());
                }
                int TAG_COUNT = this.rand.nextInt(3);
                tagCount += TAG_COUNT;
                for (int k = 0; k < TAG_COUNT; ++k) {
                    MockLogs.MockLogTag.Builder tagBuilder = MockLogs.MockLogTag.newBuilder();
                    tagBuilder.setKey(RandomString(0, 8));
                    tagBuilder.setValue(RandomString(0, 64));
                    if (this.rand.nextInt(3) == 0) {
                        tagBuilder.setT2(ByteString.copyFromUtf8(RandomString(0, 128)));
                    }
                    int t2Count = this.rand.nextInt(5);
                    for (int l = 0; l < t2Count; ++l) {
                        tagBuilder.addT1(this.rand.nextFloat());
                    }
                    logGroupBuilder.addLogTags(tagBuilder.build());
                }
                int LOG_COUNT = this.rand.nextInt(2000);
                logCount += LOG_COUNT;
                for (int k = 0; k < LOG_COUNT; ++k) {
                    MockLogs.MockLog.Builder logBuilder = MockLogs.MockLog.newBuilder();
                    if (this.rand.nextInt(3) == 0) {
                        logBuilder.setL1(this.rand.nextDouble());
                    }
                    int CONTENT_COUNT = this.rand.nextInt(30) + 1;
                    contentCount += CONTENT_COUNT;
                    for (int l = 0; l < CONTENT_COUNT; ++l) {
                        MockLogs.MockLog.MockContent.Builder contentBuilder = MockLogs.MockLog.MockContent.newBuilder();
                        contentBuilder.setKey(RandomString(0, 8));
                        contentBuilder.setValue(RandomString(0, 128));
                        if (this.rand.nextInt(3) == 0) {
                            contentBuilder.setC1(RandomString(0, 128));
                        }
                        if (this.rand.nextInt(3) == 0) {
                            contentBuilder.setC2(this.rand.nextInt());
                        }
                        if (this.rand.nextInt(3) == 0) {
                            contentBuilder.setC3(this.rand.nextLong());
                        }
                        logBuilder.addContents(contentBuilder.build());
                    }
                    logBuilder.setTime(RandomInt(minTime, maxTime));
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
            assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
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
            assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
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
            assertTrue(e.GetErrorCode() + ":" + e.GetErrorMessage(), false);
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

