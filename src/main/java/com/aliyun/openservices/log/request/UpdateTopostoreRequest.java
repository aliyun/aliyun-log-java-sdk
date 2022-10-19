package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Topostore;

public class UpdateTopostoreRequest extends TopostoreRequest{
    private Topostore topostore;

    public Topostore getTopostore() {
        return this.topostore;
    }

    public void setTopostore(Topostore topostore) {
        this.topostore = topostore;
    }

    public UpdateTopostoreRequest(Topostore topostore) {
        this.topostore = topostore;
    }

}
