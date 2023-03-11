/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.DefaultCredentails;
import com.aliyun.openservices.log.common.auth.ECSRoleCredentials;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.*;
import com.aliyun.openservices.log.http.comm.*;
import com.aliyun.openservices.log.http.signer.SlsSigner;
import com.aliyun.openservices.log.http.signer.SlsSignerBase;
import com.aliyun.openservices.log.http.utils.CodingUtils;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import com.aliyun.openservices.log.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.HttpClientConnectionManager;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Client class is the main class in the sdk, it implements the interfaces
 * defined in LogService. It can be used to send request to the log service
 * server to put/get data.
 */
public class Client implements LogService {
	private static final String DEFAULT_USER_AGENT = VersionInfoUtils.getDefaultUserAgent();
	private String httpType;
	private String hostName;
	private Credentials credentials;
	private String sourceIp;
	private ServiceClient serviceClient;
	private String realIpForConsole;
	private Boolean useSSLForConsole;
	private String userAgent = DEFAULT_USER_AGENT;
	private boolean mUUIDTag = false;
	private boolean useDirectMode = false;
	/**
	 * Real backend server's IP address. If not null, skip resolving DNS
	 */
	private String realServerIP = null;
	private String resourceOwnerAccount = null;
	private SlsSigner signer;

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

	@Deprecated
	public void EnableDirectMode() {
		setUseDirectMode(true);
	}

	@Deprecated
	public void DisableDirectMode() {
		setUseDirectMode(false);
	}

	public void setUseDirectMode(boolean useDirectMode) {
		this.useDirectMode = useDirectMode;
	}

	public boolean isUseDirectMode() {
		return useDirectMode;
	}

	public String getResourceOwnerAccount() {
		return resourceOwnerAccount;
	}

	public void setResourceOwnerAccount(String resourceOwnerAccount) {
		this.resourceOwnerAccount = resourceOwnerAccount;
	}

	public HttpClientConnectionManager getConnectionManager() {
        return serviceClient.getConnectionManager();
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
		this(endpoint, new DefaultCredentails(accessId, accessKey), null);
	}

	public Client(String endpoint, String accessId, String accessKey, ClientConfiguration configuration) {
		this(endpoint, accessId, accessKey, null, configuration);
	}

	public Client(String endpoint, String roleName) {
		this(endpoint, new ECSRoleCredentials(roleName), null);
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
	 * @param sourceIp
	 *            client ip address
	 */
	public Client(String endpoint, String accessId, String accessKey, String sourceIp) {
		this(endpoint, new DefaultCredentails(accessId, accessKey), sourceIp);
	}

	public Client(String endpoint, Credentials credentials, String sourceIp) {
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setMaxConnections(Consts.HTTP_CONNECT_MAX_COUNT);
		clientConfig.setConnectionTimeout(Consts.HTTP_CONNECT_TIME_OUT);
		clientConfig.setSocketTimeout(Consts.HTTP_SEND_TIME_OUT);
		this.serviceClient = new DefaultServiceClient(clientConfig);
		configure(endpoint, credentials, sourceIp);
	}

	/**
	 * @deprecated Use Client(String endpoint, String accessId, String accessKey, String sourceIp,
	 * 	              ClientConfiguration config) instead.
	 */
	@Deprecated
    public Client(String endpoint, String accessId, String accessKey, String sourceIp,
                  int connectMaxCount, int connectTimeout, int sendTimeout) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setMaxConnections(connectMaxCount);
        clientConfig.setConnectionTimeout(connectTimeout);
        clientConfig.setSocketTimeout(sendTimeout);
        this.serviceClient = new DefaultServiceClient(clientConfig);
		configure(endpoint, new DefaultCredentails(accessId, accessKey), sourceIp);
    }

    public Client(String endpoint, String accessId, String accessKey, ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
		configure(endpoint, new DefaultCredentails(accessId, accessKey), null);
    }

    public synchronized void setEndpoint(String endpoint) {
		Args.notNullOrEmpty(endpoint, "endpoint");
		if (endpoint.startsWith("http://")) {
			this.hostName = endpoint.substring(7);
			this.httpType = "http://";
		} else if (endpoint.startsWith("https://")) {
			this.hostName = endpoint.substring(8);
			this.httpType = "https://";
		} else {
			this.hostName = endpoint;
			this.httpType = "http://";
		}
		hostName = Utils.normalizeHostName(hostName);
		if (hostName == null) {
			throw new IllegalArgumentException("Invalid endpoint: " + endpoint);
		}
		if (NetworkUtils.isIPAddr(this.hostName)) {
			throw new IllegalArgumentException("The ip address is not supported");
		}
	}

    private void configure(String endpoint, Credentials credentials, String sourceIp) {
		setEndpoint(endpoint);
	    this.credentials = credentials;
	    this.sourceIp = sourceIp;
	    if (sourceIp == null || sourceIp.isEmpty()) {
		    this.sourceIp = NetworkUtils.getLocalMachineIP();
	    }
	    ClientConfiguration clientConfiguration = serviceClient.getClientConfiguration();
	    this.signer = SlsSignerBase.createRequestSigner(clientConfiguration, credentials);
    }

	public Client(String endpoint, String accessId, String accessKey, String sourceIp,
	              ClientConfiguration config) {
		Args.notNull(config, "Config");
		if (config.isRequestTimeoutEnabled()) {
			this.serviceClient = new TimeoutServiceClient(config);
		} else {
			this.serviceClient = new DefaultServiceClient(config);
		}
		configure(endpoint, new DefaultCredentails(accessId, accessKey), sourceIp);
	}

	public String getAccessId() {
		return credentials.getAccessKeyId();
	}

	public void setAccessId(String accessId) {
		credentials.setAccessKeyId(accessId);
	}

	public String getAccessKey() {
		return credentials.getAccessKeySecret();
	}

	public void setAccessKey(String accessKey) {
		credentials.setAccessKeySecret(accessKey);
	}

	public String getSecurityToken() {
		return credentials.getSecurityToken();
	}

	public void setSecurityToken(String securityToken) {
		credentials.setSecurityToken(securityToken);
	}

	public void shutdown() {
		serviceClient.shutdown();
	}

	URI GetHostURI(String project) {
		String endPointUrl = this.httpType + this.hostName;
		if (project != null && !project.isEmpty()) {
			if (!Utils.validateProject(project)) {
				throw new IllegalArgumentException("Invalid project: " + project);
			}
			endPointUrl = this.httpType + project + "." + this.hostName;
		}
		try {
			return new URI(endPointUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(ErrorCodes.ENDPOINT_INVALID, e);
		}
	}

	private URI GetHostURIByIp(String ip_addr) throws LogException {
		String endPointUrl = this.httpType + ip_addr;
		try {
			return new URI(endPointUrl);
		} catch (URISyntaxException e) {
			throw new LogException(ErrorCodes.ENDPOINT_INVALID,
					"Failed to get real server ip when direct mode in enabled", "");
		}
	}

	private static byte[] encodeToUtf8(String source) throws LogException {
		try {
			return source.getBytes(Consts.UTF_8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new LogException(ErrorCodes.ENCODING_EXCEPTION, e.getMessage(), "");
		}
	}

	private static String encodeResponseBodyToUtf8String(ResponseMessage response, String requestId) throws LogException {
		byte[] body = response.GetRawBody();
		if (body == null) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response body is null", null, requestId);
		}
		try {
			return new String(body, Consts.UTF_8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid utf-8 string: ", e, requestId);
		}
	}

	public GetLogtailProfileResponse ExtractLogtailProfile(Map<String, String> resHeaders, JSONObject object) throws LogException {
		try {
			int count = object.getIntValue("count");
			int total = object.getIntValue("total");
			JSONArray array = object.getJSONArray("profile");
			List<LogtailProfile> logtailProfiles = new ArrayList<LogtailProfile>();
			if (array != null) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject profileObj = array.getJSONObject(i);
					if (profileObj == null) {
						continue;
					}
					LogtailProfile logtailProfile = new LogtailProfile();
					logtailProfile.FromJsonObject(profileObj);
					logtailProfiles.add(logtailProfile);
				}
			}
			return new GetLogtailProfileResponse(resHeaders, count, total, logtailProfiles);
		} catch (LogException e) {
			throw new LogException(e.getErrorCode(), e.getMessage(),
					e.getCause(), GetRequestId(resHeaders));
		}
	}

	public TagResourcesResponse tagResources(String tagResourcesStr) throws LogException {
		CodingUtils.assertParameterNotNull(tagResourcesStr, "tagResourcesStr");
		Map<String, String> headParameter = GetCommonHeadPara("");
		byte[] body = encodeToUtf8(tagResourcesStr);
		String resourceUri = "/tag";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData("", HttpMethod.POST, resourceUri, urlParameter, headParameter, body);
        Map<String, String> resHeaders = response.getHeaders();
        return new TagResourcesResponse(resHeaders);
	}

	public TagResourcesResponse tagResources(TagResourcesRequest request) throws LogException {
		Args.notNull(request, "request");
		return tagResources(JsonUtils.serialize(request));
	}

	public UntagResourcesResponse untagResources(String untagResourcesStr) throws LogException {
		CodingUtils.assertParameterNotNull(untagResourcesStr, "tagResourcesStr");
		Map<String, String> headParameter = GetCommonHeadPara("");
		byte[] body = encodeToUtf8(untagResourcesStr);
		String resourceUri = "/untag";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData("", HttpMethod.POST, resourceUri, urlParameter, headParameter, body);
        Map<String, String> resHeaders = response.getHeaders();
        return new UntagResourcesResponse(resHeaders);
	}

	public UntagResourcesResponse untagResources(UntagResourcesRequest request) throws LogException {
		Args.notNull(request, "request");
		return untagResources(JsonUtils.serialize(request));
	}

	public TagResourcesResponse tagResourcesSystemTags(TagResourcesSystemTagsRequest request) throws LogException {
		Args.notNull(request, "request");
		Map<String, String> headParameter = GetCommonHeadPara("");
		String tagResourcesStr = JsonUtils.serialize(request);
		byte[] body = encodeToUtf8(tagResourcesStr);
		String resourceUri = "/systemtag";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData("", HttpMethod.POST, resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new TagResourcesResponse(resHeaders);
	}

	public UntagResourcesResponse untagResourcesSystemTags(UntagResourcesSystemTagsRequest request) throws LogException {
		Args.notNull(request, "request");
		String untagResourcesStr = JsonUtils.serialize(request);
		Map<String, String> headParameter = GetCommonHeadPara("");
		byte[] body = encodeToUtf8(untagResourcesStr);
		String resourceUri = "/systemuntag";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData("", HttpMethod.POST, resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UntagResourcesResponse(resHeaders);
	}

	public ListTagResourcesResponse listSystemTagResources(ListSystemTagResourcesRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		Map<String, String> headParameter = GetCommonHeadPara("");
		String resourceUri = "/systemtags";
		ResponseMessage response = SendData("", HttpMethod.GET, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		List<TagResource> tagResources = ExtractTagResources(object, requestId);
		String nextToken = object.getString("nextToken");
		return new ListTagResourcesResponse(resHeaders, nextToken, tagResources);
	}

	protected List<TagResource> ExtractTagResources(JSONObject object, String requestId)
			throws LogException {
		List<TagResource> tagResources = new ArrayList<TagResource>();
		if (object == null) {
			return tagResources;
		}
		try {
			JSONArray array = object.getJSONArray("tagResources");
			if (array == null) {
				return tagResources;
			}

			for (int index = 0; index < array.size(); index++) {
				JSONObject item = array.getJSONObject(index);
				if(item != null) {
					tagResources.add(TagResource.FromJsonObject(item));
				}
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + object.toString(), e, requestId);
		}

		return tagResources;
	}


	public ListTagResourcesResponse listTagResources(ListTagResourcesRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		Map<String, String> headParameter = GetCommonHeadPara("");
		String resourceUri = "/tags";
		ResponseMessage response = SendData("", HttpMethod.GET, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		List<TagResource> tagResources = ExtractTagResources(object, requestId);
		String nextToken = object.getString("nextToken");
		return new ListTagResourcesResponse(resHeaders, nextToken, tagResources);
	}

	public GetLogtailProfileResponse GetLogtailProfile(String project, String logstore, String source,
			int line, int offset) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logstore, "logstore");
		GetLogtailProfileRequest request = new GetLogtailProfileRequest(project, logstore, source, line, offset);
		return GetLogtailProfile(request);
	}

	public GetLogtailProfileResponse GetLogtailProfile(GetLogtailProfileRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		CodingUtils.validateLogstore(request.getLogStore());
		String resourceUri = "/logstores/" + request.getLogStore() + Consts.CONST_GETLOGTAILPROFILE_URI;
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
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
		CodingUtils.validateLogstore(request.GetLogStore());
		String resourceUri = "/logstores/" + request.GetLogStore() + "/index";
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray object = ParseResponseMessageToArray(response, requestId);
		GetHistogramsResponse histogramResponse = new GetHistogramsResponse(resHeaders);
		histogramResponse.fromJSON(object);
		return histogramResponse;
	}

	public PutLogsResponse PutLogs(String project, String logStore, byte[] logGroupBytes, String compressType, String shardHash) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(logGroupBytes, "logGroupBytes");
		PutLogsRequest request = new PutLogsRequest(project, logStore, null,
				null, logGroupBytes, shardHash);
		request.SetCompressType(CompressType.fromString(compressType));
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
		PutLogsRequest request = new PutLogsRequest(project, logStore, topic,
				source, logItems, shardHash);
		request.SetCompressType(CompressType.LZ4);
		return PutLogs(request);
	}

	@Override
	public PutLogsResponse PutLogs(String project, String logStore,
			String topic, List<LogItem> logItems, String source)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(logItems, "logGroup");
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
		String shardKey = request.getHashKey();
		CompressType compressType = request.GetCompressType();
		CodingUtils.assertParameterNotNull(compressType, "compressType");

		byte[] logBytes = request.GetLogGroupBytes();
		if (logBytes == null) {
			List<LogItem> logItems = request.GetLogItems();
			String topic = request.GetTopic();
			CodingUtils.assertParameterNotNull(topic, "topic");
			String source = request.GetSource();
			if (!Consts.CONST_SLS_JSON.equals(request.getContentType())) {
				Logs.LogGroup.Builder logs = Logs.LogGroup.newBuilder();
				logs.setTopic(topic);
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
					tagBuilder.setValue(UUID.randomUUID().toString() + "-" + Math.random());
				}
				for (LogItem item : logItems) {
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
				jsonObj.put("__topic__", topic);
				if (source == null || source.isEmpty()) {
					jsonObj.put("__source__", this.sourceIp);
				} else {
					jsonObj.put("__source__", source);
				}
				JSONArray logsArray = new JSONArray();
				for (LogItem item : logItems) {
					JSONObject jsonObjInner = new JSONObject();
					jsonObjInner.put(Consts.CONST_RESULT_TIME, item.mLogTime);
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
					tagObj.put("__pack_unique_id__", UUID.randomUUID().toString() + "-" + Math.random());
				}
				if (tagObj.size() > 0) {
					jsonObj.put("__tags__", tagObj);
				}
				logBytes = encodeToUtf8(jsonObj.toString());
			}
		}
		int bodySize = logBytes.length;
		if (bodySize > Consts.CONST_MAX_PUT_SIZE) {
			throw new LogException("InvalidLogSize",
					"logItems' size exceeds maximum limitation : "
							+ Consts.CONST_MAX_PUT_SIZE
							+ " bytes", "");
		} else if (bodySize > Consts.CONST_MAX_POST_BODY_SIZE) {
			throw new LogException("PostBodyTooLarge",
					"body size " + bodySize + " must little than " + Consts.CONST_MAX_POST_BODY_SIZE, "");
		}

		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, request.getContentType());
		long originalSize = logBytes.length;

		if (compressType == CompressType.LZ4) {
			logBytes = LZ4Encoder.compressToLhLz4Chunk(logBytes.clone());
			headParameter.put(Consts.CONST_X_SLS_COMPRESSTYPE,
					compressType.toString());
		} else if (compressType == CompressType.GZIP) {
			logBytes = GzipUtils.compress(logBytes);
			headParameter.put(Consts.CONST_X_SLS_COMPRESSTYPE,
					compressType.toString());
		}

		headParameter.put(Consts.CONST_X_SLS_BODYRAWSIZE,
				String.valueOf(originalSize));

		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/logstores/" + logStore;
		if (shardKey == null || shardKey.length() == 0) {
			resourceUri += "/shards/lb";
		} else {
			resourceUri += "/shards/route";
			urlParameter.put("key", shardKey);
			if (request.getHashRouteKeySeqId() != null) {
				urlParameter.put("seqid", String.valueOf(request.getHashRouteKeySeqId()));
			}
		}
		long cmp_size = logBytes.length;

		for (int i = 0; i < 2; i++) {
			String server_ip = this.realServerIP;
			ClientConnectionStatus connection_status = null;
			if (this.useDirectMode) {
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
				String requestId = e.getRequestId();
				if (i == 1 || requestId != null && !requestId.isEmpty())
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
				.GetConnectionContainer(this.hostName, credentials.getAccessKeyId(), credentials.getAccessKeySecret());
		ClientConnectionStatus connection_status = connection_container.GetGlobalConnection();
		if (connection_status == null || !connection_status.IsValidConnection()) {
			connection_container.UpdateGlobalConnection();
			connection_status = connection_container.GetGlobalConnection();
			if (connection_status == null || connection_status.GetIpAddress() == null
					|| connection_status.GetIpAddress().isEmpty()) {
				throw new LogException(ErrorCodes.ENDPOINT_INVALID, "Failed to get real server ip when direct mode is enabled",
						"");
			}
		}
		return connection_status;
	}

	private ClientConnectionStatus GetShardConnectionStatus(String project, String logstore, int shard_id)
			throws LogException {
		ClientConnectionContainer connection_container = ClientConnectionHelper.getInstance()
				.GetConnectionContainer(this.hostName, credentials.getAccessKeyId(), credentials.getAccessKeySecret());
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
		GetLogsRequest request = new GetLogsRequest(project, logStore, from, to, topic, query);
		return GetLogs(request);
	}
	public GetLogsResponse GetLogs(String project, String logStore, int from,
								   int to, String topic, String query, long line, long offset,
								   boolean reverse) throws LogException
	{
		return GetLogs(project,logStore,from,to,topic,query,line,offset,reverse,false);
	}
	public GetLogsResponse GetLogs(String project, String logStore, int from,
			int to, String topic, String query, long line, long offset,
			boolean reverse, boolean powerSql) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		CodingUtils.validateOffset(offset);
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query, offset, line, reverse,powerSql);
		return GetLogs(request);
	}
	public GetLogsResponse 	GetLogs(String project, String logStore, int from,
									  int to, String topic, String query, long line, long offset,
									  boolean reverse,
									  boolean powerSql,
									  boolean forward) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		CodingUtils.validateOffset(offset);
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query, offset, line, reverse,powerSql, forward);
		return GetLogs(request);
	}

	public GetLogsResponse GetLogs(String project, String logStore, int from,
								   int to, String topic, String query, long line, long offset,
								   boolean reverse, int shard) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		CodingUtils.validateOffset(offset);
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query, offset, line, reverse, shard);
		return GetLogs(request);
	}

	public GetLogsResponse GetLogs(String project, String logStore, int from,
									  int to, String topic, String query, long line, long offset,
									  boolean reverse, boolean forward, String session) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(topic, "topic");
		CodingUtils.assertParameterNotNull(query, "query");
		CodingUtils.validateOffset(offset);
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, topic, query, offset, line, reverse, forward, session);
		return GetLogs(request);
	}


	public GetLogsResponse executeLogstoreSql(String project, String logStore, int from,
											  int to, String sql, boolean powerSql) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertParameterNotNull(sql, "sql");
		GetLogsRequest request = new GetLogsRequest(project, logStore, from,
				to, "", sql);
		request.SetPowerSql(powerSql);
		return GetLogs(request);
	}

	/**
	 * getContextLogs uses @packID and @packMeta to specify a log as start log and queries logs around it.
	 *
	 * @param packID  package ID of the start log, such as 895CEA449A52FE-1 ({hex prefix}-{hex sequence number}).
	 * @param packMeta  package meta of the start log, such as 0|MTU1OTI4NTExMjg3NTQ2MjQ3MQ==|2|1.
	 * @param backLines  the number of logs to request backward, at most 100.
	 * @param forwardLines  the number of logs to request forward, at most 100.
	 * @return see getter in GetContextLogsResponse for more information.
	 * @throws LogException
	 */
	public GetContextLogsResponse getContextLogs(String project, String logstore,
			String packID, String packMeta,
			int backLines, int forwardLines) throws LogException{
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logstore, "logStore");
		GetContextLogsRequest request = new GetContextLogsRequest(project, logstore,
				packID, packMeta, backLines, forwardLines);
		return getContextLogs(request);
	}

	public GetLogsResponse GetProjectLogs(String project,String query) throws  LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(query, "query");
		GetProjectLogsRequest request = new GetProjectLogsRequest(project, query);
		return GetProjectLogs(request);
	}

	public GetLogsResponse GetProjectLogs(GetProjectLogsRequest request) throws  LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/logs";
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		GetLogsResponse getLogsResponse = new GetLogsResponse(resHeaders);
		String requestId = GetRequestId(resHeaders);
		JSONArray object = ParseResponseMessageToArrayWithFastJson(response, requestId);
		getLogsResponse.setLogs(QueryResult.parseData(object, requestId));
		return getLogsResponse;
	}

	public GetLogsResponse executeProjectSql(String project,
											 String sql, boolean powerSql) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(sql, "sql");
		GetProjectLogsRequest request = new GetProjectLogsRequest(project, sql);
		request.SetPowerSql(powerSql);
		return GetProjectLogs(request);
	}

	private JSONArray ParseResponseMessageToArrayWithFastJson(ResponseMessage response,
			String requestId) throws LogException {
		String returnStr = encodeResponseBodyToUtf8String(response, requestId);
		try {
			return (JSONArray) JSONObject.parse(returnStr, Feature.DisableSpecialKeyDetect);
		} catch (com.alibaba.fastjson.JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid json string : " + returnStr, e,
					requestId);
		}
	}

	private GetLogsResponse ParseResponseWithFastJsonStreamResolve(ResponseMessage response) throws LogException {
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		GetLogsResponse getLogsResponse = null;
		JSONReader reader = null;
		try {
			int statusCode = response.getStatusCode();
			if (statusCode != Consts.CONST_HTTP_OK) {
				try {
					ExtractResponseBody(response);
					JSONObject object = parseResponseBody(response, requestId);
					ErrorCheck(object, requestId, statusCode);
				} catch (LogException ex) {
					ex.setHttpCode(response.getStatusCode());
					throw ex;
				}
			}
			if (response.getContent() == null) {
				return getLogsResponse;
			}
			getLogsResponse = new GetLogsResponse(resHeaders);
			reader = new JSONReader(new InputStreamReader(response.getContent(), Consts.UTF_8_ENCODING));
			reader.startArray();
			while (reader.hasNext()) {
				getLogsResponse.addLog(extractLogFromReader(reader, requestId));
				//getLogsResponse.addLog(new QueriedLog(source, logItem));
			}
			reader.endArray();
		} catch (com.alibaba.fastjson.JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "resolve json error: ", e, requestId);
		} catch (UnsupportedEncodingException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid utf-8 string: ", e, requestId);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException ignore) {
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception ignore) {
			}
		}
		return getLogsResponse;
	}

	private QueriedLog extractLogFromReader(JSONReader reader, String requestId) throws JSONException, LogException {
		reader.startObject();
		String source = "";
		LogItem logItem = new LogItem();
		while (reader.hasNext()) {
			String key = reader.readString();
			String value = reader.readString();
			if (key.equals(Consts.CONST_RESULT_SOURCE)) {
				source = value;
			} else if (key.equals(Consts.CONST_RESULT_TIME)) {
				try {
					logItem.mLogTime = Integer.parseInt(value);
				} catch (NumberFormatException ex) {
					throw new LogException(Consts.INVALID_LOG_TIME,
							"The field __time__ is invalid in your query result: " + value, requestId);
				}
			} else {
				logItem.PushBack(key, value);
			}
		}
		reader.endObject();
		return new QueriedLog(source, logItem);
	}

	public GetLogsResponse GetLogs(GetLogsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		String logStore = request.GetLogStore();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		CodingUtils.validateLogstore(logStore);
		String resourceUri = "/logstores/" + logStore + "/index";
		ResponseMessage response = SendDataWithResolveResponse(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
//		Map<String, String> resHeaders = response.getHeaders();
//		String requestId = GetRequestId(resHeaders);
//		GetLogsResponse getLogsResponse = new GetLogsResponse(resHeaders);
//		ParseResponseWithFastJsonStreamResolve(response, getLogsResponse, requestId);
//		JSONArray object = ParseResponseMessageToArrayWithFastJson(response, requestId);
//		extractLogsWithFastJson(getLogsResponse, object, requestId);
		return ParseResponseWithFastJsonStreamResolve(response);
	}
	public GetLogsResponseV2 GetLogsV2(GetLogsRequestV2 request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		String logstore = request.getLogstore();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_ACCEPT_ENCODING, request.getAcceptEncoding());
		CodingUtils.validateLogstore(logstore);
		String resourceUri = "/logstores/" + logstore + "/logs";
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, request.getRequestBody());
		return GetLogsResponseV2.deserializeFrom(response);
	}

	public GetContextLogsResponse getContextLogs(GetContextLogsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> urlParameter = request.GetAllParams();
		String project = request.GetProject();
		String logStore = request.getLogstore();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		CodingUtils.validateLogstore(logStore);
		String resourceUri = "/logstores/" + logStore;
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		GetContextLogsResponse logsResponse = new GetContextLogsResponse(resHeaders, object);
		logsResponse.setLogs(QueryResult.parseData(object.getJSONArray("logs"), requestId));
		return logsResponse;
	}

	public ListLogStoresResponse ListLogStores(String project, int offset, int size) throws LogException {
		return ListLogStores(project, offset, size, "");
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
		JSONObject object = parseResponseBody(response, requestId);
		ListLogStoresResponse listLogStoresResponse = new ListLogStoresResponse(resHeaders);
		listLogStoresResponse.SetLogStores(ExtractJsonArray(
				Consts.CONST_RESULT_LOG_STORES, object));
		listLogStoresResponse.SetTotal(object.getIntValue(Consts.CONST_TOTAL));
		return listLogStoresResponse;
	}

	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, long fromTime) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		GetCursorRequest request = new GetCursorRequest(project, logStore, shardId, fromTime);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(String project, String logStore,
			int shardId, Date fromTime) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStream");
		CodingUtils.assertParameterNotNull(fromTime, "fromTime");
		long timeStamp = Utils.dateToTimestamp(fromTime);
		GetCursorRequest request = new GetCursorRequest(project, logStore, shardId, timeStamp);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(String project, String logStream,
			int shardId, CursorMode mode) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStream, "logStream");
		GetCursorRequest request = new GetCursorRequest(project, logStream, shardId, mode);
		return GetCursor(request);
	}

	public GetCursorResponse GetCursor(GetCursorRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		String shardId = String.valueOf(request.GetShardId());
		CodingUtils.validateLogstore(logStore);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + shardId;
		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = new ResponseMessage();
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);
			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);
			JSONObject object = parseResponseBody(response, requestId);
            return new GetCursorResponse(resHeaders, object.getString("cursor"));
		} catch (JSONException e) {
			throw new LogException("FailToCreateCursor", e.getMessage(), e,
					GetRequestId(response.getHeaders()));
		}
	}

	public GetCursorTimeResponse GetCursorTime(GetCursorTimeRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		String shardId = String.valueOf(request.GetShardId());
		CodingUtils.validateLogstore(logStore);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + shardId;
		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = new ResponseMessage();
		GetCursorTimeResponse getCursorTimeResponse;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);
			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);
			JSONObject object = parseResponseBody(response, requestId);
			getCursorTimeResponse = new GetCursorTimeResponse(resHeaders,
					object.getIntValue("cursor_time"));
		} catch (JSONException e) {
			throw new LogException("FailToCreateCursor", e.getMessage(), e,
					GetRequestId(response.getHeaders()));
		}
		return getCursorTimeResponse;
	}

    public GetCursorTimeResponse GetPrevCursorTime(String project, String logStore,int shardId, String cursor) throws LogException {
		if(cursor.isEmpty())
			throw new LogException(ErrorCodes.INVALID_CURSOR, "empty cursor string", "");
		long prv = Long.parseLong(new String(Base64.decodeBase64(cursor))) - 1;
		if(prv >= 0){
			cursor = new String(Base64.encodeBase64(Long.toString(prv).getBytes()));
		}
		else{
			throw new LogException(ErrorCodes.INVALID_CURSOR, "this cursor has no prev value", "");
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

	public ListShardResponse SplitShard(String project, String logStore,
			int shardId, String midHash) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "shardId");
		return SplitShard(new SplitShardRequest(project, logStore, shardId, midHash));
	}

	public ListShardResponse SplitShard(SplitShardRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);
		int shardId = request.GetShardId();
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + shardId;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ArrayList<Shard> shards = ExtractShards(array, requestId);
        return new ListShardResponse(resHeaders, shards);
	}

	public ListShardResponse MergeShards(String project, String logStore,
			int shardId) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "shardId");
		return MergeShards(new MergeShardsRequest(project, logStore, shardId));
	}

	public ListShardResponse MergeShards(MergeShardsRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);
		int shardId = request.GetShardId();
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + shardId;
		Map<String, String> urlParameter = request.GetAllParams();
        ResponseMessage response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
        String requestId = GetRequestId(resHeaders);
        JSONArray array = ParseResponseMessageToArray(response, requestId);
        ArrayList<Shard> shards = ExtractShards(array, requestId);
        return new ListShardResponse(resHeaders, shards);
	}

	public ListShardResponse ListShard(String project, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return ListShard(new ListShardRequest(project, logStore));
	}

	public String GetServerIpAddress(String project) {
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
		return Utils.getOrEmpty(out_header, Consts.CONST_X_SLS_HOSTIP);
	}
	public ListShardResponse ListShard(ListShardRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET, resourceUri, urlParameter,
				headParameter);
		Map<String, String> resHeaders = response.getHeaders();
        String requestId = GetRequestId(resHeaders);
        JSONArray array = ParseResponseMessageToArray(response, requestId);
        ArrayList<Shard> shards = ExtractShards(array, requestId);
        return new ListShardResponse(resHeaders, shards);
	}

	protected String GetRequestId(Map<String, String> headers) {
	    return Utils.getOrEmpty(headers, Consts.CONST_X_SLS_REQUESTID);
	}

	@Deprecated
	@Override
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return BatchGetLog(new BatchGetLogRequest(project, logStore, shardId, count, cursor));
	}

	@Deprecated
	@Override
	public BatchGetLogResponse BatchGetLog(String project, String logStore,
			int shardId, int count, String cursor, String end_cursor)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return BatchGetLog(new BatchGetLogRequest(project, logStore, shardId,
				count, cursor, end_cursor));
	}

	@Deprecated
	@Override
	public BatchGetLogResponse BatchGetLog(BatchGetLogRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);

		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + request.GetShardId();
		headParameter.put(Consts.CONST_ACCEPT_ENCODING, Consts.CONST_LZ4);
		headParameter.put(Consts.CONST_HTTP_ACCEPT, Consts.CONST_PROTO_BUF);
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response;
		BatchGetLogResponse batchGetLogResponse;
		for (int i = 0; i < 2; i++) {
			String server_ip = this.realServerIP;
			ClientConnectionStatus connection_status = null;
			if (this.useDirectMode) {
				connection_status = GetShardConnectionStatus(project, logStore, request.GetShardId());
				server_ip = connection_status.GetIpAddress();
			}
			try {
				response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headParameter, new byte[0], null,
						server_ip);
				Map<String, String> resHeaders = response.getHeaders();
				byte[] rawData = response.GetRawBody();

				batchGetLogResponse = new BatchGetLogResponse(resHeaders, rawData);
				if (connection_status != null) {
					connection_status.UpdateLastUsedTime(System.nanoTime());
					connection_status.AddPullDataSize(batchGetLogResponse.GetRawSize());
				}
				return batchGetLogResponse;
			} catch (LogException e) {
				if (i == 1 || e.getRequestId() != null && !e.getRequestId().isEmpty()) {
					throw e;
				}
				if (connection_status != null) {
					connection_status.DisableConnection();
				}
			}
		}
		return null; // never happen
	}

    @Override
    public PullLogsResponse pullLogs(PullLogsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        String project = request.GetProject();
        String logStore = request.getLogStore();
		CodingUtils.validateLogstore(logStore);

        Map<String, String> headers = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shards/" + request.getShardId();
        headers.put(Consts.CONST_ACCEPT_ENCODING, Consts.CONST_LZ4);
        headers.put(Consts.CONST_HTTP_ACCEPT, Consts.CONST_PROTO_BUF);
        Map<String, String> urlParameter = request.GetAllParams();

        for (int i = 0; i < 2; i++) {
            String serverIp = this.realServerIP;
            ClientConnectionStatus connectionStatus = null;
            if (useDirectMode) {
                connectionStatus = GetShardConnectionStatus(project, logStore, request.getShardId());
                serverIp = connectionStatus.GetIpAddress();
            }
            try {
                ResponseMessage response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headers, new byte[0], null, serverIp);
                Map<String, String> resHeaders = response.getHeaders();
                PullLogsResponse plr = new PullLogsResponse(resHeaders, response.GetRawBody());
                if (connectionStatus != null) {
                    connectionStatus.UpdateLastUsedTime(System.nanoTime());
                    connectionStatus.AddPullDataSize(plr.getRawSize());
                }
                return plr;
            } catch (LogException ex) {
                if (i == 1 || ex.getRequestId() != null && !ex.getRequestId().isEmpty()) {
                    throw ex;
                }
                if (connectionStatus != null) {
                    connectionStatus.DisableConnection();
                }
            }
        }
        return null;
    }

    public CreateConfigResponse CreateConfig(String project, Config config) throws LogException {
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
		byte[] body = encodeToUtf8(config.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/configs";
		Map<String, String> urlParameter = new HashMap<String, String>();
        ResponseMessage response = SendData(project, HttpMethod.POST, resourceUri,
				urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
        return new CreateConfigResponse(resHeaders);
	}

	public UpdateConfigResponse UpdateConfig(String project, Config config) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(config, "config");
		return UpdateConfig(new UpdateConfigRequest(project, config));
	}

	public UpdateConfigResponse UpdateConfig(UpdateConfigRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Config config = request.GetConfig();
		CodingUtils.assertParameterNotNull(config, "config");
		String configName = config.GetConfigName();
		CodingUtils.validateConfig(configName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(config.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/configs/" + configName;
		Map<String, String> urlParameter = new HashMap<String, String>();
        ResponseMessage response = SendData(project, HttpMethod.PUT, resourceUri, urlParameter,
				headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
        return new UpdateConfigResponse(resHeaders);
	}

	protected Config ExtractConfigFromResponse(JSONObject dict, String requestId) throws LogException {
		Config config = new Config();
		try {
			config.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.getErrorCode(), e.getMessage(),
					e.getCause(), requestId);
		}
		return config;
	}

	public GetConfigResponse GetConfig(String project, String configName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return GetConfig(new GetConfigRequest(project, configName));
	}

	public GetConfigResponse GetConfig(GetConfigRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String configName = request.GetConfigName();
		CodingUtils.validateConfig(configName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/configs/" + configName;
		Map<String, String> urlParameter = request.GetAllParams();
        ResponseMessage response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
        Config config = ExtractConfigFromResponse(object, requestId);
        return new GetConfigResponse(resHeaders, config);
	}

	public DeleteConfigResponse DeleteConfig(String project, String configName)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return DeleteConfig(new DeleteConfigRequest(project, configName));
	}

	public DeleteConfigResponse DeleteConfig(DeleteConfigRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String configName = request.GetConfigName();
		CodingUtils.validateConfig(configName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/configs/" + configName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteConfigResponse(resHeaders);
	}

	protected List<String> ExtractConfigs(JSONObject object, String requestId)
			throws LogException {
		List<String> configs = new ArrayList<String>();
		if (object == null) {
			return configs;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("configs");
			if (array == null) {
				return configs;
			}
			for (int i = 0; i < array.size(); i++) {
				String configName = array.getString(i);
				configs.add(configName);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid config json array string : "
							+ array.toString(), e, requestId);
		}
		return configs;
	}

	public ListConfigResponse ListConfig(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListConfig(new ListConfigRequest(project));
	}

	public ListConfigResponse ListConfig(String project, int offset, int size)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListConfig(new ListConfigRequest(project, offset, size));
	}

	public ListConfigResponse ListConfig(String project, String configName,
			int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		return ListConfig(new ListConfigRequest(project, configName, offset, size));
	}

	public ListConfigResponse ListConfig(String project, String configName,
			String logstoreName, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(configName, "configName");
		CodingUtils.assertStringNotNullOrEmpty(logstoreName, "logstoreName");
		return ListConfig(new ListConfigRequest(project, configName, logstoreName, offset, size));
	}

	public ListConfigResponse ListConfig(ListConfigRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/configs";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = new ResponseMessage();
		ListConfigResponse listConfigResponse;
		JSONObject object = null;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);
			Map<String, String> resHeaders = response.getHeaders();
			String requestId = GetRequestId(resHeaders);
			object = parseResponseBody(response, requestId);
			int total = object.getIntValue("total");
			int count = object.getIntValue("count");
            List<String> configs = ExtractConfigs(object, requestId);
			listConfigResponse = new ListConfigResponse(resHeaders, count,
					total, configs);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid list config json string : "
							+ Utils.safeToString(object), e,
					GetRequestId(response.getHeaders()));
		}
		return listConfigResponse;
	}

	public CreateMachineGroupResponse CreateMachineGroup(String project, MachineGroup group) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.assertParameterNotNull(group, "group");
		return CreateMachineGroup(new CreateMachineGroupRequest(project, group));
	}

	public CreateMachineGroupResponse CreateMachineGroup(CreateMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		MachineGroup group = request.GetMachineGroup();
		CodingUtils.assertParameterNotNull(group, "group");
		Map<String, String> headParameter = GetCommonHeadPara(project);
        headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		byte[] body = encodeToUtf8(group.ToRequestString());
		String resourceUri = "/machinegroups";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateMachineGroupResponse(resHeaders);
	}

	public UpdateMachineGroupResponse UpdateMachineGroup(String project, MachineGroup group) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(group, "group");
		return UpdateMachineGroup(new UpdateMachineGroupRequest(project, group));
	}

	public UpdateMachineGroupResponse UpdateMachineGroup(UpdateMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		MachineGroup group = request.GetMachineGroup();
		CodingUtils.assertParameterNotNull(group, "group");
		String groupName = group.GetGroupName();
		CodingUtils.validateMachineGroup(groupName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(group.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/machinegroups/" + groupName;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateMachineGroupResponse(resHeaders);
	}

	protected MachineGroup ExtractMachineGroupFromResponse(JSONObject dict, String requestId) throws LogException {
		MachineGroup group = new MachineGroup();
		try {
			group.FromJsonString(dict.toString());
		} catch (LogException e) {
			throw new LogException(e.getErrorCode(), e.getMessage(),
					e.getCause(), requestId);
		}
		return group;
	}

	public GetAppliedConfigResponse GetAppliedConfig(String project, String groupName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return GetAppliedConfig(new GetAppliedConfigsRequest(project, groupName));
	}

	public GetAppliedConfigResponse GetAppliedConfig(GetAppliedConfigsRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.validateMachineGroup(groupName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName + "/configs";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		List<String> group = ExtractJsonArray("configs", object);
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
        String resourceUri = "/configs/" + configName + "/machinegroups";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		List<String> group = ExtractJsonArray("machinegroups", object);
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
		CodingUtils.validateMachineGroup(groupName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		MachineGroup group = ExtractMachineGroupFromResponse(object, requestId);
        return new GetMachineGroupResponse(resHeaders, group);
	}

	@Override
	public ListMachinesResponse ListMachines(String project,
			String machineGroup, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateMachineGroup(machineGroup);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + machineGroup + "/machines";
		headParameter.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(0));
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put("offset", String.valueOf(offset));
		urlParameter.put("size", String.valueOf(size));
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		return ExtractMachinesFromResponse(resHeaders, object);
	}

	private ListMachinesResponse ExtractMachinesFromResponse(
			Map<String, String> resHeaders, JSONObject dict)
			throws LogException {
		try {
			int count = dict.getIntValue("count");
			int total = dict.getIntValue("total");
			JSONArray array = dict.getJSONArray("machines");
			List<Machine> machines = new ArrayList<Machine>();
			if (array != null) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject machine_obj = array.getJSONObject(i);
					if (machine_obj == null) {
						continue;
					}
					Machine machine = new Machine();
					machine.FromJsonObject(machine_obj);
					machines.add(machine);
				}
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
		return ApproveMachineGroup(new ApproveMachineGroupRequest(project, groupName));
	}

	public ApproveMachineGroupResponse ApproveMachineGroup(
			ApproveMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.validateMachineGroup(groupName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName + "/approve";
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
		return DeleteMachineGroup(new DeleteMachineGroupRequest(project, groupName));
	}

	public DeleteMachineGroupResponse DeleteMachineGroup(
			DeleteMachineGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String groupName = request.GetGroupName();
		CodingUtils.validateMachineGroup(groupName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteMachineGroupResponse(resHeaders);
	}

	protected List<String> ExtractMachineGroups(JSONObject object,
			String requestId) throws LogException {
		try {
			return JsonUtils.readOptionalStrings(object, "machinegroups");
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid machine group json array string : " + object, e, requestId);
		}
	}

	public ListMachineGroupResponse ListMachineGroup(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListMachineGroup(new ListMachineGroupRequest(project));
	}

	public ListMachineGroupResponse ListMachineGroup(String project, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListMachineGroup(new ListMachineGroupRequest(project, offset, size));
	}

	public ListMachineGroupResponse ListMachineGroup(String project,
			String groupName, int offset, int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(groupName, "groupName");
		return ListMachineGroup(new ListMachineGroupRequest(project, groupName, offset, size));
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
			object = parseResponseBody(response, requestId);
			int total = object.getIntValue("total");
			int count = object.getIntValue("count");
            List<String>  groups = ExtractMachineGroups(object, requestId);
			listMachineGroupResponse = new ListMachineGroupResponse(resHeaders,
					count, total, groups);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid config json string : "
							+ Utils.safeToString(object), e,
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
		CodingUtils.validateMachineGroup(groupName);
		String configName = request.GetConfigName();
		CodingUtils.validateConfig(configName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName + "/configs/" + configName;
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
		CodingUtils.validateMachineGroup(groupName);
		String configName = request.GetConfigName();
		CodingUtils.validateConfig(configName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/machinegroups/" + groupName + "/configs/" + configName;
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
		byte[] body = encodeToUtf8(acl.ToRequestString());
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

	public ListACLResponse ListACL(String project, int offset, int size)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return ListACL(new ListACLRequest(project, offset, size));
	}

	public ListACLResponse ListACL(String project, String logStore)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return ListACL(new ListACLRequest(project, logStore));
	}

	public ListACLResponse ListACL(String project, String logStore, int offset,
			int size) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStore, "logStore");
		return ListACL(new ListACLRequest(project, logStore, offset, size));
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

	protected List<ACL> ExtractACLs(JSONObject object, String requestId) throws LogException {
		List<ACL> acls = new ArrayList<ACL>();
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("acls");
			if (array == null) {
				return acls;
			}
			for (int i = 0; i < array.size(); i++) {
				JSONObject aclDict = array.getJSONObject(i);
				if (aclDict == null) {
					continue;
				}
				ACL acl = ExtractACLFromResponse(aclDict, requestId);
				acls.add(acl);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
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
		JSONObject object = null;
		ResponseMessage response = new ResponseMessage();
		ListACLResponse listACLResponse;
		try {
			response = SendData(project, HttpMethod.GET, resourceUri,
					urlParameter, headParameter);
			Map<String, String> resHeaders = response.getHeaders();
            String requestId = GetRequestId(resHeaders);
			object = parseResponseBody(response, requestId);
			int total = object.getIntValue("total");
			int count = object.getIntValue("count");
            List<ACL> acls = ExtractACLs(object, requestId);
			listACLResponse = new ListACLResponse(resHeaders, count, total, acls);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid list acl json string : "
							+ Utils.safeToString(object), e,
					GetRequestId(response.getHeaders()));
		}
		return listACLResponse;
	}

	private List<String> ExtractJsonArray(String nodeKey, JSONObject object) {
		try {
			return JsonUtils.readOptionalStrings(object, nodeKey);
		} catch (JSONException e) {
			return new ArrayList<String>();
		}
	}

	void ErrorCheck(JSONObject object, String requestId, int httpCode)
			throws LogException {
		if (object.containsKey(Consts.CONST_ERROR_CODE)) {
			try {
				String errorCode = object.getString(Consts.CONST_ERROR_CODE);
				String errorMessage = object.getString(Consts.CONST_ERROR_MESSAGE);
				throw new LogException(httpCode, errorCode, errorMessage, requestId);
			} catch (JSONException e) {
				throw new LogException(httpCode, "InvalidErrorResponse",
						"Error response is not a valid error json : \n"
								+ object.toString(), requestId);
			}
		} else {
			throw new LogException(httpCode, "InvalidErrorResponse",
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
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"Io exception happened when parse the response data : ", e,
					requestId);
		}
		response.SetBody(bytestream.toByteArray());
	}

    private JSONObject parseResponseBody(ResponseMessage response, String requestId) throws LogException {
        String body = encodeResponseBodyToUtf8String(response, requestId);
        try {
            return JSONObject.parseObject(body, Feature.DisableSpecialKeyDetect);
        } catch (JSONException ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "The response is not valid json string : " + body, ex, requestId);
        }
    }

	JSONArray ParseResponseMessageToArray(ResponseMessage response,
										  String requestId) throws LogException {
		String returnStr = encodeResponseBodyToUtf8String(response, requestId);
		try {
            return JSONArray.parseArray(returnStr);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid json string : " + returnStr, e,
					requestId);
		}
	}

	private Map<String, String> GetCommonHeadPara(String project) {
		HashMap<String, String> headParameter = new HashMap<String, String>();
		headParameter.put(Consts.CONST_USER_AGENT, userAgent);
		headParameter.put(Consts.CONST_X_SLS_BODYRAWSIZE, "0");
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_PROTO_BUF);
		if (!project.isEmpty()) {
			headParameter.put(Consts.CONST_HOST, project + "." + this.hostName);
		} else {
			headParameter.put(Consts.CONST_HOST, this.hostName);
		}
		headParameter.put(Consts.CONST_X_SLS_APIVERSION, Consts.DEFAULT_API_VESION);
		String securityToken = credentials.getSecurityToken();
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

	private ResponseMessage SendDataWithResolveResponse(String project, HttpMethod method,
									 String resourceUri, Map<String, String> urlParams,
									 Map<String, String> headParams) throws LogException {
		return SendDataWithResolveResponse(project, method, resourceUri, urlParams, headParams, new byte[0]);
	}

	protected ResponseMessage SendDataWithResolveResponse(String project, HttpMethod method, String resourceUri,
									   Map<String, String> parameters, Map<String, String> headers, byte[] body) throws LogException {
		return SendDataWithResolveResponse(project, method, resourceUri, parameters, headers, body, null, null);
	}

	private ResponseMessage SendDataWithResolveResponse(String project, HttpMethod method, String resourceUri,
									 Map<String, String> parameters, Map<String, String> headers, byte[] body,
									 Map<String, String> outputHeader, String serverIp)
			throws LogException {
		if (resourceOwnerAccount != null && !resourceOwnerAccount.isEmpty()) {
			headers.put(Consts.CONST_X_LOG_RESOURCEOWNERACCOUNT, resourceOwnerAccount);
		}
		signer.sign(method, headers, resourceUri, parameters, body);
		URI uri;
		if (serverIp == null) {
			uri = GetHostURI(project);
		} else {
			uri = GetHostURIByIp(serverIp);
		}
		RequestMessage request = BuildRequest(uri, method,
				resourceUri, parameters, headers,
				new ByteArrayInputStream(body), body.length);
		ResponseMessage response = null;
		try {
			response = this.serviceClient.sendRequest(request, Consts.UTF_8_ENCODING);
		} catch (ServiceException e) {
			throw new LogException("RequestError", "Web request failed: " + e.getMessage(), e, "");
		} catch (ClientException e) {
			throw new LogException("RequestError", "Web request failed: " + e.getMessage(), e, "");
		}
		return response;
	}

	private ResponseMessage SendData(String project, HttpMethod method,
			String resourceUri, Map<String, String> urlParams,
			Map<String, String> headParams) throws LogException {
		return SendData(project, method, resourceUri, urlParams, headParams, new byte[0]);
	}

	protected ResponseMessage SendData(String project, HttpMethod method,
			String resourceUri, Map<String, String> parameters,
			Map<String, String> headers, String requestBody) throws LogException {
		byte[] body = encodeToUtf8(requestBody);
		return SendData(project, method, resourceUri, parameters, headers, body);
	}

	protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
			Map<String, String> parameters, Map<String, String> headers, byte[] body) throws LogException {
		return SendData(project, method, resourceUri, parameters, headers, body, null, null);
	}

	private ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
									 Map<String, String> parameters, Map<String, String> headers, byte[] body,
									 Map<String, String> outputHeader, String serverIp)
			throws LogException {
		if (resourceOwnerAccount != null && !resourceOwnerAccount.isEmpty()) {
			headers.put(Consts.CONST_X_LOG_RESOURCEOWNERACCOUNT, resourceOwnerAccount);
		}
		signer.sign(method, headers, resourceUri, parameters, body);
		URI uri;
		if (serverIp == null) {
			uri = GetHostURI(project);
		} else {
			uri = GetHostURIByIp(serverIp);
		}
		RequestMessage request = BuildRequest(uri, method,
				resourceUri, parameters, headers,
				new ByteArrayInputStream(body), body.length);
		ResponseMessage response = null;
		try {
			response = this.serviceClient.sendRequest(request, Consts.UTF_8_ENCODING);
			ExtractResponseBody(response);
			if (outputHeader != null) {
				outputHeader.putAll(response.getHeaders());
			}
			int statusCode = response.getStatusCode();
			if (statusCode != Consts.CONST_HTTP_OK) {
				String requestId = GetRequestId(response.getHeaders());
				try {
					JSONObject object = parseResponseBody(response, requestId);
					ErrorCheck(object, requestId, statusCode);
				} catch (LogException ex) {
					ex.setHttpCode(response.getStatusCode());
					throw ex;
				}
			}
		} catch (ServiceException e) {
			throw new LogException("RequestError", "Web request failed: " + e.getMessage(), e, "");
		} catch (ClientException e) {
			throw new LogException("RequestError", "Web request failed: " + e.getMessage(), e, "");
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException ignore) {}
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

	ArrayList<Shard> ExtractShards(JSONArray array, String requestId) throws LogException {
		ArrayList<Shard> shards = new ArrayList<Shard>();
		if (array == null) {
			return shards;
		}
		try {
			for (int i = 0; i < array.size(); i++) {
				JSONObject shardDict = array.getJSONObject(i);
				if (shardDict == null) {
					continue;
				}
				int shardId = shardDict.getIntValue("shardID");
				String status = shardDict.getString("status");
				String begin = shardDict.getString("inclusiveBeginKey");
				String end = shardDict.getString("exclusiveEndKey");
				int createTime = shardDict.getIntValue("createTime");
				Shard shard = new Shard(shardId, status, begin, end, createTime);
				if (shardDict.containsKey("serverIp")) {
					shard.setServerIp(shardDict.getString("serverIp"));
				}
				shards.add(shard);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid shard json array string : "
							+ array.toString() + e.getMessage(), e, requestId);
		}
		return shards;
	}

	@Override
    public UpdateLogStoreInternalResponse UpdateLogStoreInternal(String project, InternalLogStore internalLogStore) throws LogException {
        CodingUtils.assertStringNotNullOrEmpty(project, "project");
        CodingUtils.assertParameterNotNull(internalLogStore, "InternallogStore");
        Map<String, String> headParameter = GetCommonHeadPara(project);
        byte[] body = encodeToUtf8(internalLogStore.ToRequestString());
        headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + internalLogStore.GetLogStoreName();
        Map<String, String> urlParameter = Collections.singletonMap("type", "inner");
        ResponseMessage response = SendData(project, HttpMethod.PUT,
                resourceUri, urlParameter, headParameter, body);
        Map<String, String> resHeaders = response.getHeaders();
        return new UpdateLogStoreInternalResponse(resHeaders);
    }

	@Override
	public CreateLogStoreInternalResponse CreateLogStoreInternal(String project, InternalLogStore internalLogStore) throws LogException {
        CodingUtils.assertStringNotNullOrEmpty(project, "project");
        CodingUtils.assertParameterNotNull(internalLogStore, "InternallogStore");
        Map<String, String> headParameter = GetCommonHeadPara(project);
        byte[] body = encodeToUtf8(internalLogStore.ToRequestString());
        headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores";
        Map<String, String> urlParameter = Collections.singletonMap("type", "inner");
        ResponseMessage response = SendData(project, HttpMethod.POST,
                resourceUri, urlParameter, headParameter, body);
        Map<String, String> resHeaders = response.getHeaders();
        return new CreateLogStoreInternalResponse(resHeaders);
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
		byte[] body = encodeToUtf8(logStore.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/logstores";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateLogStoreResponse(resHeaders);
	}

	@Override
	public CreateLinkStoreResponse CreateLinkStore(String project, LinkStore linkStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(linkStore, "logStore");
		return CreateLinkStore(new CreateLinkStoreRequest(project, linkStore));
	}

	@Override
	public CreateLinkStoreResponse CreateLinkStore(CreateLinkStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		LinkStore linkStore = request.getLinkStore();
		CodingUtils.assertParameterNotNull(linkStore, "linkStore");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(linkStore.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/logstores";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateLinkStoreResponse(resHeaders);
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
		CodingUtils.validateLogstore(logStoreName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStoreName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteLogStoreResponse(resHeaders);
	}

	@Override
	public DeleteLinkStoreResponse DeleteLinkStore(String project, String linkStoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(linkStoreName, "linkStoreName");
		return DeleteLinkStore(new DeleteLinkStoreRequest(project, linkStoreName));
	}

	@Override
	public DeleteLinkStoreResponse DeleteLinkStore(DeleteLinkStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String linkStoreName = request.getLinkStoreName();
		CodingUtils.validateLogstore(linkStoreName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/logstores/" + linkStoreName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteLinkStoreResponse(resHeaders);
	}

	@Override
	public ClearLogStoreStorageResponse ClearLogStoreStorage(String project,
			String logStoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");
		return ClearLogStoreStorage(new ClearLogStoreStorageRequest(project, logStoreName));
	}

	@Override
	public CreateProjectConsumerGroupResponse CreateProjectConsumerGroup(CreateProjectConsumerGroupRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		ProjectConsumerGroup projectConsumerGroup = request.getConsumerGroup();
		CodingUtils.assertParameterNotNull(projectConsumerGroup, "consumerGroup");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(projectConsumerGroup.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/consumergroups";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateProjectConsumerGroupResponse(resHeaders);
	}

	@Override
	public CreateProjectConsumerGroupResponse CreateProjectConsumerGroup(String project, ProjectConsumerGroup consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		return CreateProjectConsumerGroup(new CreateProjectConsumerGroupRequest(
				project, consumerGroup));
	}

	@Override
	public DeleteProjectConsumerGroupResponse DeleteProjectConsumerGroup(String project, String consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/consumergroups/" + consumerGroup;
		headParameter.put(Consts.CONST_CONTENT_TYPE, String.valueOf(0));
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteProjectConsumerGroupResponse(resHeaders);
	}

	@Override
	public ListProjectConsumerGroupResponse ListProjectConsumerGroup(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String resourceUri = "/consumergroups";
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		ArrayList<ProjectConsumerGroup> consumerGroups = new ArrayList<ProjectConsumerGroup>();
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ExtractProjectConsumerGroup(array, requestId, consumerGroups);
		ListProjectConsumerGroupResponse listProjectConsumerGroupResponse = new ListProjectConsumerGroupResponse(resHeaders);
		listProjectConsumerGroupResponse.setConsumerGroups(consumerGroups);
		return listProjectConsumerGroupResponse;
	}

	@Override
	public UpdateProjectConsumerGroupResponse UpdateProjectConsumerGroup(String project,
																		 String consumerGroup,
																		 boolean inOrder,
																		 int timeoutInSec) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		final JSONObject asJson = new JSONObject();
		asJson.put("order", inOrder);
		asJson.put("timeout", timeoutInSec);
		byte[] body = encodeToUtf8(asJson.toString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/consumergroups/" + consumerGroup;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateProjectConsumerGroupResponse(resHeaders);
	}

	@Override
	public ProjectConsumerGroupUpdateCheckPointResponse UpdateProjectConsumerGroupCheckPoint(String project, String consumerGroup, String consumer, String logStore, int shard, String checkpoint) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		CodingUtils.assertStringNotNullOrEmpty(consumer, "consumer");
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(checkpoint, "checkpoint");
		return UpdateProjectConsumerGroupCheckPoint(project, consumerGroup, consumer, logStore, shard, checkpoint, false);
	}

	@Override
	public ProjectConsumerGroupUpdateCheckPointResponse UpdateProjectConsumerGroupCheckPoint(String project, String consumerGroup, String logStore, int shard, String checkpoint) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		CodingUtils.assertStringNotNullOrEmpty(checkpoint, "checkpoint");
		return UpdateProjectConsumerGroupCheckPoint(project, consumerGroup, "", logStore, shard, checkpoint, true);
	}

	private ProjectConsumerGroupUpdateCheckPointResponse UpdateProjectConsumerGroupCheckPoint(
			String project, String consumerGroup, String consumer, String logStore, int shard, String checkpoint,
			boolean forceSuccess) throws LogException {
		String resourceUri = "/consumergroups/" + consumerGroup;
		ProjectConsumerGroupUpdateCheckPointRequest request = new ProjectConsumerGroupUpdateCheckPointRequest(
				project, consumerGroup, consumer, logStore, shard, checkpoint, forceSuccess);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = request.GetAllParams();
		byte[] body = encodeToUtf8(request.GetRequestBody());
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new ProjectConsumerGroupUpdateCheckPointResponse(resHeaders);
	}

	@Override
	public ProjectConsumerGroupCheckPointResponse GetProjectConsumerGroupCheckPoint(String project, String consumerGroup, String logStore, int shard) throws LogException {
		ProjectConsumerGroupGetCheckPointRequest request = new ProjectConsumerGroupGetCheckPointRequest(
				project, consumerGroup, logStore, shard);
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/consumergroups/" + consumerGroup;
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		return new ProjectConsumerGroupCheckPointResponse(resHeaders, object);
	}

	@Override
	public ProjectConsumerGroupCheckPointResponse GetProjectConsumerGroupCheckPoint(String project, String consumerGroup, String logStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		CodingUtils.assertStringNotNullOrEmpty(logStore, "logStore");
		return GetProjectConsumerGroupCheckPoint(project, consumerGroup, logStore, -1);
	}

	@Override
	public ProjectConsumerGroupCheckPointResponse GetProjectConsumerGroupCheckPoint(String project, String consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		return GetProjectConsumerGroupCheckPoint(project, consumerGroup, "", -1);
	}

	@Override
	public ProjectConsumerGroupHeartBeatResponse ProjectConsumerGroupHeartBeat(String project, String consumerGroup, String consumer, Map<String, ArrayList<Integer>> logStoreShards) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateConsumerGroup(consumerGroup);
		CodingUtils.assertStringNotNullOrEmpty(consumer, "consumer");
		String resourceUri = "/consumergroups/" + consumerGroup;
		ProjectConsumerGroupHeartBeatRequest request = new ProjectConsumerGroupHeartBeatRequest(
				project, consumer, logStoreShards == null ? new HashMap<String, ArrayList<Integer>>() : logStoreShards);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = request.GetAllParams();
		byte[] body = encodeToUtf8(request.GetRequestBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		return new ProjectConsumerGroupHeartBeatResponse(resHeaders, object);
	}

	@Override
	public ClearLogStoreStorageResponse ClearLogStoreStorage(ClearLogStoreStorageRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.GetLogStoreName();
		CodingUtils.validateLogstore(logStoreName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStoreName + "/storage";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new ClearLogStoreStorageResponse(resHeaders);
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
		CodingUtils.validateLogstore(logStoreName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(logStore.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStoreName;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateLogStoreResponse(resHeaders);
	}

	@Override
	public GetLogStoreResponse GetLogStore(String project, String logStoreName)
			throws LogException {
		CodingUtils.validateProject(project);
		CodingUtils.validateLogstore(logStoreName);
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
	public GetLogStoreResponse  GetLogStore(GetLogStoreRequest request)
			throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.GetLogStore();
		CodingUtils.validateLogstore(logStoreName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStoreName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		LogStore logStore = ExtractLogStoreFromResponse(object, requestId);
		return new GetLogStoreResponse(resHeaders, logStore);
	}

	@Override
	public CreateLogStoreResponse createMetricStore(String project,
													LogStore metricStore) throws LogException {
		metricStore.setTelemetryType("Metrics");
		CreateLogStoreResponse createLogStoreResponse = CreateLogStore(project, metricStore);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new LogException("Sleep Interrupted", e.getMessage(), "");
		}
		List<SubStoreKey> list = new ArrayList<SubStoreKey>();
		list.add(new SubStoreKey("__name__", "text"));
		list.add(new SubStoreKey("__labels__", "text"));
		list.add(new SubStoreKey("__time_nano__", "long"));
		list.add(new SubStoreKey("__value__", "double"));
		SubStore subStore = new SubStore("prom", metricStore.GetTtl(), 2, 2, list);
		createSubStore(project, metricStore.GetLogStoreName(), subStore);
		return createLogStoreResponse;
	}

	@Override
	public CreateLogStoreResponse createMetricStore(CreateLogStoreRequest request)
			throws LogException {
		return createMetricStore(request.GetProject(), request.GetLogStore());
	}

	@Override
	public UpdateLogStoreResponse updateMetricStore(String project,
													LogStore metricStore) throws LogException {
		metricStore.setTelemetryType("Metrics");
		UpdateLogStoreResponse updateLogStoreResponse = UpdateLogStore(project, metricStore);
		updateSubStoreTTL(project, metricStore.GetLogStoreName(), metricStore.GetTtl());
		return updateLogStoreResponse;
	}

	@Override
	public UpdateLogStoreResponse updateMetricStore(UpdateLogStoreRequest request)
			throws LogException {
		return updateMetricStore(request.GetProject(), request.GetLogStore());
	}

	@Override
	public DeleteLogStoreResponse deleteMetricStore(String project,
													String metricStoreName) throws LogException {
		return DeleteLogStore(project, metricStoreName);
	}

	@Override
	public DeleteLogStoreResponse deleteMetricStore(DeleteLogStoreRequest request)
			throws LogException {
		return deleteMetricStore(request.GetProject(), request.GetLogStoreName());
	}

	@Override
	public GetLogStoreResponse getMetricStore(String project, String metricStoreName)
			throws LogException {
		return GetLogStore(project, metricStoreName);
	}

	@Override
	public GetLogStoreResponse getMetricStore(GetLogStoreRequest request) throws LogException {
		return getMetricStore(request.GetProject(), request.GetLogStore());
	}

	@Override
	public ListSubStoreResponse listSubStore(String project, String logstore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logstore, "logstore");
		return listSubStore(new ListSubStoreRequest(project, logstore));
	}

	@Override
	public ListSubStoreResponse listSubStore(ListSubStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> urlParameter = request.GetAllParams();
		CodingUtils.validateLogstore(request.getLogstoreName());
		String resourceUri = "/logstores/" + request.getLogstoreName() + "/substores";
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		ListSubStoreResponse listSubStoreResponse = new ListSubStoreResponse(resHeaders);
		listSubStoreResponse.setSubStoreNames(ExtractJsonArray(
				Consts.CONST_RESULT_SUB_STORES, object));
		return listSubStoreResponse;
	}

	@Override
	public GetSubStoreResponse getSubStore(String project, String logstore, String subStoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logstore, "logstore");
		CodingUtils.assertStringNotNullOrEmpty(subStoreName, "subStoreName");
		return getSubStore(new GetSubStoreRequest(project, logstore, subStoreName));
	}

	@Override
	public GetSubStoreResponse getSubStore(GetSubStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String subStoreName = request.getSubStoreName();
		CodingUtils.assertStringNotNullOrEmpty(project, "subStoreName");
		Map<String, String> urlParameter = request.GetAllParams();
		CodingUtils.validateLogstore(request.getLogstoreName());
		String resourceUri = "/logstores/" + request.getLogstoreName() + "/substores/" + subStoreName;
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);

		SubStore subStore = new SubStore();
		subStore.fromJsonString(object.toString());

		return new GetSubStoreResponse(resHeaders, subStore);
	}

	@Override
	public CreateSubStoreResponse createSubStore(String project, String logStoreName, SubStore subStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStoreName, "logStoreName");
		CodingUtils.assertParameterNotNull(subStore, "subStore");
		return createSubStore(new CreateSubStoreRequest(project, logStoreName, subStore));
	}

	@Override
	public CreateSubStoreResponse createSubStore(CreateSubStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.getLogStoreName();
		CodingUtils.validateLogstore(logStoreName);
		SubStore subStore = request.getSubStore();
		CodingUtils.assertParameterNotNull(subStore, "subStore");
		if (!subStore.isValid()) {
			throw new IllegalArgumentException("SubStore is invalid");
		}
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(subStore.toRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/logstores/" + logStoreName + "/substores";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateSubStoreResponse(resHeaders);
	}

	@Override
	public UpdateSubStoreResponse updateSubStore(String project, String logStoreName, SubStore subStore) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(logStoreName, "logStoreName");
		CodingUtils.assertParameterNotNull(subStore, "subStore");
		return updateSubStore(new UpdateSubStoreRequest(project, logStoreName, subStore));
	}

	@Override
	public UpdateSubStoreResponse updateSubStore(UpdateSubStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.getLogStoreName();
		CodingUtils.validateLogstore(logStoreName);
		SubStore subStore = request.getSubStore();
		CodingUtils.assertParameterNotNull(subStore, "subStore");
		if (!subStore.isValid()) {
			throw new IllegalArgumentException("SubStore is invalid");
		}
		String subStoreName = subStore.getName();
		CodingUtils.assertStringNotNullOrEmpty(subStoreName, "subStoreName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(subStore.toRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/logstores/" + logStoreName + "/substores/" + subStoreName;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateSubStoreResponse(resHeaders);
	}

	@Override
	public DeleteSubStoreResponse deleteSubStore(String project, String logStoreName, String subStoreName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(logStoreName, "logStoreName");
		CodingUtils.assertStringNotNullOrEmpty(subStoreName, "subStoreName");
		return deleteSubStore(new DeleteSubStoreRequest(project, logStoreName, subStoreName));
	}

	@Override
	public DeleteSubStoreResponse deleteSubStore(DeleteSubStoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStoreName = request.getLogStoreName();
		CodingUtils.validateLogstore(logStoreName);
		String subStoreName = request.getSubStoreName();
		CodingUtils.assertStringNotNullOrEmpty(subStoreName, "subStoreName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/logstores/" + logStoreName + "/substores/" + subStoreName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteSubStoreResponse(resHeaders);
	}

	@Override
	public GetSubStoreTTLResponse getSubStoreTTL(String project, String logstoreName) throws LogException {
		return getSubStoreTTL(new GetSubStoreTTLResquest(project, logstoreName));
	}

	@Override
	public GetSubStoreTTLResponse getSubStoreTTL(GetSubStoreTTLResquest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> urlParameter = request.GetAllParams();
		String logstoreName = request.getLogstoreName();
		CodingUtils.validateLogstore(logstoreName);
		String resourceUri = "/logstores/" + logstoreName + "/substores/storage/ttl";
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		GetSubStoreTTLResponse getSubStoreTTLResponse = new GetSubStoreTTLResponse(resHeaders);
		int ttl = object.getIntValue(Consts.CONST_TTL);
		getSubStoreTTLResponse.setTtl(ttl);
		return getSubStoreTTLResponse;
	}

	@Override
	public UpdateSubStoreTTLResponse updateSubStoreTTL(String project, String logstore, int ttl) throws LogException {
		return updateSubStoreTTL(new UpdateSubStoreTTLRequest(project, logstore, ttl));
	}

	@Override
	public UpdateSubStoreTTLResponse updateSubStoreTTL(UpdateSubStoreTTLRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> urlParameter = request.GetAllParams();
		String logstoreName = request.getLogstoreName();
		CodingUtils.validateLogstore(logstoreName);
		String resourceUri = "/logstores/" + logstoreName + "/substores/storage/ttl";
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		UpdateSubStoreTTLResponse updateSubStoreTTLResponse = new UpdateSubStoreTTLResponse(resHeaders);
		return updateSubStoreTTLResponse;
	}

	@Override
	public ListLogStoresResponse listLogStores(String project, int offset, int size,String logstoreName, String telemetryType) throws LogException {
		return ListLogStores(new ListLogStoresRequest(project, offset, size, logstoreName, telemetryType));
	}

	public CreateExternalStoreResponse createExternalStore(CreateExternalStoreRequest request) throws LogException {
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		ExternalStore externalStore = request.getExternalStore();
		CodingUtils.assertParameterNotNull(externalStore, "ExternalStore");
		String externalStoreName = externalStore.getExternalStoreName();
		CodingUtils.assertStringNotNullOrEmpty(externalStoreName, "externalStoreName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(externalStore.toJson().toJSONString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/externalstores";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateExternalStoreResponse(resHeaders);
	}

	public DeleteExternalStoreResponse deleteExternalStore(DeleteExternalStoreRequest request) throws LogException {
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String externalStoreName = request.getExternalStoreName();
		CodingUtils.assertStringNotNullOrEmpty(externalStoreName, "externalStoreName");
		Map<String, String> urlParameter = request.GetAllParams();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/externalstores/" + externalStoreName;
		ResponseMessage response = SendData(project, HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteExternalStoreResponse(resHeaders);
	}

	public UpdateExternalStoreResponse updateExternalStore(UpdateExternalStoreRequest request) throws LogException {
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		ExternalStore externalStore = request.getExternalStore();
		CodingUtils.assertParameterNotNull(externalStore, "ExternalStore");
		String externalStoreName = externalStore.getExternalStoreName();
		CodingUtils.assertParameterNotNull(externalStoreName, "externalStoreName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(externalStore.toJson().toJSONString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = "/externalstores/" + externalStoreName;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new UpdateExternalStoreResponse(resHeaders);
	}

	public GetExternalStoreResponse getExternalStore(GetExternalStoreRequest request) throws LogException {
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String externalStoreName = request.getExternalStoreName();
		CodingUtils.assertStringNotNullOrEmpty(externalStoreName, "externalStoreName");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/externalstores/" + externalStoreName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		ExternalStore externalStore = new ExternalStore(object);
		externalStore.setExternalStoreName(externalStoreName);
		return new GetExternalStoreResponse(resHeaders, externalStore);
	}

	public ListExternalStroesResponse listExternalStores(ListExternalStoresRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> urlParameter = request.GetAllParams();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/externalstores";
		ResponseMessage response = SendData(project, HttpMethod.GET, resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		ListExternalStroesResponse listExternalStroesResponse = new ListExternalStroesResponse(resHeaders);
		listExternalStroesResponse.setExternalStores(ExtractJsonArray(Consts.CONST_RESULT_EXTERNAL_STORES, object));
		listExternalStroesResponse.setTotal(object.getIntValue(Consts.CONST_TOTAL));
		listExternalStroesResponse.setCount(object.getIntValue(Consts.CONST_COUNT));
		return listExternalStroesResponse;
	}

	@Override
	public CreateIndexResponse CreateIndex(String project, String logStore,
										   String indexJsonString) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.assertParameterNotNull(indexJsonString, "indexJsonString");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(indexJsonString);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStore + "/index";
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
		CodingUtils.validateLogstore(logStore);
		Index index = request.GetIndex();
		CodingUtils.assertParameterNotNull(index, "index");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(index.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStore + "/index";
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
		CodingUtils.validateLogstore(logStore);
		CodingUtils.assertParameterNotNull(indexJsonString, "indexJsonString");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(indexJsonString);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStore + "/index";
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
		CodingUtils.validateLogstore(logStore);
		Index index = request.GetIndex();
		CodingUtils.assertParameterNotNull(index, "index");

		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(index.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStore + "/index";
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
		CodingUtils.validateLogstore(logStore);

		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/index";
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

	private Index ExtractIndexFromResponseWithFastJson(JSONObject dict, String requestId)
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
	public GetIndexResponse GetIndex(GetIndexRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/index";
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		Index index = ExtractIndexFromResponseWithFastJson(object, requestId);
		return new GetIndexResponse(resHeaders, index);
	}

    @Override
    public GetIndexStringResponse GetIndexString(GetIndexRequest request)
                    throws LogException {
	    CodingUtils.assertParameterNotNull(request, "request");
	    String project = request.GetProject();
	    CodingUtils.assertStringNotNullOrEmpty(project, "project");
	    String logStore = request.GetLogStore();
		CodingUtils.validateLogstore(logStore);

	    Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/index";
        Map<String, String> urlParameter = request.GetAllParams();
        ResponseMessage response = SendData(project, HttpMethod.GET,
                resourceUri, urlParameter, headParameter);
        Map<String, String> resHeaders = response.getHeaders();
        return new GetIndexStringResponse(resHeaders, response.GetStringBody());
    }

	@Override
	public CreateShipperResponse CreateShipper(String project, String logStore,
			String shipperName, ShipperConfig shipConfig) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.assertParameterNotNull(shipperName, "shipperName");
		CodingUtils.assertParameterNotNull(shipConfig, "shipConfig");

		Map<String, String> headParameter = GetCommonHeadPara(project);
        headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/logstores/" + logStore + "/shipper";
		JSONObject jsonBody = new JSONObject();
        jsonBody.put("shipperName", shipperName);
        jsonBody.put("targetType", shipConfig.GetShipperType());
        jsonBody.put("targetConfiguration", shipConfig.GetJsonObj());
		byte[] body = encodeToUtf8(jsonBody.toString());
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
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateShipper(shipperName);
		CodingUtils.assertParameterNotNull(shipConfig, "shipConfig");
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shipper/" + shipperName;
		JSONObject jsonBody = new JSONObject();
        jsonBody.put("shipperName", shipperName);
        jsonBody.put("targetType", shipConfig.GetShipperType());
        jsonBody.put("targetConfiguration", shipConfig.GetJsonObj());

		byte[] body = encodeToUtf8(jsonBody.toString());
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
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateShipper(shipperName);

		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shipper/" + shipperName;
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
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateShipper(shipperName);

		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shipper/" + shipperName;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);

		ShipperConfig config;
		if (object.containsKey("targetType")
				&& object.getString("targetType").equals("odps")) {
			config = new OdpsShipperConfig();
			config.FromJsonObj(object.getJSONObject("targetConfiguration"));
		} else if (object.containsKey("targetType")
				&& object.getString("targetType").equals("oss")) {
			config = new OssShipperConfig();
			config.FromJsonObj(object.getJSONObject("targetConfiguration"));
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
		CodingUtils.validateLogstore(logStore);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shipper";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		return new ListShipperResponse(resHeaders, object.getIntValue("count"),
                object.getIntValue("total"), ExtractJsonArray("shipper", object));
	}

	@Override
	public GetShipperTasksResponse GetShipperTasks(String project,
			String logStore, String shipperName, int startTime, int endTime,
			String statusType, int offset, int size) throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateShipper(shipperName);
		CodingUtils.assertParameterNotNull(statusType, "statusType");
		Map<String, String> headParameter = GetCommonHeadPara(project);

        String resourceUri = "/logstores/" + logStore + "/shipper/" + shipperName + "/tasks";
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
		JSONObject object = parseResponseBody(response, requestId);
		return new GetShipperTasksResponse(resHeaders,
				object.getIntValue("count"),
				object.getIntValue("total"),
				ExtractTasksStatisTic(object), ExtractShipperTask(object));
	}

	@Override
	public RetryShipperTasksResponse RetryShipperTasks(String project,
			String logStore, String shipperName, List<String> taskList)
			throws LogException {
		CodingUtils.assertParameterNotNull(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateShipper(shipperName);
		CodingUtils.assertParameterNotNull(taskList, "taskList");

		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/shipper/" + shipperName + "/tasks";
		JSONArray array = new JSONArray();
        array.addAll(taskList);
		byte[] body = encodeToUtf8(array.toString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new RetryShipperTasksResponse(resHeaders);
	}

	private ShipperTasksStatistic ExtractTasksStatisTic(JSONObject obj) {
		JSONObject statistic_obj = obj.getJSONObject("statistics");
		return new ShipperTasksStatistic(statistic_obj.getIntValue("running"),
				statistic_obj.getIntValue("success"), statistic_obj.getIntValue("fail"));
	}

	private List<ShipperTask> ExtractShipperTask(JSONObject object) {
		List<ShipperTask> res = new ArrayList<ShipperTask>();
		JSONArray array = object.getJSONArray("tasks");
		if (array == null) {
			return res;
		}
		for (int i = 0; i < array.size(); i++) {
			JSONObject item = array.getJSONObject(i);
			if (item == null) {
				continue;
			}
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
		byte[] body = encodeToUtf8(consumerGroup.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		CodingUtils.validateLogstore(request.GetLogStore());
		String resourceUri = "/logstores/" + request.GetLogStore() + "/consumergroups";
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
		CodingUtils.validateLogstore(logStore);
		CodingUtils.assertStringNotNullOrEmpty(consumerGroup, "consumerGroup");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		final JSONObject asJson = new JSONObject();
        if (inOrder != null) {
            asJson.put("order", inOrder);
        }
        if (timeoutInSec != null) {
            asJson.put("timeout", timeoutInSec);
        }
		byte[] body = encodeToUtf8(asJson.toString());
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
		return UpdateConsumerGroup(project, logStore, consumerGroup, inOrder, null);
	}

	public UpdateConsumerGroupResponse UpdateConsumerGroup(String project,
			String logStore, String consumerGroup, int timeoutInSec)
			throws LogException {
		return UpdateConsumerGroup(project, logStore, consumerGroup, null, timeoutInSec);
	}

	@Override
	public DeleteConsumerGroupResponse DeleteConsumerGroup(String project,
			String logStore, String consumerGroup) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateConsumerGroup(consumerGroup);
		Map<String, String> headParameter = GetCommonHeadPara(project);
        String resourceUri = "/logstores/" + logStore + "/consumergroups/" + consumerGroup;
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
		CodingUtils.validateLogstore(logStore);
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
				if (consumerGroup == null) {
					continue;
				}
				consumerGroups.add(new ConsumerGroup(consumerGroup.getString("name"),
                        consumerGroup.getIntValue("timeout"),
						consumerGroup.getBoolean("order")));
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid consumer group json array string : "
							+ array.toString(), e, requestId);
		}
	}

	public ConsumerGroupUpdateCheckPointResponse UpdateCheckPoint(
			String project, String logStore, String consumerGroup,
			String consumer, int shard, String checkpoint) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logStore);
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
		CodingUtils.validateLogstore(logStore);
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
		byte[] body = encodeToUtf8(request.GetRequestBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new ConsumerGroupUpdateCheckPointResponse(resHeaders);
	}

	@Override
	public ConsumerGroupHeartBeatResponse HeartBeat(String project,
													String logStore, String consumerGroup, String consumer,
													List<Integer> shards) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateConsumerGroup(consumerGroup);
		CodingUtils.assertStringNotNullOrEmpty(consumer, "consumer");
		String resourceUri = "/logstores/" + logStore + "/consumergroups/"
				+ consumerGroup;
		ConsumerGroupHeartBeatRequest request = new ConsumerGroupHeartBeatRequest(
				project, logStore, consumerGroup, consumer,
				shards == null ? new ArrayList<Integer>() : shards);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = request.GetAllParams();

		byte[] body = encodeToUtf8(request.GetRequestBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		List<Integer> responseShards = new ArrayList<Integer>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, body);

		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
		ExtractShards(array, requestId, responseShards);
		return new ConsumerGroupHeartBeatResponse(resHeaders, responseShards);
	}

	protected void ExtractShards(JSONArray array, String requestId, List<Integer> shards) throws LogException {
		if (array == null) {
			return;
		}
		try {
			for (int i = 0; i < array.size(); i++) {
				shards.add(array.getIntValue(i));
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid shard json array string : "
							+ array.toString(), e, requestId);
		}
	}

	public GetCheckPointResponse getCheckpoint(String project,
											   String logstore,
											   String consumerGroup,
											   int shard)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logstore);
		CodingUtils.validateConsumerGroup(consumerGroup);
		Args.check(shard >= 0, "Shard must be >= 0");
		ConsumerGroupGetCheckPointRequest request = new ConsumerGroupGetCheckPointRequest(
				project, logstore, consumerGroup, shard);
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/logstores/" + logstore + "/consumergroups/" + consumerGroup;
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
		return new GetCheckPointResponse(resHeaders, array);
	}

	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup) throws LogException {
		return GetCheckPoint(project, logStore, consumerGroup, -1);
	}

	@Deprecated
	public ConsumerGroupCheckPointResponse GetCheckPoint(String project,
			String logStore, String consumerGroup, int shard)
			throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.validateLogstore(logStore);
		CodingUtils.validateConsumerGroup(consumerGroup);
		ConsumerGroupGetCheckPointRequest request = new ConsumerGroupGetCheckPointRequest(
				project, logStore, consumerGroup, shard);
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = "/logstores/" + logStore + "/consumergroups/" + consumerGroup;
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
        return new ConsumerGroupCheckPointResponse(resHeaders, array);
	}

	@Override
	public CreateProjectResponse CreateProject(String project,
			String projectDescription) throws LogException {
		return createProject(project, projectDescription, null);
	}

	@Override
	public CreateProjectResponse createProject(String project,
			String projectDescription, String resourceGroupId) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		if (projectDescription == null) {
			projectDescription = "";
		}
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = "/";
		JSONObject jsonBody = new JSONObject();
        jsonBody.put("projectName", project);
        jsonBody.put("description", projectDescription);
		if (resourceGroupId != null) {
			jsonBody.put("resourceGroupId", resourceGroupId);
		}
		byte[] body = encodeToUtf8(jsonBody.toString());
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
		GetProjectResponse getProjectResponse = new GetProjectResponse(resHeaders);
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
		getProjectResponse.FromJsonObject(object);
		return getProjectResponse;
	}

	@Override
	public DeleteProjectResponse DeleteProject(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		String resourceUri = "/";
		Map<String, String> urlParameter = new HashMap<String, String>();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteProjectResponse(resHeaders);
	}

    @Override
    public UpdateProjectResponse updateProject(UpdateProjectRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        final String resourceUri = "/";
        final String project = request.GetProject();
        Map<String, String> headers = GetCommonHeadPara(project);
        ResponseMessage response = SendData(project, HttpMethod.PUT, resourceUri,
                Collections.<String, String>emptyMap(), headers, JsonUtils.serialize(request.getBody()));
        return new UpdateProjectResponse(response.getHeaders());
    }

	@Override
	public ChangeResourceGroupResponse changeResourceGroup(String resourceType, String resourceId, String resourceGroupId) throws  LogException {
		CodingUtils.assertStringNotNullOrEmpty(resourceType, "resourceType");
		CodingUtils.assertStringNotNullOrEmpty(resourceId, "resourceId");
		CodingUtils.assertStringNotNullOrEmpty(resourceGroupId, "resourceGroupId");
		Map<String, String> headParameter = GetCommonHeadPara("");
		Map<String, String> urlParameter = new HashMap<String, String>();
		String resourceUri = "/resourcegroup";
		JSONObject jsonBody = new JSONObject();
		jsonBody.put("resourceType", resourceType);
		jsonBody.put("resourceId", resourceId);
		jsonBody.put("resourceGroupId", resourceGroupId);
		byte[] body = encodeToUtf8(jsonBody.toString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(resourceId, HttpMethod.PUT, resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new ChangeResourceGroupResponse(resHeaders);
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
		byte[] body = encodeToUtf8(machineList.ToRequestString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
        String resourceUri = "/machinegroups/" + groupName + "/machines";
		Map<String, String> urlParameter = new HashMap<String, String>();
        // delete machine from machine group
        urlParameter.put(Consts.ACTION, isDelete ? "delete" : "add");
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
			object = parseResponseBody(response, requestId);
			listProjectResponse = new ListProjectResponse(resHeaders);
			listProjectResponse.fromJSON(object);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid list project json string : "
							+ Utils.safeToString(object), e,
					GetRequestId(response.getHeaders()));
		}
		return listProjectResponse;
	}

	// saved search api
	private void checkSavedSearchResource(SavedSearch savedSearch) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(savedSearch.getSavedSearchName(), "savedsearchName");
		CodingUtils.assertStringNotNullOrEmpty(savedSearch.getLogstore(), "logstore");
	}

	@Override
	public CreateChartResponse createChart(CreateChartRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		CodingUtils.validateDashboardName(request.getDashboardName());
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
		CodingUtils.validateDashboardName(request.getDashboardName());
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
		CodingUtils.validateDashboardName(request.getDashboardName());
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
		CodingUtils.validateDashboardName(request.getDashboardName());
		String resourceUri = "/dashboards/" + request.getDashboardName() + "/charts/" + request.getChartName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		Chart chart = ExtractChartFromResponse(object, requestId);
        return new GetChartResponse(response.getHeaders(), chart);
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
		CodingUtils.validateDashboardName(request.getDashboard().getDashboardName());
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
		CodingUtils.validateDashboardName(request.getDashboardName());
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
		CodingUtils.validateDashboardName(request.getDashboardName());
		String resourceUri = "/dashboards/" + request.getDashboardName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		Dashboard dashboard = ExtractDashboardFromResponse(object, requestId);
        return new GetDashboardResponse(response.getHeaders(), dashboard);
	}

	protected List<Dashboard> ExtractDashboards(JSONObject object, String requestId)
			throws LogException {
		List<Dashboard> dashboards = new ArrayList<Dashboard>();
		if (object == null) {
			return dashboards;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("dashboardItems");
			if (array == null) {
				return dashboards;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				Dashboard dashboard = new Dashboard();
				dashboard.setDashboardName(jsonObject.getString("dashboardName"));
				dashboard.setDisplayName(jsonObject.getString("displayName"));
				dashboards.add(dashboard);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
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
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
        List<Dashboard> dashboards = ExtractDashboards(object, requestId);
        return new ListDashboardResponse(response.getHeaders(), count, total, dashboards);
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
		CodingUtils.validateSavedSearch(request.getSavedSearchName());
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
		CodingUtils.validateSavedSearch(request.getSavedSearchName());
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_SAVEDSEARCH_URI + "/" + request.getSavedSearchName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		SavedSearch savedSearch = ExtractSavedSearchFromResponse(object, requestId);
        return new GetSavedSearchResponse(response.getHeaders(), savedSearch);
	}

	protected List<SavedSearch> ExtractSavedSearches(JSONObject object, String requestId)
			throws LogException {
		List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();
		if (object == null) {
			return savedSearches;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("savedsearchItems");
			if (array == null) {
				return savedSearches;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				SavedSearch savedSearch = new SavedSearch();
				savedSearch.setSavedSearchName(jsonObject.getString("savedsearchName"));
				savedSearch.setDisplayName(jsonObject.getString("displayName"));
				savedSearches.add(savedSearch);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
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
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
        List<SavedSearch> savedSearches = ExtractSavedSearches(object, requestId);
        return new ListSavedSearchResponse(response.getHeaders(), count, total, savedSearches);
	}

	@Override
	public CreateDomainResponse createDomain(String project, Domain domain) throws LogException {
		CreateDomainRequest request = new CreateDomainRequest(project, domain);
		return createDomain(request);
	}

	@Override
	public CreateDomainResponse createDomain(CreateDomainRequest request) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getDomain().getDomainName(), "domainName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_DOMAIN_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST, resourceUri, urlParameter, headParameter, request.getDomain().toJsonString());
		return new CreateDomainResponse(response.getHeaders());
	}

	@Override
	public DeleteDomainResponse deleteDomain(String project, String domainName) throws LogException {
		DeleteDomainRequest request = new DeleteDomainRequest(project, domainName);
		return deleteDomain(request);
	}

	@Override
	public DeleteDomainResponse deleteDomain(DeleteDomainRequest request) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		CodingUtils.assertStringNotNullOrEmpty(request.getDomainName(), "domainName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_DOMAIN_URI + "/" + request.getDomainName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, urlParameter, headParameter);
		return new DeleteDomainResponse(response.getHeaders());
	}

	@Override
	public ListDomainsResponse listDomains(String project, String domainName, int offset, int size) throws LogException {
		ListDomainsRequest request = new ListDomainsRequest(project, domainName, offset, size);
		return listDomains(request);
	}

	protected List<Domain> ExtractDomains(JSONObject object, String requestId)
			throws LogException {
		List<Domain> domains = new ArrayList<Domain>();
		if (object == null) {
			return domains;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("domains");
			if (array == null) {
				return domains;
			}
			for (int index = 0; index < array.size(); index++) {
				Domain domain = new Domain();
				domain.setDomainName(array.getString(index));
				domains.add(domain);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}

		return domains;
	}

	@Override
	public ListDomainsResponse listDomains(ListDomainsRequest request) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(request.GetProject(), "projectName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_DOMAIN_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
        List<Domain> domains = ExtractDomains(object, requestId);
        return new ListDomainsResponse(response.getHeaders(), count, total, domains);
	}

	@Override
	public CreateIngestionResponse createIngestion(CreateIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new CreateIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public UpdateIngestionResponse updateIngestion(UpdateIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new UpdateIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public DeleteIngestionResponse deleteIngestion(DeleteIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new DeleteIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public GetIngestionResponse getIngestion(GetIngestionRequest request) throws LogException {
		ResponseMessage response = send(request);
		JSONObject responseBody = parseResponseBody(response, response.getRequestId());
		GetIngestionResponse ingestionResponse = new GetIngestionResponse(response.getHeaders());
		ingestionResponse.deserialize(responseBody, response.getRequestId());
		return ingestionResponse;
	}

	@Override
	public ListIngestionResponse listIngestion(ListIngestionRequest request) throws LogException {
		ResponseMessage response = send(request);
		JSONObject responseBody = parseResponseBody(response, response.getRequestId());
		ListIngestionResponse listIngestionResponse = new ListIngestionResponse(response.getHeaders());
		listIngestionResponse.deserialize(responseBody, response.getRequestId());
		return listIngestionResponse;
	}

	@Override
	public StopIngestionResponse stopIngestion(StopIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StopIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public StartIngestionResponse startIngestion(StartIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StartIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public StartIngestionResponse restartIngestion(RestartIngestionRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StartIngestionResponse(responseMessage.getHeaders());
	}

	@Override
	public CreateRebuildIndexResponse createRebuildIndex(CreateRebuildIndexRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new CreateRebuildIndexResponse(responseMessage.getHeaders());
	}

	@Override
	public GetRebuildIndexResponse getRebuildIndex(GetRebuildIndexRequest request) throws LogException {
		ResponseMessage message = send(request);
		JSONObject responseBody = parseResponseBody(message, message.getRequestId());
		GetRebuildIndexResponse response = new GetRebuildIndexResponse(message.getHeaders());
		response.deserialize(responseBody, message.getRequestId());
		return response;
	}

	@Override
	public DeleteRebuildIndexResponse deleteRebuildIndex(DeleteRebuildIndexRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new DeleteRebuildIndexResponse(responseMessage.getHeaders());
	}

	@Override
	public ListRebuildIndexResponse listRebuildIndex(ListRebuildIndexRequest request) throws LogException {
		ResponseMessage message = send(request);
		JSONObject responseBody = parseResponseBody(message, message.getRequestId());
		ListRebuildIndexResponse response = new ListRebuildIndexResponse(message.getHeaders());
		response.deserialize(responseBody, message.getRequestId());
		return response;
	}

	@Override
	public StopRebuildIndexResponse stopRebuildIndex(StopRebuildIndexRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StopRebuildIndexResponse(responseMessage.getHeaders());
	}

	@Override
	public CreateAuditJobResponse createAuditJob(CreateAuditJobRequest request) throws LogException {
		ResponseMessage responseMessage = send(request, request.getBody().toString());
		return new CreateAuditJobResponse(responseMessage.getHeaders());
	}

	@Override
	public UpdateAuditJobResponse updateAuditJob(UpdateAuditJobRequest request) throws LogException {
		ResponseMessage responseMessage = send(request, request.getBody().toString());
		return new UpdateAuditJobResponse(responseMessage.getHeaders());
	}

	@Override
	public GetAuditJobResponse getAuditJob(GetAuditJobRequest request) throws LogException {
		ResponseMessage message = send(request);
		JSONObject responseBody = parseResponseBody(message, message.getRequestId());
		GetAuditJobResponse response = new GetAuditJobResponse(message.getHeaders());
		response.deserialize(responseBody, message.getRequestId());
		return response;
	}

	@Override
	public DeleteAuditJobResponse deleteAuditJob(DeleteAuditJobRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new DeleteAuditJobResponse(responseMessage.getHeaders());
	}

	@Override
	public ListAuditJobResponse listAuditJob(ListAuditJobRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		ListAuditJobResponse listResp = new ListAuditJobResponse(resp.getHeaders());
		listResp.deserialize(respBody, resp.getRequestId());
		return listResp;
	}

	@Override
	public StopAuditJobResponse stopAuditJob(StopAuditJobRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StopAuditJobResponse(responseMessage.getHeaders());
	}

	@Override
	public CreateTopostoreResponse createTopostore(CreateTopostoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostore(), "topostore");
		Topostore topostore = request.getTopostore();
		topostore.checkForCreate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostore.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI;
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new CreateTopostoreResponse(response.getHeaders());
	}

	@Override
	public UpdateTopostoreResponse updateTopostore(UpdateTopostoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostore(), "topostore");
		Topostore topostore = request.getTopostore();
		topostore.checkForUpdate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostore.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + topostore.getName();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpdateTopostoreResponse(response.getHeaders());
	}

	protected Topostore extractTopostoreFromResponse(JSONObject dict, String requestId) throws LogException {
		Topostore topostore = new Topostore();
		try {
			topostore.FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid json string : " + dict.toString(), e, requestId);
		}
		return topostore;
	}

	protected List<Topostore> extractTopostores(JSONObject object, String requestId) throws LogException {
		List<Topostore> topostores = new ArrayList<Topostore>();
		if (object == null) {
			return topostores;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("items");
			if (array == null) {
				return topostores;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				Topostore topostore = new Topostore();
				topostore.FromJsonObject(jsonObject);
				topostores.add(topostore);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}
		return topostores;
	}


	@Override
	public GetTopostoreResponse getTopostore(GetTopostoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		Topostore topostore = extractTopostoreFromResponse(object, requestId);
		return new GetTopostoreResponse(response.getHeaders(), topostore);
	}


	@Override
	public ListTopostoreResponse listTopostore(ListTopostoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int count = object.getIntValue(Consts.CONST_COUNT);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		List<Topostore> topostores = extractTopostores(object, requestId);
		return new ListTopostoreResponse(response.getHeaders(), topostores, count, total);
	}

	@Override
	public DeleteTopostoreResponse deleteTopostore(DeleteTopostoreRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName();
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE,
				resourceUri, request.GetAllParams(), headParameter);
		return new DeleteTopostoreResponse(response.getHeaders());
	}

	@Override
	public CreateTopostoreNodeResponse createTopostoreNode(CreateTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreNode(), "topostoreNode");

		TopostoreNode topostoreNode = request.getTopostoreNode();
		topostoreNode.checkForCreate();;

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostoreNode.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/nodes";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new CreateTopostoreNodeResponse(response.getHeaders());
	}

	@Override
	public UpdateTopostoreNodeResponse updateTopostoreNode(UpdateTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreNode(), "topostoreNode");

		TopostoreNode topostoreNode = request.getTopostoreNode();
		topostoreNode.checkForUpdate();;

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostoreNode.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/nodes/" + topostoreNode.getNodeId();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpdateTopostoreNodeResponse(response.getHeaders());
	}

	@Override
	public DeleteTopostoreNodeResponse deleteTopostoreNode(DeleteTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreNodeIds(), "topostoreNodeIds");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/nodes";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE,
				resourceUri, request.GetAllParams(), headParameter);
		return new DeleteTopostoreNodeResponse(response.getHeaders());
	}


	protected TopostoreNode extractTopostoreNodeFromResponse(JSONObject dict, String requestId) throws LogException {
		TopostoreNode node = new TopostoreNode();
		try {
			node.FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid json string : " + dict.toString(), e, requestId);
		}
		return node;
	}

	public GetTopostoreNodeResponse getTopostoreNode(GetTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() 
			+ "/nodes/" +  request.getTopostoreNodeId();

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		TopostoreNode node = extractTopostoreNodeFromResponse(object, requestId);
		return new GetTopostoreNodeResponse(response.getHeaders(), node);
	}

	protected List<TopostoreNode> extractTopostoreNodesFromResponse(JSONObject object, String requestId) throws LogException {
		List<TopostoreNode> topostoreNodes = new ArrayList<TopostoreNode>();
		if (object == null) {
			return topostoreNodes;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("items");
			if (array == null) {
				return topostoreNodes;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				TopostoreNode node = new TopostoreNode();
				node.FromJsonObject(jsonObject);
				topostoreNodes.add(node);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}
		return topostoreNodes;
	}

	@Override
	public ListTopostoreNodeResponse listTopostoreNode(ListTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/nodes";

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		List<TopostoreNode> nodes = extractTopostoreNodesFromResponse(object, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
		return new ListTopostoreNodeResponse(response.getHeaders(), nodes, count, total);
	}

	@Override
	public UpsertTopostoreNodeResponse upsertTopostoreNode(UpsertTopostoreNodeRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreNodes(), "topostoreNodes");
		for (TopostoreNode n: request.getTopostoreNodes()) {
			n.checkForUpsert();
		}

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getPostBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/nodes";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpsertTopostoreNodeResponse(response.getHeaders());
	}

	@Override
	public CreateTopostoreRelationResponse createTopostoreRelation(CreateTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreRelation(), "topostoreRelation");

		TopostoreRelation topostoreRelation = request.getTopostoreRelation();
		topostoreRelation.checkForCreate();;

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostoreRelation.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/relations";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new CreateTopostoreRelationResponse(response.getHeaders());
	}

	@Override
	public UpdateTopostoreRelationResponse updateTopostoreRelation(UpdateTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreRelation(), "topostoreRelation");

		TopostoreRelation topostoreRelation = request.getTopostoreRelation();
		topostoreRelation.checkForUpdate();;

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(topostoreRelation.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/relations/" + topostoreRelation.getRelationId();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpdateTopostoreRelationResponse(response.getHeaders());
	}

	@Override
	public DeleteTopostoreRelationResponse deleteTopostoreRelation(DeleteTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreRelationIds(), "topostoreRelationIds");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/relations";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE,
				resourceUri, request.GetAllParams(), headParameter);
		return new DeleteTopostoreRelationResponse(response.getHeaders());
	}


	protected TopostoreRelation extractTopostoreRelationFromResponse(JSONObject dict, String requestId) throws LogException {
		TopostoreRelation relation = new TopostoreRelation();
		try {
			relation.FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid json string : " + dict.toString(), e, requestId);
		}
		return relation;
	}

	public GetTopostoreRelationResponse getTopostoreRelation(GetTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getTopostoreName(), "topostoreName");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() 
			+ "/relations/" +  request.getTopostoreRelationId();

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		TopostoreRelation relation = extractTopostoreRelationFromResponse(object, requestId);
		return new GetTopostoreRelationResponse(response.getHeaders(), relation);
	}

	protected List<TopostoreRelation> extractTopostoreRelationsFromResponse(JSONObject object, String requestId) throws LogException {
		List<TopostoreRelation> topostoreRelations = new ArrayList<TopostoreRelation>();
		if (object == null) {
			return topostoreRelations;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("items");
			if (array == null) {
				return topostoreRelations;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				TopostoreRelation relation = new TopostoreRelation();
				relation.FromJsonObject(jsonObject);
				topostoreRelations.add(relation);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}
		return topostoreRelations;
	}

	@Override
	public ListTopostoreRelationResponse listTopostoreRelation(ListTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());

		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/relations";

		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		List<TopostoreRelation> relations = extractTopostoreRelationsFromResponse(object, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
		return new ListTopostoreRelationResponse(response.getHeaders(), relations, count, total);
	}

	@Override
	public UpsertTopostoreRelationResponse upsertTopostoreRelation(UpsertTopostoreRelationRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getTopostoreName(), "topostoreName");
		CodingUtils.assertParameterNotNull(request.getTopostoreRelations(), "topostoreRelations");
		for (TopostoreRelation n: request.getTopostoreRelations()) {
			n.checkForUpsert();
		}

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getPostBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri =Consts.TOPOSTORE_URI + "/" + request.getTopostoreName() + "/relations";
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpsertTopostoreRelationResponse(response.getHeaders());
	}

	public List<Set<String>> traverseNodeRelations(Map<String, List<TopostoreRelation>> nodeRelationMap,String direction,
			String nodeId, long depth, boolean depthMode, List<String> relationTypes){
		if(depthMode && depth<0){
			return new ArrayList<Set<String>>();
		}

		Set<String> nodeIds = new HashSet<String>();
		nodeIds.add(nodeId);
		Set<String> relationIds = new HashSet<String>();
		List<Set<String>>  ret = new ArrayList<Set<String>>();

		if(depthMode && depth==0){
			ret.add(nodeIds);
			ret.add(relationIds);
			return ret;
		}

		if(nodeRelationMap.containsKey(nodeId)){
			for(TopostoreRelation relation: nodeRelationMap.get(nodeId)){
				if(relationTypes == null || relationTypes.size()==0 || relationTypes.contains(relation.getRelationType())){	
					String mNodeId=null;
					if(direction.equals(Consts.TOPOSTORE_RELATION_DIRECTION_IN)){
						mNodeId = relation.getSrcNodeId();
					} else if(direction.equals(Consts.TOPOSTORE_RELATION_DIRECTION_OUT)){	
						mNodeId = relation.getDstNodeId();
					}

					if(mNodeId!=null){
						nodeIds.add(mNodeId);
						relationIds.add(relation.getRelationId());
						// 防止环
						nodeRelationMap.remove(nodeId);

						List<Set<String>> childs =  traverseNodeRelations(nodeRelationMap, direction, mNodeId, depth-1, depthMode, relationTypes);
						if(childs.size()==2){
							for(String n: childs.get(0)){
								nodeIds.add(n);
							}
							for(String r: childs.get(1)){
								relationIds.add(r);
							}
						}
					}

				}
			}
		}

		ret.add(nodeIds);
		ret.add(relationIds);
		return ret;
	}

	private List<TopostoreNode> listTopostoreNodeWithAutoPage(String topostoreName, List<String> reqNodeIds, 
		List<String> nodeTypes, Map<String, String> nodeProperties, Map<String, String> params) throws LogException{
		List<TopostoreNode> finalNodes = new ArrayList<TopostoreNode>();

		if(reqNodeIds.size()==0){
			int offset = 0;
			int total = 100000;

			while( offset < total ){
				ListTopostoreNodeRequest listNodeReq = new ListTopostoreNodeRequest();
				listNodeReq.setTopostoreName(topostoreName);
				listNodeReq.setProperties(nodeProperties);
				listNodeReq.setNodeTypes(nodeTypes);
				for(Map.Entry<String, String> kv: params.entrySet()){
					listNodeReq.SetParam(kv.getKey(), kv.getValue());
				}
				ListTopostoreNodeResponse listNodeResp = this.listTopostoreNode(listNodeReq);
				total = listNodeResp.getTotal();
				for (TopostoreNode n: listNodeResp.getTopostoreNodes()){
					offset++;
					finalNodes.add(n);
				}
			}
		} else {
			List<List<String>> reqNodeIdLists = new ArrayList<List<String>>();
			for(int i=0; i<reqNodeIds.size();i++) {
				if(i % 200 == 0){
					reqNodeIdLists.add(new ArrayList<String>());
				}
				reqNodeIdLists.get(i/200).add(reqNodeIds.get(i));
			}
			for(int i=0; i<reqNodeIdLists.size(); i++){
				ListTopostoreNodeRequest listNodeReq = new ListTopostoreNodeRequest();
				listNodeReq.setTopostoreName(topostoreName);
				listNodeReq.setNodeIds(reqNodeIdLists.get(i));
				listNodeReq.setProperties(nodeProperties);
				listNodeReq.setNodeTypes(nodeTypes);
				for(Map.Entry<String, String> kv: params.entrySet()){
					listNodeReq.SetParam(kv.getKey(), kv.getValue());
				}
				ListTopostoreNodeResponse listNodeResp = this.listTopostoreNode(listNodeReq);
				for (TopostoreNode n: listNodeResp.getTopostoreNodes()){
					finalNodes.add(n);
				}
			}
		}

		return finalNodes;
	}

	private List<TopostoreRelation> listTopostoreRelationWithAutoPage(String topostoreName, List<String> reqRelationIds,
		Map<String, String> params) throws LogException{
		List<List<String>> reqRelationIdLists = new ArrayList<List<String>>();
		for(int i=0; i<reqRelationIds.size();i++) {
			if(i % 200 == 0){
				reqRelationIdLists.add(new ArrayList<String>());
			}
			reqRelationIdLists.get(i/200).add(reqRelationIds.get(i));
		}
		List<TopostoreRelation> finalRelations = new ArrayList<TopostoreRelation>();
		for(int i=0; i<reqRelationIdLists.size(); i++){
			ListTopostoreRelationRequest listRelationReq = new ListTopostoreRelationRequest();
			listRelationReq.setTopostoreName(topostoreName);
			listRelationReq.setRelationIds(reqRelationIdLists.get(i));
			for(Map.Entry<String, String> kv: params.entrySet()){
				listRelationReq.SetParam(kv.getKey(), kv.getValue());
			}
			ListTopostoreRelationResponse listRelationResp = this.listTopostoreRelation(listRelationReq);
			for (TopostoreRelation r: listRelationResp.getTopostoreRelations()){
				finalRelations.add(r);
			}
		}
		return finalRelations;
	}


	public ListTopostoreNodeRelationResponse listTopostoreNodeRelations(ListTopostoreNodeRelationRequest request) throws LogException {
		ListTopostoreNodeRelationResponse response = new ListTopostoreNodeRelationResponse();

		// get all nodes
		List<String> allNodeIds = new ArrayList<String>();
		List<TopostoreNode> allTopoNodes = this.listTopostoreNodeWithAutoPage(request.getTopostoreName(), request.getNodeIds(), 
			request.getNodeTypes(), request.getNodeProperities(), request.GetParam());
		
		for(TopostoreNode n: allTopoNodes){
			allNodeIds.add(n.getNodeId());
		}

		if(request.getDepth()==0 || allNodeIds.size() == 0){
			response.setRelations(new ArrayList<TopostoreRelation>());
			response.setNodes(allTopoNodes);
			return response;
		}

		// prepare relation maps
		int relationOffset = 0;
		int relationTotal = 100000;
		Map<String, Map<String, List<TopostoreRelation>>> nodeRelationMap = new HashMap<String,Map<String,List<TopostoreRelation>>>();
		nodeRelationMap.put(Consts.TOPOSTORE_RELATION_DIRECTION_IN, new HashMap<String,List<TopostoreRelation>>());
		nodeRelationMap.put(Consts.TOPOSTORE_RELATION_DIRECTION_OUT, new HashMap<String,List<TopostoreRelation>>());

		while( relationOffset < relationTotal ){
			ListTopostoreRelationRequest listRelationReq = new ListTopostoreRelationRequest();
			listRelationReq.setTopostoreName(request.getTopostoreName());
			for(Map.Entry<String, String> kv: request.GetParam().entrySet()){
				listRelationReq.SetParam(kv.getKey(), kv.getValue());
			}

			ListTopostoreRelationResponse listRelationResp = this.listTopostoreRelation(listRelationReq);
	
			relationTotal = listRelationResp.getTotal();
			
			for(TopostoreRelation relation: listRelationResp.getTopostoreRelations()){
				String srcNodeId = relation.getSrcNodeId();
				String dstNodeId = relation.getDstNodeId();
	
				relationOffset++;
				if (!nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_IN).containsKey(dstNodeId)){
					nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_IN).put(dstNodeId, new ArrayList<TopostoreRelation>());
				}
				nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_IN).get(dstNodeId).add(relation);
	
				if (!nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_OUT).containsKey(srcNodeId)){
					nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_OUT).put(srcNodeId, new ArrayList<TopostoreRelation>());
				}
				nodeRelationMap.get(Consts.TOPOSTORE_RELATION_DIRECTION_OUT).get(srcNodeId).add(relation);
			}
		}

		// traverse
		Set<String> finalNodeIds = new HashSet<String>();
		Set<String> finalRelationIds = new HashSet<String>();

		for(String nodeId : allNodeIds){
			for(Map.Entry<String, Map<String, List<TopostoreRelation>>> entry: nodeRelationMap.entrySet()){
				if(request.getDirection().equals(entry.getKey())
					||request.getDirection().equals(Consts.TOPOSTORE_RELATION_DIRECTION_BOTH)){
						boolean depthMode=false;
						if(request.getDepth()>0){
							depthMode = true;
						}
						List<Set<String>>  ret = traverseNodeRelations(entry.getValue(), entry.getKey(), nodeId, 
						request.getDepth(), depthMode, request.getRelationTypes());
						if(ret.size() == 2){
							for(String n: ret.get(0)){
								finalNodeIds.add(n);
							}
							for(String r: ret.get(1)){
								finalRelationIds.add(r);
							}
						}
				}
			}
		}

		// prepare results

		List<String> reqNodeIds = new ArrayList<String>();
		reqNodeIds.addAll(finalNodeIds);
				
		if(reqNodeIds.size()>0){
			response.setNodes(this.listTopostoreNodeWithAutoPage(request.getTopostoreName(), reqNodeIds, null, null, request.GetParam()));
		} else {
			response.setNodes(new ArrayList<TopostoreNode>());
		}

		List<String> reqRelationIds = new ArrayList<String>();
		reqRelationIds.addAll(finalRelationIds);
		if(reqRelationIds.size()>0){
			response.setRelations(this.listTopostoreRelationWithAutoPage(request.getTopostoreName(), reqRelationIds, request.GetParam()));
		} else {
			response.setRelations(new ArrayList<TopostoreRelation>());
		}

		
		return response;
	}
	
	@Override
	public CreateResourceResponse createResource(CreateResourceRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getResource(), "resource");
		Resource resource = request.getResource();
		resource.checkForCreate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(resource.ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_RESOURCE_URI;
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new CreateResourceResponse(response.getHeaders());
	}

	@Override
	public UpdateResourceResponse updateResource(UpdateResourceRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertParameterNotNull(request.getResource(), "resource");
		request.getResource().checkForUpdate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getResource().ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		CodingUtils.validateResource(request.getResource().getName());
		String resourceUri = String.format(Consts.CONST_RESOURCE_NAME_URI, request.getResource().getName());
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpdateResourceResponse(response.getHeaders());
	}

	@Override
	public DeleteResourceResponse deleteResource(DeleteResourceRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = String.format(Consts.CONST_RESOURCE_NAME_URI, request.getResourceName());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE, resourceUri, request.GetAllParams(), headParameter);
		return new DeleteResourceResponse(response.getHeaders());
	}

	@Override
	public GetResourceResponse getResource(GetResourceRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = String.format(Consts.CONST_RESOURCE_NAME_URI, request.getResourceName());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		Resource resource = extractResourceFromResponse(object, requestId);
		return new GetResourceResponse(response.getHeaders(), resource);
	}

	protected Resource extractResourceFromResponse(JSONObject dict, String requestId) throws LogException {
		Resource resource = new Resource();
		try {
			resource.FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid json string : " + dict.toString(), e, requestId);
		}
		return resource;
	}

	@Override
	public ListResourceResponse listResource(ListResourceRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = Consts.CONST_RESOURCE_URI;
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
		List<Resource> resources = extractResources(object, requestId);
		return new ListResourceResponse(response.getHeaders(), count, total, resources);
	}

	protected List<Resource> extractResources(JSONObject object, String requestId) throws LogException {
		List<Resource> resources = new ArrayList<Resource>();
		if (object == null) {
			return resources;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("items");
			if (array == null) {
				return resources;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				Resource resource = new Resource();
				resource.FromJsonObject(jsonObject);
				resources.add(resource);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}
		return resources;
	}

	@Override
	public CreateResourceRecordResponse createResourceRecord(CreateResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		CodingUtils.assertParameterNotNull(request.getRecord(), "record");
		request.getRecord().checkForCreate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getRecord().ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_URI, request.getResourceName());
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new CreateResourceRecordResponse(response.getHeaders());
	}

	@Override
	public UpsertResourceRecordResponse upsertResourceRecord(UpsertResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		CodingUtils.assertParameterNotNull(request.getRecords(), "records");
		for (ResourceRecord r: request.getRecords()) {
			CodingUtils.assertParameterNotNull(r, "record");
			r.checkForUpsert();
		}

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getPostBody());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_URI, request.getResourceName());
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpsertResourceRecordResponse(response.getHeaders());
	}

	@Override
	public UpdateResourceRecordResponse updateResourceRecord(UpdateResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		CodingUtils.assertParameterNotNull(request.getRecord(), "record");
		CodingUtils.assertStringNotNullOrEmpty(request.getRecord().getId(), "recordId");
		request.getRecord().checkForUpdate();

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		byte[] body = encodeToUtf8(request.getRecord().ToJsonString());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_ID_URI, request.getResourceName(), request.getRecord().getId());
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.PUT,
				resourceUri, request.GetAllParams(), headParameter, body);
		return new UpdateResourceRecordResponse(response.getHeaders());
	}

	@Override
	public DeleteResourceRecordResponse deleteResourceRecord(DeleteResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		CodingUtils.assertParameterNotNull(request.getRecordIds(), "recordIds");
		for (String id: request.getRecordIds()) {
			CodingUtils.assertStringNotNullOrEmpty(id, "recordId");
		}

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_URI, request.getResourceName());
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		return new DeleteResourceRecordResponse(response.getHeaders());
	}

	@Override
	public GetResourceRecordResponse getResourceRecord(GetResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");
		CodingUtils.assertStringNotNullOrEmpty(request.getRecordId(), "recordId");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_ID_URI,
				request.getResourceName(), request.getRecordId());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, request.GetAllParams(), headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		ResourceRecord record = extractResourceRecordFromResponse(object, requestId);
		return new GetResourceRecordResponse(response.getHeaders(), record);
	}

	protected ResourceRecord extractResourceRecordFromResponse(JSONObject dict, String requestId) throws LogException {
		ResourceRecord record = new ResourceRecord();
		try {
			record.FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid json string : " + dict.toString(), e, requestId);
		}
		return record;
	}

	@Override
	public ListResourceRecordResponse listResourceRecord(ListResourceRecordRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		CodingUtils.assertStringNotNullOrEmpty(request.getResourceName(), "resourceName");

		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		String resourceUri = String.format(Consts.CONST_RESOURCE_RECORD_URI, request.getResourceName());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		int total = object.getIntValue(Consts.CONST_TOTAL);
		int count = object.getIntValue(Consts.CONST_COUNT);
		List<ResourceRecord> records = extractResourceRecords(object, requestId);
		return new ListResourceRecordResponse(response.getHeaders(), count, total, records);
	}

	protected List<ResourceRecord> extractResourceRecords(JSONObject object, String requestId) throws LogException {
		List<ResourceRecord> records = new ArrayList<ResourceRecord>();
		if (object == null) {
			return records;
		}
		JSONArray array = new JSONArray();
		try {
			array = object.getJSONArray("items");
			if (array == null) {
				return records;
			}
			for (int index = 0; index < array.size(); index++) {
				JSONObject jsonObject = array.getJSONObject(index);
				if (jsonObject == null) {
					continue;
				}
				ResourceRecord record = new ResourceRecord();
				record.FromJsonObject(jsonObject);
				records.add(record);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
		}
		return records;
	}

	@Override
	public StartAuditJobResponse startAuditJob(StartAuditJobRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StartAuditJobResponse(responseMessage.getHeaders());
	}

	@Override
    public UpdateAlertResponse updateAlert(UpdateAlertRequest request) throws LogException {
        ResponseMessage message = send(request);
        return new UpdateAlertResponse(message.getHeaders());
    }

    @Override
    public DeleteAlertResponse deleteAlert(DeleteAlertRequest request) throws LogException {
        ResponseMessage responseMessage = send(request);
        return new DeleteAlertResponse(responseMessage.getHeaders());
    }

    @Override
    public GetAlertResponse getAlert(GetAlertRequest request) throws LogException {
        ResponseMessage response = send(request);
        JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        GetAlertResponse alertResponse = new GetAlertResponse(response.getHeaders());
        alertResponse.deserialize(responseBody, response.getRequestId());
        return alertResponse;
    }

    @Override
    public ListAlertResponse listAlert(ListAlertRequest request) throws LogException {
        ResponseMessage response = send(request);
        JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        ListAlertResponse alertResponse = new ListAlertResponse(response.getHeaders());
        alertResponse.deserialize(responseBody, response.getRequestId());
        return alertResponse;
    }

	@Override
	public CreateReportResponse createReport(CreateReportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new CreateReportResponse(responseMessage.getHeaders());
	}

	@Override
	public GetReportResponse getReport(GetReportRequest request) throws LogException {
		ResponseMessage response = send(request);
		JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        GetReportResponse getReportResponse = new GetReportResponse(response.getHeaders());
        getReportResponse.deserialize(responseBody, response.getRequestId());
        return getReportResponse;
	}

	@Override
	public UpdateReportResponse updateReport(UpdateReportRequest request) throws LogException {
		ResponseMessage message = send(request);
		return new UpdateReportResponse(message.getHeaders());
	}

	@Override
	public DeleteReportResponse deleteReport(DeleteReportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new DeleteReportResponse(responseMessage.getHeaders());
	}

	@Override
	public ListReportResponse listReport(ListReportRequest request) throws LogException {
		ResponseMessage response = send(request);
		JSONObject responseBody = parseResponseBody(response, response.getRequestId());
		ListReportResponse listReportResponse = new ListReportResponse(response.getHeaders());
		listReportResponse.deserialize(responseBody, response.getRequestId());
		return listReportResponse;
	}

	@Override
	public EnableReportResponse enableReport(EnableReportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new EnableReportResponse(responseMessage.getHeaders());
	}

	@Override
	public DisableReportResponse disableReport(DisableReportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new DisableReportResponse(responseMessage.getHeaders());
	}

	@Override
	public CreateEtlJobResponse createEtlJob(CreateEtlJobRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		EtlJob etlJob = request.getEtlJob();
		CodingUtils.assertParameterNotNull(etlJob, "etlJob");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(etlJob.toJsonString(true, true));
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
		CodingUtils.validateETLJob(etlJobName);
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
		CodingUtils.validateETLJob(etlJob.getJobName());
		Map<String, String> headParameter = GetCommonHeadPara(project);
		byte[] body = encodeToUtf8(etlJob.toJsonString(false, false));

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
		CodingUtils.validateETLJob(etlJobName);
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLJOB_URI + "/" + etlJobName;
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONObject object = parseResponseBody(response, requestId);
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
		Map<String, String> urlParameter = request.GetAllParams();
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		ListEtlJobResponse listResp = new ListEtlJobResponse(response.getHeaders(), object.getIntValue(Consts.CONST_TOTAL));
		listResp.setEtlJobNameList(ExtractJsonArray("etlJobNameList", object));
		return listResp;
	}

	@Override
	public CreateEtlMetaResponse createEtlMeta(String project, EtlMeta etlMeta) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMeta, "etlMeta");
		etlMeta.checkForCreate();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, etlMeta.toJsonObject().toString());
		return new CreateEtlMetaResponse(response.getHeaders());
	}

	@Override
	public CreateEtlMetaResponse batchCreateEtlMeta(String project, ArrayList<EtlMeta> etlMetaList) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMetaList, "etlMetaList");
		if (etlMetaList.size() == 0 || etlMetaList.size() > 100) {
			throw new IllegalArgumentException("etlMetaList size not valid, should be [1, 100]");
		}
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		JSONObject requestBodyJsonObject = new JSONObject();
		JSONArray etlMetaJsonArray = new JSONArray();
		for (EtlMeta meta : etlMetaList) {
		    meta.checkForCreate();
			etlMetaJsonArray.add(meta.toJsonObject());
		}
		requestBodyJsonObject.put(Consts.ETL_META_LIST, etlMetaJsonArray);
		ResponseMessage response = SendData(project, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, requestBodyJsonObject.toString());
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
	public BatchModifyEtlMetaStatusResponse batchModifyEtlMetaStatus(String project, String etlMetaName, String etlMetaTag, Consts.BatchModifyEtlMetaType type) throws LogException {
		return batchModifyEtlMetaStatus(project, etlMetaName, null, etlMetaTag, Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE_ALL, type);
	}

	@Override
	public BatchModifyEtlMetaStatusResponse batchModifyEtlMetaStatus(String project, String etlMetaName, ArrayList<String> etlMetaKeyList, Consts.BatchModifyEtlMetaType type) throws LogException {
		return batchModifyEtlMetaStatus(project, etlMetaName, etlMetaKeyList, Consts.CONST_ETLMETA_ALL_TAG_MATCH, type);
	}

	@Override
	public BatchModifyEtlMetaStatusResponse batchModifyEtlMetaStatus(String project, String etlMetaName, ArrayList<String> etlMetaKeyList, String etlMetaTag, Consts.BatchModifyEtlMetaType type) throws LogException {
		return batchModifyEtlMetaStatus(project, etlMetaName, etlMetaKeyList, etlMetaTag, Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE_LIST, type);
	}

	private BatchModifyEtlMetaStatusResponse batchModifyEtlMetaStatus(String project, String etlMetaName, ArrayList<String> etlMetaKeyList, String etlMetaTag, String range, Consts.BatchModifyEtlMetaType type) throws LogException {
		if (range.equals(Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE_LIST)) {
			if (etlMetaKeyList == null || etlMetaKeyList.size() == 0 || etlMetaKeyList.size() > 200) {
				throw new IllegalArgumentException("etlMetaKeyList size not valid, should be [1, 200]");
			}
		} else if (range.equals(Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE_ALL)) {
			if (etlMetaTag.equals(Consts.CONST_ETLMETA_ALL_TAG_MATCH)) {
				throw new IllegalArgumentException("parameter etlMetaTag can not be `__all_etl_meta_tag_match__` when batchDelete by tag");
			}
		} else {
			throw new IllegalArgumentException("range not valid, should be `all` or `list`");
		}
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(etlMetaName, "etlMetaName");
		CodingUtils.assertParameterNotNull(etlMetaTag, "etlMetaTag");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		urlParameter.put("type", type.toString());
		JSONObject requestBodyJsonObject = new JSONObject();
		requestBodyJsonObject.put(Consts.ETL_META_NAME, etlMetaName);
		requestBodyJsonObject.put(Consts.ETL_META_TAG, etlMetaTag);
		requestBodyJsonObject.put(Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE, range);
		if (range.equals(Consts.ETL_META_BATCH_MODIFY_STATUS_RANGE_LIST)) {
			JSONArray etlMetaKeyJsonArray = new JSONArray();
			etlMetaKeyJsonArray.addAll(etlMetaKeyList);
			requestBodyJsonObject.put(Consts.ETL_META_KEY_LIST, etlMetaKeyJsonArray);
		}
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, requestBodyJsonObject.toString());
		return new BatchModifyEtlMetaStatusResponse(response.getHeaders());
	}

	@Override
	public UpdateEtlMetaResponse updateEtlMeta(String project, EtlMeta etlMeta) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMeta, "etlMeta");
		etlMeta.checkForUpdate();
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, etlMeta.toJsonObject().toString());
		return new UpdateEtlMetaResponse(response.getHeaders());
	}

	@Override
	public UpdateEtlMetaResponse batchUpdateEtlMeta(String project, ArrayList<EtlMeta> etlMetaList) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertParameterNotNull(etlMetaList, "etlMetaList");
		if (etlMetaList.size() == 0 || etlMetaList.size() > 100) {
			throw new IllegalArgumentException("etlMetaList size not valid, should be [1, 100]");
		}
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri = Consts.CONST_ETLMETA_URI;
		Map<String, String> urlParameter = new HashMap<String, String>();
		JSONObject requestBodyJsonObject = new JSONObject();
		JSONArray etlMetaJsonArray = new JSONArray();
		for (EtlMeta meta : etlMetaList) {
			meta.checkForUpdate();
			etlMetaJsonArray.add(meta.toJsonObject());
		}
		requestBodyJsonObject.put(Consts.ETL_META_LIST, etlMetaJsonArray);
		ResponseMessage response = SendData(project, HttpMethod.PUT,
				resourceUri, urlParameter, headParameter, requestBodyJsonObject.toString());
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
		JSONObject object = parseResponseBody(response, requestId);
		ListEtlMetaNameResponse listResp = new ListEtlMetaNameResponse(response.getHeaders(), object.getIntValue(Consts.CONST_TOTAL));
		listResp.setEtlMetaNameList(ExtractJsonArray("etlMetaNameList", object));
		return listResp;
	}

	private ListEtlMetaResponse listEtlMeta(ListEtlMetaRequest request) throws LogException {
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		Map<String, String> urlParameter = request.GetAllParams();
		String resourceUri = Consts.CONST_ETLMETA_URI;
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.GET, resourceUri, urlParameter, headParameter);
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		ListEtlMetaResponse listResp = new ListEtlMetaResponse(response.getHeaders(), object.getIntValue(Consts.CONST_TOTAL));
		try {
			JSONArray items = object.getJSONArray("etlMetaList");
			if (items == null) {
				return listResp;
			}
			for (int i = 0; i < items.size(); i++) {
				JSONObject jsonObject = items.getJSONObject(i);
				if (jsonObject == null) {
					continue;
				}
				EtlMeta meta = new EtlMeta();
				meta.fromJsonObject(jsonObject);
				listResp.addEtlMeta(meta);
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE, e.getMessage(), listResp.GetRequestId());
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

	@Override
	public CreateLoggingResponse createLogging(final CreateLoggingRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		final String project = request.GetProject();
		Map<String, String> headers = GetCommonHeadPara(project);
		final Logging logging = request.getLogging();
		ResponseMessage response = SendData(project, HttpMethod.POST,
                Consts.LOGGING_URI, Collections.<String, String>emptyMap(), headers, logging.marshal().toString());
		return new CreateLoggingResponse(response.getHeaders());
	}

    @Override
    public UpdateLoggingResponse updateLogging(final UpdateLoggingRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        final String project = request.GetProject();
        Map<String, String> headers = GetCommonHeadPara(project);
        final Logging logging = request.getLogging();
        ResponseMessage response = SendData(project, HttpMethod.PUT,
                Consts.LOGGING_URI, Collections.<String, String>emptyMap(), headers, logging.marshal().toString());
        return new UpdateLoggingResponse(response.getHeaders());
    }

    @Override
    public GetLoggingResponse getLogging(final GetLoggingRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        final String project = request.GetProject();
        Map<String, String> headers = GetCommonHeadPara(project);
        ResponseMessage response = SendData(project, HttpMethod.GET,
                Consts.LOGGING_URI, Collections.<String, String>emptyMap(), headers);
        JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        try {
            return new GetLoggingResponse(response.getHeaders(), Logging.unmarshal(responseBody));
        } catch (JSONException ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, ex.getMessage(), response.getRequestId());
        }
    }

    @Override
    public DeleteLoggingResponse deleteLogging(final DeleteLoggingRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        final String project = request.GetProject();
        Map<String, String> headers = GetCommonHeadPara(project);
        ResponseMessage response = SendData(project, HttpMethod.DELETE,
                Consts.LOGGING_URI, Collections.<String, String>emptyMap(), headers);
        return new DeleteLoggingResponse(response.getHeaders());
    }

    @Override
    public CreateJobResponse createJob(CreateJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new CreateJobResponse(response.getHeaders());
    }

    @Override
    public CreateAlertResponse createAlert(CreateAlertRequest request) throws LogException {
        ResponseMessage responseMessage = send(request);
        return new CreateAlertResponse(responseMessage.getHeaders());
    }

    @Override
    public GetJobResponse getJob(GetJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        GetJobResponse getJobResponse = new GetJobResponse(response.getHeaders());
        getJobResponse.deserialize(responseBody, response.getRequestId());
        return getJobResponse;
    }

    @Override
    public UpdateJobResponse updateJob(UpdateJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new UpdateJobResponse(response.getHeaders());
    }

    @Override
    public DeleteJobResponse deleteJob(DeleteJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new DeleteJobResponse(response.getHeaders());
    }

    @Override
    public EnableJobResponse enableJob(EnableJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new EnableJobResponse(response.getHeaders());
    }

    @Override
    public EnableAlertResponse enableAlert(EnableAlertRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new EnableAlertResponse(response.getHeaders());
    }

    @Override
    public DisableJobResponse disableJob(DisableJobRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new DisableJobResponse(response.getHeaders());
    }

    @Override
    public DisableAlertResponse disableAlert(DisableAlertRequest request) throws LogException {
        ResponseMessage response = send(request);
        return new DisableAlertResponse(response.getHeaders());
    }

    @Override
    public ListJobsResponse listJobs(ListJobsRequest request) throws LogException {
        ResponseMessage response = send(request);
        JSONObject responseBody = parseResponseBody(response, response.getRequestId());
        ListJobsResponse jobsResponse = new ListJobsResponse(response.getHeaders());
        jobsResponse.deserialize(responseBody, response.getRequestId());
        return jobsResponse;
    }

	@Override
	public CreateETLV2Response createETLV2(CreateETLV2Request request) throws LogException {
		ResponseMessage resp = send(request);
		return new CreateETLV2Response(resp.getHeaders());
	}

	@Override
	public UpdateETLV2Response updateETLV2(UpdateETLV2Request request) throws LogException {
		ResponseMessage resp = send(request);
		return new UpdateETLV2Response(resp.getHeaders());
	}

	@Override
	public DeleteETLV2Response deleteETLV2(DeleteETLV2Request request) throws LogException {
		ResponseMessage resp = send(request);
		return new DeleteETLV2Response(resp.getHeaders());
	}

	@Override
	public GetETLV2Response getETLV2(GetETLV2Request request) throws LogException {
		ResponseMessage response = send(request);
		JSONObject responseBody = parseResponseBody(response, response.getRequestId());
		GetETLV2Response etlResponse = new GetETLV2Response(response.getHeaders());
		etlResponse.deserialize(responseBody, response.getRequestId());
		return etlResponse;
	}

	@Override
	public ListETLV2Response listETLV2(ListETLV2Request request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		ListETLV2Response listResp = new ListETLV2Response(resp.getHeaders());
		listResp.deserialize(respBody, resp.getRequestId());
		return listResp;
	}

	@Override
	public StopETLV2Response stopETLV2(StopETLV2Request request)throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StopETLV2Response(responseMessage.getHeaders());
	}

	@Override
	public StartETLV2Response startETLV2(StartETLV2Request request)throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StartETLV2Response(responseMessage.getHeaders());
	}

	@Override
	public ReStartETLV2Response reStartETLV2(ReStartETLV2Request request) throws LogException{
		ResponseMessage responseMessage = send(request);
		return new ReStartETLV2Response(responseMessage.getHeaders());
	}

	@Override
	public CreateExportResponse createExport(CreateExportRequest request) throws LogException {
		ResponseMessage resp = send(request);
		return new CreateExportResponse(resp.getHeaders());
	}

	@Override
	public UpdateExportResponse updateExport(UpdateExportRequest request) throws LogException {
		ResponseMessage resp = send(request);
		return new UpdateExportResponse(resp.getHeaders());
	}

	@Override
	public DeleteExportResponse deleteExport(DeleteExportRequest request) throws LogException {
		ResponseMessage resp = send(request);
		return new DeleteExportResponse(resp.getHeaders());
	}

	@Override
	public GetExportResponse getExport(GetExportRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		GetExportResponse exportResp = new GetExportResponse(resp.getHeaders());
		exportResp.deserialize(respBody, resp.getRequestId());
		return exportResp;
	}

	@Override
	public ListExportResponse listExport(ListExportRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		ListExportResponse listResp = new ListExportResponse(resp.getHeaders());
		listResp.deserialize(respBody, resp.getRequestId());
		return listResp;
	}

	@Override
	public StopExportResponse stopExport(StopExportRequest request) throws LogException {
        ResponseMessage responseMessage = send(request);
        return new StopExportResponse(responseMessage.getHeaders());
	}

	@Override
	public StartExportResponse startExport(StartExportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new StartExportResponse(responseMessage.getHeaders());
	}

	@Override
	public RestartExportResponse restartExport(RestartExportRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new RestartExportResponse(responseMessage.getHeaders());
	}

	@Override
	public CreateScheduledSQLResponse createScheduledSQL(CreateScheduledSQLRequest request) throws LogException{
		ScheduledSQL scheduledSQL = (ScheduledSQL) request.getBody();
		Long fromTime = scheduledSQL.getConfiguration().getFromTime();
        Long toTime = scheduledSQL.getConfiguration().getToTime();
		boolean timeRange = fromTime > 1451577600L && toTime > fromTime;
		boolean sustained = fromTime > 1451577600L && toTime == 0;
		if ((!timeRange) && (!sustained)){
			throw new IllegalArgumentException("Invalid fromTime: "+fromTime+" toTime: "+toTime+
                    ", please ensure fromTime more than 1451577600.");
		}
		ResponseMessage resp = send(request);
		return new CreateScheduledSQLResponse(resp.getHeaders());
	}
	@Override
	public DeleteScheduledSQLResponse deleteScheduledSQL(DeleteScheduledSQLRequest request) throws LogException {
		ResponseMessage resp = send(request);
		return new DeleteScheduledSQLResponse(resp.getHeaders());
	}
	@Override
	public GetScheduledSQLResponse getScheduledSQL(GetScheduledSQLRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		GetScheduledSQLResponse scheduledSQLResp = new GetScheduledSQLResponse(resp.getHeaders());
		scheduledSQLResp.deserialize(respBody, resp.getRequestId());
		return scheduledSQLResp;
	}
	@Override
	public ListScheduledSQLResponse listScheduledSQL(ListScheduledSQLRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		ListScheduledSQLResponse listResp = new ListScheduledSQLResponse(resp.getHeaders());
		listResp.deserialize(respBody, resp.getRequestId());
		return listResp;
	}
	@Override
	public UpdateScheduledSQLResponse updateScheduledSQL(UpdateScheduledSQLRequest request) throws LogException {
		ResponseMessage resp = send(request);
		return new UpdateScheduledSQLResponse(resp.getHeaders());
	}
	@Override
	public GetJobInstanceResponse getJobInstance(GetJobInstanceRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		GetJobInstanceResponse getJobInstanceRes = new GetJobInstanceResponse(resp.getHeaders());
		getJobInstanceRes.deserialize(respBody, resp.getRequestId());
		return getJobInstanceRes;
	}
	@Override
	public ModifyJobInstanceStateResponse modifyJobInstanceState(ModifyJobInstanceStateRequest request) throws LogException {
		ResponseMessage responseMessage = send(request);
		return new ModifyJobInstanceStateResponse(responseMessage.getHeaders());
	}
	@Override
	public ListJobInstancesResponse listJobInstances(ListJobInstancesRequest request) throws LogException {
		ResponseMessage resp = send(request);
		JSONObject respBody = parseResponseBody(resp, resp.getRequestId());
		ListJobInstancesResponse listResp = new ListJobInstancesResponse(resp.getHeaders());
		listResp.deserialize(respBody, resp.getRequestId());
		return listResp;
	}

	private ResponseMessage send(BasicRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
        final String project = request.GetProject();
        final Map<String, String> headers = GetCommonHeadPara(project);
        final Object body = request.getBody();
        final byte[] requestBody = body == null ? new byte[0] : encodeToUtf8(JsonUtils.serialize(body));
        return SendData(project, request.getMethod(), request.getUri(), request.GetAllParams(), headers, requestBody);
    }

	private ResponseMessage send(BasicRequest request, String body) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		final String project = request.GetProject();
		final Map<String, String> headers = GetCommonHeadPara(project);
		final byte[] requestBody = encodeToUtf8(body);
		return SendData(project, request.getMethod(), request.getUri(), request.GetAllParams(), headers, requestBody);
	}

	private void ExtractProjectConsumerGroup(JSONArray array, String requestId,
											 List<ProjectConsumerGroup> consumerGroups) throws LogException {
		try {
			for (int i = 0; i < array.size(); i++) {
				JSONObject consumerGroup = array.getJSONObject(i);
				consumerGroups.add(new ProjectConsumerGroup(
						consumerGroup.getString("name"),
						consumerGroup.getString("logstoreName"),
						consumerGroup.getIntValue("timeout"),
						consumerGroup.getBoolean("order")
				));
			}
		} catch (JSONException e) {
			throw new LogException(ErrorCodes.BAD_RESPONSE,
					"The response is not valid consumer group json array string : "
							+ array.toString(), e, requestId);
		}
	}

	public String getRealServerIP() {
		return realServerIP;
	}

	public void setRealServerIP(String realServerIP) {
		this.realServerIP = realServerIP;
	}

	@Override
	public CreateOrUpdateSqlInstanceResponse createSqlInstance(CreateOrUpdateSqlInstanceRequest request) throws LogException {
		return createOrUpdateSqlInstance(request, HttpMethod.POST);
	}

	@Override
	public CreateOrUpdateSqlInstanceResponse updateSqlInstance(CreateOrUpdateSqlInstanceRequest request) throws LogException {
		return createOrUpdateSqlInstance(request, HttpMethod.PUT);
	}

	public CreateOrUpdateSqlInstanceResponse createOrUpdateSqlInstance(CreateOrUpdateSqlInstanceRequest request, HttpMethod method) throws LogException {
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		int cu = request.getCu();
		JSONObject object = new JSONObject();
		object.put("cu",cu);
		object.put("useAsDefault", request.isUseAsDefault());
		byte[] body = encodeToUtf8(object.toJSONString());
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		String resourceUri ="/sqlinstance";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(project, method,
				resourceUri, urlParameter, headParameter, body);
		Map<String, String> resHeaders = response.getHeaders();
		return new CreateOrUpdateSqlInstanceResponse(resHeaders);
	}
	@Override
	public ListSqlInstanceResponse listSqlInstance(ListSqlInstanceRequest request) throws LogException
	{
		String project = request.GetProject();
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		Map<String, String> urlParameter = new HashMap<String, String>();
		String resourceUri = "/sqlinstance";
		ResponseMessage response = SendData(project, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		String requestId = GetRequestId(resHeaders);
		JSONArray array = ParseResponseMessageToArray(response, requestId);
		List<SqlInstance> sqlInstances = new ArrayList<SqlInstance>();
		for(int i = 0; array != null && i < array.size();++i) {
			SqlInstance sqlInstance = new SqlInstance();
			sqlInstance.fromJson(array.getJSONObject(i));
			sqlInstances.add(sqlInstance);
		}
		return new ListSqlInstanceResponse(resHeaders, sqlInstances);
	}

	@Override
	public CreateMetricAggRulesResponse createMetricAggRules(CreateMetricAggRulesRequest request) throws LogException {
		MetricAggRules metricAggRules = request.getMetricAggRules();
		CodingUtils.assertParameterNotNull(metricAggRules, "metricAggRules");
		ETLV2 etl = metricAggRules.createScheduledETL(metricAggRules);
		JobSchedule jobSchedule = new JobSchedule();
		jobSchedule.setType(JobScheduleType.RESIDENT);
		etl.setSchedule(jobSchedule);
		CreateETLV2Response createETLV2Response = createETLV2(new CreateETLV2Request(request.GetProject(), etl));
		return new CreateMetricAggRulesResponse(createETLV2Response.GetAllHeaders());
	}

	@Override
	public ListMetricAggRulesResponse listMetricAggRules(ListMetricAggRulesRequest request) throws LogException {
		ListETLV2Response listETLV2Response = listETLV2(request);
		List<ETLV2> etls = listETLV2Response.getResults();
		List<MetricAggRules> metricAggRulesList = new ArrayList<MetricAggRules>();
		for(ETLV2 etl : etls){
			ETLConfiguration configuration = etl.getConfiguration();
			if(configuration!=null){
				Map<String, String> parameters = configuration.getParameters();
				if(parameters.containsKey("config.ml.scheduled_sql")
						&& parameters.get("config.ml.scheduled_sql") != null
						&& !parameters.get("config.ml.scheduled_sql").isEmpty()){
					MetricAggRules metricAggRules = new MetricAggRules();
					metricAggRules.deserialize(etl);
					metricAggRulesList.add(metricAggRules);
				}
			}
		}
		ListMetricAggRulesResponse listResp = new ListMetricAggRulesResponse(listETLV2Response.GetAllHeaders(), metricAggRulesList.size());
		listResp.setMetricAggRules(metricAggRulesList);
		return listResp;
	}

	@Override
	public GetMetricAggRulesResponse getMetricAggRules(GetMetricAggRulesRequest request) throws LogException {
		GetETLV2Response getETLV2Response = getETLV2(request);
		ETLV2 etl = getETLV2Response.getEtl();
		MetricAggRules metricAggRules = new MetricAggRules();
		metricAggRules.deserialize(etl);
		GetMetricAggRulesResponse getMetricAggRulesResponse = new GetMetricAggRulesResponse(getETLV2Response.GetAllHeaders());
		getMetricAggRulesResponse.setMetricAggRules(metricAggRules);
		return getMetricAggRulesResponse;
	}

	@Override
	public UpdateMetricAggRulesResponse updateMetricAggRules(UpdateMetricAggRulesRequest request) throws LogException {
		MetricAggRules metricAggRules = request.getMetricAggRules();
		CodingUtils.assertParameterNotNull(metricAggRules, "metricAggRules");
		ETLV2 etl = metricAggRules.createScheduledETL(metricAggRules);
		UpdateETLV2Response updateETLV2Response = updateETLV2(new UpdateETLV2Request(request.GetProject(), etl));
		return new UpdateMetricAggRulesResponse(updateETLV2Response.GetAllHeaders());
	}

	@Override
	public DeleteMetricAggRulesResponse deleteMetricAggRules(DeleteMetricAggRulesRequest request) throws LogException {
		DeleteETLV2Response deleteETLV2Response = deleteETLV2(request);
		return new DeleteMetricAggRulesResponse(deleteETLV2Response.GetAllHeaders());
	}

	@Override
	public SetProjectPolicyResponse setProjectPolicy(String projectName, String policyText) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(projectName, "project");
		CodingUtils.assertParameterNotNull(policyText, "policy");
		Map<String, String> headParameter = GetCommonHeadPara(projectName);
		String resourceUri = "/policy";
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(projectName, HttpMethod.POST,
				resourceUri, urlParameter, headParameter, policyText);
		Map<String, String> resHeaders = response.getHeaders();
		return new SetProjectPolicyResponse(resHeaders);
	}

	@Override
	public GetProjectPolicyReponse getProjectPolicy(String projectName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(projectName, "project");
		Map<String, String> headParameter = GetCommonHeadPara(projectName);
		String resourceUri = "/policy";
		Map<String, String> urlParameter = new HashMap<String, String>();
		ResponseMessage response = SendData(projectName, HttpMethod.GET,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new GetProjectPolicyReponse(resHeaders, response.GetStringBody());
	}

	@Override
	public DeleteProjectPolicyReponse deleteProjectPolicy(String projectName) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(projectName, "project");
		String resourceUri = "/policy";
		Map<String, String> urlParameter = new HashMap<String, String>();
		Map<String, String> headParameter = GetCommonHeadPara(projectName);
		ResponseMessage response = SendData(projectName, HttpMethod.DELETE,
				resourceUri, urlParameter, headParameter);
		Map<String, String> resHeaders = response.getHeaders();
		return new DeleteProjectPolicyReponse(resHeaders);
	}

	@Override
	public SetProjectCnameResponse setProjectCname(SetProjectCnameRequest request) throws LogException {
		CodingUtils.assertParameterNotNull(request, "request");
		Map<String, String> headParameter = GetCommonHeadPara(request.GetProject());
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(request.GetProject(), HttpMethod.POST,
				Consts.CONST_CNAME_URI, Collections.<String, String>emptyMap(), headParameter, request.marshal().toString());
		SetProjectCnameResponse addCnameResponse = new SetProjectCnameResponse(response.getHeaders());
		String requestId = GetRequestId(response.getHeaders());
		JSONObject object = parseResponseBody(response, requestId);
		addCnameResponse.setCertId(object.getString("certId"));
		return addCnameResponse;
	}

	@Override
	public ListProjectCnameResponse listProjectCname(String project) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(project, HttpMethod.GET,
				Consts.CONST_CNAME_URI, Collections.<String, String>emptyMap(), headParameter);
		ListProjectCnameResponse listCnameResponse = new ListProjectCnameResponse(response.getHeaders());
		String requestId = GetRequestId(response.getHeaders());
		JSONArray result = ParseResponseMessageToArray(response, requestId);
		listCnameResponse.unmarshal(result);
		return listCnameResponse;
	}

	@Override
	public VoidResponse deleteProjectCname(String project, String domain) throws LogException {
		CodingUtils.assertStringNotNullOrEmpty(project, "project");
		CodingUtils.assertStringNotNullOrEmpty(domain, "domain");
		Map<String, String> headParameter = GetCommonHeadPara(project);
		headParameter.put(Consts.CONST_CONTENT_TYPE, Consts.CONST_SLS_JSON);
		ResponseMessage response = SendData(project, HttpMethod.DELETE,
				Consts.CONST_CNAME_URI + "/" + domain, Collections.<String, String>emptyMap(), headParameter);
		return new VoidResponse(response.getHeaders());
	}
}
