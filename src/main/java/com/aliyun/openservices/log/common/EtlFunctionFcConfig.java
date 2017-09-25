package com.aliyun.openservices.log.common;

public class EtlFunctionFcConfig extends EtlFunctionConfig {

    private static final long serialVersionUID = -1178380817388401117L;
    private String endpoint;
    private String accountId;
    private String regionName;
    private String serviceName;
    private String functionName;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public EtlFunctionFcConfig(String functionProvider, String endpoint, String accountId, String regionName, String serviceName, String functionName) {
        super(functionProvider);
        this.endpoint = endpoint;
        this.accountId = accountId;
        this.regionName = regionName;
        this.serviceName = serviceName;
        this.functionName = functionName;
    }
}
