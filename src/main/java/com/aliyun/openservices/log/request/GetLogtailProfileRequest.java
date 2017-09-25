package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class GetLogtailProfileRequest extends Request {

	private static final long serialVersionUID = -7890862657888958738L;
	private String logStore;

	public GetLogtailProfileRequest(String project, String logstore, String source, int line, int offset) {
		super(project);
		setLogStore(logstore);
		setSource(source);
		setLine(line);
		setOffset(offset);
	}
	
	public String getLogStore() {
		return logStore;
	}

	public void setLogStore(String logStore) {
		this.logStore = logStore;
	}
	
	public String getSource() {
		return GetParam(Consts.CONST_GETLOGTAILPROFILE_SOURCE);
	}
	
	public void setSource(String source) {
		SetParam(Consts.CONST_GETLOGTAILPROFILE_SOURCE, source);
	}
	
	public int getLine() {
		String lineStr = GetParam(Consts.CONST_SIZE);
		if (lineStr.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(lineStr);
		}
	}
	
	public void setLine(int line) {
		SetParam(Consts.CONST_SIZE, String.valueOf(line));
	}
	
	public int getOffset() {
		String offsetStr = GetParam(Consts.CONST_OFFSET);
		if (offsetStr.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(offsetStr);
		}
	}
	
	public void setOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}
}
