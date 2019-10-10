package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;


public class GetContextLogsRequest extends Request {
    private static final long serialVersionUID = -4623939929477897059L;
    private String logstore;

    /**
     * @note see Client.getContextLogs for more information about parameters.
     */
    public GetContextLogsRequest(String project, String logstore,
            String packID, String packMeta, int backLines, int forwardLines) {
        super(project);
        this.logstore = logstore;
        setPackID(packID);
        setPackMeta(packMeta);
        setBackLines(backLines);
        setForwardLines(forwardLines);
        SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_CONTEXT_LOG);
    }

    public void setPackID(String packID) { SetParam(Consts.CONST_PACK_ID, packID); }

    public String getLogstore() { return logstore; }

    public void setPackMeta(String packMeta) { SetParam(Consts.CONST_PACK_META, packMeta); }

    public void setBackLines(int backLines) { SetParam(Consts.CONST_BACK_LINES, String.valueOf(backLines)); }

    public void setForwardLines(int forwardLines) { SetParam(Consts.CONST_FORWARD_LINES, String.valueOf(forwardLines)); }
}
