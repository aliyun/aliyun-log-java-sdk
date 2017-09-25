package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.LogtailProfile;
import com.aliyun.openservices.log.common.Machine;

public class GetLogtailProfileResponse extends Response {

	private static final long serialVersionUID = 4756628719652701656L;
	private long total = 0;
	private long count = 0;
	private ArrayList<LogtailProfile> logtailProfiles = new ArrayList<LogtailProfile>();

	public GetLogtailProfileResponse(Map<String, String> headers, int count, int total, List<LogtailProfile> logtailProfiles) {
		super(headers);
		this.total = total;
		this.count = count;
		setLogtailProfiles(logtailProfiles);
	}
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public ArrayList<LogtailProfile> getLogtailProfiles() {
		return logtailProfiles;
	}

	public void setLogtailProfiles(List<LogtailProfile> logtailProfiles) {
		this.logtailProfiles =  new ArrayList<LogtailProfile>(logtailProfiles);
	}

	public GetLogtailProfileResponse(Map<String, String> headers) {
		super(headers);
	}

}
