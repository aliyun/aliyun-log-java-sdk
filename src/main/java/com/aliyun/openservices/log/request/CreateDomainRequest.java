package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Domain;

public class CreateDomainRequest extends Request {
	private static final long serialVersionUID = 798259435839686910L;
	private Domain domain;
	
	public CreateDomainRequest(String project, Domain domain) {
		super(project);
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

}
