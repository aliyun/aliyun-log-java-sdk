package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Domain;


public class ListDomainsResponse extends Response {

	private static final long serialVersionUID = -565128846073332929L;
	protected int count = 0;
	protected int total = 0;
	protected List<Domain> domains = new ArrayList<Domain>();
	
	public ListDomainsResponse(Map<String, String> headers, int count, int total, List<Domain> domains) {
		super(headers);
		this.count = count;
		this.total = total;
		setDomains(domains);
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = new ArrayList<Domain>();
		for (Domain domain : domains) {
			this.domains.add(domain);
		}
	}

}
