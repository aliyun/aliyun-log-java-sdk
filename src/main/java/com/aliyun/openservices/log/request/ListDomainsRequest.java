package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListDomainsRequest extends Request {

	private static final long serialVersionUID = 2353419550512548162L;

	public ListDomainsRequest(String project, String domainName, int offset, int size) {
		super(project);
		setDomainName(domainName);
		setOffset(offset);
		setSize(size);
	}

	public String getDomainName() {
		return GetParam("domainName");
	}

	public void setDomainName(String domainName) {
		SetParam("domainName", domainName);
	}

	public int getOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void setOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int getSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void setSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}
}
