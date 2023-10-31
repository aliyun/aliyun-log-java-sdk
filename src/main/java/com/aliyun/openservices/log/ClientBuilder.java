package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.util.Args;

/**
 * Used for building an {@link Client} instance.
 * <p></p>
 * Example code:
 * <pre>
 * {@code
 * Credentials credentials = new DefaultCredentials(accessKeyId, accessKeySecret);
 * CredentialsProvider provider = new StaticCredentialsProvider(credentials);
 * ClientBuilder builder = new ClientBuilder("<region>.log.aliyuncs.com", provider);
 * Client client = builder.sourceIp("127.0.0.1")
 *                        .serviceClient(serviceClient)
 *                        .clientConfiguration(clientConfiguration)
 *                        .build();
 * }
 * </pre>
 */
public class ClientBuilder {
    private final String endpoint;
    private final CredentialsProvider credentialsProvider;
    private ClientConfiguration clientConfiguration;
    private ServiceClient serviceClient;
    private String sourceIp;

    /**
     * Uses the specified SLS Endpoint and CredentialsProvider to create a new
     * {@link Client} instance.
     *
     * @param endpoint            required not null, the log service server address
     * @param credentialsProvider required not null, interface which provide credentials
     */
    public ClientBuilder(String endpoint, CredentialsProvider credentialsProvider) {
        this.endpoint = endpoint;
        this.credentialsProvider = credentialsProvider;
    }

    public ClientBuilder clientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        return this;
    }

    public ClientBuilder serviceClient(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
        return this;
    }

    /**
     * @param sourceIp Client ip address for build {@link Client} instance,
     *                 used as client ip in logs when write
     *                 logs to SLS server.
     */
    public ClientBuilder sourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
        return this;
    }

    public Client build() {
        Args.notNull(this.endpoint, "endpoint");
        Args.notNull(this.credentialsProvider, "credentialsProvider");

        if (this.clientConfiguration == null) {
            this.clientConfiguration = Client.getDefaultClientConfiguration();
        }
        if (this.serviceClient == null) {
            this.serviceClient = Client.buildServiceClient(this.clientConfiguration);
        }
        return new Client(endpoint, credentialsProvider, serviceClient, sourceIp);
    }
}
