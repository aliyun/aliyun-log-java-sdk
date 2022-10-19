package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Domain;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListDomainsResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DomainTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testCRUD() throws LogException {
        //create
        client.createDomain(TEST_PROJECT, new Domain("test-domain"));
        try {
            client.createDomain(TEST_PROJECT, new Domain("test-domain"));
            fail();
        } catch (LogException e) {
            assertEquals("ExceedQuota", e.GetErrorCode());
            assertEquals("domain quota exceed", e.GetErrorMessage());
        }
        ListDomainsResponse domains = client.listDomains(TEST_PROJECT, "", 0, 10);
        assertEquals(1, domains.getCount());
        assertEquals(1, domains.getTotal());
        assertEquals("test-domain", domains.getDomains().get(0).getDomainName());
        client.deleteDomain(TEST_PROJECT, "test-domain");
    }
}
