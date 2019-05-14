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

	public ClientConnectionStatus GetShardConnection(String project, String logstore, int shardId) {
		String key = project + "#" + logstore;
		if (mShardLastUpdateTime.containsKey(key) == false)
		{
			mShardLastUpdateTime.put(key, (long)0);
		}
		String keyShard = project + "#" + logstore + "#" + shardId;
		if (mShardConnections.containsKey(keyShard) == false) {
			UpdateShardConnection(key);
		}
		if (mShardConnections.containsKey(keyShard))
		{
			return mShardConnections.get(keyShard);
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
		long curTime = System.nanoTime();
		boolean toUpdate = false;
		if (mGlobalConnection == null || mGlobalConnection.IsValidConnection() == false) {
            toUpdate = true;
		} else if (curTime - mGlobalConnection.GetLastUsedTime() < mGlobalConnectionValidInterval
				&& (curTime - mGlobalConnection.GetCreateTime() > mGlobalConnectionUpdateInterval
						|| mGlobalConnection.GetSendDataSize() > mGlobalConnectionUpdateSendSize
						|| mGlobalConnection.GetPullDataSize() > mGlobalConnectionUpdateSendSize)) {
            toUpdate = true;
		}
		if (toUpdate) {
			String ipAddress = mClient.GetServerIpAddress("");
			if (ipAddress != null && ipAddress.isEmpty() == false) {
				mGlobalConnection = new ClientConnectionStatus(ipAddress);
			}
		}
	}

	public void UpdateShardConnections() {
		for (Map.Entry<String, Long> entry : mShardLastUpdateTime.entrySet()) {
			String key = entry.getKey();
			UpdateShardConnection(key);
		}
	}

	private void UpdateShardConnection(String projectLogstore)
	{
		if (mShardLastUpdateTime.containsKey(projectLogstore) == false)
		{
			return ;
		}
		Long lastUpdateTime = mShardLastUpdateTime.get(projectLogstore);
		long curTime = System.nanoTime();
		if (curTime - lastUpdateTime < mShardConnectionUpdateInterval)
		{
			return;
		}
		String[] items = projectLogstore.split("#");
		if (items.length == 2)
		{
			try {
				ListShardResponse res = mClient.ListShard(items[0], items[1]);
				ArrayList<Shard> allShards = res.GetShards();
				for(Shard shard : allShards)
				{
					String serverIp = shard.getServerIp();
					if (serverIp != null && serverIp.isEmpty() == false) {
						int shardId = shard.GetShardId();
						String key = projectLogstore + "#" + shardId;
						mShardConnections.put(key, new ClientConnectionStatus(serverIp));
					}
				}
			} catch (LogException e) {
			}
		}
		mShardLastUpdateTime.put(projectLogstore, curTime);
	}

}
