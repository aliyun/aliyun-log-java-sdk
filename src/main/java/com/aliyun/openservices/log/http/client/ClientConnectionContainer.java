package com.aliyun.openservices.log.http.client;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListShardResponse;

public class ClientConnectionContainer {
	private Client mClient;
	private ClientConnectionStatus mGlobalConnection;
	// project_name#logstore#shard_id
	private Map<String, ClientConnectionStatus> mShardConnections;
	private Map<String, Long> mShardLastUpdateTime;
	private long mGlobalConnectionUpdateInterval = 30L * 1000 * 1000 * 1000;
	private long mShardConnectionUpdateInterval = 300L * 1000 * 1000 * 1000;
	private long mGlobalConnectionValidInterval = 60L * 1000 * 1000 * 1000;
	private long mGlobalConnectionUpdateSendSize = 100 * 1024 * 1024;
	
	public ClientConnectionContainer()
	{
		mClient = null;
		mGlobalConnection = null;
		mShardConnections = new ConcurrentHashMap<String, ClientConnectionStatus>();
		mShardLastUpdateTime = new ConcurrentHashMap<String, Long>();
	}

	public void Init(String endpoint, String accessId, String accessKey) {
		mClient = new Client(endpoint, accessId, accessKey);
	}

	public ClientConnectionStatus GetShardConnection(String project, String logstore, int shard_id) {
		String key =  project + "#" + logstore;
		if (mShardLastUpdateTime.containsKey(key) == false)
		{
			mShardLastUpdateTime.put(key, (long)0);
		}
		String key_shard = project + "#" + logstore + "#" + String.valueOf(shard_id);
		if (mShardConnections.containsKey(key_shard) == false) {
			UpdateShardConnection(key);
		}
		if (mShardConnections.containsKey(key_shard))
		{
			return mShardConnections.get(key_shard);
		}
		return null;
	}
	public void ResetGlobalConnection()
	{
		mGlobalConnection = null;
	}

	
	public ClientConnectionStatus GetGlobalConnection()
	{
		return mGlobalConnection;
	}

	public void UpdateConnections()
	{
		UpdateGlobalConnection();
		UpdateShardConnections();
	}

	public void UpdateGlobalConnection() {
		long cur_time = System.nanoTime();
		boolean to_update = false;
		if (mGlobalConnection == null || mGlobalConnection.IsValidConnection() == false) {
			to_update = true;
		} else if (cur_time - mGlobalConnection.GetLastUsedTime() < mGlobalConnectionValidInterval
				&& (cur_time - mGlobalConnection.GetCreateTime() > mGlobalConnectionUpdateInterval
						|| mGlobalConnection.GetSendDataSize() > mGlobalConnectionUpdateSendSize
						|| mGlobalConnection.GetPullDataSize() > mGlobalConnectionUpdateSendSize)) {
			to_update = true;
		}
		if (to_update) {
			String ip_address = mClient.GetServerIpAddress("");
			if (ip_address != null && ip_address.isEmpty() == false) {
				mGlobalConnection = new ClientConnectionStatus(ip_address);
			}
		}
	}

	public void UpdateShardConnections() {
		for (Map.Entry<String, Long> entry : mShardLastUpdateTime.entrySet()) {
			String key = entry.getKey();
			UpdateShardConnection(key);
		}
	}

	private void UpdateShardConnection(String project_logstore)
	{
		if (mShardLastUpdateTime.containsKey(project_logstore) == false)
		{
			return ;
		}
		Long last_update_time = mShardLastUpdateTime.get(project_logstore);
		long cur_time = System.nanoTime();
		if (cur_time - last_update_time < mShardConnectionUpdateInterval)
		{
			return;
		}
		String[] items = project_logstore.split("#");
		if (items.length == 2)
		{
			try {
				ListShardResponse res = mClient.ListShard(items[0], items[1]);
				ArrayList<Shard> all_shards = res.GetShards();
				for(Shard shard : all_shards)
				{
					String server_ip = shard.getServerIp();
					if (server_ip != null && server_ip.isEmpty() == false) {
						int shard_id = shard.GetShardId();
						String key = project_logstore + "#" + String.valueOf(shard_id);
						mShardConnections.put(key, new ClientConnectionStatus(server_ip));
					}
				}
			} catch (LogException e) {
			}
		}
		mShardLastUpdateTime.put(project_logstore, cur_time);
	}

}
