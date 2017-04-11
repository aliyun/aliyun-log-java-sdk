package com.aliyun.openservices.log.sample;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;

public class ShardBalanceTool {
	static String endpoint = "";
	static String project = "";
	static String logstore = "";
	static String accesskeyId = "";
	static String accesskey = "";
	static ArrayList<String> Split(int count)
	{
		if(count <= 1) throw new IllegalArgumentException("count > 1");
		ArrayList<String> sps = new ArrayList<String>();
		BigInteger max = new BigInteger("ffffffffffffffffffffffffffffffff", 16);
		max.add(new BigInteger("1", 10));
		BigInteger step = max.divide(new BigInteger(count + "", 10));
		BigInteger res = new BigInteger("0", 10);
		for(int i = 0; i < count - 1; ++i){
			res = res.add(step);
			String hash = res.toString(16);
			while(hash.length() < 32) hash = "0" + hash;
			sps.add(hash);
		}
		return sps;
	}
	static ArrayList<String> SplitWithBound(int count)
	{
		ArrayList<String> sps = Split(count);
		sps.add(0, "00000000000000000000000000000000");
		sps.add(sps.size(), "ffffffffffffffffffffffffffffffff");
		return sps;
	}
	static boolean Contain(String bhash, String ehash, String sp)
	{
		if(bhash.compareToIgnoreCase(sp) > 0 || ehash.compareToIgnoreCase(sp) <= 0) return false;
		return true;
	}
	public static void main(String[] args) throws LogException, InterruptedException {
		int count = 24;
		ArrayList<String> res = Split(count);
		System.out.println(res);
		Client client = new Client(endpoint, accesskeyId, accesskey);
		while(res.size() > 0){
			ArrayList<Shard> shards = client.ListShard(project, logstore).GetShards();
			System.out.println("list shards: " + shards);
			HashSet<String> eraseHash = new HashSet<String>();
			for(Shard shard: shards){
				if(shard.getStatus().compareToIgnoreCase("readwrite") == 0){
					for(String hash: res){
						if(shard.getInclusiveBeginKey().compareToIgnoreCase(hash) != 0 && Contain(shard.getInclusiveBeginKey(), shard.getExclusiveEndKey(), hash)){
							System.out.println("split shard: " + shard.toString());
							client.SplitShard(project, logstore, shard.GetShardId(), hash);
							eraseHash.add(hash);
							break;
						}
						else if(shard.getInclusiveBeginKey().compareToIgnoreCase(hash) == 0){
							eraseHash.add(hash);
							break;
						}
					}
				}
			}
			ArrayList<String> newRes = new ArrayList<String>();
			for(String hash: res){
				if(!eraseHash.contains(hash)) newRes.add(hash);
			}
			res = newRes;
			if(res.size() > 0) Thread.sleep(60 * 1000);
		}
		Thread.sleep(60 * 1000);
		res = SplitWithBound(count);
		boolean mloop = true;
		while(mloop){
			mloop = false;
			ArrayList<Shard> shards = client.ListShard(project, logstore).GetShards();
			for(int i = 1; i < res.size(); ++i){
				String bh = res.get(i - 1), eh = res.get(i);
				ArrayList<Shard> rangeShards = new ArrayList<Shard>();
				for(Shard s: shards){
					if(s.getStatus().compareToIgnoreCase("readwrite") == 0 && Contain(bh, eh, s.getInclusiveBeginKey())){
						rangeShards.add(s);
					}
				}
				if(rangeShards.size() > 1){
					System.out.println("merge shard: " + rangeShards.get(0).toString());
					client.MergeShards(project, logstore, rangeShards.get(0).GetShardId());
					mloop = true;
				}
			}
			Thread.sleep(60 * 1000);
		}
	}
}
