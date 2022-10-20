package com.aliyun.openservices.log.request;

public class CreateOrUpdateSqlInstanceRequest extends Request {
    private int cu;
    private boolean useAsDefault;

    public CreateOrUpdateSqlInstanceRequest(String project, int cu, boolean useAsDefault) {
        super(project);
        this.cu = cu;
        this.useAsDefault = useAsDefault;
    }

    public int getCu() {
        return cu;
    }

    public void setCu(int cu) {
        this.cu = cu;
    }

    public boolean isUseAsDefault() {
        return useAsDefault;
    }

    public void setUseAsDefault(boolean useAsDefault) {
        this.useAsDefault = useAsDefault;
    }
}
