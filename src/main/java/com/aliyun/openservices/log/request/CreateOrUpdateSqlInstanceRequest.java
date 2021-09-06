package com.aliyun.openservices.log.request;

public class CreateOrUpdateSqlInstanceRequest extends Request {
    private int cu;

    public CreateOrUpdateSqlInstanceRequest(String project, int cu) {
        super(project);
        this.cu = cu;
    }

    public int getCu() {
        return cu;
    }

    public void setCu(int cu) {
        this.cu = cu;
    }
}
