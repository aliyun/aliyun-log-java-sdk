package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Topostore;

public class GetTopostoreResponse extends Response {
    private Topostore topostore;

    public GetTopostoreResponse(Map<String, String> headers, Topostore topostore) {
        super(headers);
        this.topostore = topostore;
    }


    public Topostore getTopostore() {
        return this.topostore;
    }

    public void setTopostore(Topostore topostore) {
        this.topostore = topostore;
    }
    
}
