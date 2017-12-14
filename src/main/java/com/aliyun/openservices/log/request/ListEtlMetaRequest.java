package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ListEtlMetaRequest extends Request {

    private static final long serialVersionUID = -8984223668075815621L;
    private String etlMetaName = "";
    private String etlMetaKey = "";
    private String etlMetaTag = Consts.CONST_ETLMETA_ALL_TAG_MATCH;

    public ListEtlMetaRequest(String project, int offset, int size) {
        super(project);
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
        SetParam(Consts.CONST_SIZE, String.valueOf(size));
        SetParam(Consts.ETL_META_NAME, this.etlMetaName);
        SetParam(Consts.ETL_META_KEY, this.etlMetaKey);
        SetParam(Consts.ETL_META_TAG, this.etlMetaTag);
    }

    public String getEtlMetaName() {
        return etlMetaName;
    }

    public void setEtlMetaName(String etlMetaName) {
        this.etlMetaName = etlMetaName;
        SetParam(Consts.ETL_META_NAME, this.etlMetaName);
    }

    public String getEtlMetaKey() {
        return etlMetaKey;
    }

    public void setEtlMetaKey(String etlMetaKey) {
        this.etlMetaKey = etlMetaKey;
        SetParam(Consts.ETL_META_KEY, this.etlMetaKey);
    }

    public String getEtlMetaTag() {
        return etlMetaTag;
    }

    public void setEtlMetaTag(String etlMetaTag) {
        this.etlMetaTag = etlMetaTag;
        SetParam(Consts.ETL_META_TAG, this.etlMetaTag);
    }
}
