package com.aliyun.openservices.log.internal.json;

import static org.junit.Assert.assertEquals;

/**
 * Semantic JSON comparison for tests: key order is irrelevant, values must
 * match exactly.
 */
public final class JsonAsserts {

    private JsonAsserts() {
    }

    public static void assertJsonEquals(String expected, String actual) {
        assertEquals("expected=" + expected + " actual=" + actual,
                JSON.parse(expected), JSON.parse(actual));
    }
}
