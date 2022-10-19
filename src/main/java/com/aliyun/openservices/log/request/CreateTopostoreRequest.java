package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Topostore;

public class CreateTopostoreRequest extends TopostoreRequest {
    private Topostore topostore;

    public Topostore getTopostore() {
        return this.topostore;
    }

    public void setTopostore(Topostore topostore) {
        this.topostore = topostore;
    }

    public CreateTopostoreRequest(Topostore topostore) {
        this.topostore = topostore;
    }

}
