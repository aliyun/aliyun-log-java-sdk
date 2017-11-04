package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class EtlFunctionConfig implements Serializable {

    private static final long serialVersionUID = 4383422999087255764L;
    protected String functionProvider;

    public String getFunctionProvider() {
        return functionProvider;
    }

    public void setFunctionProvider(String functionProvider) {
        this.functionProvider = functionProvider;
    }

    public EtlFunctionConfig(String functionProvider) {

        this.functionProvider = functionProvider;
    }
}
