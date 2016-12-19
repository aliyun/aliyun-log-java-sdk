package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

import com.aliyun.openservices.log.common.Shard;

public class ListShardResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1451751250790494673L;
	protected ArrayList<Shard> mShards = null;
	
	public ListShardResponse(Map<String, String> headers, ArrayList<Shard> shards) {
		super(headers);
		mShards = new ArrayList<Shard>();
		for (Shard shard:shards) {
			mShards.add(shard);
		}
	}

	public ArrayList<Shard> GetShards() {
		return mShards;
	}
}
