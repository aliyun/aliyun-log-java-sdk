package com.aliyun.openservices.log.http.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.aliyun.openservices.log.http.utils.CaseInsensitiveMap;

/**
 * The base class for message of HTTP request and response.
 * @author xiaoming.yin
 *
 */
public abstract class HttpMessage {
	
	private Map<String, String> headers = new CaseInsensitiveMap<String>();
    private InputStream content;
    private long contentLength;

    protected HttpMessage() {
        super();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers){
        assert (headers != null);
        this.headers = headers;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void close() throws IOException{
        if (content != null){
            content.close();
            content = null;
        }
    }
}
