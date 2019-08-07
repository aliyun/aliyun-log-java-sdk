/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.openservices.log.http.comm;

import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.client.ClientException;
import com.aliyun.openservices.log.http.utils.ExceptionFactory;
import com.aliyun.openservices.log.internal.ErrorCodes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Implementation of {@link ServiceClient} which support timeout.
 */
public class TimeoutServiceClient extends DefaultServiceClient {
    private ThreadPoolExecutor executor;

    public TimeoutServiceClient(ClientConfiguration config) {
        super(config);
        int processors = Runtime.getRuntime().availableProcessors();
        this.executor = createThreadPool(processors * 5, processors * 10, processors * 100);
    }

    public TimeoutServiceClient(ClientConfiguration config,
                                int corePoolSize,
                                int maximumPoolSize,
                                int queueSize) {
        super(config);
        this.executor = createThreadPool(corePoolSize, maximumPoolSize, queueSize);
    }

    private ThreadPoolExecutor createThreadPool(int corePoolSize, int maximumPoolSize, int queueSize) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public TimeoutServiceClient(ClientConfiguration config, ThreadPoolExecutor executor) {
        super(config);
        this.executor = executor;
    }

    @Override
    public ResponseMessage sendRequestCore(ServiceClient.Request request, String charset) throws IOException {
        HttpRequestBase httpRequest = httpRequestFactory.createHttpRequest(request, charset);
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setRequestConfig(this.requestConfig);

        CloseableHttpResponse httpResponse = null;
        HttpRequestTask httpRequestTask = new HttpRequestTask(httpRequest, httpContext);
        Future<CloseableHttpResponse> future = executor.submit(httpRequestTask);

        try {
            httpResponse = future.get(this.config.getRequestTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            httpRequest.abort();
            throw new ClientException(e.getMessage(), e);
        } catch (ExecutionException e) {
            httpRequest.abort();
            throw ExceptionFactory.createNetworkException((IOException) e.getCause());
        } catch (TimeoutException e) {
            httpRequest.abort();
            throw new ClientException(e.getMessage(), ErrorCodes.REQUEST_TIMEOUT, "Unknown", e);
        }

        return buildResponse(request, httpResponse);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(ClientConfiguration.DEFAULT_THREAD_POOL_WAIT_TIME, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
                executor.awaitTermination(ClientConfiguration.DEFAULT_THREAD_POOL_WAIT_TIME,
                        TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        super.shutdown();
    }

    class HttpRequestTask implements Callable<CloseableHttpResponse> {
        private HttpRequestBase httpRequest;
        private HttpClientContext httpContext;

        public HttpRequestTask(HttpRequestBase httpRequest, HttpClientContext httpContext) {
            this.httpRequest = httpRequest;
            this.httpContext = httpContext;
        }

        @Override
        public CloseableHttpResponse call() throws Exception {
            return httpClient.execute(httpRequest, httpContext);
        }
    }

}
