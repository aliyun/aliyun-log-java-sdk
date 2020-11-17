package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Domain;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListDomainsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DomainTest extends FunctionTest {
    private static final String TEST_PROJECT = "test-domain-project-" + getNowTimestamp();

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "test domain");
        waitForSeconds(10);
    }

    @After
    public void afterTest() {
        safeDeleteProject(TEST_PROJECT);
    }

    @Test
    public void testCRUD() {
        //delete
        //Unauthorized
        //this api does not support sub user or role
        try {
            client.deleteDomain(TEST_PROJECT, "test-domain");
        } catch (LogException e) {
            System.err.println(e.GetErrorCode());
            System.err.println(e.GetErrorMessage());
        }
        //create
        try {
            client.createDomain(TEST_PROJECT, new Domain("test-domain"));
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        try {
            client.createDomain(TEST_PROJECT, new Domain("test-domain"));
        } catch (LogException e) {
            assertEquals("ExceedQuota", e.GetErrorCode());
            assertEquals("domain quota exceed", e.GetErrorMessage());
        }
        //get no get
        //update no update
        //list
        try {
            ListDomainsResponse domains = client.listDomains(TEST_PROJECT, "", 0, 10);
            assertEquals(1, domains.getCount());
            assertEquals(1, domains.getTotal());
            assertEquals("test-domain", domains.getDomains().get(0).getDomainName());
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
        try {
            client.deleteDomain(TEST_PROJECT, "test-domain");
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
    }
}
