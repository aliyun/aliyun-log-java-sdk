package com.aliyun.openservices.log.http.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.junit.Test;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class HttpFactoryTest {

    @Test
    public void testCreateHttpClient() {
        ClientConfiguration config = new ClientConfiguration();

        HttpFactory factory = new HttpFactory();
        HttpClient httpClient = factory.createHttpClient(config);

        // Should be thread-safed.
        ClientConnectionManager connMgr = httpClient.getConnectionManager();
        assertTrue(connMgr instanceof ThreadSafeClientConnManager);
    }

    @Test
    public void testCreateHttpRequest() throws Exception {
        String charsetString = Consts.UTF_8_ENCODING;
        String url = "http://127.0.0.1";
        String content = "This is a test request";
        byte[] contentBytes = null;
        try {
            contentBytes = content.getBytes(charsetString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        String contentType = "application/x-www-form-urlencoded; " +
                "charset=" + charsetString.toLowerCase();

        HttpFactory factory = new HttpFactory();

        ServiceClient.Request request = new ServiceClient.Request();
        request.setUrl(url);

        HttpRequestBase httpRequest = null;

        // GET
        request.setMethod(HttpMethod.GET);
        httpRequest = factory.createHttpRequest(request, charsetString);
        HttpGet getMethod = (HttpGet)httpRequest;
        assertEquals(url, getMethod.getURI().toString());
        assertEquals(contentType, getMethod.getFirstHeader("Content-Type").getValue());

        // DELETE
        request.setMethod(HttpMethod.DELETE);
        httpRequest = factory.createHttpRequest(request, charsetString);
        HttpDelete delMethod = (HttpDelete)httpRequest;
        assertEquals(url, delMethod.getURI().toString());

        // HEAD
        request.setMethod(HttpMethod.HEAD);
        httpRequest = factory.createHttpRequest(request, charsetString);
        HttpHead headMethod = (HttpHead)httpRequest;
        assertEquals(url, headMethod.getURI().toString());

        //POST
        request.setContent(new ByteArrayInputStream(contentBytes));
        request.setContentLength(contentBytes.length);
        request.setMethod(HttpMethod.POST);
        httpRequest = factory.createHttpRequest(request, charsetString);
        HttpPost postMethod = (HttpPost)httpRequest;

        assertEquals(url, postMethod.getURI().toString());
        HttpEntity entity = postMethod.getEntity();

        try {
            assertEquals(content, readSting(entity.getContent()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        //PUT
        request.setContent(new ByteArrayInputStream(contentBytes));
        request.setContentLength(contentBytes.length);
        request.setMethod(HttpMethod.PUT);
        httpRequest = factory.createHttpRequest(request, charsetString);
        HttpPut putMethod = (HttpPut)httpRequest;

        assertEquals(url, putMethod.getURI().toString());
        entity = putMethod.getEntity();
        try {
            assertEquals(content, readSting(entity.getContent()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        try {
            request.close();
        } catch (IOException e) { }
    }

    private String readSting(InputStream input){
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
        finally{
            try {
                br.close();
                reader.close();
            } catch (IOException e) {
            }
        }
    }
}
