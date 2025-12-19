package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.ShipperConfig;

public class GetShipperResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -893143600187284633L;

	private ShipperConfig config = null;
	
	public GetShipperResponse(Map<String, String> headers, ShipperConfig config) {
		super(headers);
		this.config = config;
	}
	
	public ShipperConfig GetConfig()
	{
		return config;
	}
	public void SetConfig(ShipperConfig config)
	{
		this.config = config;
	}
}
