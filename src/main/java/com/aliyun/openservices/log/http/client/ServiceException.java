/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */

package com.aliyun.openservices.log.http.client;

/**
 * <p>
 * 表示阿里云服务返回的错误消息。
 * </p>
 * 
 * <p>
 * {@link ServiceException}用于处理阿里云服务返回的错误消息。比如，用于身份验证的Access ID不存在，
 * 则会抛出{@link ServiceException}（严格上讲，会是该类的一个继承类。比如，OTSClient会抛出OTSException）。
 * 异常中包含了错误代码，用于让调用者进行特定的处理。
 * </p>
 * 
 * <p>
 * {@link ClientException}表示的则是在向阿里云服务发送请求时出现的错误，以及客户端无法处理返回结果。
 * 例如，在发送请求时网络连接不可用，则会抛出{@link ClientException}的异常。
 * </p>
 * 
 * <p>
 * 通常来讲，调用者只需要处理{@link ServiceException}。因为该异常表明请求被服务处理，但处理的结果表明
 * 存在错误。异常中包含了细节的信息，特别是错误代码，可以帮助调用者进行处理。
 * </p>
 * 
 * @author xiaoming.yin
 *
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 430933593095358673L;
    private String errorCode;
    private String requestId;
    private String hostId;

    /**
     * 构造新实例。
     */
    public ServiceException() {
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     * @param message 异常信息。
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * 用表示异常原因的对象构造新实例。
     * @param cause 异常原因。
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param message 异常信息。
     * @param cause 异常原因。
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     * @param message 异常信息。
     * @param cause 异常原因。
     * @param errorCode 错误代码。
     * @param requestId Request ID。
     * @param hostId Host ID。
     */
    public ServiceException(String message, Throwable cause,
            String errorCode, String requestId, String hostId){
        this(message, cause);

        this.errorCode = errorCode;
        this.requestId = requestId;
        this.hostId = hostId;
    }

    /**
     * 返回错误代码的字符串表示。
     * @return 错误代码的字符串表示。
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 返回Request标识。
     * @return Request标识。
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 返回Host标识。
     * @return Host标识。
     */
    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return "[Error Code]:" + errorCode + ", "
                + "[Message]:" + getMessage();
    }
}
