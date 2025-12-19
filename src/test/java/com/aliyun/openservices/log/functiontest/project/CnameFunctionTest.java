package com.aliyun.openservices.log.functiontest.project;

import com.aliyun.openservices.log.IOUtils;
import com.aliyun.openservices.log.ResourceUtils;
import com.aliyun.openservices.log.common.CertificateConfiguration;
import com.aliyun.openservices.log.common.CnameConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.request.SetProjectCnameRequest;
import com.aliyun.openservices.log.response.ListProjectCnameResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CnameFunctionTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testPutCname() throws Exception {
        String cname = "example.domain.com";
        SetProjectCnameRequest request = new SetProjectCnameRequest(TEST_PROJECT, cname);
        client.setProjectCname(request);

        ListProjectCnameResponse response = client.listProjectCname(TEST_PROJECT);
        List<CnameConfiguration> list = response.getCnameConfigurations();
        Assert.assertEquals(1, list.size());
        CnameConfiguration cnameConfiguration = list.get(0);
        Assert.assertEquals(cname, cnameConfiguration.getDomain());
        Assert.assertEquals(CnameConfiguration.CnameStatus.Enabled, cnameConfiguration.getStatus());
        Assert.assertNull(cnameConfiguration.getCertificate());

        String anotherProject = makeProjectName();
        SetProjectCnameRequest request12 = new SetProjectCnameRequest(anotherProject, cname);
        client.CreateProject(anotherProject, "");
        try {
            try {
                client.setProjectCname(request12);
                Assert.fail();
            } catch (LogException ex) {
                Assert.assertEquals(400, ex.getHttpCode());
                Assert.assertEquals("CnameAlreadyExists", ex.GetErrorCode());
                Assert.assertEquals("The cname has been attached to your other project already.", ex.GetErrorMessage());
            }
        } finally {
            client.DeleteProject(anotherProject);
        }

        client.deleteProjectCname(TEST_PROJECT, cname);
        try {
            client.deleteProjectCname(TEST_PROJECT, cname);
        } catch (LogException ex) {
            Assert.assertEquals(404, ex.getHttpCode());
            Assert.assertEquals("NoSuchCname", ex.GetErrorCode());
            Assert.assertEquals("No Cname found in Cname Table.", ex.GetErrorMessage());
        }

        response = client.listProjectCname(TEST_PROJECT);
        list = response.getCnameConfigurations();
        Assert.assertEquals(0, list.size());

        // set certificate
        String pubKey = IOUtils.readStreamAsString(ResourceUtils.getTestInputStream("testputcname.com.crt"), "utf8");
        String priKey = IOUtils.readStreamAsString(ResourceUtils.getTestInputStream("testputcname.com.key"), "utf8");

        SetProjectCnameRequest request1 = new SetProjectCnameRequest(TEST_PROJECT, cname);
        request1.setCertificateConfiguration(new CertificateConfiguration()
                .withPublicKey(pubKey)
                .withPrivateKey(priKey));
        client.setProjectCname(request1);

        response = client.listProjectCname(TEST_PROJECT);
        list = response.getCnameConfigurations();
        Assert.assertEquals(1, list.size());
        cnameConfiguration = list.get(0);
        Assert.assertEquals(cname, cnameConfiguration.getDomain());
        Assert.assertEquals(CnameConfiguration.CnameStatus.Enabled, cnameConfiguration.getStatus());
        CnameConfiguration.Certificate certificate = cnameConfiguration.getCertificate();
        Assert.assertEquals(certificate.getValidEndDate(), "Jan 19 01:54:49 2024 GMT");
        Assert.assertEquals(certificate.getValidStartDate(), "Jan 19 01:54:49 2023 GMT");
        Assert.assertEquals(certificate.getStatus(), CnameConfiguration.CertStatus.Enabled);
        Assert.assertEquals(certificate.getFingerprint(), "5F:EB:A4:72:46:5A:19:AF:E1:35:A2:50:92:3C:55:84:82:4A:09:06");

        // certificate not attached
        SetProjectCnameRequest addProjectCnameRequest = new SetProjectCnameRequest(TEST_PROJECT, cname);
        client.setProjectCname(addProjectCnameRequest);
        response = client.listProjectCname(TEST_PROJECT);
        list = response.getCnameConfigurations();
        Assert.assertEquals(1, list.size());
        cnameConfiguration = list.get(0);
        Assert.assertEquals(cname, cnameConfiguration.getDomain());
        Assert.assertEquals(CnameConfiguration.CnameStatus.Enabled, cnameConfiguration.getStatus());
        certificate = cnameConfiguration.getCertificate();
        Assert.assertEquals(certificate.getValidEndDate(), "Jan 19 01:54:49 2024 GMT");
        Assert.assertEquals(certificate.getValidStartDate(), "Jan 19 01:54:49 2023 GMT");
        Assert.assertEquals(certificate.getStatus(), CnameConfiguration.CertStatus.Enabled);
        Assert.assertEquals(certificate.getFingerprint(), "5F:EB:A4:72:46:5A:19:AF:E1:35:A2:50:92:3C:55:84:82:4A:09:06");

        // certificate deleted
        SetProjectCnameRequest request2 = new SetProjectCnameRequest(TEST_PROJECT, cname);
        request2.setCertificateConfiguration(new CertificateConfiguration()
                .withDeleteCertificate(true));
        client.setProjectCname(request2);
        response = client.listProjectCname(TEST_PROJECT);
        list = response.getCnameConfigurations();
        Assert.assertEquals(1, list.size());
        cnameConfiguration = list.get(0);
        Assert.assertEquals(cname, cnameConfiguration.getDomain());
        Assert.assertEquals(CnameConfiguration.CnameStatus.Enabled, cnameConfiguration.getStatus());
        certificate = cnameConfiguration.getCertificate();
        Assert.assertNull(certificate);

        client.deleteProjectCname(TEST_PROJECT, cname);
        response = client.listProjectCname(TEST_PROJECT);
        list = response.getCnameConfigurations();
        Assert.assertEquals(0, list.size());
    }
}
