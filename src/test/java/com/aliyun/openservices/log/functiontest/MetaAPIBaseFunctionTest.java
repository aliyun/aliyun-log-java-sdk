package com.aliyun.openservices.log.functiontest;

import org.junit.After;
import org.junit.Before;

public class MetaAPIBaseFunctionTest extends FunctionTest {

    protected static final String TEST_PROJECT = makeProjectName();

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "SDK intg test");
    }

    @After
    public void clearDown() {
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
    }

}
