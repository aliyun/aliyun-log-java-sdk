/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.ConsumerGroup;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.MachineList;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.ApplyConfigToMachineGroupResponse;
import com.aliyun.openservices.log.response.ApproveMachineGroupResponse;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.ConsumerGroupCheckPointResponse;
import com.aliyun.openservices.log.response.ConsumerGroupHeartBeatResponse;
import com.aliyun.openservices.log.response.ConsumerGroupUpdateCheckPointResponse;
import com.aliyun.openservices.log.response.CreateAlertResponse;
import com.aliyun.openservices.log.response.CreateChartResponse;
import com.aliyun.openservices.log.response.CreateConfigResponse;
import com.aliyun.openservices.log.response.CreateConsumerGroupResponse;
import com.aliyun.openservices.log.response.CreateDashboardResponse;
import com.aliyun.openservices.log.response.CreateIndexResponse;
import com.aliyun.openservices.log.response.CreateLogStoreResponse;
import com.aliyun.openservices.log.response.CreateMachineGroupResponse;
import com.aliyun.openservices.log.response.CreateProjectResponse;
import com.aliyun.openservices.log.response.CreateSavedSearchResponse;
import com.aliyun.openservices.log.response.CreateShipperResponse;
import com.aliyun.openservices.log.response.DeleteAlertResponse;
import com.aliyun.openservices.log.response.DeleteChartResponse;
import com.aliyun.openservices.log.response.DeleteConfigResponse;
import com.aliyun.openservices.log.response.DeleteConsumerGroupResponse;
import com.aliyun.openservices.log.response.DeleteDashboardResponse;
import com.aliyun.openservices.log.response.DeleteIndexResponse;
import com.aliyun.openservices.log.response.DeleteLogStoreResponse;
import com.aliyun.openservices.log.response.DeleteMachineGroupResponse;
import com.aliyun.openservices.log.response.DeleteProjectResponse;
import com.aliyun.openservices.log.response.DeleteSavedSearchResponse;
import com.aliyun.openservices.log.response.DeleteShardResponse;
import com.aliyun.openservices.log.response.DeleteShipperResponse;
import com.aliyun.openservices.log.response.GetAlertResponse;
import com.aliyun.openservices.log.response.GetAppliedConfigResponse;
import com.aliyun.openservices.log.response.GetAppliedMachineGroupsResponse;
import com.aliyun.openservices.log.response.GetChartResponse;
import com.aliyun.openservices.log.response.GetConfigResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetCursorTimeResponse;
import com.aliyun.openservices.log.response.GetDashboardResponse;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetIndexResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.GetMachineGroupResponse;
import com.aliyun.openservices.log.response.GetProjectResponse;
import com.aliyun.openservices.log.response.GetSavedSearchResponse;
import com.aliyun.openservices.log.response.GetShipperResponse;
import com.aliyun.openservices.log.response.GetShipperTasksResponse;
import com.aliyun.openservices.log.response.ListACLResponse;
import com.aliyun.openservices.log.response.ListAlertFailResponse;
import com.aliyun.openservices.log.response.ListAlertResponse;
import com.aliyun.openservices.log.response.ListConfigResponse;
import com.aliyun.openservices.log.response.ListConsumerGroupResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListMachineGroupResponse;
import com.aliyun.openservices.log.response.ListMachinesResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import com.aliyun.openservices.log.response.ListSavedSearchResponse;
import com.aliyun.openservices.log.response.ListShardResponse;
import com.aliyun.openservices.log.response.ListShipperResponse;
import com.aliyun.openservices.log.response.ListTopicsResponse;
import com.aliyun.openservices.log.response.PutLogsResponse;
import com.aliyun.openservices.log.response.RemoveConfigFromMachineGroupResponse;
import com.aliyun.openservices.log.response.RetryShipperTasksResponse;
import com.aliyun.openservices.log.response.UpdateACLResponse;
import com.aliyun.openservices.log.response.UpdateAlertResponse;
import com.aliyun.openservices.log.response.UpdateChartResponse;
import com.aliyun.openservices.log.response.UpdateConfigResponse;
import com.aliyun.openservices.log.response.UpdateConsumerGroupResponse;
import com.aliyun.openservices.log.response.UpdateDashboardResponse;
import com.aliyun.openservices.log.response.UpdateIndexResponse;
import com.aliyun.openservices.log.response.UpdateLogStoreResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupMachineResponse;
import com.aliyun.openservices.log.response.UpdateMachineGroupResponse;
import com.aliyun.openservices.log.response.UpdateSavedSearchResponse;
import com.aliyun.openservices.log.response.UpdateShipperResponse;
import com.aliyun.openservices.log.response.CreateEtlJobResponse;
import com.aliyun.openservices.log.response.DeleteEtlJobResponse;
import com.aliyun.openservices.log.response.UpdateEtlJobResponse;
import com.aliyun.openservices.log.response.GetEtlJobResponse;
import com.aliyun.openservices.log.response.ListEtlJobResponse;
import com.aliyun.openservices.log.response.ListEtlMetaResponse;
import com.aliyun.openservices.log.response.ListEtlMetaNameResponse;
import com.aliyun.openservices.log.response.DeleteEtlMetaResponse;
import com.aliyun.openservices.log.response.CreateEtlMetaResponse;
import com.aliyun.openservices.log.response.UpdateEtlMetaResponse;


public interface LogService {

	/**
	 * Get The log status(histogram info) from log service server which match
	 * input parameters. All the logs with logstore and topic in [from, to)
	 * which contain the keys in query are the matched data.
	 * 
	 * @param project
	 *            the project name
	 * @param logstore
	 *            the result data logstore
	 * @param from
	 *            the begin time of the result data to get
	 * @param to
	 *            the end time of the result data to get
	 * @param topic
	 *            the result data topic
	 * 
	 * @param query
	 *            If the query is not empty, it will return the logs contain the
	 *            keys in query, if "all_hit" is contained in the query, only
	 *            the logs contains all the keys in query are matched logs,
	 *            other wise logs contain any key in query are matched logs.
	 * @return the histogram response of the matched logs
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public GetHistogramsResponse GetHistograms(String project, String logstore,
			int from, int to, String topic, String query) throws LogException;

	/**
	 * Get The log status(histogram info) from log service server which match
	 * input parameters. All the logs with logstore and topic in [from, to)
	 * which contain the keys in query are the matched data.
	 * 
	 * @param request
	 *            the get histogram request
	 * @return the histogram response of the matched logs
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if required parameter is null
	 */
	public GetHistogramsResponse GetHistograms(GetHistogramsRequest request)
			throws LogException;

	/**
	 * Get The sub set of logs data from log service server which match input
	 * parameters. By default, it will return at most 20 lines
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the result data logstore
	 * @param from
	 *            the begin time of the result data to get
	 * @param to
	 *            the end time of the result data to get
	 * @param topic
	 *            the result data topic
	 * @param query
	 *            If the query is not empty, it will return the logs contain the
	 *            keys in query, if "all_hit" is contained in the query, only
	 *            the logs contains all the keys in query are matched logs,
	 *            other wise logs contain any key in query are matched logs
	 * @return a response contains a sub set of the logs matched the input
	 *         parameters
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public GetLogsResponse GetLogs(String project, String logStore, int from,
			int to, String topic, String query) throws LogException;

	/**
	 * Get The sub set of logs data from log service server which match input
	 * parameters. All the data with logstore and topic in [from, to) which
	 * contain the keys in query are the matched data.
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the result data logstore
	 * @param from
	 *            the begin time of the result data to get
	 * @param to
	 *            the end time of the result data to get
	 * @param topic
	 *            the result data topic
	 * @param reverse
	 *            a flag to determine the return data order, if reverse is set
	 *            to false, the return logs is ascending order by time, other
	 *            wise, it's descending order
	 * @param line
	 *            how many lines to get, the max lines is decided by the sls
	 *            backend server
	 * @param offset
	 *            the start log index in all the matched logs.
	 * @param query
	 *            If the query is not empty, it will return the logs contain the
	 *            keys in query, if "all_hit" is contained in the query, only
	 *            the logs contains all the keys in query are matched logs,
	 *            other wise logs contain any key in query are matched logs.
	 * @return a response contains a sub set of the logs matched the input
	 *         parameters
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public GetLogsResponse GetLogs(String project, String logStore, int from,
			int to, String topic, String query, int line, int offset,
			boolean reverse) throws LogException;

	/**
	 * Get The sub set of logs data from log service server which match input
	 * parameters.
	 * 
	 * @param request
	 *            the get logs request
	 * @return a response contains a sub set of the logs matched the input
	 *         parameters
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 */
	public GetLogsResponse GetLogs(GetLogsRequest request) throws LogException;


	/**
	 * compute logs with a sql query from the whole project
	 *
	 * @param project
	 *            the project name
	 * @param query
	 *            stardard sql query, compute from the whole project
	 * @return a response contains a sub set of the logs matched the input
	 *         parameters
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public GetLogsResponse GetProjectLogs(String project,String query) throws  LogException;


	/**
	 * compute logs with a sql query from the whole project
	 *
	 * @param request
	 *            the get project logs request
	 * @return a response contains a sub set of the logs matched the input
	 *         parameters
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 */
	public GetLogsResponse GetProjectLogs(GetProjectLogsRequest request) throws  LogException;

	/**
	 * Get all the logstore for the user
	 * 
	 * @param project
	 *            the project name
	 * 
	 * @param offset
	 *            the begin offset
	 * 
	 * @param size
	 *            the query logstore name count
	 * 
	 * @param logstoreName
	 *            part name of the logstore, only return the logstores which
	 *            contains the input name
	 * 
	 * @return the user's logstore response
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public ListLogStoresResponse ListLogStores(String project, int offset,
			int size, String logstoreName) throws LogException;

	/**
	 * Get all the logstore of a project
	 * 
	 * @param request
	 *            the list log store request
	 * @return the user's logstore response
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if required parameter is null
	 */
	public ListLogStoresResponse ListLogStores(ListLogStoresRequest request)
			throws LogException;

	/**
	 * Get the topics in the logtstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the topic belongs to
	 * @param token
	 *            all the returned topics are equal or larger than the given
	 *            token according to topics' lexicographical order
	 * @param line
	 *            the topic number from log service server
	 * @return the log store's topics response
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public ListTopicsResponse ListTopics(String project, String logStore,
			String token, int line) throws LogException;

	/**
	 * Get the topics in the logtstore
	 * 
	 * @param request
	 *            the list topics request
	 * @return the log store's topics response
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if required parameter is null
	 */
	public ListTopicsResponse ListTopics(ListTopicsRequest request)
			throws LogException;

	/**
	 * Send Data to log service server
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the log store where the source data should be put
	 * @param topic
	 *            source data topic
	 * @param logItems
	 *            the log data to send
	 * 
	 * @param source
	 *            the source of the data, if the source is empty, it will be
	 *            reset to the host ip
	 * 
	 * @return The put logs response
	 * 
	 * @throws LogException
	 *             if any error happen when send data to the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty, or the logGroup log count
	 *             exceed 4096, or the total data size exceed 5MB
	 */
	public PutLogsResponse PutLogs(String project, String logStore,
			String topic, List<LogItem> logItems, String source)
			throws LogException;

	/**
	 * Send Data to log service server
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the log store where the source data should be put
	 * @param topic
	 *            source data topic
	 * @param logItems
	 *            the log data to send
	 * @param source
	 *            the source of the data, if the source is empty, it will be
	 *            reset to the host ip
	 * @param shardHash
	 *            the hash key md5value (00000000000000000000000000000000 ~
	 *            ffffffffffffffffffffffffffffffff)
	 * @return The put logs response
	 * @throws LogException
	 *             if any error happen when send data to the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty, or the logGroup log count
	 *             exceed 4096, or the total data size exceed 5MB
	 */
	public PutLogsResponse PutLogs(String project, String logStore,
			String topic, List<LogItem> logItems, String source,
			String shardHash) throws LogException;
	/**
	 * Send Data to log service server
	 * 
	 * @param request
	 *            the put log request
	 * 
	 * @return The put logs response
	 * 
	 * @throws LogException
	 *             if any error happen when send data to the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty, or the logGroup log count
	 *             exceed 4096, or the total data size exceed 5MB
	 */
	public PutLogsResponse PutLogs(PutLogsRequest request) throws LogException;

	/**
	 * Get cursor from log service server
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the log store name
	 * @param shardId
	 *            the shard where the cursor should be get
	 * @param fromTime
	 *            the from time of log data in unix time stamp sec
	 * 
	 * @return The get cursor response
	 * 
	 * @throws LogException
	 *             if any error happen when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logStore is empty
	 */
	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, long fromTime) throws LogException;

	/**
	 * Get cursor from log service server
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the log store name
	 * @param shardId
	 *            the shard where the cursor should be get
	 * @param fromTime
	 *            the from time of log data in java Date
	 * 
	 * @return The get cursor response
	 * 
	 * @throws LogException
	 *             if any error happen when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string argument in request is empty
	 */
	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, Date fromTime) throws LogException;

	/**
	 * Get cursor from log service server
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the log store name
	 * @param shardId
	 *            the shard where the cursor should be get
	 * @param mode
	 *            the mode to get cursor, include BEGIN and END
	 * 
	 * @return The get cursor response
	 * 
	 * @throws LogException
	 *             if any error happen when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string argument in request is empty
	 */
	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, CursorMode mode) throws LogException;

	/**
	 * Get cursor from log service server
	 * 
	 * @param request
	 *            the get cursor request
	 * 
	 * @return The get cursor response
	 * 
	 * @throws LogException
	 *             if any error happen when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string argument in request is empty
	 */
	public GetCursorResponse GetCursor(GetCursorRequest request)
			throws LogException;

	/**
	 * Get the receive time of the package according to the cursor
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the logstore name
	 * @param shardId
	 *            shard id
	 * @param cursor
	 *            cursor of a shard
	 * @return the cursor time response
	 * @throws LogException
	 *             if any error happen when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string argument in request is empty
	 */
	public GetCursorTimeResponse GetCursorTime(String project, String logStore,
			int shardId, String cursor) throws LogException;
	
	/**
	 * Get the receive time of the package according to the cursor
	 * 
	 * @param request
	 *            the request
	 * @return cursor time response * @throws LogException if any error happen
	 *         when get cursor from the server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string argument in request is empty
	 */
	public GetCursorTimeResponse GetCursorTime(GetCursorTimeRequest request)
			throws LogException;
	/**
	 * Get the shards in the logtstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * 
	 * @return the log store's shards response
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore is empty
	 */
	public ListShardResponse ListShard(String project, String logStore)
			throws LogException;

	/**
	 * Get the shards in the logtstore
	 * 
	 * @param request
	 *            the get cursor request
	 * 
	 * @return the log store's shards response
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public ListShardResponse ListShard(ListShardRequest request)
			throws LogException;

	/**
	 * split a readwrite shard in the logtstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * @param shardId
	 *            the shard id to split
	 * @param midHash
	 *            the middle md5 hash string to split the shard
	 * 
	 * @return the splited shard and two new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public ListShardResponse SplitShard(String project, String logStore,
			int shardId, String midHash) throws LogException;

	/**
	 * split a readwrite shard in the logtstore
	 * 
	 * @param request
	 *            split shard request
	 * 
	 * @return the splited shard and two new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public ListShardResponse SplitShard(SplitShardRequest request)
			throws LogException;

	/**
	 * merge two readwrite shards in the logtstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * @param shardId
	 *            the shard id to merge with right adjacent shard
	 * 
	 * @return the merges shards and new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public ListShardResponse MergeShards(String project, String logStore,
			int shardId) throws LogException;

	/**
	 * merge two readwrite shards in the logtstore
	 * 
	 * @param request
	 *            the project name
	 * 
	 * @return the merges shards and new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public ListShardResponse MergeShards(MergeShardsRequest request)
			throws LogException;

	/**
	 * delete a readonly shard in the logtstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * @param shardId
	 *            the shard id to delete
	 * 
	 * @return the merges shards and new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public DeleteShardResponse DeleteShard(String project, String logStore,
			int shardId) throws LogException;

	/**
	 * delete a readonly shard in the logtstore
	 * 
	 * @param request
	 *            delete shard request
	 * 
	 * @return the merges shards and new generated readwrite shard
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore in request is empty
	 */
	public DeleteShardResponse DeleteShard(DeleteShardRequest request)
			throws LogException;

	/**
	 * Batch get log
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * @param shardId
	 *            the shard to batch get log
	 * @param count
	 *            the logrgroup num
	 * @param cursor
	 *            the cursor to batch get log
	 * 
	 * @return batch get log response
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore or cursor is empty
	 */
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor) throws LogException;
	
	/**
	 * Batch get log
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            where the shard belongs to
	 * @param shardId
	 *            the shard to batch get log
	 * @param count
	 *            the logrgroup num
	 * @param cursor
	 *            the cursor to batch get log
	 *            
	 * @param end_cursor
	 * 			  the end cursor to batch get log
	 * 
	 * @return batch get log response
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore or cursor is empty
	 */
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor, String end_cursor) throws LogException;

	/**
	 * Batch get log
	 * 
	 * @param request
	 *            the batch get log request
	 * 
	 * @return batch get log response
	 * 
	 * @throws LogException
	 *             if any error happen when get the data from log service server
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project or logstore or cursor in request is empty
	 */
	public BatchGetLogResponse BatchGetLog(BatchGetLogRequest request)
			throws LogException;

	/**
	 * Create logtail config
	 * 
	 * @param project
	 *            the project name
	 * @param config
	 *            the full config resource
	 * 
	 * @return the create config response
	 * 
	 * @throws LogException
	 *             if any error happen when creating logtail config
	 * @throws NullPointerException
	 *             if config resource parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateConfigResponse CreateConfig(String project, Config config)
			throws LogException;

	/**
	 * Create logtail config
	 * 
	 * @param request
	 *            the create config request
	 * 
	 * @return the create config response
	 * 
	 * @throws LogException
	 *             if any error happen when creating logtail config
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateConfigResponse CreateConfig(CreateConfigRequest request)
			throws LogException;

	/**
	 * Update logtail config
	 * 
	 * @param project
	 *            the project name
	 * @param config
	 *            the full config resource
	 * 
	 * @return the update config response
	 * 
	 * @throws LogException
	 *             if any error happen when updating logtail config
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateConfigResponse UpdateConfig(String project, Config config)
			throws LogException;

	/**
	 * Update logtail config
	 * 
	 * @param request
	 *            the update config request
	 * 
	 * @return the update config response
	 * 
	 * @throws LogException
	 *             if any error happen when updating logtail config
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateConfigResponse UpdateConfig(UpdateConfigRequest request)
			throws LogException;

	/**
	 * Get logtail config
	 * 
	 * @param project
	 *            the project name
	 * @param configName
	 *            the config name
	 * 
	 * @return the get config response
	 * 
	 * @throws LogException
	 *             if any error happen when getting logtail config
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if configName is empty
	 * 
	 */
	public GetConfigResponse GetConfig(String project, String configName)
			throws LogException;

	/**
	 * Get logtail config
	 * 
	 * @param request
	 *            the get config request
	 * 
	 * @return the get config response
	 * 
	 * @throws LogException
	 *             if any error happen when getting logtail config
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetConfigResponse GetConfig(GetConfigRequest request)
			throws LogException;

	/**
	 * Delete logtail config
	 * 
	 * @param project
	 *            the project name
	 * 
	 * @param configName
	 *            the config name
	 * 
	 * @return the delete config response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting logtail config
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteConfigResponse DeleteConfig(String project, String configName)
			throws LogException;

	/**
	 * Delete logtail config
	 * 
	 * @param request
	 *            the delete config request
	 * 
	 * @return the delete config response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting logtail config
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteConfigResponse DeleteConfig(DeleteConfigRequest request)
			throws LogException;

	/**
	 * List logtail configs
	 * 
	 * @param project
	 *            the project name
	 * @return the list configs response
	 * 
	 * @throws LogException
	 *             if any error happen when listing logtail configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListConfigResponse ListConfig(String project) throws LogException;

	/**
	 * List logtail configs
	 * 
	 * @param project
	 *            the project name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list configs response
	 * 
	 * @throws LogException
	 *             if any error happen when listing logtail configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListConfigResponse ListConfig(String project, int offSet, int size)
			throws LogException;

	/**
	 * List logtail configs
	 * 
	 * @param project
	 *            the project name
	 * @param configName
	 *            the config name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list configs response
	 * 
	 * @throws LogException
	 *             if any error happen when listing logtail configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListConfigResponse ListConfig(String project, String configName,
			int offSet, int size) throws LogException;

	/**
	 * List logtail configs
	 * 
	 * @param request
	 *            the list configs request
	 * 
	 * @return the list configs response
	 * 
	 * @throws LogException
	 *             if any error happen when listing logtail configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListConfigResponse ListConfig(ListConfigRequest request)
			throws LogException;

	/**
	 * get applied config on a certain machine group
	 * 
	 * @param project
	 *            the project name
	 * 
	 * @param groupName
	 *            the machine group name
	 * 
	 * @return get applied config response
	 * 
	 * @throws LogException
	 *             if any error happen when get applied config configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetAppliedConfigResponse GetAppliedConfig(String project,
			String groupName) throws LogException;

	/**
	 * get applied config on a certain machine group
	 * 
	 * @param request
	 *            get applied config request
	 * 
	 * @return get applied config response
	 * 
	 * @throws LogException
	 *             if any error happen when get applied config configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetAppliedConfigResponse GetAppliedConfig(
			GetAppliedConfigsRequest request) throws LogException;

	/**
	 * get applied machine group for a certain logtail config
	 * 
	 * @param project
	 *            the project name
	 * 
	 * @param configName
	 *            the config name
	 * 
	 * @return get applied machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when get applied config configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetAppliedMachineGroupsResponse GetAppliedMachineGroups(
			String project, String configName) throws LogException;

	/**
	 * get applied machine group for a certain logtail config
	 * 
	 * @param request
	 *            get applied machine group request
	 * 
	 * @return get applied machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when get applied config configs
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetAppliedMachineGroupsResponse GetAppliedMachineGroups(
			GetAppliedMachineGroupRequest request) throws LogException;

	/**
	 * Create machine group
	 * 
	 * @param project
	 *            the project name
	 * @param group
	 *            the full machine group resource
	 * 
	 * @return the create machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when creating machine group
	 * @throws NullPointerException
	 *             if machine group resource parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateMachineGroupResponse CreateMachineGroup(String project,
			MachineGroup group) throws LogException;

	/**
	 * Create machine group
	 * 
	 * @param request
	 *            the create machine group request
	 * 
	 * @return the create machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when creating machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateMachineGroupResponse CreateMachineGroup(
			CreateMachineGroupRequest request) throws LogException;

	/**
	 * Update machine group
	 * 
	 * @param project
	 *            the project name
	 * @param group
	 *            the full machine group resource
	 * 
	 * @return the update machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when updating machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateMachineGroupResponse UpdateMachineGroup(String project,
			MachineGroup group) throws LogException;

	/**
	 * Update machine group
	 * 
	 * @param request
	 *            the update machine group request
	 * 
	 * @return the update machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when updating machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateMachineGroupResponse UpdateMachineGroup(
			UpdateMachineGroupRequest request) throws LogException;

	/**
	 * add machine into machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * @param machineList
	 * 			  machine id list
	 * 
	 * @return add machine into machine group response
	 * 
	 * @throws LogException
	 * 			   if any error happen when adding machine into machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty		
	 */
	public UpdateMachineGroupMachineResponse AddMachineIntoMahineGroup(String project,
			String groupName,
			MachineList machineList) throws LogException;
	
	/**
	 * add machine into machine group
	 * 
	 * @param request
	 *            update machine group resource
	 * 
	 * @return add machine into machine group response
	 * 
	 * @throws LogException
	 * 			   if any error happen when adding machine into machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty		
	 */
	public UpdateMachineGroupMachineResponse AddMachineIntoMachineGroup(
			UpdateMachineGroupMachineRequest request) throws LogException;
	
	/**
	 * delete machine into machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * @param machineList
	 * 			  machine id list
	 * 
	 * @return delete machine from machine group response
	 * 
	 * @throws LogException
	 * 			   if any error happen when adding machine into machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty		
	 */
	public UpdateMachineGroupMachineResponse DeleteMachineFromMachineGroup(String project,
			String groupName,
			MachineList machineList) throws LogException;
	
	/**
	 * delete machine into machine group
	 * 
	 * @param request
	 *            update machine group machine resource
	 * 
	 * @return delete machine from machine group response
	 * 
	 * @throws LogException
	 * 			   if any error happen when adding machine into machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty		
	 */
	public UpdateMachineGroupMachineResponse DeleteMachineFromMachineGroup(
			UpdateMachineGroupMachineRequest request) throws LogException;
	
	/**
	 * Get machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * 
	 * @return the get machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when getting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetMachineGroupResponse GetMachineGroup(String project,
			String groupName) throws LogException;

	/**
	 * Get machine group
	 * 
	 * @param request
	 *            the get machine group request
	 * 
	 * @return the get machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when getting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetMachineGroupResponse GetMachineGroup(
			GetMachineGroupRequest request) throws LogException;

	/**
	 * approve machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * 
	 * @return approve machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ApproveMachineGroupResponse ApproveMachineGroup(String project,
			String groupName) throws LogException;
	
	/**
	 * approve machine group
	 * 
	 * @param request
	 *            the approve machine group request
	 * 
	 * @return the approve machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */	
	public ApproveMachineGroupResponse ApproveMachineGroup(
			ApproveMachineGroupRequest request) throws LogException;
	
	/**
	 * Delete machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * 
	 * @return the delete machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteMachineGroupResponse DeleteMachineGroup(String project,
			String groupName) throws LogException;

	/**
	 * Delete machine group
	 * 
	 * @param request
	 *            the delete machine group request
	 * 
	 * @return the delete machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when deleting machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteMachineGroupResponse DeleteMachineGroup(
			DeleteMachineGroupRequest request) throws LogException;

	/**
	 * List machine groups
	 * 
	 * @param project
	 *            the project name
	 * @return the list machine groups response
	 * 
	 * @throws LogException
	 *             if any error happen when listing machine groups
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListMachineGroupResponse ListMachineGroup(String project)
			throws LogException;

	/**
	 * List the real machines in the machine group
	 * 
	 * @param project
	 *            the project name
	 * @param machineGroup
	 *            the machine group name
	 * 
	 * @param offset
	 *            the offset in the machine group
	 * @param size
	 *            the size to get
	 * @return the machines in the machine group
	 * @throws LogException
	 *             if any error happen when listing machines
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListMachinesResponse ListMachines(String project,
			String machineGroup, int offset, int size) throws LogException;

	/**
	 * List machine groups
	 * 
	 * @param project
	 *            the project name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list machine groups response
	 * 
	 * @throws LogException
	 *             if any error happen when listing machine groups
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListMachineGroupResponse ListMachineGroup(String project,
			int offSet, int size) throws LogException;

	/**
	 * List machine groups
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list machine groups response
	 * 
	 * @throws LogException
	 *             if any error happen when listing machine groups
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListMachineGroupResponse ListMachineGroup(String project,
			String groupName, int offSet, int size) throws LogException;

	/**
	 * List machine groups
	 * 
	 * @param request
	 *            the list machine groups request
	 * 
	 * @return the list machine groups response
	 * 
	 * @throws LogException
	 *             if any error happen when listing machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListMachineGroupResponse ListMachineGroup(
			ListMachineGroupRequest request) throws LogException;

	/**
	 * Apply config to machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * @param configName
	 *            the logtail config name
	 * 
	 * @return the apply config to machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when applying config to machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ApplyConfigToMachineGroupResponse ApplyConfigToMachineGroup(
			String project, String groupName, String configName)
			throws LogException;

	/**
	 * Apply config to machine group
	 * 
	 * @param request
	 *            apply config to machine group request
	 * 
	 * @return the apply config to machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when applying config to machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ApplyConfigToMachineGroupResponse ApplyConfigToMachineGroup(
			ApplyConfigToMachineGroupRequest request) throws LogException;

	/**
	 * Remove config from machine group
	 * 
	 * @param project
	 *            the project name
	 * @param groupName
	 *            the machine group name
	 * @param configName
	 *            the logtail config name
	 * 
	 * @return the remove config from machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when removing config from machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public RemoveConfigFromMachineGroupResponse RemoveConfigFromMachineGroup(
			String project, String groupName, String configName)
			throws LogException;

	/**
	 * Remove config from machine group
	 * 
	 * @param request
	 *            remove config from machine group request
	 * 
	 * @return the remove config from machine group response
	 * 
	 * @throws LogException
	 *             if any error happen when removing config from machine group
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public RemoveConfigFromMachineGroupResponse RemoveConfigFromMachineGroup(
			RemoveConfigFromMachineGroupRequest request) throws LogException;

	/**
	 * Update project ACL
	 * 
	 * @param project
	 *            the project name
	 * @param acl
	 *            the full acl resource with aclId
	 * 
	 * @return the update acl response
	 * 
	 * @throws LogException
	 *             if any error happen when updating acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateACLResponse UpdateACL(String project, ACL acl)
			throws LogException;

	/**
	 * update log sotre acl
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name
	 * @param acl
	 *            acl config
	 * @return update acl config
	 * @throws LogException
	 *             if any error happen
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateACLResponse UpdateACL(String project, String logStore, ACL acl)
			throws LogException;

	/**
	 * Update ACL
	 * 
	 * @param request
	 *            the update acl request
	 * 
	 * @return the update acl response
	 * 
	 * @throws LogException
	 *             if any error happen when updating acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateACLResponse UpdateACL(UpdateACLRequest request)
			throws LogException;

	/**
	 * List ACL
	 * 
	 * @param project
	 *            the project name
	 * @return the list acl response
	 * 
	 * @throws LogException
	 *             if any error happen when listing acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListACLResponse ListACL(String project) throws LogException;

	/**
	 * List ACL
	 * 
	 * @param project
	 *            the project name
	 * 
	 * @param logStore
	 *            the logstore name
	 * @return the list acl response
	 * 
	 * @throws LogException
	 *             if any error happen when listing acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListACLResponse ListACL(String project, String logStore)
			throws LogException;

	/**
	 * List ACL
	 * 
	 * @param project
	 *            the project name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list acl response
	 * 
	 * @throws LogException
	 *             if any error happen when listing acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListACLResponse ListACL(String project, int offSet, int size)
			throws LogException;

	/**
	 * List ACL
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the logstore name
	 * @param offSet
	 *            the list offset
	 * @param size
	 *            the query size
	 * 
	 * @return the list acl response
	 * 
	 * @throws LogException
	 *             if any error happen when listing acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListACLResponse ListACL(String project, String logStore, int offSet,
			int size) throws LogException;

	/**
	 * List ACL
	 * 
	 * @param request
	 *            the list acl request
	 * 
	 * @return the list acl response
	 * 
	 * @throws LogException
	 *             if any error happen when listing acl
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListACLResponse ListACL(ListACLRequest request) throws LogException;

	/**
	 * create a logstore in a project
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the config
	 * @return the create log store response
	 * @throws LogException
	 *             if any error happen when creasting logstore
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project is empty
	 */
	public CreateLogStoreResponse CreateLogStore(String project,
			LogStore logStore) throws LogException;

	/**
	 * create logstore
	 * 
	 * @param request
	 *            logstore create request
	 * @return the create log store response
	 * @throws LogException
	 *             if any error happen when creasting logstore
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateLogStoreResponse CreateLogStore(CreateLogStoreRequest request)
			throws LogException;

	/**
	 * Update log store config
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the logsotre config
	 * @return update logstore response
	 * @throws LogException
	 *             if any error happen when updating logstore
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if project is empty
	 */
	public UpdateLogStoreResponse UpdateLogStore(String project,
			LogStore logStore) throws LogException;

	/**
	 * Update logstore config
	 * 
	 * @param request
	 *            update logstore requst
	 * @return Update logstore response
	 * 
	 * @throws LogException
	 *             if any error happen when updating logstore
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateLogStoreResponse UpdateLogStore(UpdateLogStoreRequest request)
			throws LogException;

	/**
	 * Delete the logstore
	 * 
	 * @param project
	 *            the project name
	 * @param logStoreName
	 *            the lostore to delete
	 * @return delete logstore response
	 * @throws LogException
	 *             if any error happen when deleting logstore
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if an param is empty
	 */
	public DeleteLogStoreResponse DeleteLogStore(String project,
			String logStoreName) throws LogException;

	/**
	 * Delete logstore
	 * 
	 * @param request
	 *            delete logstore request
	 * @return the delete logstore response
	 * @throws LogException
	 *             if any error happen when deleting logstore
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteLogStoreResponse DeleteLogStore(DeleteLogStoreRequest request)
			throws LogException;

	/**
	 * Get the logstore config
	 * 
	 * @param project
	 *            the project name
	 * @param logStoreName
	 *            the logstore name
	 * @return the get logstore response
	 * @throws LogException
	 *             if any error happen when getting logstore config
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any parameter is empty
	 */
	public GetLogStoreResponse GetLogStore(String project, String logStoreName)
			throws LogException;

	/**
	 * get the logstore config
	 * 
	 * @param request
	 *            the get logstore config request
	 * @return the get logstore response
	 * @throws LogException
	 *             if any error happen when getting logstore config
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetLogStoreResponse GetLogStore(GetLogStoreRequest request)
			throws LogException;

	/**
	 * create logstore index
	 *
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the logstore name
	 * @param indexJsonString
	 *            logstore indexJsonString
	 * @return create index response
	 * @throws LogException
	 *             if any error happen when creating index
	 *
	 * @throws NullPointerException
	 *             if the request is null
	 *
	 * @throws IllegalArgumentException
	 *             if any string paramter is empty
	 */
	public CreateIndexResponse CreateIndex(String project, String logStore,
										   String indexJsonString) throws LogException;

	/**
	 * create logstore index
	 * 
	 * @param project
	 *            the project name
	 * @param logStore
	 *            the logstore name
	 * @param index
	 *            logstore index config
	 * @return create index response
	 * @throws LogException
	 *             if any error happen when creating index
	 * 
	 * @throws NullPointerException
	 *             if the request is null
	 * 
	 * @throws IllegalArgumentException
	 *             if any string paramter is empty
	 */
	public CreateIndexResponse CreateIndex(String project, String logStore,
			Index index) throws LogException;

	/**
	 * create logstore index
	 * 
	 * @param request
	 *            the create logstore index request
	 * @return create index response
	 * @throws LogException
	 *             if any error happen when creating index
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateIndexResponse CreateIndex(CreateIndexRequest request)
			throws LogException;

	/**
	 * Update logstore index config
	 *
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param indexJsonString
	 *            logstore indexJsonString
	 * @return update logstore index response
	 * @throws LogException
	 *             if any error happen when updating logstore index config
	 *
	 * @throws NullPointerException
	 *             if any parameter is null
	 *
	 * @throws IllegalArgumentException
	 *             if any string parameter is empty
	 */
	public UpdateIndexResponse UpdateIndex(String project, String logStore,
										   String indexJsonString) throws LogException;

	/**
	 * Update logstore index config
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param index
	 *            logstore index config
	 * @return update logstore index response
	 * @throws LogException
	 *             if any error happen when updating logstore index config
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * 
	 * @throws IllegalArgumentException
	 *             if any string parameter is empty
	 */
	public UpdateIndexResponse UpdateIndex(String project, String logStore,
			Index index) throws LogException;

	/**
	 * update logstore index config
	 * 
	 * @param request
	 *            update logstore index request
	 * @return update logstore index response
	 * @throws LogException
	 *             if any error happen when update logstore index config
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateIndexResponse UpdateIndex(UpdateIndexRequest request)
			throws LogException;

	/**
	 * delete logstore index
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            store name
	 * @return delete logstore index response
	 * @throws LogException
	 *             if any error happen when deleting logstore index config
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string parameter is empty
	 */
	public DeleteIndexResponse DeleteIndex(String project, String logStore)
			throws LogException;

	/**
	 * delete logstore index
	 * 
	 * @param request
	 *            delete logstore index config
	 * @return deleing index response
	 * @throws LogException
	 *             if any error happen when deleting logstore index config
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteIndexResponse DeleteIndex(DeleteIndexRequest request)
			throws LogException;

	/**
	 * Get logstore index config
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @return get index config response
	 * @throws LogException
	 *             if any error happen when get index config
	 * 
	 * @throws NullPointerException
	 *             if any parameter is null
	 * @throws IllegalArgumentException
	 *             if any string parameter is empty
	 */
	public GetIndexResponse GetIndex(String project, String logStore)
			throws LogException;

	/**
	 * Get logstore index config
	 * 
	 * 
	 * @param request
	 *            get logstore index request
	 * @return get index config response
	 * @throws LogException
	 *             if any error happen when getting index config
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetIndexResponse GetIndex(GetIndexRequest request)
			throws LogException;

	/**
	 * create logstore consumer group
	 * 
	 * 
	 * @param request
	 *            contains all of the parameters needed
	 * @return create logstore consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateConsumerGroupResponse CreateConsumerGroup(
			CreateConsumerGroupRequest request) throws LogException;

	/**
	 * create logstore consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            contains all of the parameters needed by consumer group
	 * @return create logstore consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateConsumerGroupResponse CreateConsumerGroup(String project,
			String logStore, ConsumerGroup consumerGroup) throws LogException;

	/**
	 * delete logstore consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @return delete logstore consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteConsumerGroupResponse DeleteConsumerGroup(String project,
			String logStore, String consumerGroup) throws LogException;

	/**
	 * list logstore consumer groups
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @return list logstore consumer groups response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListConsumerGroupResponse ListConsumerGroup(String project,
			String logStore) throws LogException;

	/**
	 * update consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param inOrder
	 *            consume data in oder or not
	 * @param timeoutInSec
	 *            if the time interval of a consumer's heartbeat exceed this
	 *            value in second, the consumer will be deleted.
	 * @return update consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, boolean inOrder,
			int timeoutInSec) throws LogException;

	public UpdateConsumerGroupResponse
	/**
	 * update consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param inOrder
	 *            consume data in oder or not
	 * @return update consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	UpdateConsumerGroup(String project, String logStore, String consumerGroup,
			boolean inOrder) throws LogException;

	/**
	 * update consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param timeoutInSec
	 *            if the time interval of a consumer's heartbeat exceed this
	 *            value in second, the consumer will be deleted.
	 * @return update consumer group response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, int timeoutInSec)
			throws LogException;

	/**
	 * update consume checkpoint
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param consumer
	 *            consumer name
	 * @param shard
	 *            shard id
	 * @param checkpoint
	 *            shard cursor
	 * @return update consume checkpoint response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup,
			String consumer, int shard, String checkpoint) throws LogException;

	/**
	 * update consume checkpoint
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param shard
	 *            shard id
	 * @param checkpoint
	 *            shard cursor
	 * @return update consume checkpoint response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup, int shard,
			String checkpoint) throws LogException;

	/**
	 * notify the server periodically to show that the consumer is still alive.
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param consumer
	 *            consumer name
	 * @param shards
	 *            shards hold by the consumer
	 * @return heartbeat response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ConsumerGroupHeartBeatResponse HeartBeat(String project,
			String logStore, String consumerGroup, String consumer,
			ArrayList<Integer> shards) throws LogException;

	/**
	 * get shard checkpoint in the consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @param shard
	 *            shard id
	 * @return get shard checkpoint response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup, int shard)
			throws LogException;

	/**
	 * get all of the shard checkpoints in the consumer group
	 * 
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param consumerGroup
	 *            consumer group name
	 * @return get shard checkpoint response
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup) throws LogException;

	/**
	 * create a shipper
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param shipperName
	 *            shipper name
	 * @param shipConfig
	 *            the OssShipperConfig
	 * @return CreateShipperResponse
	 * @throws LogException
	 *             if any error happened
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateShipperResponse CreateShipper(String project, String logStore,
			String shipperName, ShipperConfig shipConfig) throws LogException;

	/**
	 * update a shipper
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param shipperName
	 *            shipper name
	 * @param shipConfig
	 *            the OssShipperConfig or OdpsShipperConfig
	 * @return UpdateShipperResponse
	 * 
	 * @throws LogException
	 *             if any error happened
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public UpdateShipperResponse UpdateShipper(String project, String logStore,
			String shipperName, ShipperConfig shipConfig) throws LogException;

	/**
	 * Delete a shipper
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param ShipperName
	 *            shipper name
	 * @return DeleteShipperResponse
	 * 
	 * @throws LogException
	 *             if any error happened
	 * 
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteShipperResponse DeleteShipper(String project, String logStore,
			String ShipperName) throws LogException;

	/**
	 * Get a shipper config
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param shipperName
	 *            shipper name
	 * @return GetShipperResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetShipperResponse GetShipperConfig(String project, String logStore,
			String shipperName) throws LogException;

	/**
	 * List shipper of a logstore
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @return ListShipperResponse if any error happened
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListShipperResponse ListShipper(String project, String logStore)
			throws LogException;

	/**
	 * Get the tasks of a logstore shipper
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param shipperName
	 *            shipper name
	 * @param startTime
	 *            the start time in timestamp (from 1970.1.1)
	 * @param endTime
	 *            the end time
	 * @param statusType
	 *            one of ['', 'success', 'running', 'fail'], if statusType is
	 *            '', return all tasks
	 * @param offset
	 *            the offset
	 * @param size
	 *            the task count
	 * @return GetShipperTasksResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetShipperTasksResponse GetShipperTasks(String project,
			String logStore, String shipperName, int startTime, int endTime,
			String statusType, int offset, int size) throws LogException;

	/**
	 * retry the failed tasks, for every time it can only retry 10 task
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            logstore name
	 * @param shipperName
	 *            shipper name
	 * @param taskList
	 *            failed task id list
	 * @return RetryShipperTasksResponse
	 * 
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public RetryShipperTasksResponse RetryShipperTasks(String project,
			String logStore, String shipperName, List<String> taskList)
			throws LogException;

	/**
	 * create a project
	 * 
	 * @param project
	 *            project name
	 * @param projectDescription
	 *            project description
	 * @return CreateProjectResponse
	 * 
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public CreateProjectResponse CreateProject(String project,
			String projectDescription) throws LogException;

	/**
	 * get a project
	 * 
	 * @param project
	 *            project name
	 * @return GetProjectResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public GetProjectResponse GetProject(String project) throws LogException;
	
	/**
	 * list project
	 * 
	 * @param ProjectName
	 * 			project name
	 * @param offset
	 * 			offset
	 * @param size
	 * 			size
	 * @return ListProjectResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListProjectResponse ListProject(String ProjectName, int offset, int size) throws LogException;
	
	/**
	 * list all project
	 * 
	 * @param request
	 * 				request class
	 * @return ListProjectResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public ListProjectResponse ListProject(ListProjectRequest request) throws LogException;

	/**
	 * delete a project
	 * 
	 * @param project
	 *            project name
	 * @return DeleteProjectResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	public DeleteProjectResponse DeleteProject(String project)
			throws LogException;

	/**
	 * list project
	 * 
	 * @return ListProjectResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListProjectResponse ListProject() throws LogException;

	/**
	 * create saved search
	 * 
	 * @param request
	 * 				request class
	 * @return createSavedSearchResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	CreateSavedSearchResponse createSavedSearch(CreateSavedSearchRequest request) throws LogException;

	/**
	 * update saved search
	 * 
	 * @param request
	 * 				request class
	 * @return updateSavedSearchResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	UpdateSavedSearchResponse updateSavedSearch(UpdateSavedSearchRequest request) throws LogException;

	/**
	 * delete saved search
	 * 
	 * @param request
	 * 				request class
	 * @return deleteSavedSearchResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	DeleteSavedSearchResponse deleteSavedSearch(DeleteSavedSearchRequest request) throws LogException;

	/**
	 * get saved search
	 * 
	 * @param request
	 * 				request class
	 * @return GetSavedSearchResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	GetSavedSearchResponse getSavedSearch(GetSavedSearchRequest request) throws LogException;

	/**
	 * list saved search
	 * 
	 * @param request
	 * 				request class
	 * @return ListSavedSearchResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListSavedSearchResponse listSavedSearch(ListSavedSearchRequest request) throws LogException;
	
	/**
	 * create alert
	 * 
	 * @param request
	 * 				request class
	 * @return CreateAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	CreateAlertResponse createAlert(CreateAlertRequest request) throws LogException;

	/**
	 * update alert
	 * 
	 * @param request
	 * 				request class
	 * @return UpdateAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	UpdateAlertResponse updateAlert(UpdateAlertRequest request) throws LogException;

	/**
	 * delete alert
	 * 
	 * @param request
	 * 				request class
	 * @return DeleteAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	DeleteAlertResponse deleteAlert(DeleteAlertRequest request) throws LogException;

	/**
	 * get alert
	 * 
	 * @param request
	 * 				request class
	 * @return GetAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	GetAlertResponse getAlert(GetAlertRequest request) throws LogException;

	/**
	 * list alert
	 * 
	 * @param request
	 * 				request class
	 * @return GetAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListAlertResponse listAlert(ListAlertRequest request) throws LogException;

	/**
	 * list alert fail
	 * 
	 * @param request
	 * 				request class
	 * @return GetAlertResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListAlertFailResponse listAlertFail(ListAlertFailRequest request) throws LogException;

	CreateDashboardResponse createDashboard(CreateDashboardRequest request) throws LogException;
	UpdateDashboardResponse updateDashboard(UpdateDashboardRequest request) throws LogException;
	DeleteDashboardResponse deleteDashboard(DeleteDashboardRequest request) throws LogException;
	GetDashboardResponse getDashboard(GetDashboardRequest request) throws LogException;
	ListDashboardResponse listDashboard(ListDashboardRequest request) throws LogException;

	
	CreateChartResponse createChart(CreateChartRequest request) throws LogException;
	UpdateChartResponse updateChart(UpdateChartRequest request) throws LogException;
	DeleteChartResponse deleteChart(DeleteChartRequest request) throws LogException;
	GetChartResponse getChart(GetChartRequest request) throws LogException;

	/**
	 * @param request
     *			CreateEtlJobRequest
	 * @return
	 * 			CreateEtlJobResponse
	 * @throws LogException
	 */
	CreateEtlJobResponse createEtlJob(CreateEtlJobRequest request) throws LogException;

	/**
	 * @param request
	 * 			DeleteEtlJobRequest
	 * @return
	 * 			DeleteEtlJobResponse
	 * @throws LogException
	 */
	DeleteEtlJobResponse deleteEtlJob(DeleteEtlJobRequest request) throws LogException;

	/**
	 * @param request
	 * 			UpdateEtlJobRequest
	 * @return
	 * 			UpdateEtlJobResponse
	 * @throws LogException
	 */
	UpdateEtlJobResponse updateEtlJob(UpdateEtlJobRequest request) throws LogException;

	/**
	 * @param request
	 * 			GetEtlJobRequest
	 * @return
	 * 			GetEtlJobResponse
	 * @throws LogException
	 */
	GetEtlJobResponse getEtlJob(GetEtlJobRequest request) throws LogException;

	/**
	 * @param request
	 * 			ListEtlJobRequest
	 * @return
	 * 			ListEtlJobResponse
	 * @throws LogException
	 */
	ListEtlJobResponse listEtlJob(ListEtlJobRequest request) throws LogException;

	/**
	 * @param project
     * 			project name
	 * @param etlMeta
	 * 			etlMeta which contains the metaName/metaKey/metaTag/metaValue
	 * @return
     * 			CreateEtlMetaResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	CreateEtlMetaResponse createEtlMeta(String project, EtlMeta etlMeta) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param etlMetaName
     * 			etl meta name
	 * @param etlMetaKey
	 * 			etl meta key
	 * @return
	 * 			DeleteEtlMetaResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	DeleteEtlMetaResponse deleteEtlMeta(String project, String etlMetaName, String etlMetaKey) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param etlMetaName
	 * 			etl meta name
	 * @param etlMetaKey
	 * 			etl meta key
	 * @param etlMetaTag
	 * 			etl meta key
	 * @return
	 * 			DeleteEtlMetaResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	DeleteEtlMetaResponse deleteEtlMeta(String project, String etlMetaName, String etlMetaKey, String etlMetaTag) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param etlMeta
	 * 			etlMeta which contains the metaName/metaKey/metaTag/metaValue
	 * @return
	 * 			UpdateEtlMetaResponse
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	UpdateEtlMetaResponse updateEtlMeta(String project, EtlMeta etlMeta) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param offset
	 * 			should > 0
	 * @param size
	 * 			[1, 200]
	 * @return
	 * 			every request will return the subset [offset, offset + size) of complete etl meta name list
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListEtlMetaNameResponse listEtlMetaName(String project, int offset, int size) throws LogException;

	/**
	 * @param project
     * 			project name
	 * @param etlMetaName
	 * 			etl meta name
	 * @param offset
	 * 			should > 0
	 * @param size
	 * 			[1, 200]
	 * @return
	 * 			every request will return the subset [offset, offset + size) of complete etl meta list
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListEtlMetaResponse listEtlMeta(String project, String etlMetaName, int offset, int size) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param etlMetaName
	 * 			etl meta name
	 * @param etlMetaTag
	 * 			etl meta tag, useful to filter lots of meta keys
	 * @param offset
	 * 			should > 0
	 * @param size
	 * 			[1, 200]
	 * @return
	 * 			every request will return the subset [offset, offset + size) of complete etl meta list
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListEtlMetaResponse listEtlMeta(String project, String etlMetaName, String etlMetaTag, int offset, int size) throws LogException;

	/**
	 * @param project
	 * 			project name
	 * @param etlMetaName
	 * 			etl meta name
	 * @param etlMetaKey
	 * 			etl meta key
	 * @return
	 * 			every request will return the only one EtlMeta
	 * 			use ListEtlMetaResponse.GetHeadEtlMeta to get the value, null if no etl meta matched
	 * @throws LogException
	 *             if any error happened
	 * @throws NullPointerException
	 *             if required parameter is null
	 * @throws IllegalArgumentException
	 *             if any required string parameter is empty
	 */
	ListEtlMetaResponse getEtlMeta(String project, String etlMetaName, String etlMetaKey) throws LogException;

}
