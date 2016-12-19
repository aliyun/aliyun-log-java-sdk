package com.aliyun.openservices.log.http.comm;

import java.io.UnsupportedEncodingException;

/**
 * 表示返回结果的信息。
 *
 */
public class ResponseMessage extends HttpMesssage {
    private String uri;
    private int statusCode;
    private static final int HTTP_SUCCESS_STATUS_CODE = 200;
    private byte[] body = null;

    /**
     * 构造函数。
     */
    public ResponseMessage(){
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public boolean isSuccessful(){
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }
    
    public void SetBody(byte[] body)
    {
    	this.body = body;
    }
    
    public byte[] GetRawBody()
    {
    	return this.body;
    }

	public String GetStringBody() {
		try {
			return new String(this.body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
