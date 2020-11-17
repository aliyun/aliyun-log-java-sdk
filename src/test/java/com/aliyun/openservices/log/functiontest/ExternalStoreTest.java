package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.ExternalStore;
import com.aliyun.openservices.log.common.Parameter;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateExternalStoreRequest;
import com.aliyun.openservices.log.request.DeleteExternalStoreRequest;
import com.aliyun.openservices.log.request.GetExternalStoreRequest;
import com.aliyun.openservices.log.request.ListExternalStoresRequest;
import com.aliyun.openservices.log.request.UpdateExternalStoreRequest;
import com.aliyun.openservices.log.response.GetExternalStoreResponse;
import com.aliyun.openservices.log.response.ListExternalStroesResponse;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExternalStoreTest extends FunctionTest {
    private static final String project = "test-java-sdk-external-store-" + getNowTimestamp();

    @BeforeClass
    public static void setUp() {
        safeCreateProject(project, "");
    }

    @AfterClass
    public static void tearDown() {
        safeDeleteProject(project);
    }

    @Test
    public void testRdsVpc() throws LogException {
        //create
        Parameter parameter = new Parameter();
        //下面参数都可以不要，会用空字符替代
        parameter.setHost("test-host");
        parameter.setPort("test-port");
        parameter.setTable("test-table");
        parameter.setUsername("test-user");
        ExternalStore externalStore = new ExternalStore("name-rds-vpc", "rds-vpc", parameter);
        CreateExternalStoreRequest createRequest = new CreateExternalStoreRequest(project, externalStore);
        client.createExternalStore(createRequest);

        try {
            client.createExternalStore(createRequest);
            fail("Already exists");
        } catch (LogException ex) {
            assertEquals("ParameterInvalid", ex.GetErrorCode());
            assertEquals(400, ex.GetHttpCode());
        }

        //get
        GetExternalStoreRequest getRequest1 = new GetExternalStoreRequest(project, "name-rds-vpc");
        GetExternalStoreResponse getResponse1 = client.getExternalStore(getRequest1);
        assertEquals(getResponse1.getExternalStore().getParameter().getHost(), "test-host");

        //update
        Parameter parameter2 = new Parameter();
        //下面参数都可以不要，会用空字符替代
        parameter2.setHost("test-host-2");
        parameter2.setPort("test-port-2");
        parameter2.setTable("test-table-2");
        parameter2.setUsername("test-user-2");
        ExternalStore externalStore2 = new ExternalStore("name-rds-vpc", "rds-vpc", parameter2);
        UpdateExternalStoreRequest updateRequest = new UpdateExternalStoreRequest(project, externalStore2);
        client.updateExternalStore(updateRequest);

        //get
        GetExternalStoreRequest getRequest2 = new GetExternalStoreRequest(project, "name-rds-vpc");
        GetExternalStoreResponse getResponse2 = client.getExternalStore(getRequest2);
        assertEquals(getResponse2.getExternalStore().getParameter().getHost(), "test-host-2");

        //list
        waitForSeconds(10);
//        ListExternalStoresRequest listRequest = new ListExternalStoresRequest(project, null, 0, 10);//pattern 支持模糊查询，为null/""代表查询所有
        ListExternalStoresRequest listRequest = new ListExternalStoresRequest(project, "vpc", 0, 10);
        ListExternalStroesResponse listResponse = client.listExternalStores(listRequest);
        assertTrue(listResponse.getExternalStores().contains("name-rds-vpc"));

        //delete
        DeleteExternalStoreRequest deleteRequest = new DeleteExternalStoreRequest(project, "name-rds-vpc");
        client.deleteExternalStore(deleteRequest);
    }

    @Test
    public void testOSS() throws LogException {
        //create
        Parameter parameter = new Parameter();
        //以下四个参数，只要不是null都可以创建
        parameter.setEndpoint("123");
        parameter.setAccessid("456");
        parameter.setAccesskey("789");
        parameter.setBucket("0");
        ExternalStore externalStore = new ExternalStore("name-oss", "oss", parameter);
        CreateExternalStoreRequest createRequest = new CreateExternalStoreRequest(project, externalStore);
        client.createExternalStore(createRequest);

        try {
            client.createExternalStore(createRequest);
            fail("Already exists");
        } catch (LogException ex) {
            assertEquals("ParameterInvalid", ex.GetErrorCode());
            assertEquals(400, ex.GetHttpCode());
        }

        //get
//        注意，此处会返回创建者的accessid和accesskey
        GetExternalStoreRequest getRequest1 = new GetExternalStoreRequest(project, "name-oss");
        GetExternalStoreResponse getResponse1 = client.getExternalStore(getRequest1);   //LogException: Internal Server Error
        assertEquals(getResponse1.getExternalStore().getParameter().getEndpoint(), "123");

        //update
        Parameter parameter2 = new Parameter();
        parameter2.setEndpoint("321");
        parameter2.setAccessid("654");
        parameter2.setAccesskey("987");
        parameter2.setBucket("1");
        ExternalStore externalStore2 = new ExternalStore("name-oss", "oss", parameter2);
        UpdateExternalStoreRequest updateRequest = new UpdateExternalStoreRequest(project, externalStore2);
        client.updateExternalStore(updateRequest);

        //get
        GetExternalStoreRequest getRequest2 = new GetExternalStoreRequest(project, "name-oss");
        GetExternalStoreResponse getResponse2 = client.getExternalStore(getRequest2);
        assertEquals(getResponse2.getExternalStore().getParameter().getEndpoint(), "321");

        waitForSeconds(10); //need to wait to get value
        //list
        ListExternalStoresRequest listRequest = new ListExternalStoresRequest(project, "", 0, 10);
        ListExternalStroesResponse response = client.listExternalStores(listRequest);
        assertTrue(response.getExternalStores().contains("name-oss"));

        //delete
        DeleteExternalStoreRequest deleteRequest = new DeleteExternalStoreRequest(project, "name-oss");
        client.deleteExternalStore(deleteRequest);
        waitForSeconds(5);
        client.createExternalStore(createRequest);
        client.deleteExternalStore(deleteRequest);
    }
}
