package com.aliyun.openservices.log.common;

public class SubStoreKey {
    private String name;
    private String type;

    public SubStoreKey() {
    }

    public SubStoreKey(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public boolean isValid(){
        if(this.name.length() == 0) {
            return false;
        }
        if (!"text".equals(this.type) &&
                !"long".equals(this.type) &&
                !"double".equals(this.type)) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
