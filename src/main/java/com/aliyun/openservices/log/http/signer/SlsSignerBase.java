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
package com.aliyun.openservices.log.http.signer;

import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.http.client.ClientConfiguration;

public abstract class SlsSignerBase {

    protected CredentialsProvider credentialsProvider;

    /**
     * Use <pre>
     *     {@code SlsSignerBase(new StaticCredentialsProvider(credentials))}
     * </pre> instead.
     */
    @Deprecated
    public SlsSignerBase(Credentials credentials) {
        this(new StaticCredentialsProvider(credentials));
    }

    public SlsSignerBase(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Deprecated
    public static SlsSigner createRequestSigner(ClientConfiguration clientConfiguration, Credentials credentials) {
        if (clientConfiguration.getSignatureVersion() == SignVersion.V4) {
            return new SlsV4Signer(credentials, clientConfiguration.getRegion());
        } else {
            return new SlsV1Signer(credentials);
        }
    }

    public static SlsSigner createRequestSigner(ClientConfiguration clientConfiguration, CredentialsProvider credentialsProvider) {
        if (clientConfiguration.getSignatureVersion() == SignVersion.V4) {
            return new SlsV4Signer(credentialsProvider, clientConfiguration.getRegion());
        } else {
            return new SlsV1Signer(credentialsProvider);
        }
    }
}
