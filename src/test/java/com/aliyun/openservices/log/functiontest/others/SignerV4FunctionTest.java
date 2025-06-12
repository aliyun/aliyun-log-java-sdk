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
package com.aliyun.openservices.log.functiontest.others;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.logstore.FunctionTest;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.signer.SignVersion;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SignerV4FunctionTest extends FunctionTest {

    @Test
    public void testSignatureV4() throws LogException {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setRegion(credentials.getRegion());
        clientConfiguration.setSignatureVersion(SignVersion.V4);
        String project = makeProjectName();
        Client client = new Client(credentials.getEndpoint(),
                credentials.getAccessKeyId(),
                credentials.getAccessKey(),
                clientConfiguration);
        try {
            try {
                client.GetProject(project);
                Assert.fail();
            } catch (LogException ex) {
                Assert.assertEquals(ex.getHttpCode(), 404);
            }
            client.CreateProject(project, "");

            LogStore logStore = new LogStore("logstore1", 1, 1);
            client.CreateLogStore(project, logStore);
            client.GetLogStore(project, logStore.GetLogStoreName());

            ListLogStoresResponse listLogStoresResponse = client.ListLogStores(project, 0, 100);
            Assert.assertEquals(1, listLogStoresResponse.GetTotal());
            Assert.assertEquals(1, listLogStoresResponse.GetCount());
            for (String name : listLogStoresResponse.GetLogStores()) {
                Assert.assertEquals(name, logStore.GetLogStoreName());
            }
            waitOneMinutes();
            int total = writeData(client, project, logStore.GetLogStoreName());
            List<FastLogGroup> logGroups = pullAllLogGroups(client, project, logStore.GetLogStoreName(), logStore.GetShardCount());
            int n = 0;
            for (FastLogGroup logGroup : logGroups) {
                n += logGroup.getLogsCount();
            }
            Assert.assertEquals(n, total);
            client.DeleteLogStore(project, logStore.GetLogStoreName());
        } finally {
            client.DeleteProject(project);
        }
    }

}
