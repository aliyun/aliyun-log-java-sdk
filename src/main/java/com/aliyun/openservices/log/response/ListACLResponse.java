package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.ACL;

public class ListACLResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5489271894642250372L;
	protected int total = 0;
	protected int count = 0;
	protected List<ACL> acls = new ArrayList<ACL>();
	
	public ListACLResponse(Map<String, String> headers, int count, int total, List<ACL> acls) {
		super(headers);
		this.total = total;
		this.count = count;
		SetACLs(acls);
	}

	public int GetTotal() {
		return total;
	}

	public int GetCount() {
		return count;
	}

	public List<ACL> GetACLs() {
		return acls;
	}

	private void SetACLs(List<ACL> acls) {
		this.acls = new ArrayList<ACL>();
		for (ACL project:acls) {
			this.acls.add(project);
		}
	}
}
