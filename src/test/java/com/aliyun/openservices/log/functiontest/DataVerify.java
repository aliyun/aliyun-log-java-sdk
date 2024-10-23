package com.aliyun.openservices.log.functiontest;

import org.junit.Before;

import com.aliyun.openservices.log.common.LogItem;

public class DataVerify extends MetaAPIBaseFunctionTest {

    @Before
    @Override
    public void setUp() {
        super.setUp();
        waitForSeconds(60);
    }

    static protected boolean VerifyLogItem(LogItem logItem, LogItem logItemSample) {
        boolean ret = false;

        do {
            if (logItem.GetTime() != logItemSample.GetTime()) {
                break;
            } else {
                System.out.println("Time:" + logItem.GetTime());
                System.out.println("Time:" + logItemSample.GetTime());
            }

            if (logItem.GetLogContents().size() != logItemSample.GetLogContents().size()) {
                break;
            } else {
                System.out.println("Size:" + logItem.GetLogContents().size());
                System.out.println("Size:" + logItemSample.GetLogContents().size());
            }

            boolean fail = false;
            for (int i = 0; i < logItem.GetLogContents().size(); i++) {
                String key = logItem.GetLogContents().get(i).GetKey();
                String value = logItem.GetLogContents().get(i).GetValue();
                boolean hasKey = false;
                boolean hasValue = false;
                for (int j = 0; j < logItemSample.GetLogContents().size(); j++) {
                    if (key.equals(logItemSample.GetLogContents().get(j).GetKey())) {
                        hasKey = true;
                        if (value.equals(logItemSample.GetLogContents().get(j).GetValue())) {
                            hasValue = true;
                            /*
							System.out.println("Key:" + key);
							System.out.println("Key:" + logItemSample.GetLogContents().get(j).GetKey());
							System.out.println("Value:" + value);
							System.out.println("Value:" + logItemSample.GetLogContents().get(j).GetValue());
							*/
                            break;
                        } else {
                            break;
                        }
                    }
                }

                if (!hasKey || !hasValue) {
                    fail = true;
                }

                if (fail) {
                    break;
                }
            }

            if (fail) {
                break;
            }

            ret = true;
        } while (false);

        return ret;
    }
}
