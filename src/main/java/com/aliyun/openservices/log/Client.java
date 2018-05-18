/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.aliyun.openservices.log.request.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.routines.InetAddressValidator;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.common.EtlJob;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.AlertFail;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.ConsumerGroup;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.LZ4Encoder;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.LogtailProfile;
import com.aliyun.openservices.log.common.Machine;
import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.common.MachineList;
import com.aliyun.openservices.log.common.OdpsShipperConfig;
import com.aliyun.openservices.log.common.OssShipperConfig;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.common.SavedSearch;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.common.ShipperTask;
import com.aliyun.openservices.log.common.ShipperTasksStatistic;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.client.ClientConnectionContainer;
import com.aliyun.openservices.log.http.client.ClientConnectionHelper;
import com.aliyun.openservices.log.http.client.ClientConnectionStatus;
import com.aliyun.openservices.log.http.client.ClientException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.client.ServiceException;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.http.comm.RequestMessage;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.http.utils.CodingUtils;
import com.aliyun.openservices.log.http.utils.DateUtil;
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
import com.aliyun.openservices.log.response.GetIndexStringResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.GetLogtailProfileResponse;
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

/**
 * SlsClient class is the main class in the sdk, it implements the interfaces
 * defined in LogService. It can be used to send request to the log service
 * server to put/get data.
 * 
 * @author sls_dev
 * 
 */
public class Client implements LogService {
	private String httpType;
	private String hostName;
	private String accessId;
	private String accessKey;
	private String sourceIp;
	// private boolean compressFlag;
	private ServiceClient serviceClient;
	private String securityToken;
	private String realIpForConsole;
	private Boolean useSSLForConsole;
	private String userAgent = Consts.CONST_USER_AGENT_VALUE;
	private boolean mUUIDTag = false;
	private Boolean mUseDirectMode = false;
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRealIpForConsole() {
		return realIpForConsole;
	}

	public void setRealIpForConsole(String realIpForConsole) {
		this.realIpForConsole = realIpForConsole;
	}

	public boolean isUseSSLForConsole() {
		return useSSLForConsole;
	}

	public void setUseSSLForConsole(boolean useSSLForConsole) {
		this.useSSLForConsole = useSSLForConsole;
	}

	public void ClearConsoleResources() {
		realIpForConsole = null;
		useSSLForConsole = null;
	}

	public void EnableUUIDTag() { mUUIDTag = true; }

	public void DisableUUIDTag() { mUUIDTag = false; }

	public String GetSecurityToken() {
		return securityToken;
	}

	public void SetSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public void RemoveSecurityToken() {
		securityToken = null;
	}

	public void EnableDirectMode()
	{
		mUseDirectMode = true;
	}

	public void DisableDirectMode()
	{
		mUseDirectMode = false;
	}

	/**
	 * Construct the sls client with accessId, accessKey and server address, all
	 * other parameters will be set to default value
	 * 
	 * @throws NullPointerException
	 *             if the input parameter is null
	 * @throws IllegalArgumentException
	 *             if the input parameter is empty
	 * 
	 * @param endpoint
	 *            the log service server address
	 * @param accessId
	 *            aliyun accessId
	 * @param accessKey
	 *            aliyun accessKey
	 */
	public Client(String endpoint, String accessId, String accessKey) {
		this(endpoint, accessId, accessKey, GetLocalMachineIp());
	}

	/**
	 * Construct the sls client with accessId, accessKey , server address and
	 * client ip address, all other parameters will be set to default value
	 * 
	 * 
	 * @throws NullPointerException
	 *             if the input parameter is null
	 * @throws IllegalArgumentException
	 *             if the input parameter is empty
	 * 
	 * @param endpoint
	 *            the log service server address
	 * @param accessId
	 *            aliyun accessId
	 * @param accessKey
	 *            aliyun accessKey
	 * @param SourceIp
	 *            client ip address
	 */
	public Client(String endpoint, String accessId, String accessKey,
			String SourceIp) {
		this(endpoint, accessId, accessKey, SourceIp,
				Consts.DEFAULT_SLS_COMPRESS_FLAG);
	}

	/**
	 * Construct sls client with full parameters
	 *
	 * @throws NullPointerException
	 *             if the input parameter is null
	 * @throws IllegalArgumentException
	 *             if the input parameter is empty
	 *
	 * @param endpoint
	 *            the log service server address
	 * @param accessId
	 *            aliyun accessId
	 * @param accessKey
	 *            aliyun accessKey
	 * @param sourceIp
	 *            client ip address
	 * @param compressFlag
	 *            a flag to determine if the send data will compressed , default
	 *            is true ( data compressed)
	 * @param connectMaxCount
	 * 			  a flag to determine max count connection
	 * @param connectTimeout
	 * 			  a flag to determine max connect timeout
	 * @param sendTimeout
	 * 			  a flag to determine max request timeout
	 */
	public Client(String endpoint, String accessId, String accessKey,
				  String sourceIp, boolean compressFlag,
				  int connectMaxCount,
				  int connectTimeout,
				  int sendTimeout) {
		CodingUtils.assertStringNotNullOrEmpty(endpoint, "endpoint");
		CodingUtils.assertStringNotNullOrEmpty(accessId, "accessId");
		CodingUtils.assertStringNotNullOrEmpty(accessKey, "accessKey");

		if (endpoint.startsWith("http://")) {
			this.hostName = endpoint.substring(7);
			this.httpType = new String("http://");
		} else if (endpoint.startsWith("https://")) {
			this.hostName = endpoint.substring(8);
			this.httpType = new String("https://");
		} else {
			this.hostName = endpoint;
			this.httpType = new String("http://");
		}
		while (this.hostName.endsWith("/")) {
			this.hostName = this.hostName.substring(0,
					this.hostName.length() - 1);
		}
		if (IsIpAddress(this.hostName)) {
			throw new IllegalArgumentException("EndpontInvalid", new Exception(
					"The ip address is not supported"));
		}
		this.accessId = accessId;
		this.accessKey = accessKey;
		this.sourceIp = sourceIp;
		if (sourceIp == null || sourceIp.isEmpty()) {
			this.sourceIp = GetLocalMachineIp();
		}
		// this.compressFlag = compressFlag;
		ClientConfiguration clientConfig = new ClientConfiguration();

		clientConfig.setMaxConnections(connectMaxCount);
		clientConfig.setConnectionTimeout(connectTimeout);
		clientConfig.setSocketTimeout(sendTimeout);

		this.serviceClient = new DefaultServiceClient(clientConfig);
	}

	/**
	 * Construct sls client with full parameters
	 * 
	 * @throws NullPointerException
	 *             if the input parameter is null
	 * @throws IllegalArgumentException
	 *             if the input parameter is empty
	 * 
	 * @param endpoint
	 *            the log service server address
	 * @param accessId
	 *            aliyun accessId
	 * @param accessKey
	 *            aliyun accessKey
	 * @param sourceIp
	 *            client ip address
	 * @param compressFlag
	 *            a flag to determine if the send data will compressed , default
	 *            is true ( data compressed)
	 */
	public Client(String endpoint, String accessId, String accessKey,
			String sourceIp, boolean compressFlag) {
		this(endpoint, accessId, accessKey, sourceIp, compressFlag,
				Consts.HTTP_CONNECT_MAX_COUNT,
				Consts.HTTP_CONNECT_TIME_OUT,
				Consts.HTTP_SEND_TIME_OUT);
	}

	private URI GetHostURI(String project) {
		String endPointUrl = this.httpType + this.hostName;
		if (project != null && !project.isEmpty()) {
			endPointUrl = this.httpType + project + "." + this.hostName;
		}
		try {
			return new URI(endPointUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("EndpointInvalid", e);
		}

	}
	private URI GetHostURIByIp(String ip_addr) throws LogException
	{
		String endPointUrl = this.httpType + ip_addr;
		try {
			return new URI(endPointUrl);
		} catch (URISyntaxException e) {
			throw new LogException("EndpointInvalid", 
					"Failed to get real server ip when direct mode in enabled", "");
		}
	}

	private static boolean IsIpAddress(String str) {
		Pattern pattern = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}");
		return pattern.matcher(str).matches();
	}

	public GetLogtailProfileResponse ExtractLogtailProfile(Map<String, String> resHeaders, JSONObject object) throws LogException {
		try {
			int count = object.getInt("count");
			int total = object.getInt("total");
			JSONArray array = object.getJSONArray("profile");
			List<LogtailProfile> logtailProfiles = new ArrayList<LogtailProfile>();
			for (int i = 0; i < array.size(); i++) {
				JSONObject profileObj = array.getJSONObject(i);
				LogtailProfile logtailProfile = new LogtailProfile();
				logtailProfile.FromJsonObject(profileObj);
				logtailProfiles.add(logtailProfile);
			}
			return new GetLogtailProfileResponse(resHeaders, count, total, logtailProfiles);

		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), GetRequestId(resHeaders));
		}
	}
	
	public GetLogtailProfileResponse GetLogtailProfile(String project, String logstore, String source, 
			int line, int offset) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logstore, "logstore");
		
		GetLogtailProfileRequest request = new GetLogtailProfileRequest(project, logstore, source, line, offset);
		return GetLogtailProfile(request);
	}
	
	public GetLogtailProfileResponse GetLogtailProfile(GetLogtailProfileRequest request) throws LogException
	{
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/logstores/" + request.getLogStore() + Consts.CONST_GETLOGTAILPROFILE_URI;

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		return ExtractLogtailProfile(resHeaders, object);
	}
	
	public GetHistogramsResponse GetHistograms(String project, String logStore,
			int from, int to, String topic, String query) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");

		GetHistogramsRequest request = new GetHistogramsRequest(project,
				logStore, topic, query, from, to);

		return GetHistograms(request);

	}

	public GetHistogramsResponse GetHistograms(GetHistogramsRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/logstores/" + request.GetLogStore() + "/index";

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray object = ParseResponseMessageToArray(response, requestId);
		GetHistogramsResponse hisTogramResponse = new GetHistogramsResponse(
				resHeaders);
		ExtractHistograms(hisTogramResponse, object);
		return hisTogramResponse;

	}

	public PutLogsResponse PutLogs(String project, String logStore, byte[] logGroupBytes, String compressType, String shardHash) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(logGroupBytes, "logGroupBytes");
		PutLogsRequest request = new PutLogsRequest(project, logStore, null,
				null, logGroupBytes, shardHash);
		if (compressType.equals(Consts.CONST_LZ4)) {
			request.SetCompressType(CompressType.LZ4);
		} else if (compressType.equals(Consts.CONST_GZIP_ENCODING)) {
			request.SetCompressType(CompressType.GZIP);
		} else if (compressType.isEmpty()) {
			request.SetCompressType(CompressType.NONE);
		} else {
			throw new IllegalArgumentException("invalid CompressType: " + compressType + ", should be (" + CompressType.NONE + ", " + CompressType.GZIP + ", " + CompressType.LZ4 + ")");
		}
		return PutLogs(request);
	}

	@Override
	public PutLogsResponse PutLogs(String project, String logStore,
			String topic, List<LogItem> logItems, String source,
			String shardHash) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(logItems, "logGroup");
		// CodingUtils.assertParameterNotNull(source, "source");
		PutLogsRequest request = new PutLogsRequest(project, logStore, topic,
				source, logItems, shardHash);
		request.SetCompressType(CompressType.LZ4);
		return PutLogs(request);
	}

	@Override
	public PutLogsResponse PutLogs(String project, String logStore,
			String topic, List<LogItem> logItems, String source)
			throws LogException {
		// TODO Auto-generated method stub
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(logItems, "logGroup");
		// CodingUtils.assertParameterNotNull(source, "source");
		PutLogsRequest request = new PutLogsRequest(project, logStore, topic,
				source, logItems, null);
		request.SetCompressType(CompressType.LZ4);
		return PutLogs(request);
	}

	public PutLogsResponse PutLogs(PutLogsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		String shardKey = request.GetRouteKey();
		CompressType compressType = request.GetCompressType();
		CodingUtils.assertParameterNotNull(compressType, "compressType");

		byte[] logBytes = request.GetLogGroupBytes();
		if (logBytes != null) {
		} else {
			List<LogItem> logItems = request.GetLogItems();
			if (logItems.size() > Consts.CONST_MAX_PUT_LINES) {
				throw new LogException("InvalidLogSize",
						"logItems' length exceeds maximum limitation : " + String.valueOf(Consts.CONST_MAX_PUT_LINES) + " lines", "");
			}
			String topic = request.GetTopic();
			CodingUtils.assertParameterNotNull(topic, "topic");
			String source = request.GetSource();
			if (request.getContentType() != Consts.CONST_SLS_JSON) {
				Logs.LogGroup.Builder logs = Logs.LogGroup.newBuilder();
				if (topic != null) {
					logs.setTopic(topic);
				}
				if (source == null || source.isEmpty()) {
					logs.setSource(this.sourceIp);
				} else {
					logs.setSource(source);
				}
				ArrayList<TagContent> tags = request.GetTags();
				if (tags != null && tags.size() > 0) {
					for (TagContent tag : tags) {
						Logs.LogTag.Builder tagBuilder = logs.addLogTagsBuilder();
						tagBuilder.setKey(tag.getKey());
						tagBuilder.setValue(tag.getValue());
					}
				}
				if (this.mUUIDTag) {
					Logs.LogTag.Builder tagBuilder = logs.addLogTagsBuilder();
					tagBuilder.setKey("__pack_unique_id__");
					tagBuilder.setValue(UUID.randomUUID().toString() + "-" + String.valueOf(Math.random()));
				}
				for (int i = 0; i < logItems.size(); i++) {
					LogItem item = logItems.get(i);
					Logs.Log.Builder log = logs.addLogsBuilder();
					log.setTime(item.mLogTime);
					for (LogContent content : item.mContents) {
						CodingUtils.assertStringNotNullOrEmpty(content.mKey, "key");
						Logs.Log.Content.Builder contentBuilder = log
								.addContentsBuilder();
						contentBuilder.setKey(content.mKey);
						if (content.mValue == null) {
							contentBuilder.setValue("");
						} else {
							contentBuilder.setValue(content.mValue);
						}
					}
				}
				logBytes = logs.build().toByteArray();
			} else {
				JSONObject jsonObj = new JSONObject();
				if (topic != null) {
					jsonObj.put("__topic__", topic);
				}
				if (source == null || source.isEmpty()) {
					jsonObj.put("__source__", this.sourceIp);
				} else {
					jsonObj.put("__source__", source);
				}
				JSONArray logsArray = new JSONArray();
				for (int i = 0; i < logItems.size(); i++) {
					LogItem item = logItems.get(i);
					JSONObject jsonObjInner = new JSONObject();
					jsonObjInner.put("__time__", item.mLogTime);
					for (LogContent content : item.mContents) {
						jsonObjInner.put(content.mKey, content.mValue);
					}
					logsArray.add(jsonObjInner);
				}
				jsonObj.put("__logs__", logsArray);
				JSONObject tagObj = new JSONObject();
				ArrayList<TagContent> tags = request.GetTags();
				if (tags != null && tags.size() > 0) {
					for (TagContent tag : tags) {
						tagObj.put(tag.getKey(), tag.getValue());
					}
				}
				if (this.mUUIDTag)  {
					tagObj.put("__pack_unique_id__", UUID.randomUUID().toString() + "-" + String.valueOf(Math.random()));
				}
				if (tagObj.size() > 0) {
					jsonObj.put("__tags__", tagObj);
				}
				try {
					logBytes = jsonObj.toString().getBytes("utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new LogException("UnsupportedEncoding", e.getMessage(), "");
				}
			}
		}
		if (logBytes.length > Consts.CONST_MAX_PUT_SIZE) {
			throw new LogException("InvalidLogSize",
					"logItems' size exceeds maximum limitation : "
							+ String.valueOf(Consts.CONST_MAX_PUT_SIZE)
							+ " bytes", "");
		}

		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, request.getContentType());
		long orignalSize = logBytes.length;

		if (compressType == CompressType.LZ4) {
			logBytes = LZ4Encoder.compressToLhLz4Chunk(logBytes.clone());
			headParameter.put(Consts.CONST_X_SLS_COMPRESSTYPE,
					compressType.toString());
		} else if (compressType == CompressType.GZIP) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(
					logBytes.length);

			Deflater compresser = new Deflater();
			compresser.setInput(logBytes);
			compresser.finish();

			byte[] buf = new byte[10240];
			while (compresser.finished() == false) {
				int count = compresser.deflate(buf);
				out.write(buf, 0, count);
			}

			logBytes = out.toByteArray();
			headParameter.put(Consts.CONST_X_SLS_COMPRESSTYPE,
					compressType.toString());
		}

		headParameter.put(Consts.CONST_X_SLS_BODYRAWSIZE,
				String.valueOf(orignalSize));

		String resourceUri = "/logstores/" + logStore;
		if (shardKey == null || shardKey.length() == 0) {
			resourceUri += "/shards/lb";
		} else
			resourceUri += "/shards/route?key=" + shardKey;
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		long cmp_size = logBytes.length;

		
		for (int i = 0; i < 2; i++) {
			String server_ip = null;
			ClientConnectionStatus connection_status = null;
			if (this.mUseDirectMode) {
				connection_status = GetGlobalConnectionStatus();
				server_ip = connection_status.GetIpAddress();
			}
			try {
				ResponseMessage response = SendData(project, HttpMethod.POST, resourceUri, urlParameter, headParameter,
						logBytes, null, server_ip);
				Map<String, String> resHeaders = response.getHeaders();
				PutLogsResponse putLogsResponse = new PutLogsResponse(resHeaders);
				if (connection_status != null) {
					connection_status.AddSendDataSize(cmp_size);
					connection_status.UpdateLastUsedTime(System.nanoTime());
				}
				return putLogsResponse;
			} catch (LogException e) {			
				String request_id = e.GetRequestId();
				if (i == 1 || request_id != null && request_id.isEmpty() == false)
				{
					throw e;
				}
				if (connection_status != null)
				{
					connection_status.DisableConnection();
				}
			}
		}
		return null; // never happen
	}
	
	private ClientConnectionStatus GetGlobalConnectionStatus() throws LogException {
		ClientConnectionContainer connection_container = ClientConnectionHelper.getInstance()
				.GetConnectionContainer(this.hostName, this.accessId, this.accessKey);
		ClientConnectionStatus connection_status = connection_container.GetGlobalConnection();
		if (connection_status == null || connection_status.IsValidConnection() == false) {
			connection_container.UpdateGlobalConnection();
			connection_status = connection_container.GetGlobalConnection();
			if (connection_status == null || connection_status.GetIpAddress() == null
					|| connection_status.GetIpAddress().isEmpty()) {
				throw new LogException("EndpointInvalid", "Failed to get real server ip when direct mode is enabled",
						"");
			}
		}
		return connection_status;
	}

	private ClientConnectionStatus GetShardConnectionStatus(String project, String logstore, int shard_id)
			throws LogException {
		ClientConnectionContainer connection_container = ClientConnectionHelper.getInstance()
				.GetConnectionContainer(this.hostName, this.accessId, this.accessKey);
		ClientConnectionStatus connection_status = connection_container.GetShardConnection(project, logstore, shard_id);
		if (connection_status != null && connection_status.IsValidConnection()) {
			return connection_status;
		}
		return GetGlobalConnectionStatus();
	}

	public GetLogsResponse GetLogs(String project, String logStore, int from,
			int to, String topic, String query) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query);
		return GetLogs(request);
	}

	public GetLogsResponse GetLogs(String project, String logStore, int from,
			int to, String topic, String query, int line, int offset,
			boolean reverse) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query, offset, line, reverse);
		return GetLogs(request);
	}

	public GetLogsResponse GetProjectLogs(String project,String query) throws  LogException
	{
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(query, "query");
		GetProjectLogsRequest request = new GetProjectLogsRequest(project, query);
		return GetProjectLogs(request);
	}

	public GetLogsResponse GetProjectLogs(GetProjectLogsRequest request) throws  LogException
	{
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();

		String project = request.GetProject();

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/logs";

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		com.alibaba.fastjson.JSONArray object = ParseResponseMessageToArrayWithFastJson(response, requestId);
		GetLogsResponse getLogsResponse = new GetLogsResponse(resHeaders);
		ExtractLogsWithFastJson(getLogsResponse, object);
		return getLogsResponse;
	}
	private com.alibaba.fastjson.JSONArray ParseResponseMessageToArrayWithFastJson(ResponseMessage response,
			String requestId) throws LogException {
		byte[] body = response.GetRawBody();
		if (body == null) {
			throw new LogException("BadResponse", "The response body is null",
					null, requestId);
		}
		String returnStr;
		try {
			returnStr = new String(body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("BadResponse",
					"The response is not valid utf-8 string : ", e, requestId);
		}

		try {
			com.alibaba.fastjson.JSONArray array = com.alibaba.fastjson.JSONArray.parseArray(returnStr);
			return array;
		} catch (com.alibaba.fastjson.JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid json string : " + returnStr, e,
					requestId);
		}
	}
	
	private void ExtractLogsWithFastJson(GetLogsResponse response, com.alibaba.fastjson.JSONArray logs) {
		try {
			for (int i = 0; i < logs.size(); i++) {
				com.alibaba.fastjson.JSONObject log = logs.getJSONObject(i);
				String source = new String();
				LogItem logItem = new LogItem();
				Set<String> keySet = log.keySet();
				for (String key:keySet) {
					String value = log.getString(key);
					if (key.equals(Consts.CONST_RESULT_SOURCE)) {
						source = value;
					} else if (key.equals(Consts.CONST_RESULT_TIME)) {
						logItem.mLogTime = Integer.parseInt(value);
					} else {
						logItem.PushBack(key, value);
					}
				}
				response.AddLog(new QueriedLog(source, logItem));
			}
		} catch (JSONException e) {
			// ignore;
		}

	}
	
	public GetLogsResponse GetLogs(GetLogsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();

		String project = request.GetProject();
		String logStore = request.GetLogStore();

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/logstores/" + logStore + "/index";

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		com.alibaba.fastjson.JSONArray object = ParseResponseMessageToArrayWithFastJson(response, requestId);
		GetLogsResponse getLogsResponse = new GetLogsResponse(resHeaders);
		ExtractLogsWithFastJson(getLogsResponse, object);
		return getLogsResponse;

	}

	public ListLogStoresResponse ListLogStores(String project, int offset,
			int size, String logstoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");

		ListLogStoresRequest request = new ListLogStoresRequest(project,
				offset, size, logstoreName);

		return ListLogStores(request);

	}

	public ListLogStoresResponse ListLogStores(ListLogStoresRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/logstores";

		String project = request.GetProject();

		Map<String, String> headParameter = GetCommonHeadPara(project);

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		ListLogStoresResponse listLogStoresResponse = new ListLogStoresResponse(
				resHeaders);
		listLogStoresResponse.SetLogStores(ExtractJsonArray(
				Consts.CONST_RESULT_LOG_STORES, object));

		listLogStoresResponse.SetTotal(this.ExtractJsonInteger(
				Consts.CONST_TOTAL, object));
		return listLogStoresResponse;
	}

	public ListTopicsResponse ListTopics(String project, String logStore,
			String token, int line) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(token, "token");
		ListTopicsRequest request = new ListTopicsRequest(project, logStore,
				token, line);
		return ListTopics(request);
	}

	public ListTopicsResponse ListTopics(ListTopicsRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		String logStore = request.GetLogStore();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/logstores/" + logStore + "/index";
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONArray json_array = this.ParseResponseMessageToArray(response,
				requestId);
		ListTopicsResponse listTopicRespone = new ListTopicsResponse(resHeaders);
		List<String> string_array = new ArrayList<String>();
		for (int index = 0; index < json_array.size(); index++) {
			string_array.add(json_array.getString(index));
		}
		listTopicRespone.SetTopics(string_array);
		return listTopicRespone;

	}

	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, long fromTime) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		GetCursorRequest request = new GetCursorRequest(project, logStore,
				shardId, fromTime);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, Date fromTime) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStream");
		CodingUtils.assertParameterNotNull(fromTime, "fromTime");
		long timeStamp = fromTime.getTime() / (long) 1000;
		GetCursorRequest request = new GetCursorRequest(project, logStore,
				shardId, timeStamp);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(String project, String logStream,
			int shardId, CursorMode mode) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStream, "logStream");
		GetCursorRequest request = new GetCursorRequest(project, logStream,
				shardId, mode);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(GetCursorRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		String shardId = String.valueOf(request.GetShardId());

		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);
		String resourceUri = resourceUriBuilder.toString();

		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		GetCursorResponse getCursorResponse = null;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);
			JSONObject object = ParserResponseMessage(response, requestId);

			getCursorResponse = new GetCursorResponse(resHeaders,
					object.getString("cursor"));
		} catch (JSONException e) {
			throw new LogException("FailToCreateCursor", e.getMessage(), e,
					GetRequestId(response.getHeaders()));
		}

		return getCursorResponse;
	}

	public GetCursorTimeResponse GetCursorTime(GetCursorTimeRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		String shardId = String.valueOf(request.GetShardId());

		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);
		String resourceUri = resourceUriBuilder.toString();

		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		GetCursorTimeResponse getCursorTimeResponse = null;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);
			JSONObject object = ParserResponseMessage(response, requestId);

			getCursorTimeResponse = new GetCursorTimeResponse(resHeaders,
					object.getInt("cursor_time"));
		} catch (JSONException e) {
			throw new LogException("FailToCreateCursor", e.getMessage(), e,
					GetRequestId(response.getHeaders()));
		}

		return getCursorTimeResponse;
	}
	public GetCursorTimeResponse GetPrevCursorTime(String project, String logStore,int shardId, String cursor) throws LogException
	{
		if(cursor.isEmpty())
			throw new LogException("InvalidCursor", "empty cursor string", "");
		Long prv = Long.parseLong(new String(Base64.decodeBase64(cursor))) - 1;
		if(prv >= 0){
			cursor = new String(Base64.encodeBase64(prv.toString().getBytes()));
		}
		else{
			throw new LogException("InvalidCursor", "this cursor has no prev value", "");
		}
		GetCursorTimeRequest request = new GetCursorTimeRequest(project,
				logStore, shardId, cursor);
		return GetCursorTime(request);
	}
	public GetCursorTimeResponse GetCursorTime(String project, String logStore,
			int shardId, String cursor) throws LogException {
		GetCursorTimeRequest request = new GetCursorTimeRequest(project,
				logStore, shardId, cursor);
		return GetCursorTime(request);
	}

	public ListShardResponse SplitShard(String prj, String logStore,
			int shardId, String midHash) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(prj, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "shardId");
		CodingUtils.assertStringNotNullOrEmpty(midHash, "midHash");
		return SplitShard(new SplitShardRequest(prj, logStore, shardId, midHash));
	}

	public ListShardResponse SplitShard(SplitShardRequest request)
			throws LogException {
		// TODO Auto-generated method stub
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		int shardId = request.GetShardId();
		String midHash = request.GetMidHash();
		CodingUtils.assertStringNotNullOrEmpty(midHash, "midHashKey");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		String requestId = "";
		JSONArray array = null;
		ResponseMessage response = new ResponseMessage();
		ListShardResponse listShardResponse = null;

		response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		requestId = GetRequestId(resHeaders);

		ArrayList<Shard> shards = new ArrayList<Shard>();

		array = ParseResponseMessageToArray(response, requestId);
		shards = ExtractShards(array, requestId);

		listShardResponse = new ListShardResponse(resHeaders, shards);

		return listShardResponse;
	}

	public ListShardResponse MergeShards(String prj, String logStore,
			int shardId) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(prj, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "shardId");
		return MergeShards(new MergeShardsRequest(prj, logStore, shardId));
	}

	public ListShardResponse MergeShards(MergeShardsRequest request)
			throws LogException {
		// TODO Auto-generated method stub
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		int shardId = request.GetShardId();

		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		String requestId = "";
		JSONArray array = null;
		ResponseMessage response = new ResponseMessage();
		ListShardResponse listShardResponse = null;

		response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		requestId = GetRequestId(resHeaders);

		ArrayList<Shard> shards = new ArrayList<Shard>();

		array = ParseResponseMessageToArray(response, requestId);
		shards = ExtractShards(array, requestId);

		listShardResponse = new ListShardResponse(resHeaders, shards);

		return listShardResponse;
	}

	public DeleteShardResponse DeleteShard(String prj, String logStore,
			int shardId) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(prj, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "shardId");
		return DeleteShard(new DeleteShardRequest(prj, logStore, shardId));
	}

	public DeleteShardResponse DeleteShard(DeleteShardRequest request)
			throws LogException {
		// TODO Auto-generated method stub
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		int shardId = request.GetShardId();

		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();

		response = SendData(project, HttpMethod.DELETE, resourceUri,
				urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();

		DeleteShardResponse res = new DeleteShardResponse(resHeaders);

		return res;
	}

	public ListShardResponse ListShard(String prj, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(prj, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return ListShard(new ListShardRequest(prj, logStore));
	}

	public String GetServerIpAddress(String project)
	{
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/direct_mode_ip";
		Map<String, String> urlParameter = new HashMap<String, String>();
		Map<String, String> out_header = new HashMap<String, String>();
		try {
			SendData(project, HttpMethod.GET, resourceUri, urlParameter,
					headParameter, new byte[0], out_header, null);
		} catch (LogException e) {
			// ignore
		}
		if(out_header.containsKey(Consts.CONST_X_SLS_HOSTIP))
		{
			return out_header.get(Consts.CONST_X_SLS_HOSTIP);
		}
		return "";
	}
	public ListShardResponse ListShard(ListShardRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		String requestId = "";
		JSONArray array = null;
		ResponseMessage response = new ResponseMessage();
		ListShardResponse listShardResponse = null;

		response = SendData(project, HttpMethod.GET, resourceUri, urlParameter,
				headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		requestId = GetRequestId(resHeaders);

		ArrayList<Shard> shards = new ArrayList<Shard>();

		array = ParseResponseMessageToArray(response, requestId);
		shards = ExtractShards(array, requestId);

		listShardResponse = new ListShardResponse(resHeaders, shards);

		return listShardResponse;
	}

	protected String GetRequestId(Map<String, String> headers) {
		if (headers.containsKey(Consts.CONST_X_SLS_REQUESTID)) {
			return headers.get(Consts.CONST_X_SLS_REQUESTID);
		} else {
			return "";
		}
	}

	@Override
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return BatchGetLog(new BatchGetLogRequest(project, logStore, shardId,
				count, cursor));
	}

	@Override
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor, String end_cursor)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return BatchGetLog(new BatchGetLogRequest(project, logStore, shardId,
				count, cursor, end_cursor));
	}

	@Override
	public BatchGetLogResponse BatchGetLog(BatchGetLogRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		String shardId = String.valueOf(request.GetShardId());
		CodingUtils.assertStringNotNullOrEmpty(shardId, "shardId");

		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shards/").append(shardId);

		String resourceUri = resourceUriBuilder.toString();

		headParameter.put(Consts.CONST_ACCEPT_ENCODING, Consts.CONST_LZ4);

		headParameter.put(Consts.CONST_HTTP_ACCEPT, Consts.CONST_PROTO_BUF);

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		BatchGetLogResponse batchGetLogResponse = null;
		for (int i = 0; i < 2; i++) {
			String server_ip = null;
			ClientConnectionStatus connection_status = null;
			if (this.mUseDirectMode) {
				connection_status = GetShardConnectionStatus(project, logStore, request.GetShardId());
				server_ip = connection_status.GetIpAddress();
			}
			try {
				response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headParameter, new byte[0], null,
						server_ip);
				Map<String, String> resHeaders = response.getHeaders();
				byte[] rawData = response.GetRawBody();

				batchGetLogResponse = new BatchGetLogResponse(resHeaders, rawData);
				if (connection_status != null)
				{
					connection_status.UpdateLastUsedTime(System.nanoTime());
					connection_status.AddPullDataSize(batchGetLogResponse.GetRawSize());
				}
				return batchGetLogResponse;
			} catch (LogException e) {
				if (i == 1 || e.GetRequestId() != null && e.GetRequestId().isEmpty() == false) {
					throw e;
				}
				if (connection_status != null) {
					connection_status.DisableConnection();
				}
			}
		}
		return null; // never happen
	}

	public CreateConfigResponse CreateConfig(String project, Config config)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(config, "config");
		return CreateConfig(new CreateConfigRequest(project, config));
	}


	
	public CreateConfigResponse CreateConfig(CreateConfigRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Config config = request.GetConfig();
		CodingUtils.assertParameterNotNull(config, "config");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = config.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/configs";

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = new ResponseMessage();
		CreateConfigResponse createConfigResponse = null;

		response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		createConfigResponse = new CreateConfigResponse(resHeaders);

		return createConfigResponse;
	}

	public UpdateConfigResponse UpdateConfig(String project, Config config)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(config, "config");
		return UpdateConfig(new UpdateConfigRequest(project, config));
	}

	public UpdateConfigResponse UpdateConfig(UpdateConfigRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Config config = request.GetConfig();
		CodingUtils.assertParameterNotNull(config, "config");
		String configName = config.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = config.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/configs/").append(configName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = new ResponseMessage();
		UpdateConfigResponse updateConfigResponse = null;

		response = SendData(project, HttpMethod.PUT, resourceUri, urlParameter,
				headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();

		updateConfigResponse = new UpdateConfigResponse(resHeaders);

		return updateConfigResponse;
	}

	protected Config ExtractConfigFromResponse(JSONObject dict, String requestId)
			throws LogException {
		Config config = new Config();
		try {
			config.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return config;
	}

	public GetConfigResponse GetConfig(String project, String configName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return GetConfig(new GetConfigRequest(project, configName));
	}

	public GetConfigResponse GetConfig(GetConfigRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String configName = request.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/configs/").append(configName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		GetConfigResponse getConfigResponse = null;

		response = SendData(project, HttpMethod.GET, resourceUri, urlParameter,
				headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		Config config = null;

		config = ExtractConfigFromResponse(object, requestId);

		getConfigResponse = new GetConfigResponse(resHeaders, config);

		return getConfigResponse;
	}

	public DeleteConfigResponse DeleteConfig(String project, String configName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return DeleteConfig(new DeleteConfigRequest(project, configName));
	}

	public DeleteConfigResponse DeleteConfig(DeleteConfigRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String configName = request.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/configs/").append(configName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteConfigResponse(resHeaders);

	}

	protected List<String> ExtractConfigs(JSONObject object, String requestId)
			throws LogException {
		List<String> configs = new ArrayList<String>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("configs");
			for (int i = 0; i < array.size(); i++) {
				String configName = array.getString(i);
				configs.add(configName);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid config json array string : "
							+ array.toString(), e, requestId);
		}

		return configs;
	}

	public ListConfigResponse ListConfig(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListConfig(new ListConfigRequest(project));
	}

	public ListConfigResponse ListConfig(String project, int offSet, int size)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListConfig(new ListConfigRequest(project, offSet, size));
	}

	public ListConfigResponse ListConfig(String project, String configName,
			int offSet, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return ListConfig(new ListConfigRequest(project, configName, offSet,
				size));
	}

	public ListConfigResponse ListConfig(String project, String configName,
			String logstoreName, int offSet, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		CodingUtils.assertStringNotNullOrEmpty(logstoreName, "logstoreName");
		return ListConfig(new ListConfigRequest(project, configName,
				logstoreName, offSet, size));
	}

	public ListConfigResponse ListConfig(ListConfigRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/configs";

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		ListConfigResponse listConfigResponse = null;
		JSONObject object = null;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);

			object = ParserResponseMessage(response, requestId);

			int total = 0;
			int count = 0;
			List<String> configs = new ArrayList<String>();

			total = object.getInt("total");
			count = object.getInt("count");
			configs = ExtractConfigs(object, requestId);

			listConfigResponse = new ListConfigResponse(resHeaders, count,
					total, configs);
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid list config json string : "
							+ object.toString(), e,
					GetRequestId(response.getHeaders()));
		}
		return listConfigResponse;
	}

	public CreateMachineGroupResponse CreateMachineGroup(String project,
			MachineGroup group) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(group, "group");
		return CreateMachineGroup(new CreateMachineGroupRequest(project, group));
	}

	public CreateMachineGroupResponse CreateMachineGroup(
			CreateMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		MachineGroup group = request.GetMachineGroup();
		CodingUtils.assertParameterNotNull(group, "group");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = group.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/machinegroups";

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateMachineGroupResponse(resHeaders);
	}

	public UpdateMachineGroupResponse UpdateMachineGroup(String project,
			MachineGroup group) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(group, "group");
		return UpdateMachineGroup(new UpdateMachineGroupRequest(project, group));
	}

	public UpdateMachineGroupResponse UpdateMachineGroup(
			UpdateMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		MachineGroup group = request.GetMachineGroup();
		CodingUtils.assertParameterNotNull(group, "group");
		String groupName = group.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = group.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateMachineGroupResponse(resHeaders);
	}

	protected MachineGroup ExtractMachineGroupFromResponse(JSONObject dict,
			String requestId) throws LogException {
		MachineGroup group = new MachineGroup();
		try {
			group.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return group;
	}

	protected ArrayList<String> ExtractConfigsFromResponse(JSONObject object,
			String requestId) throws LogException {
		ArrayList<String> configs = new ArrayList<String>();
		JSONArray configobj = object.getJSONArray("configs");
		for (int i = 0; i < configobj.size(); ++i) {
			configs.add(configobj.getString(i));
		}
		return configs;
	}

	protected ArrayList<String> ExtractConfigMachineGroupFromResponse(
			JSONObject object, String requestId) throws LogException {
		ArrayList<String> configs = new ArrayList<String>();
		JSONArray configobj = object.getJSONArray("machinegroups");
		for (int i = 0; i < configobj.size(); ++i) {
			configs.add(configobj.getString(i));
		}
		return configs;
	}

	public GetAppliedConfigResponse GetAppliedConfig(String project,
			String groupName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return GetAppliedConfig(new GetAppliedConfigsRequest(project, groupName));
	}

	public GetAppliedConfigResponse GetAppliedConfig(
			GetAppliedConfigsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName)
				.append("/configs");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		ArrayList<String> group = null;
		group = ExtractConfigsFromResponse(object, requestId);
		return new GetAppliedConfigResponse(resHeaders, group);
	}

	public GetAppliedMachineGroupsResponse GetAppliedMachineGroups(
			String project, String configName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return GetAppliedMachineGroups(new GetAppliedMachineGroupRequest(
				project, configName));
	}

	public GetAppliedMachineGroupsResponse GetAppliedMachineGroups(
			GetAppliedMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String configName = request.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "groupName");
		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/configs/").append(configName)
				.append("/machinegroups");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		ArrayList<String> group = null;

		group = this.ExtractConfigMachineGroupFromResponse(object, requestId);

		return new GetAppliedMachineGroupsResponse(resHeaders, group);

	}

	public GetMachineGroupResponse GetMachineGroup(String project,
			String groupName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return GetMachineGroup(new GetMachineGroupRequest(project, groupName));
	}

	public GetMachineGroupResponse GetMachineGroup(
			GetMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		GetMachineGroupResponse getMachineGroupResponse = null;

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);

		MachineGroup group = ExtractMachineGroupFromResponse(object, requestId);

		getMachineGroupResponse = new GetMachineGroupResponse(resHeaders, group);

		return getMachineGroupResponse;
	}

	@Override
	public ListMachinesResponse ListMachines(String project,
			String machineGroup, int offset, int size) throws LogException {

		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(machineGroup, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(machineGroup)
				.append("/machines");

		String resourceUri = resourceUriBuilder.toString();

		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));

		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put("offset", String.valueOf(offset));
		urlParameter.put("size", String.valueOf(size));

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONObject object = ParserResponseMessage(response, requestId);

		return ExtructMachinesFromResponse(resHeaders, object);

	}

	private ListMachinesResponse ExtructMachinesFromResponse(
			Map<String, String> resHeaders, JSONObject dict)
			throws LogException {

		try {
			int count = dict.getInt("count");
			int total = dict.getInt("total");
			JSONArray array = dict.getJSONArray("machines");
			List<Machine> machines = new ArrayList<Machine>();
			for (int i = 0; i < array.size(); i++) {
				JSONObject machine_obj = array.getJSONObject(i);
				Machine machine = new Machine();
				machine.FromJsonObject(machine_obj);
				machines.add(machine);
			}
			return new ListMachinesResponse(resHeaders, count, total, machines);

		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), GetRequestId(resHeaders));
		}
	}

	public ApproveMachineGroupResponse ApproveMachineGroup(String project,
			String groupName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return ApproveMachineGroup(new ApproveMachineGroupRequest(project,
				groupName));
	}
	
	public ApproveMachineGroupResponse ApproveMachineGroup(
			ApproveMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName).append("/approve");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new ApproveMachineGroupResponse(resHeaders);

	}
	
	public DeleteMachineGroupResponse DeleteMachineGroup(String project,
			String groupName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return DeleteMachineGroup(new DeleteMachineGroupRequest(project,
				groupName));
	}

	public DeleteMachineGroupResponse DeleteMachineGroup(
			DeleteMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteMachineGroupResponse(resHeaders);

	}

	protected List<String> ExtractMachineGroups(JSONObject object,
			String requestId) throws LogException {
		List<String> machineGroups = new ArrayList<String>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("machinegroups");
			for (int i = 0; i < array.size(); i++) {
				String groupName = array.getString(i);
				machineGroups.add(groupName);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid machine group json array string : "
							+ array.toString(), e, requestId);
		}

		return machineGroups;
	}

	public ListMachineGroupResponse ListMachineGroup(String project)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListMachineGroup(new ListMachineGroupRequest(project));
	}

	public ListMachineGroupResponse ListMachineGroup(String project,
			int offSet, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListMachineGroup(new ListMachineGroupRequest(project, offSet,
				size));
	}

	public ListMachineGroupResponse ListMachineGroup(String project,
			String groupName, int offSet, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return ListMachineGroup(new ListMachineGroupRequest(project, groupName,
				offSet, size));
	}

	public ListMachineGroupResponse ListMachineGroup(
			ListMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/machinegroups";

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		ListMachineGroupResponse listMachineGroupResponse = null;
		JSONObject object = null;

		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);

			object = ParserResponseMessage(response, requestId);

			int total = 0;
			int count = 0;
			List<String> groups = new ArrayList<String>();

			total = object.getInt("total");
			count = object.getInt("count");

			groups = ExtractMachineGroups(object, requestId);

			listMachineGroupResponse = new ListMachineGroupResponse(resHeaders,
					count, total, groups);
		} catch (LogException e) {
			throw e;
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid config json string : "
							+ object.toString(), e,
					GetRequestId(response.getHeaders()));
		}
		return listMachineGroupResponse;
	}

	public ApplyConfigToMachineGroupResponse ApplyConfigToMachineGroup(
			String project, String groupName, String configName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return ApplyConfigToMachineGroup(new ApplyConfigToMachineGroupRequest(
				project, groupName, configName));
	}

	public ApplyConfigToMachineGroupResponse ApplyConfigToMachineGroup(
			ApplyConfigToMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		String configName = request.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName)
				.append("/configs/").append(configName);
		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new ApplyConfigToMachineGroupResponse(resHeaders);

	}

	public RemoveConfigFromMachineGroupResponse RemoveConfigFromMachineGroup(
			String project, String groupName, String configName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return RemoveConfigFromMachineGroup(new RemoveConfigFromMachineGroupRequest(
				project, groupName, configName));
	}

	public RemoveConfigFromMachineGroupResponse RemoveConfigFromMachineGroup(
			RemoveConfigFromMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		String configName = request.GetConfigName();
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName)
				.append("/configs/").append(configName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new RemoveConfigFromMachineGroupResponse(resHeaders);

	}

	public UpdateACLResponse UpdateACL(String project, ACL acl)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(acl, "acl");
		return UpdateACL(new UpdateACLRequest(project, acl));
	}

	public UpdateACLResponse UpdateACL(String project, String logStore, ACL acl)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(acl, "acl");
		return UpdateACL(new UpdateACLRequest(project, logStore, acl));
	}

	public UpdateACLResponse UpdateACL(UpdateACLRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		ACL acl = request.GetACL();
		CodingUtils.assertParameterNotNull(acl, "acl");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = acl.ToRequestString();

		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/";
		if (!logStore.isEmpty()) {
			resourceUri += "logstores/" + logStore;
		}

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateACLResponse(resHeaders);

	}

	public ListACLResponse ListACL(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListACL(new ListACLRequest(project));
	}

	public ListACLResponse ListACL(String project, int offSet, int size)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListACL(new ListACLRequest(project, offSet, size));
	}

	public ListACLResponse ListACL(String project, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return ListACL(new ListACLRequest(project, logStore));
	}

	public ListACLResponse ListACL(String project, String logStore, int offSet,
			int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return ListACL(new ListACLRequest(project, logStore, offSet, size));
	}

	protected ACL ExtractACLFromResponse(JSONObject dict, String requestId)
			throws LogException {
		ACL acl = new ACL();
		try {
			acl.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return acl;
	}

	protected List<ACL> ExtractACLs(JSONObject object, String requestId)
			throws LogException {

		List<ACL> acls = new ArrayList<ACL>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("acls");
			for (int i = 0; i < array.size(); i++) {
				JSONObject aclDict = array.getJSONObject(i);
				ACL acl = ExtractACLFromResponse(aclDict, requestId);
				acls.add(acl);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid acl json array string : "
							+ array.toString(), e, requestId);
		}

		return acls;
	}

	public ListACLResponse ListACL(ListACLRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertParameterNotNull(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/";
		if (!logStore.isEmpty()) {
			resourceUri += "logstores/" + logStore;
		}

		Map<String, String> urlParameter = request.GetAllParams();

		String requestId = "";
		JSONObject object = null;
		ResponseMessage response = new ResponseMessage();
		ListACLResponse listACLResponse = null;

		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			requestId = GetRequestId(resHeaders);
			object = ParserResponseMessage(response, requestId);

			int total = 0;
			int count = 0;
			List<ACL> acls = new ArrayList<ACL>();

			total = object.getInt("total");
			count = object.getInt("count");

			acls = ExtractACLs(object, requestId);

			listACLResponse = new ListACLResponse(resHeaders, count, total,
					acls);

		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid list acl json string : "
							+ object.toString(), e,
					GetRequestId(response.getHeaders()));

		}

		return listACLResponse;
	}

	@SuppressWarnings("unused")
	private String ExtractJsonString(String nodeKey, JSONObject object) {
		try {
			return object.getString(nodeKey);
		} catch (JSONException e) {
			// ignore
		}
		return "";
	}

	private int ExtractJsonInteger(String nodeKey, JSONObject object) {
		try {
			return object.getInt(nodeKey);
		} catch (JSONException e) {
			// ignore
		}
		return -1;
	}

	private List<String> ExtractJsonArray(String nodeKey, JSONObject object) {
		try {
			JSONArray items = object.getJSONArray(nodeKey);
			return ExtractJsonArray(items);
		} catch (JSONException e) {
			return new ArrayList<String>();
		}
	}

	private List<String> ExtractJsonArray(JSONArray ojbect) {
		ArrayList<String> result = new ArrayList<String>();
		try {

			for (int i = 0; i < ojbect.size(); i++) {
				result.add(ojbect.getString(i));
			}
		} catch (JSONException e) {
			// ignore
		}
		return result;
	}
	
	private void ExtractHistograms(GetHistogramsResponse response,
			JSONArray items) {
		try {
			for (int i = 0; i < items.size(); i++) {
				JSONObject item = items.getJSONObject(i);
				Histogram histogram = new Histogram(
						item.getInt(Consts.CONST_FROM),
						item.getInt(Consts.CONST_TO),
						item.getLong(Consts.CONST_RESULT_COUNT),
						item.getString(Consts.CONST_RESULT_PROCESS));
				response.AddHistogram(histogram);
			}
		} catch (JSONException e) {
			// ignore
		}
	}

	protected void ErrorCheck(JSONObject object, String requestId)
			throws LogException {
		if (object.containsKey(Consts.CONST_ERROR_CODE)) {
			try {
				String errorCode = object.getString(Consts.CONST_ERROR_CODE);
				String errorMessage = object
						.getString(Consts.CONST_ERROR_MESSAGE);

				throw new LogException(errorCode, errorMessage, requestId);
			} catch (JSONException e) {
				throw new LogException("InvalidErrorResponse",
						"Error response is not a valid error json : \n"
								+ object.toString(), requestId);
			}
		} else {
			throw new LogException("InvalidErrorResponse",
					"Error response is not a valid error json : \n"
							+ object.toString(), requestId);
		}
	}

	private void ExtractResponseBody(ResponseMessage response)
			throws LogException {
		InputStream in = response.getContent();
		if (in == null) {
			return;
		}
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();

		String requestId = GetRequestId(response.getHeaders());
		int ch;
		try {
			byte[] cache = new byte[1024];
			while ((ch = in.read(cache, 0, 1024)) != -1) {
				bytestream.write(cache, 0, ch);
			}
		} catch (IOException e) {
			throw new LogException("BadResponse",
					"Io exception happened when parse the response data : ", e,
					requestId);
		}

		response.SetBody(bytestream.toByteArray());

	}

	protected JSONObject ParserResponseMessage(ResponseMessage response,
			String requestId) throws LogException {
		byte[] body = response.GetRawBody();

		if (body == null) {
			throw new LogException("BadResponse", "The response body is null",
					null, requestId);
		}
		String res;
		try {
			res = new String(body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("BadResponse",
					"The response is not valid utf-8 string : ", e, requestId);
		}
		try {
			JSONObject object = JSONObject.fromObject(res);

			return object;
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid json string : " + res, e,
					requestId);
		}
	}
	
	protected com.alibaba.fastjson.JSONObject ParserResponseMessageWithFastJson(ResponseMessage response,
			String requestId) throws LogException {
		byte[] body = response.GetRawBody();

		if (body == null) {
			throw new LogException("BadResponse", "The response body is null",
					null, requestId);
		}
		String res;
		try {
			res = new String(body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("BadResponse",
					"The response is not valid utf-8 string : ", e, requestId);
		}
		try {
			com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(res);

			return object;
		} catch (com.alibaba.fastjson.JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid json string : " + res, e,
					requestId);
		}
	}

	private JSONArray ParseResponseMessageToArray(ResponseMessage response,
			String requestId) throws LogException {
		byte[] body = response.GetRawBody();
		if (body == null) {
			throw new LogException("BadResponse", "The response body is null",
					null, requestId);
		}
		String returnStr;
		try {
			returnStr = new String(body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("BadResponse",
					"The response is not valid utf-8 string : ", e, requestId);
		}

		try {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setIgnoreDefaultExcludes(true);
			JSONArray array = JSONArray.fromObject(returnStr, jsonConfig);
			return array;
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid json string : " + returnStr, e,
					requestId);
		}
	}

	private String GetMd5Value(byte[] bytes) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance(Consts.CONST_MD5);
			String res = new BigInteger(1, md.digest(bytes)).toString(16)
					.toUpperCase();

			StringBuilder zeros = new StringBuilder();
			for (int i = 0; i + res.length() < 32; i++) {
				zeros.append("0");
			}
			return zeros.toString() + res;
		} catch (NoSuchAlgorithmException e) {
			// never happen
			throw new RuntimeException("Not Supported signature method "
					+ Consts.CONST_MD5, e);
		}
	}

	private Map<String, String> GetCommonHeadPara(String project) {
		HashMap<String, String> headParameter = new HashMap<String, String>();
		headParameter.put(Consts.CONST_USER_AGENT, userAgent);
		headParameter.put(Consts.CONST_CONTENT_LENGTH, "0");
		headParameter.put(Consts.CONST_X_SLS_BODYRAWSIZE, "0");
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_PROTO_BUF);
		headParameter.put(Consts.CONST_DATE,
				DateUtil.formatRfc822Date(new Date()));

		if (!project.isEmpty() && project != "") {
			headParameter.put(Consts.CONST_HOST, project + "." + this.hostName);
		} else {
			headParameter.put(Consts.CONST_HOST, this.hostName);
		}

		headParameter.put(Consts.CONST_X_SLS_APIVERSION,
				Consts.DEFAULT_API_VESION);
		headParameter.put(Consts.CONST_X_SLS_SIGNATUREMETHOD, Consts.HMAC_SHA1);
		if (securityToken != null && !securityToken.isEmpty()) {
			headParameter.put(Consts.CONST_X_ACS_SECURITY_TOKEN, securityToken);
		}
		if (realIpForConsole != null && !realIpForConsole.isEmpty()) {
			headParameter.put(Consts.CONST_X_SLS_IP, realIpForConsole);
		}
		if (useSSLForConsole != null) {
			headParameter.put(Consts.CONST_X_SLS_SSL, useSSLForConsole ? "true"
					: "false");
		}
		return headParameter;
	}

	private ResponseMessage SendData(String project, HttpMethod method,
			String resourceUri, Map<String, String> urlParams,
			Map<String, String> headParams) throws LogException {
		return SendData(project, method, resourceUri, urlParams, headParams,
				new byte[0]);
	}

	protected ResponseMessage SendData(String project, HttpMethod method,
			String resourceUri, Map<String, String> parameters,
			Map<String, String> headers, String requestBody) throws LogException {
		byte[] body;
		try {
			body = requestBody.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		return SendData(project, method, resourceUri, parameters, headers, body);
	}

	protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
			Map<String, String> parameters, Map<String, String> headers, byte[] body) throws LogException {
		return SendData(project, method, resourceUri, parameters, headers, body, null, null);
	}

	protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
			Map<String, String> parameters, Map<String, String> headers, byte[] body, 
			Map<String, String> output_header, String serverIp)
			throws LogException {
		if (body.length > 0) {
			headers.put(Consts.CONST_CONTENT_MD5, GetMd5Value(body));
		}
		headers.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(body.length));

		GetSignature(this.accessId, this.accessKey, method.toString(), headers,
				resourceUri, parameters);
		URI uri = null;
		if (serverIp == null)
		{
			uri = GetHostURI(project);
		}
		else
		{
			uri = GetHostURIByIp(serverIp);
		}

		RequestMessage request = BuildRequest(uri, method,
				resourceUri, parameters, headers,
				new ByteArrayInputStream(body), body.length);
		ResponseMessage response = null;
		try {
			response = this.serviceClient.sendRequest(request,
					Consts.UTF_8_ENCODING);
			ExtractResponseBody(response);
			if (output_header != null)
			{
				output_header.putAll(response.getHeaders());
			}
			int statusCode = response.getStatusCode();
			if (statusCode != Consts.CONST_HTTP_OK) {
				String requestId = GetRequestId(response.getHeaders());
				JSONObject object = ParserResponseMessage(response, requestId);
				ErrorCheck(object, requestId);
			}
		} catch (ServiceException e) {
			throw new LogException("RequestError", "Web request failed: "
					+ e.getMessage(), e, "");
		} catch (ClientException e) {
			throw new LogException("RequestError", "Web request failed: "
					+ e.getMessage(), e, "");
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}

		}
		return response;
	}

	private static RequestMessage BuildRequest(URI endpoint,
			HttpMethod httpMethod, String resourceUri,
			Map<String, String> parameters, Map<String, String> headers,
			InputStream content, long size) {
		RequestMessage request = new RequestMessage();
		request.setMethod(httpMethod);
		request.setEndpoint(endpoint);
		request.setResourcePath(resourceUri);
		request.setParameters(parameters);
		request.setHeaders(headers);
		request.setContent(content);
		request.setContentLength(size);

		return request;
	}

	private String BuildUrlParameter(Map<String, String> paras) {
		Map<String, String> treeMap = new TreeMap<String, String>(paras);
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			if (isFirst == true) {
				isFirst = false;
			} else {
				builder.append("&");
			}
			builder.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return builder.toString();
	}

	private String GetMapValue(Map<String, String> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return "";
		}
	}

	private String GetCanonicalizedHeaders(Map<String, String> headers) {
		Map<String, String> treeMap = new TreeMap<String, String>(headers);
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			if (!entry.getKey().startsWith(Consts.CONST_X_SLS_PREFIX)
					&& !entry.getKey().startsWith(Consts.CONST_X_ACS_PREFIX)) {
				continue;
			}
			if (isFirst == true) {
				isFirst = false;
			} else {
				builder.append("\n");
			}
			builder.append(entry.getKey()).append(":").append(entry.getValue());
		}
		return builder.toString();
	}

	private void GetSignature(String accessid, String accesskey, String verb,
			Map<String, String> headers, String resourceUri,
			Map<String, String> urlParams) {
		StringBuilder builder = new StringBuilder();
		builder.append(verb).append("\n");
		builder.append(GetMapValue(headers, Consts.CONST_CONTENT_MD5)).append(
				"\n");
		builder.append(GetMapValue(headers, Consts.CONST_CONTENT_TYPE)).append(
				"\n");
		builder.append(GetMapValue(headers, Consts.CONST_DATE)).append("\n");
		builder.append(GetCanonicalizedHeaders(headers)).append("\n");
		builder.append(resourceUri);
		if (urlParams.isEmpty() == false) {
			builder.append("?");
			builder.append(BuildUrlParameter(urlParams));
		}
		String signature = GetSignature(accesskey, builder.toString());
		headers.put(Consts.CONST_AUTHORIZATION,
				Consts.CONST_HEADSIGNATURE_PREFIX + accessid + ":" + signature);
	}

	private static String GetSignature(String accesskey, String data) {
		try {
			byte[] keyBytes = accesskey.getBytes(Consts.UTF_8_ENCODING);
			byte[] dataBytes = data.getBytes(Consts.UTF_8_ENCODING);
			Mac mac = Mac.getInstance(Consts.HMAC_SHA1_JAVA);
			mac.init(new SecretKeySpec(keyBytes, Consts.HMAC_SHA1_JAVA));
			String sig = new String(Base64.encodeBase64(mac.doFinal(dataBytes)));
			return sig;
		} catch (UnsupportedEncodingException e) { // actually these exceptions
													// should never happened
			throw new RuntimeException("Not Supported encoding method "
					+ Consts.UTF_8_ENCODING, e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Not Supported signature method "
					+ Consts.HMAC_SHA1, e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Failed to calcuate the signature", e);
		}
	}

	private static String GetLocalMachineIp() {
		InetAddressValidator validator = new InetAddressValidator();
		String candidate = new String();
		try {
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface
					.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = ifaces.nextElement();

				if (iface.isUp()) {
					for (Enumeration<InetAddress> addresses = iface
							.getInetAddresses(); addresses.hasMoreElements();) {

						InetAddress address = addresses.nextElement();

						if (address.isLinkLocalAddress() == false
								&& address.getHostAddress() != null) {
							String ipAddress = address.getHostAddress();
							if (ipAddress.equals(Consts.CONST_LOCAL_IP)) {
								continue;
							}
							if (validator.isValidInet4Address(ipAddress)) {
								return ipAddress;
							}
							if (validator.isValid(ipAddress)) {
								candidate = ipAddress;
							}
						}
					}
				}
			}
		} catch (SocketException e) {

		}
		return candidate;
	}

	protected ArrayList<Shard> ExtractShards(JSONArray array, String requestId)
			throws LogException {
		ArrayList<Shard> shards = new ArrayList<Shard>();
		try {
			for (int i = 0; i < array.size(); i++) {
				JSONObject shardDict = array.getJSONObject(i);
				int shardId = shardDict.getInt("shardID");
				String status = shardDict.getString("status");
				String begin = shardDict.getString("inclusiveBeginKey");
				String end = shardDict.getString("exclusiveEndKey");
				int createTime = shardDict.getInt("createTime");
				Shard shard = new Shard(shardId, status, begin, end, createTime);
				if (shardDict.containsKey("serverIp"))
				{
					shard.setServerIp(shardDict.getString("serverIp"));
				}
				shards.add(shard);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid shard json array string : "
							+ array.toString() + e.getMessage(), e, requestId);
		}

		return shards;
	}

	@Override
	public CreateLogStoreResponse CreateLogStore(String project,
			LogStore logStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return CreateLogStore(new CreateLogStoreRequest(project, logStore));
	}

	@Override
	public CreateLogStoreResponse CreateLogStore(CreateLogStoreRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		LogStore logStore = request.GetLogStore();
		CodingUtils.assertParameterNotNull(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = logStore.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/logstores";

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateLogStoreResponse(resHeaders);

	}

	@Override
	public DeleteLogStoreResponse DeleteLogStore(String project,
			String logStoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");
		return DeleteLogStore(new DeleteLogStoreRequest(project, logStoreName));
	}

	@Override
	public DeleteLogStoreResponse DeleteLogStore(DeleteLogStoreRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.GetLogStoreName();
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStoreName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteLogStoreResponse(resHeaders);

	}

	@Override
	public UpdateLogStoreResponse UpdateLogStore(String project,
			LogStore logStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return UpdateLogStore(new UpdateLogStoreRequest(project, logStore));
	}

	@Override
	public UpdateLogStoreResponse UpdateLogStore(UpdateLogStoreRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		LogStore logStore = request.GetLogStore();
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		String logStoreName = logStore.GetLogStoreName();
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = logStore.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStoreName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateLogStoreResponse(resHeaders);

	}

	@Override
	public GetLogStoreResponse GetLogStore(String project, String logStoreName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");
		return GetLogStore(new GetLogStoreRequest(project, logStoreName));
	}

	private LogStore ExtractLogStoreFromResponse(JSONObject dict,
			String requestId) throws LogException {
		LogStore logStore = new LogStore();
		try {
			logStore.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return logStore;
	}

	@Override
	public GetLogStoreResponse GetLogStore(GetLogStoreRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStoreName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONObject object = ParserResponseMessage(response, requestId);
		LogStore logStore = ExtractLogStoreFromResponse(object, requestId);

		return new GetLogStoreResponse(resHeaders, logStore);

	}

	@Override
	public CreateIndexResponse CreateIndex(String project, String logStore,
										   String indexJsonString) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(indexJsonString, "indexJsonString");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;

		try {
			body = indexJsonString.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateIndexResponse(resHeaders);
	}

	@Override
	public CreateIndexResponse CreateIndex(String project, String logStore,
			Index index) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(index, "index");
		return CreateIndex(new CreateIndexRequest(project, logStore, index));
	}

	@Override
	public CreateIndexResponse CreateIndex(CreateIndexRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		Index index = request.GetIndex();
		CodingUtils.assertParameterNotNull(index, "index");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = index.ToRequestString();

		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateIndexResponse(resHeaders);

	}

	@Override
	public UpdateIndexResponse UpdateIndex(String project, String logStore,
										   String indexJsonString) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(indexJsonString, "indexJsonString");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		try {
			body = indexJsonString.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateIndexResponse(resHeaders);
	}

	@Override
	public UpdateIndexResponse UpdateIndex(String project, String logStore,
			Index index) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(index, "index");
		return UpdateIndex(new UpdateIndexRequest(project, logStore, index));
	}

	@Override
	public UpdateIndexResponse UpdateIndex(UpdateIndexRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		Index index = request.GetIndex();
		CodingUtils.assertParameterNotNull(index, "index");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = index.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateIndexResponse(resHeaders);
	}

	@Override
	public DeleteIndexResponse DeleteIndex(String project, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return DeleteIndex(new DeleteIndexRequest(project, logStore));
	}

	@Override
	public DeleteIndexResponse DeleteIndex(DeleteIndexRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteIndexResponse(resHeaders);
	}

	@Override
	public GetIndexResponse GetIndex(String project, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return GetIndex(new GetIndexRequest(project, logStore));
	}
	
    @Override
    public GetIndexStringResponse GetIndexString(String project, String logStore)
                    throws LogException {
            CodingUtils.assertStringNotNullOrEmpty(project, "project");
            CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
            return GetIndexString(new GetIndexRequest(project, logStore));
    }
	
	private Index ExtractIndexFromResponseWithFastJson(com.alibaba.fastjson.JSONObject dict, String requestId)
			throws LogException {
		Index index = new Index();
		try {
			index.FromJsonString(dict.toJSONString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return index;
	}

	@Override
	public GetIndexResponse GetIndex(GetIndexRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/index");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		com.alibaba.fastjson.JSONObject object = ParserResponseMessageWithFastJson(response, requestId);
		Index

		index = ExtractIndexFromResponseWithFastJson(object, requestId);

		return new GetIndexResponse(resHeaders, index);

	}

    @Override
    public GetIndexStringResponse GetIndexString(GetIndexRequest request)
                    throws LogException {
            CodingUtils.assertParameterNotNull(request, "request");
            String project = request.GetProject();
            CodingUtils.assertStringNotNullOrEmpty(project, "project");
            String logStore = request.GetLogStore();
            CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");

            Map<String, String> headParameter = GetCommonHeadPara(project);

            StringBuilder resourceUriBuilder = new StringBuilder();
            resourceUriBuilder.append("/logstores/").append(logStore)
                            .append("/index");

            String resourceUri = resourceUriBuilder.toString();

            Map<String, String> urlParameter = request.GetAllParams();

            ResponseMessage response = SendData(project, HttpMethod.GET,
                            resourceUri, urlParameter, headParameter);

            Map<String, String> resHeaders = response.getHeaders();
            String requestId = GetRequestId(resHeaders);

            return new GetIndexStringResponse(resHeaders, response.GetStringBody());

    }
	
	@Override
	public CreateShipperResponse CreateShipper(String project, String logStore,
			String shipperName, ShipperConfig shipConfig) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");
		CodingUtils.assertParameterNotNull(shipConfig, "shipConfig");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper");

		String resourceUri = resourceUriBuilder.toString();

		JSONObject json_body = new JSONObject();
		json_body.put("shipperName", shipperName);
		json_body.put("targetType", shipConfig.GetShipperType());
		json_body.put("targetConfiguration", shipConfig.GetJsonObj());

		byte[] body = null;
		try {
			body = json_body.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateShipperResponse(resHeaders);

	}

	@Override
	public UpdateShipperResponse UpdateShipper(String project, String logStore,
			String shipperName, ShipperConfig shipConfig) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");
		CodingUtils.assertParameterNotNull(shipConfig, "shipConfig");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper/").append(shipperName);

		String resourceUri = resourceUriBuilder.toString();

		JSONObject json_body = new JSONObject();
		json_body.put("shipperName", shipperName);
		json_body.put("targetType", shipConfig.GetShipperType());
		json_body.put("targetConfiguration", shipConfig.GetJsonObj());

		byte[] body;
		try {
			body = json_body.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateShipperResponse(resHeaders);
	}

	@Override
	public DeleteShipperResponse DeleteShipper(String project, String logStore,
			String shipperName) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper/").append(shipperName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteShipperResponse(resHeaders);
	}

	@Override
	public GetShipperResponse GetShipperConfig(String project, String logStore,
			String shipperName) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper/").append(shipperName);

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);

		ShipperConfig config = null;
		if (object.containsKey("targetType")
				&& object.getString("targetType").equals("odps")) {
			config = new OdpsShipperConfig();
			config.FromJsonObj(object.getJSONObject("targetConfiguration"));
		} else if (object.containsKey("targetType")
				&& object.getString("targetType").equals("oss")) {
			config = new OssShipperConfig();
			config.FromJsonObj(object.getJSONObject("targetConfiguration"));
			;
		} else {
			throw new LogException("InvalidShipperType",
					"The return shipper config is:" + object.toString(), null,
					requestId);
		}
		return new GetShipperResponse(resHeaders, config);
	}

	@Override
	public ListShipperResponse ListShipper(String project, String logStore)
			throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONObject object = ParserResponseMessage(response, requestId);

		return new ListShipperResponse(resHeaders, ExtractJsonInteger("count",
				object), ExtractJsonInteger("total", object), ExtractJsonArray(
				"shipper", object));
	}

	@Override
	public GetShipperTasksResponse GetShipperTasks(String project,
			String logStore, String shipperName, int startTime, int endTime,
			String statusType, int offset, int size) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");
		CodingUtils.assertParameterNotNull(statusType, "statusType");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper/").append(shipperName).append("/tasks");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put("from", String.valueOf(startTime));
		urlParameter.put("to", String.valueOf(endTime));
		urlParameter.put("status", statusType);
		urlParameter.put("offset", String.valueOf(offset));
		urlParameter.put("size", String.valueOf(size));

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		return new GetShipperTasksResponse(resHeaders, ExtractJsonInteger(
				"count", object), ExtractJsonInteger("total", object),
				ExtractTasksStatisTic(object), ExtractShipperTask(object));
	}

	@Override
	public RetryShipperTasksResponse RetryShipperTasks(String project,
			String logStore, String shipperName, List<String> taskList)
			throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");
		CodingUtils.assertParameterNotNull(taskList, "taskList");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/shipper/").append(shipperName).append("/tasks");

		String resourceUri = resourceUriBuilder.toString();

		JSONArray array = new JSONArray();
		for (String task : taskList) {
			array.add(task);
		}
		byte[] body;
		try {
			body = array.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new RetryShipperTasksResponse(resHeaders);

	}

	private ShipperTasksStatistic ExtractTasksStatisTic(JSONObject obj) {
		JSONObject statistic_obj = obj.getJSONObject("statistics");

		return new ShipperTasksStatistic(statistic_obj.getInt("running"),
				statistic_obj.getInt("success"), statistic_obj.getInt("fail"));

	}

	private List<ShipperTask> ExtractShipperTask(JSONObject object) {
		List<ShipperTask> res = new ArrayList<ShipperTask>();
		JSONArray array = object.getJSONArray("tasks");
		for (int i = 0; i < array.size(); i++) {
			JSONObject item = array.getJSONObject(i);
			ShipperTask task = new ShipperTask();
			task.FromJsonObject(item);
			res.add(task);
		}
		return res;
	}

	@Override
	public CreateConsumerGroupResponse CreateConsumerGroup(String project,
			String logStore, ConsumerGroup consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return CreateConsumerGroup(new CreateConsumerGroupRequest(project,
				logStore, consumerGroup));
	}

	@Override
	public CreateConsumerGroupResponse CreateConsumerGroup(
			CreateConsumerGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		ConsumerGroup consumerGroup = request.GetConsumerGroup();
		CodingUtils.assertParameterNotNull(consumerGroup, "consumerGroup");

		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body;
		String bodyStr = consumerGroup.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/logstores/" + request.GetLogStore()
				+ "/consumergroups";
		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();
		return new CreateConsumerGroupResponse(resHeaders);

	}

	protected UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, Boolean inOrder,
			Integer timeoutInSec) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logstore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body;
		String bodyStr;
		if (inOrder != null && timeoutInSec != null) {
			bodyStr = "{\"order\":" + inOrder + ",\"timeout\":" + timeoutInSec
					+ "}";
		} else if (inOrder != null) {
			bodyStr = "{\"order\":" + inOrder + "}";
		} else {
			bodyStr = "{\"timeout\":" + timeoutInSec + "}";
		}
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		String resourceUri = "/logstores/" + logStore + "/consumergroups/"
				+ consumerGroup;
		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateConsumerGroupResponse(resHeaders);

	}

	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, boolean inOrder,
			int timeoutInSec) throws LogException {
		return UpdateConsumerGroup(project, logStore, consumerGroup,
				(Boolean) inOrder, (Integer) timeoutInSec);
	}

	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, boolean inOrder)
			throws LogException {
		return UpdateConsumerGroup(project, logStore, consumerGroup, inOrder,
				null);
	}

	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, int timeoutInSec)
			throws LogException {
		return UpdateConsumerGroup(project, logStore, consumerGroup, null,
				timeoutInSec);
	}

	@Override
	public DeleteConsumerGroupResponse DeleteConsumerGroup(String project,
			String logStore, String consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/logstores/").append(logStore)
				.append("/consumergroups/").append(consumerGroup);
		String resourceUri = resourceUriBuilder.toString();
		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteConsumerGroupResponse(resHeaders);

	}

	public ListConsumerGroupResponse ListConsumerGroup(String project,
			String logStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		String resourceUri = "/logstores/" + logStore + "/consumergroups";
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		ArrayList<ConsumerGroup> consumerGroups = new ArrayList<ConsumerGroup>();
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ExtractConsumerGroups(array, requestId, consumerGroups);

		ListConsumerGroupResponse listConsumerGroupResponse = new ListConsumerGroupResponse(
				resHeaders);
		listConsumerGroupResponse.SetConsumerGroups(consumerGroups);
		return listConsumerGroupResponse;
	}

	private void ExtractConsumerGroups(JSONArray array, String requestId,
			ArrayList<ConsumerGroup> consumerGroups) throws LogException {
		try {
			for (int i = 0; i < array.size(); i++) {
				JSONObject consumerGroup = array.getJSONObject(i);
				consumerGroups.add(new ConsumerGroup(consumerGroup
						.getString("name"), consumerGroup.getInt("timeout"),
						consumerGroup.getBoolean("order")));
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid consumer group json array string : "
							+ array.toString(), e, requestId);
		}
	}

	public ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup,
			String consumer, int shard, String checkpoint) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		CodingUtils.assertStringNotNullOrEmpty(consumer, "consumer");
		CodingUtils.assertStringNotNullOrEmpty(checkpoint, "checkpoint");
		return UpdateCheckPoint(project, logStore, consumerGroup, consumer,
				shard, checkpoint, false);
	}

	public ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup, int shard,
			String checkpoint) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		CodingUtils.assertStringNotNullOrEmpty(checkpoint, "checkpoint");
		return UpdateCheckPoint(project, logStore, consumerGroup, "", shard,
				checkpoint, true);
	}

	protected ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup,
			String consumer, int shard, String checkpoint, boolean forceSuccess)
			throws LogException {
		String resourceUri = "/logstores/" + logStore + "/consumergroups/"
				+ consumerGroup;
		ConsumerGroupUpdateCheckPointRequest request = new ConsumerGroupUpdateCheckPointRequest(
				project, logStore, consumerGroup, consumer, forceSuccess,
				shard, checkpoint);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = request.GetAllParams();

		byte[] body;
		String bodyStr = request.GetRequestBody();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new ConsumerGroupUpdateCheckPointResponse(resHeaders);
	}

	public ConsumerGroupHeartBeatResponse HeartBeat(String project,
			String logStore, String consumerGroup, String consumer,
			ArrayList<Integer> shards) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		CodingUtils.assertStringNotNullOrEmpty(consumer, "consumer");
		String resourceUri = "/logstores/" + logStore + "/consumergroups/"
				+ consumerGroup;
		ConsumerGroupHeartBeatRequest request = new ConsumerGroupHeartBeatRequest(
				project, logStore, consumerGroup, consumer,
				shards == null ? new ArrayList<Integer>() : shards);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = request.GetAllParams();

		byte[] body;
		String bodyStr = request.GetRequestBody();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		ArrayList<Integer> responseShards = new ArrayList<Integer>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ExtractShards(array, requestId, responseShards);

		return new ConsumerGroupHeartBeatResponse(resHeaders, responseShards);

	}

	protected void ExtractShards(JSONArray array, String requestId,
			ArrayList<Integer> shards) throws LogException {
		try {
			for (int i = 0; i < array.size(); i++) {
				shards.add(array.getInt(i));
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid shard json array string : "
							+ array.toString(), e, requestId);
		}
	}

	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup) throws LogException {
		return GetCheckPoint(project, logStore, consumerGroup, -1);
	}

	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup, int shard)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		ConsumerGroupGetCheckPointRequest request = new ConsumerGroupGetCheckPointRequest(
				project, logStore, consumerGroup, shard);
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/logstores/" + logStore + "/consumergroups/"
				+ consumerGroup;

		Map<String, String> headParameter = GetCommonHeadPara(project);

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);

		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ConsumerGroupCheckPointResponse consumerGroupCheckPointResponse = new ConsumerGroupCheckPointResponse(
				resHeaders, array);
		return consumerGroupCheckPointResponse;

	}

	@Override
	public CreateProjectResponse CreateProject(String project,
			String projectDescription) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		if (projectDescription == null) {
			projectDescription = "";
		}

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/";

		JSONObject json_body = new JSONObject();
		json_body.put("projectName", project);
		json_body.put("description", projectDescription);

		byte[] body = null;
		try {
			body = json_body.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();

		return new CreateProjectResponse(resHeaders);
	}

	@Override
	public GetProjectResponse GetProject(String project) throws LogException {

		CodingUtils.assertStringNotNullOrEmpty(project, "project");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/";

		Map<String, String> urlParameter = new HashMap<String, String>();

		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		GetProjectResponse getProjectResponse = new GetProjectResponse(
				resHeaders);
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		getProjectResponse.FromJsonObject(object);
		return getProjectResponse;
	}

	@Override
	public DeleteProjectResponse DeleteProject(String project)
			throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");

		String resourceUri = "/";
		Map<String, String> urlParameter = new HashMap<String, String>();

		Map<String, String> headParameter = GetCommonHeadPara(project);

		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);

		Map<String, String> resHeaders = response.getHeaders();

		return new DeleteProjectResponse(resHeaders);

	}

	@Override
	public UpdateMachineGroupMachineResponse AddMachineIntoMahineGroup(
			String project, String groupName, MachineList machineList)
			throws LogException {
		return UpdateMachineGroupMachine(project, groupName, machineList, false);
	}

	@Override
	public UpdateMachineGroupMachineResponse AddMachineIntoMachineGroup(
			UpdateMachineGroupMachineRequest request) throws LogException {
		return UpdateMachineGroupMachine(request.GetProject(),
				request.GetGroupName(), request.GetMachineList(), false);
	}

	@Override
	public UpdateMachineGroupMachineResponse DeleteMachineFromMachineGroup(
			String project, String groupName, MachineList machineList)
			throws LogException {
		return UpdateMachineGroupMachine(project, groupName, machineList, true);
	}

	@Override
	public UpdateMachineGroupMachineResponse DeleteMachineFromMachineGroup(
			UpdateMachineGroupMachineRequest request) throws LogException {
		return UpdateMachineGroupMachine(request.GetProject(),
				request.GetGroupName(), request.GetMachineList(), true);
	}

	protected UpdateMachineGroupMachineResponse UpdateMachineGroupMachine(
			String project, String groupName, MachineList machineList,
			boolean isDelete) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");

		Map<String, String> headParameter = GetCommonHeadPara(project);

		byte[] body;
		String bodyStr = machineList.ToRequestString();
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);

		StringBuilder resourceUriBuilder = new StringBuilder();
		resourceUriBuilder.append("/machinegroups/").append(groupName)
				.append("/machines");

		String resourceUri = resourceUriBuilder.toString();

		Map<String, String> urlParameter = new HashMap<String, String>();
		if (isDelete) // delete machine from machine group
			urlParameter.put("action", "delete");
		else
			urlParameter.put("action", "add");

		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();

		return new UpdateMachineGroupMachineResponse(resHeaders);
	}

	@Override
	public ListProjectResponse ListProject() throws LogException {
		ListProjectRequest listProjectRequest = new ListProjectRequest("", 0, 500);
		return ListProject(listProjectRequest);
	}
	
	@Override
	public ListProjectResponse ListProject(String projectName, int offset, int size) throws LogException {
		ListProjectRequest listProjectRequest = new ListProjectRequest(projectName, offset, size);
		return ListProject(listProjectRequest);
	}
	
	protected List<Project> ExtractProjects(JSONObject object, String requestId)
			throws LogException {
		List<Project> projects = new ArrayList<Project>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("projects");
			for (int i = 0; i < array.size(); i++) {
				Project tempProject = new Project();
				tempProject.FromJsonObject(array.getJSONObject(i));
				projects.add(tempProject);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid config json array string : "
							+ array.toString(), e, requestId);
		}

		return projects;
	}
	
	@Override
	public ListProjectResponse ListProject(ListProjectRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		Map<String, String> headParameter = GetCommonHeadPara(project);

		String resourceUri = "/";

		Map<String, String> urlParameter = request.GetAllParams();

		ResponseMessage response = new ResponseMessage();
		ListProjectResponse listProjectResponse = null;
		JSONObject object = null;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);

			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);

			object = ParserResponseMessage(response, requestId);

			int total = 0;
			int count = 0;
			List<Project> projects = new ArrayList<Project>();

			total = object.getInt(Consts.CONST_TOTAL);
			count = object.getInt(Consts.CONST_COUNT);
			projects = ExtractProjects(object, requestId);

			listProjectResponse = new ListProjectResponse(resHeaders, total, count, projects);
		} catch (JSONException e) {
			throw new LogException("BadResponse",
					"The response is not valid list project json string : "
							+ object.toString(), e,
					GetRequestId(response.getHeaders()));
		}
		return listProjectResponse;
	}
		
	// saved search api
	private void checkSavedSearchResource(SavedSearch savedSearch) {
		CodingUtils.assertStringNotNullOrEmpty(savedSearch.getSavedSearchName(), "savedsearchName");
		CodingUtils.assertStringNotNullOrEmpty(savedSearch.getLogstore(), "logstore");
	}
	
	@Override
	public CreateChartResponse createChart(CreateChartRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName() + "/charts";
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST, resourceUri, urlParameter, headParameter, request.getChart().ToJsonString());
		return new CreateChartResponse(response.getHeaders());
	}

	@Override
	public UpdateChartResponse updateChart(UpdateChartRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName() + "/charts/" + request.getChartName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT, resourceUri, urlParameter, headParameter, request.getChart().ToJsonString());
		return new UpdateChartResponse(response.getHeaders());
	}

	@Override
	public DeleteChartResponse deleteChart(DeleteChartRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName() + "/charts/" + request.getChartName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		return new DeleteChartResponse(response.getHeaders());
	}
	
	protected Chart ExtractChartFromResponse(JSONObject dict,
			String requestId) throws LogException {
		Chart chart = new Chart();
		try {
			chart.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return chart;
	}
	
	@Override
	public GetChartResponse getChart(GetChartRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName() + "/charts/" + request.getChartName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		Chart chart = ExtractChartFromResponse(object, requestId);
		GetChartResponse getChartResponse = new GetChartResponse(response.getHeaders(), chart);	
		return getChartResponse;
	}
	
	@Override
	public CreateDashboardResponse createDashboard(CreateDashboardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards";
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST, resourceUri, urlParameter, headParameter, request.getDashboard().ToJsonString());
		return new CreateDashboardResponse(response.getHeaders());
	}
	
	@Override
	public UpdateDashboardResponse updateDashboard(UpdateDashboardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboard().getDashboardName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT, resourceUri, urlParameter, headParameter, request.getDashboard().ToJsonString());
		return new UpdateDashboardResponse(response.getHeaders());
	}

	@Override
	public DeleteDashboardResponse deleteDashboard(DeleteDashboardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		return new DeleteDashboardResponse(response.getHeaders());
	}
	
	protected Dashboard ExtractDashboardFromResponse(JSONObject dict,
			String requestId) throws LogException {
		Dashboard dashboard = new Dashboard();
		try {
			dashboard.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return dashboard;
	}
	
	@Override
	public GetDashboardResponse getDashboard(GetDashboardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards/" + request.getDashboardName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		Dashboard dashboard = ExtractDashboardFromResponse(object, requestId);
		GetDashboardResponse getDashboardResponse = new GetDashboardResponse(response.getHeaders(), dashboard);	
		return getDashboardResponse;
	}
	
	protected List<String> ExtractDashboards(JSONObject object, String requestId)
			throws LogException {
		List<String> dashboards = new ArrayList<String>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("dashboards");
			for (int i = 0; i < array.size(); i++) {
				dashboards.add(array.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse", "The response is not valid config json array string : " + array.toString(), e, requestId);
		}

		return dashboards;
	}
	
	@Override
	public ListDashboardResponse listDashboard(ListDashboardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = "/dashboards";
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		int total = 0;
		int count = 0;
		List<String> dashboards = new ArrayList<String>();
		total = object.getInt(Consts.CONST_TOTAL);
		count = object.getInt(Consts.CONST_COUNT);
		dashboards = ExtractDashboards(object, requestId);
		ListDashboardResponse listDashboardResponse = new ListDashboardResponse(response.getHeaders(), count, total, dashboards);
		return listDashboardResponse;
	}
	
	@Override
	public CreateSavedSearchResponse createSavedSearch(CreateSavedSearchRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		checkSavedSearchResource(request.getSavedSearch());
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST, resourceUri, urlParameter, headParameter, request.getSavedSearch().ToJsonString());
		return new CreateSavedSearchResponse(response.getHeaders());
	}
	@Override
	public UpdateSavedSearchResponse updateSavedSearch(UpdateSavedSearchRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		checkSavedSearchResource(request.getSavedSearch());
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI + "/" + request.getSavedSearch().getSavedSearchName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT, resourceUri, urlParameter, headParameter, request.getSavedSearch().ToJsonString());
		return new UpdateSavedSearchResponse(response.getHeaders());
	}

	@Override
	public DeleteSavedSearchResponse deleteSavedSearch(DeleteSavedSearchRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getSavedSearchName(), "savedsearchName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI + "/" + request.getSavedSearchName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		return new DeleteSavedSearchResponse(response.getHeaders());
	}
	
	protected SavedSearch ExtractSavedSearchFromResponse(JSONObject dict,
			String requestId) throws LogException {
		SavedSearch savedSearch = new SavedSearch();
		try {
			savedSearch.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return savedSearch;
	}
	
	@Override
	public GetSavedSearchResponse getSavedSearch(GetSavedSearchRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getSavedSearchName(), "savedsearchName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI + "/" + request.getSavedSearchName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		SavedSearch savedSearch = ExtractSavedSearchFromResponse(object, requestId);
		GetSavedSearchResponse getSavedSearchResponse = new GetSavedSearchResponse(response.getHeaders(), savedSearch);	
		return getSavedSearchResponse;
	}
	
	protected List<String> ExtractSavedSearches(JSONObject object, String requestId)
			throws LogException {
		List<String> savedSearches = new ArrayList<String>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("savedsearches");
			for (int i = 0; i < array.size(); i++) {
				savedSearches.add(array.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse", "The response is not valid config json array string : " + array.toString(), e, requestId);
		}

		return savedSearches;
	}
	
	@Override
	public ListSavedSearchResponse listSavedSearch(ListSavedSearchRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		int total = 0;
		int count = 0;
		List<String> savedSearches = new ArrayList<String>();
		total = object.getInt(Consts.CONST_TOTAL);
		count = object.getInt(Consts.CONST_COUNT);
		savedSearches = ExtractSavedSearches(object, requestId);
		ListSavedSearchResponse listSavedSearchResponse = new ListSavedSearchResponse(response.getHeaders(), count, total, savedSearches);
		return listSavedSearchResponse;
	}
	
	// alert api
	private void checkAlertResource(Alert alert) {
		CodingUtils.assertStringNotNullOrEmpty(alert.getAlertName(), "alertName");
		CodingUtils.assertStringNotNullOrEmpty(alert.getSavedSearchName(), "savedsearchName");
		CodingUtils.assertStringNotNullOrEmpty(alert.getRoleArn(), "roleArn");
		CodingUtils.assertStringNotNullOrEmpty(alert.getAlertKey(), "alertKey");
		CodingUtils.assertStringNotNullOrEmpty(alert.getComparator(), "comparator");
		CodingUtils.assertStringNotNullOrEmpty(alert.getActionType(), "actionType");
	}
	
	@Override
	public CreateAlertResponse createAlert(CreateAlertRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		checkAlertResource(request.getAlert());
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST, resourceUri, urlParameter, headParameter, request.getAlert().ToJsonString());
		return new CreateAlertResponse(response.getHeaders());
	}
	
	@Override
	public UpdateAlertResponse updateAlert(UpdateAlertRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		checkAlertResource(request.getAlert());
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI + "/" + request.getAlert().getAlertName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT, resourceUri, urlParameter, headParameter, request.getAlert().ToJsonString());
		return new UpdateAlertResponse(response.getHeaders());
	}
	
	@Override
	public DeleteAlertResponse deleteAlert(DeleteAlertRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getAlertName(), "alertName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI + "/" + request.getAlertName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		return new DeleteAlertResponse(response.getHeaders());
	}
	
	protected Alert ExtractAlertFromResponse(JSONObject dict,
			String requestId) throws LogException {
		Alert alert = new Alert();
		try {
			alert.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.GetErrorCode(), e.GetErrorMessage(),
					e.getCause(), requestId);
		}
		return alert;
	}
	
	@Override
	public GetAlertResponse getAlert(GetAlertRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getAlertName(), "alertName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI + "/" + request.getAlertName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		Alert alert = ExtractAlertFromResponse(object, requestId);
		GetAlertResponse getAlertResponse = new GetAlertResponse(response.getHeaders(), alert);	
		return getAlertResponse;
	}
	
	protected List<String> ExtractAlerts(JSONObject object, String requestId)
			throws LogException {
		List<String> alerts = new ArrayList<String>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("alerts");
			for (int i = 0; i < array.size(); i++) {
				alerts.add(array.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse", "The response is not valid config json array string : " + array.toString(), e, requestId);
		}

		return alerts;
	}
	
	@Override
	public ListAlertResponse listAlert(ListAlertRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		int total = 0;
		int count = 0;
		List<String> alerts = new ArrayList<String>();
		total = object.getInt(Consts.CONST_TOTAL);
		count = object.getInt(Consts.CONST_COUNT);
		alerts = ExtractAlerts(object, requestId);
		ListAlertResponse listAlertResponse = new ListAlertResponse(response.getHeaders(), count, total, alerts);
		return listAlertResponse;
	}
	
	protected List<AlertFail> ExtractAlertFails(JSONObject object, String requestId)
			throws LogException {
		List<AlertFail> alerts = new ArrayList<AlertFail>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("failAlerts");
			for (int i = 0; i < array.size(); i++) {
				AlertFail alertFail = new AlertFail();
				JSONObject item = array.getJSONObject(i);
				alertFail.FromJsonObject(item);
				alerts.add(alertFail);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse", "The response is not valid config json array string : " + array.toString(), e, requestId);
		}

		return alerts;
	}
	
	@Override
	public ListAlertFailResponse listAlertFail(ListAlertFailRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getAlertName(), "alertName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ALERT_URI + "/" + request.getAlertName() + "/fail";
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		int total = 0;
		int count = 0;
		List<AlertFail> alertFails = new ArrayList<AlertFail>();
		total = object.getInt(Consts.CONST_TOTAL);
		count = object.getInt(Consts.CONST_COUNT);
		alertFails = ExtractAlertFails(object, requestId);
		ListAlertFailResponse listAlertFailsResponse = new ListAlertFailResponse(response.getHeaders(), count, total, alertFails);	
		return listAlertFailsResponse;
	}

	@Override
	public CreateEtlJobResponse createEtlJob(CreateEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		EtlJob etlJob = request.getEtlJob();
		CodingUtils.assertParameterNotNull(etlJob, "etlJob");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body;
		String bodyStr = etlJob.toJsonString(true,true);
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLJOB_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		return new CreateEtlJobResponse(response.getHeaders());
	}

	@Override
	public DeleteEtlJobResponse deleteEtlJob(DeleteEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String etlJobName = request.getEtlJobName();
		CodingUtils.assertStringNotNullOrEmpty(etlJobName, "etlJobName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLJOB_URI + "/" + etlJobName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		return new DeleteEtlJobResponse(response.getHeaders());
	}

	@Override
	public UpdateEtlJobResponse updateEtlJob(UpdateEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		EtlJob etlJob = request.getEtlJob();
		CodingUtils.assertParameterNotNull(etlJob, "etlJob");
		CodingUtils.assertStringNotNullOrEmpty(etlJob.getJobName(), "etlJobName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body;
		String bodyStr = etlJob.toJsonString(false,false);
		try {
			body = bodyStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new LogException("EncodingException", e.getMessage(), "");
		}
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLJOB_URI+ "/" + etlJob.getJobName();
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		return new UpdateEtlJobResponse(response.getHeaders());
	}

	@Override
	public GetEtlJobResponse getEtlJob(GetEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String etlJobName = request.getEtlJobName();
		CodingUtils.assertStringNotNullOrEmpty(etlJobName, "etlJobName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLJOB_URI + "/" + etlJobName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		EtlJob etlJob = new EtlJob();
		etlJob.fromJsonObject(object);
		return new GetEtlJobResponse(resHeaders, etlJob);
	}

	@Override
	public ListEtlJobResponse listEtlJob(ListEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "project");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_ETLJOB_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		ListEtlJobResponse listResp = new ListEtlJobResponse(response.getHeaders(), object.getInt(Consts.CONST_TOTAL));
		listResp.setEtlJobNameList(ExtractJsonArray("etlJobNameList", object));
		return listResp;
	}

	@Override
	public CreateEtlMetaResponse createEtlMeta(String project, EtlMeta etlMeta) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMeta, "etlMeta");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, etlMeta.toJsonObject().toString());
		return new CreateEtlMetaResponse(response.getHeaders());
	}

	@Override
	public DeleteEtlMetaResponse deleteEtlMeta(String project, String etlMetaName, String etlMetaKey) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaKey, "etlMetaKey");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put(Consts.ETL_META_NAME, etlMetaName);
		urlParameter.put(Consts.ETL_META_KEY, etlMetaKey);
		urlParameter.put(Consts.ETL_META_TAG, Consts.CONST_ETLMETA_ALL_TAG_MATCH);
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		return new DeleteEtlMetaResponse(response.getHeaders());
	}

	@Override
	public DeleteEtlMetaResponse deleteEtlMeta(String project, String etlMetaName, String etlMetaKey, String etlMetaTag) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaKey, "etlMetaKey");
		CodingUtils.assertParameterNotNull(etlMetaTag, "etlMetaTag");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put(Consts.ETL_META_NAME, etlMetaName);
		urlParameter.put(Consts.ETL_META_KEY, etlMetaKey);
		urlParameter.put(Consts.ETL_META_TAG, etlMetaTag);
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		return new DeleteEtlMetaResponse(response.getHeaders());
	}

	@Override
	public UpdateEtlMetaResponse updateEtlMeta(String project, EtlMeta etlMeta) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMeta, "etlMeta");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, etlMeta.toJsonObject().toString());
		return new UpdateEtlMetaResponse(response.getHeaders());
	}

	@Override
	public ListEtlMetaNameResponse listEtlMetaName(String project, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(offset, "offset");
		CodingUtils.assertParameterNotNull(size, "size");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLMETANAME_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put(Consts.CONST_OFFSET, String.valueOf(offset));
		urlParameter.put(Consts.CONST_SIZE, String.valueOf(size));
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = ParserResponseMessage(response, requestId);
		ListEtlMetaNameResponse listResp = new ListEtlMetaNameResponse(response.getHeaders(), object.getInt(Consts.CONST_TOTAL));
		listResp.setEtlMetaNameList(ExtractJsonArray("etlMetaNameList", object));
		return listResp;
	}

	private ListEtlMetaResponse listEtlMeta(ListEtlMetaRequest request) throws LogException {
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = Consts.CONST_ETLMETA_URI;
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = ParserResponseMessage(response, requestId);
		ListEtlMetaResponse listResp = new ListEtlMetaResponse(response.getHeaders(), object.getInt(Consts.CONST_TOTAL));
		try {
			JSONArray items = object.getJSONArray("etlMetaList");
			for (int i = 0; i < items.size(); i++) {
				EtlMeta meta = new EtlMeta();
				meta.fromJsonObject(items.getJSONObject(i));
				listResp.addEtlMeta(meta);
			}
		} catch (JSONException e) {
			throw new LogException("BadResponse", e.getMessage(), listResp.GetRequestId());
		}
		return listResp;
	}

	@Override
	public ListEtlMetaResponse listEtlMeta(String project, String etlMetaName, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertParameterNotNull(offset, "offset");
		CodingUtils.assertParameterNotNull(size, "size");
		ListEtlMetaRequest request = new ListEtlMetaRequest(project, offset, size);
		request.setEtlMetaName(etlMetaName);
		request.setEtlMetaKey("");
		request.setEtlMetaTag(Consts.CONST_ETLMETA_ALL_TAG_MATCH);
		return listEtlMeta(request);
	}

	@Override
	public ListEtlMetaResponse listEtlMeta(String project, String etlMetaName, String etlMetaTag, int offset, int size) throws LogException {
        CodingUtils.assertStringNotNullOrEmpty(project, "project");
        CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
        CodingUtils.assertParameterNotNull(etlMetaTag, "etlMetaTag");
		CodingUtils.assertParameterNotNull(offset, "offset");
		CodingUtils.assertParameterNotNull(size, "size");
		ListEtlMetaRequest request = new ListEtlMetaRequest(project, offset, size);
		request.setEtlMetaName(etlMetaName);
		request.setEtlMetaKey("");
        request.setEtlMetaTag(etlMetaTag);
		return listEtlMeta(request);
	}

	@Override
    public ListEtlMetaResponse listEtlMeta(String project, String etlMetaName, String dispatchProject, String dispatchLogstore, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertParameterNotNull(dispatchProject, "dispatchProject");
		CodingUtils.assertParameterNotNull(dispatchLogstore, "dispatchLogstore");
		CodingUtils.assertParameterNotNull(offset, "offset");
		CodingUtils.assertParameterNotNull(size, "size");
		ListEtlMetaRequest request = new ListEtlMetaRequest(project, offset, size);
		request.setEtlMetaName(etlMetaName);
		request.setEtlMetaKey("");
		request.setEtlMetaTag(Consts.CONST_ETLMETA_ALL_TAG_MATCH);
		request.setDispatchProject(dispatchProject);
		request.setDispatchLogstore(dispatchLogstore);
		return listEtlMeta(request);
	}

	@Override
	public ListEtlMetaResponse getEtlMeta(String project, String etlMetaName, String etlMetaKey) throws LogException {
        CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaKey, "etlMetaKey");
		ListEtlMetaRequest request = new ListEtlMetaRequest(project, 0, 1);
		request.setEtlMetaName(etlMetaName);
		request.setEtlMetaKey(etlMetaKey);
		request.setEtlMetaTag(Consts.CONST_ETLMETA_ALL_TAG_MATCH);
		return listEtlMeta(request);
	}
};
