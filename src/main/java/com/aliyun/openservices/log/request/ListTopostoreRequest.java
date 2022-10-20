package com.aliyun.openservices.log.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

public class ListTopostoreRequest extends TopostoreRequest{
    private Integer offset=0;
    private Integer size=200;
    private String tagKey;
    private String tagValue;
    private List<String> topostoreNames = new ArrayList<String>();
    private Map<String, String> tags;

    public Map<String,String> getTags() {
        return this.tags;
    }

    public void setTags(Map<String,String> tags) {
        this.tags = tags;
    }

    public ListTopostoreRequest(Integer offset, Integer size, String tagKey, String tagValue, List<String> topostoreNames) {
        this.offset = offset;
        this.size = size;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
        this.topostoreNames = topostoreNames;
    }

    public ListTopostoreRequest(){
    }

    public ListTopostoreRequest(String tagKey, String tagValue){
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTagKey() {
        return this.tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValue() {
        return this.tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public List<String> getTopostoreNames() {
        return this.topostoreNames;
    }

    public void setTopostoreNames(List<String> topostoreNames) {
        this.topostoreNames = topostoreNames;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }

        if (tagKey != null) {
            SetParam(Consts.TOPOSTORE_TAG_KEY, tagKey);
        }

        if (tagValue != null) {
            SetParam(Consts.TOPOSTORE_TAG_VALUE, tagValue);
        }

        if(tags!=null){
            JSONObject tagObj = new JSONObject();
            for(Map.Entry<String, String> kv : tags.entrySet()){
                tagObj.put(kv.getKey(), kv.getValue());
            }
            
            try{
                SetParam(Consts.TOPOSTORE_TAGS, URLEncoder.encode(new String(
                    Base64.getEncoder().encodeToString(tagObj.toJSONString().getBytes())), "utf-8"));
            } catch(UnsupportedEncodingException e){
                throw new RuntimeException(e);
            }
        }

        if (topostoreNames != null && !topostoreNames.isEmpty()) {
            SetParam(Consts.TOPOSTORE_NAME_LIST, Utils.join(",", topostoreNames));
        }
        return super.GetAllParams();
    }

}
