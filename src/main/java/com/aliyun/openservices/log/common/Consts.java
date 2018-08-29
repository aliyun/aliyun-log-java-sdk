/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

public class Consts {
	public enum CompressType {
		NONE(""), LZ4(Consts.CONST_LZ4), GZIP(Consts.CONST_GZIP_ENCODING);
		
		private String strValue;
		
		private CompressType(String strValue) {
			this.strValue = strValue;
		}
		
		public String toString() {
			return strValue;
		}
	}
	
	public enum CursorMode {
		NONE(""), BEGIN("begin"), END("end");
		
		private String strValue;
		
		private CursorMode(String strValue) {
			this.strValue = strValue;
		}
		
		public String toString() {
			return strValue;
		}
	}
	
	public enum ACLPrivilege {
		READ("READ"), WRITE("WRITE"), LIST("LIST"), ADMIN("ADMIN");
		
		private String strValue;
		
		private ACLPrivilege(String strValue) {
			this.strValue = strValue;
		}
		
		public String toString() {
			return strValue;
		}
	}
	
	public enum ACLAction {
		GRANT("grant"), REVOKE("revoke");
		private String strValue;

		private ACLAction(String strValue) {
			this.strValue = strValue;
		}

		public String toString() {
			return strValue;
		}
	}
	

	public static final boolean DEFAULT_SLS_COMPRESS_FLAG = true;
	
	public static final boolean DEFAULT_SLS_REVERSE = false;
	public static final int DEFAULT_SLS_LINES = 20;
	public static final int DEFAULT_SLS_OFFSET = 0;
	public static final String DEFAULT_SLS_QUERY = "";
	public static final String DEFAULT_API_VESION = "0.6.0";
	
	public static final String DEFAULT_REQUEST_PARAM_GROUPNAME = "";
	public static final String DEFAULT_REQUEST_PARAM_CONFIGNAME = "";
	public static final String DEFAULT_REQUEST_PARAM_LOGSTORENAME = "";
	public static final String DEFAULT_REQUEST_PARAM_SAVEDSEARCHNAME = "";
	public static final String DEFAULT_REQUEST_PARAM_ALERTNAME = "";
	public static final int DEFAULT_REQUEST_PARAM_OFFSET = 0;
	public static final int DEFAULT_REQUEST_PARAM_SIZE = 500;


    public static final String CONST_HEADSIGNATURE_PREFIX = "LOG ";
	
	public static final String CONST_ACCESS_ID = "AccessKeyId";
	public static final String CONST_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String CONST_GZIP_ENCODING = "deflate";
	
	public static final String CONST_CONTENT_TYPE = "Content-Type";
	public static final String CONST_PROTO_BUF = "application/x-protobuf";
	public static final String CONST_CONTENT_LENGTH = "Content-Length";
	public static final String CONST_CONTENT_MD5 = "Content-MD5";
	public static final String CONST_AUTHORIZATION = "Authorization";
	public static final String CONST_SLS_JSON = "application/json";
	
	public static final String CONST_X_SLS_APIVERSION = "x-log-apiversion";
	public static final String CONST_X_SLS_COMPRESSTYPE= "x-log-compresstype";
	public static final String CONST_X_SLS_BODYRAWSIZE= "x-log-bodyrawsize";
	public static final String CONST_X_SLS_SIGNATUREMETHOD= "x-log-signaturemethod";
	public static final String CONST_X_SLS_REQUESTID = "x-log-requestid" ; 
	public static final String CONST_X_SLS_HOSTIP = "x-log-hostip" ; 
	public static final String CONST_X_SLS_IP = "x-log-ip";
	public static final String CONST_X_SLS_SSL = "x-log-ssl";
	public static final String CONST_X_SLS_PREFIX= "x-log-";
	public static final String CONST_X_ACS_PREFIX= "x-acs-";
	public static final String CONST_X_SLS_CURSOR = "x-log-cursor";
	public static final String CONST_X_SLS_COUNT = "x-log-count";
	public static final String CONST_X_SLS_PROCESS = "x-log-progress";
	public static final String CONST_X_SLS_NEXT_TOKEN = "x-log-nexttoken";
	public static final String CONST_X_SLS_CONTENTENCODING = "x-log-contentencoding";
	public static final String CONST_X_ACS_SECURITY_TOKEN = "x-acs-security-token";

	public static final String CONST_X_LOG_AGGQUERY = "x-log-agg-query";
	public static final String CONST_X_LOG_WHEREQUERY = "x-log-where-query";
	public static final String CONST_X_LOG_HASSQL = "x-log-has-sql";
	public static final String CONST_X_LOG_PROCESSEDROWS = "x-log-processed-rows";
	public static final String CONST_X_LOG_ELAPSEDMILLISECOND = "x-log-elapsed-millisecond";
	public static final String CONST_X_LOG_QUERY_INFO = "x-log-query-info";
		
	public static final String CONST_HOST = "Host";
	public static final String CONST_DATE = "Date";
	
	public static final String CONST_USER_AGENT = "User-Agent";
	public static final String CONST_USER_AGENT_VALUE = "sls-java-sdk-v-0.6.1";
	public static final String CONST_TOPIC = "topic";
	public static final String CONST_FROM = "from";
	public static final String CONST_TO = "to";
	public static final String CONST_LINE = "line";
	public static final String CONST_OFFSET = "offset";
	public static final String CONST_REVERSE = "reverse";
	public static final String CONST_QUERY = "query";
	public static final String CONST_TOKEN = "token";
	public static final String CONST_CURSOR = "cursor";
	public static final String CONST_END_CURSOR = "end_cursor";
	public static final String CONST_COUNT = "count";
	public static final String CONST_CONFIGNAME = "configName";
	public static final String CONST_GROUPNAME = "groupName";
	public static final String CONST_PRINCIPLEID= "principleId";
	public static final String CONST_LOGSTORE_NAME= "logstoreName";
	public static final String CONST_SPLIT_MID_HASH="key";
	public static final String CONST_ACTION="action";
	public static final String CONST_ACTION_SPLIT="split";
	public static final String CONST_ACTION_MERGE="merge";
	public static final String CONST_ROUTE_KEY="key";
	
	// logtail config related
	public static final String CONST_CONFIG_INPUTTYPE_FILE = "file";
	public static final String CONST_CONFIG_INPUTTYPE_STREAMLOG = "streamlog";
	public static final String CONST_CONFIG_INPUTTYPE_PLUGIN = "plugin";
	public static final String CONST_CONFIG_INPUTTYPE_SYSLOG = "syslog";
	public static final String CONST_CONFIG_LOGTYPE = "logType";
	public static final String CONST_CONFIG_LOGTYPE_JSON = "json_log";
	public static final String CONST_CONFIG_LOGTYPE_APSARA = "apsara_log";
	public static final String CONST_CONFIG_LOGTYPE_COMMON = "common_reg_log";
	public static final String CONST_CONFIG_LOGTYPE_DELIMITER = "delimiter_log";
	public static final String CONST_CONFIG_DEFAULT_TOPICFORMAT = "none";
	// logtail config input detail
	public static final String CONST_CONFIG_INPUTDETAIL_TAG = "tag";
	public static final String CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE = "localStorage";
 	public static final String CONST_CONFIG_INPUTDETAIL_FILEPATTERN = "filePattern";
	public static final String CONST_CONFIG_INPUTDETAIL_LOGPATH = "logPath";
	public static final String CONST_CONFIG_INPUTDETAIL_LOGTYPE = "logType";
	public static final String CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX = "logBeginRegex";
	public static final String CONST_CONFIG_INPUTDETAIL_REGEX = "regex";
	public static final String CONST_CONFIG_INPUTDETAIL_KEY = "key";
	public static final String CONST_CONFIG_INPUTDETAIL_TIMEKEY = "timeKey";
	public static final String CONST_CONFIG_INPUTDETAIL_TIMEFORMAT = "timeFormat";
	public static final String CONST_CONFIG_INPUTDETAIL_FILTERREGEX = "filterRegex";
	public static final String CONST_CONFIG_INPUTDETAIL_FILTERKEY = "filterKey";
	public static final String CONST_CONFIG_INPUTDETAIL_TOPICFORMAT = "topicFormat";
	public static final String CONST_CONFIG_INPUTDETAIL_PRESERVE = "preserve";
	public static final String CONST_CONFIG_INPUTDETAIL_PRESERVERDEPTH = "preserveDepth";
	public static final String CONST_CONFIG_INPUTDETAIL_MAXDEPTH = "maxDepth";
	public static final int CONST_CONFIG_INPUTDETAUL_DEFAULTMAXDEPTH = 1000;
	public static final String CONST_CONFIG_INPUTDETAIL_FILEENCODING = "fileEncoding";
	public static final String CONST_CONFIG_INPUTDETAIL_FILEENCODING_UTF8 = "utf8";
	public static final String CONST_CONFIG_INPUTDETAIL_FILEENCODING_GBK = "gbk";
	public static final String CONST_CONFIG_INPUTDETAIL_SEPARATOR = "separator";
	public static final String CONST_CONFIG_INPUTDETAIL_QUOTE = "quote";
	public static final String CONST_CONFIG_INPUTDETAIL_AUTOEXTEND = "autoExtend";
	public static final String CONST_CONFIG_INPUTDETAIL_DISCARDUNMATCH = "discardUnmatch";
	public static final String CONST_CONFIG_INPUTDETAIL_ENABLETAG = "enableTag";
	public static final String CONST_CONFIG_INPUTDETAIL_ENABLERAWLOG = "enableRawLog";
	public static final String CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY = "shardHashKey";
	public static final String CONST_CONFIG_INPUTDETAIL_DISCARDNONUTF8 = "discardNonUtf8";
	public static final String CONST_CONFIG_INPUTDETAIL_TAILEXISTED = "tailExisted";
	public static final String CONST_CONFIG_INPUTDETAIL_ISDOCKERFILE = "dockerFile";
	public static final String CONST_CONFIG_INPUTDETAIL_DOCKER_INCLUDE_LABEL = "dockerIncludeLabel";
	public static final String CONST_CONFIG_INPUTDETAIL_DOCKER_EXCLUDE_LABEL = "dockerExcludeLabel";
	public static final String CONST_CONFIG_INPUTDETAIL_DOCKER_INCLUDE_ENV = "dockerIncludeEnv";
	public static final String CONST_CONFIG_INPUTDETAIL_DOCKER_EXCLUDE_ENV = "dockerExcludeEnv";
	public static final String CONST_CONFIG_INPUTDETAIL_MAXSENDRATE = "maxSendRate";
	public static final String CONST_CONFIG_INPUTDETAIL_SENDRATEEXPIRE = "sendRateExpire";
	public static final String CONST_CONFIG_INPUTDETAIL_MERGETYPE = "mergeType";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS = "sensitive_keys";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_TYPE = "type";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_KEY = "key";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXBEGIN = "regex_begin";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXCONTENT = "regex_content";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_ALL = "all";
	public static final String CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_CONST = "const";
	public static final String CONST_CONFIG_INPUTDETAIL_DELAYALARMBYTES = "delayAlarmBytes";
	public static final String CONST_CONFIG_INPUTDETAIL_ADJUSTTIMEZONE = "adjustTimezone";
	public static final String CONST_CONFIG_INPUTDETAIL_LOGTIMEZONE = "logTimezone";
	public static final String CONST_CONFIG_INPUTDETAIL_PRIORITY = "priority";
	public static final String CONST_CONFIG_INPUTDETAIL_DELAYSKIPBYTES = "delaySkipBytes";
	public static final String CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS = "customizedFields";
	public static final String CONST_CONFIG_INPUTDETAIL_ACCEPTNOENOUGHKEYS = "acceptNoEnoughKeys";
	
	public static final String CONST_TYPE = "type";
	public static final String CONST_TYPE_CURSOR = "cursor";
	public static final String CONST_TYPE_CURSOR_TIME = "cursor_time";
	public static final String CONST_TYPE_HISTOGRAM = "histogram";
	public static final String CONST_TYPE_LOG = "log";
	public static final String CONST_TYPE_TOPIC = "topic";
	
	public static final String CONST_RESULT_LOGS = "logs";
	public static final String CONST_RESULT_LOG_STORES = "logstores";
	public static final String CONST_RESULT_HISTOGRAMS = "histograms";
	public static final String CONST_RESULT_PROCESS = "progress";
	public static final String CONST_RESULT_COUNT = "count";
	public static final String CONST_RESULT_COMPLETE = "Complete";
	public static final String CONST_RESULT_INCOMPLETE = "Incomplete";

	public static final String CONST_RESULT_TOPICS = "topics";
	public static final String CONST_RESULT_TIME = "__time__";
	public static final String CONST_RESULT_SOURCE = "__source__";
	
	
	
	public static final String CONST_ERROR_CODE = "errorCode";
	public static final String CONST_ERROR_MESSAGE = "errorMessage";
	
	public static final String CONST_MD5 = "MD5";
	public static final String UTF_8_ENCODING = "UTF-8";
	public static final String HMAC_SHA1 = "hmac-sha1";
	public static final String HMAC_SHA1_JAVA = "HmacSHA1";
	
	public static final String CONST_LOCAL_IP= "127.0.0.1";
	public static int CONST_MAX_PUT_SIZE = 50 * 1024 * 1024;
	public static int CONST_MAX_PUT_LINES = 40960;
	
	public static final int CONST_UN_AUTHORIZATION_CODE = 401;
	public static final int CONST_HTTP_OK = 200;
	public static int HTTP_CONNECT_MAX_COUNT= 1000;
	public static int HTTP_CONNECT_TIME_OUT = 5 * 1000;
	public static int HTTP_SEND_TIME_OUT = 60 * 1000;
	
	public static final String CONST_GZIP = "gzip";
	public static final String CONST_LZ4 = "lz4";
	public static final String CONST_HTTP_ACCEPT = "accept";
	
	// common 
	public static final String CONST_CREATTIME = "createTime";
	public static final String CONST_LASTMODIFYTIME = "lastModifyTime";
	public static final String CONST_TOTAL= "total";
	public static final String CONST_SIZE= "size";

	// project relate
	public static final String CONST_PROJECTNAME = "projectName";
	public static final String CONST_PROJECTSTATUS = "status";
	public static final String CONST_PROJECTOWNER = "owner";
	public static final String CONST_PROJECTDESC = "description";
	public static final String CONST_PROJECTREGION = "region";
	
	// savedsearch api
	public static final String CONST_SAVEDSEARCH_URI = "/savedsearches";
	public static final String CONST_SAVEDSEARCH_NAME = "savedsearchName";
	public static final String CONST_SAVEDSEARCH_DISPLAYNAME = "displayName";
	public static final String CONST_SAVEDSEARCH_QUERY = "searchQuery";
	public static final String CONST_SAVEDSEARCH_LOGSTORE = "logstore";
	public static final String CONST_SAVEDSEARCH_TOPIC = "topic";
	
	// alert api
	public static final String CONST_ALERT_URI = "/alerts";
	public static final String CONST_ALERT_NAME = "alertName";
	public static final String CONST_ALERT_DISPLAYNAME = "displayName";
	public static final String CONST_ALERT_SAVEDSEARCHNAME = "savedsearchName";
	public static final String CONST_ALERT_ROLEARN = "roleArn";
	public static final String CONST_ALERT_FROM = "from";
	public static final String CONST_ALERT_TO = "to";
	public static final String CONST_ALERT_CHECKINTERVAL = "checkInterval";
	public static final String CONST_ALERT_COUNT = "count";
	public static final String CONST_ALERT_DETAIL = "alertDetail";
	public static final String CONST_ALERT_KEY = "alertKey";
	public static final String CONST_ALERT_VALUE = "alertValue";
	public static final String CONST_ALERT_COMPARATOR = "comparator";
	public static final String CONST_ALERT_MESSAGE = "alertMessage";
	public static final String CONST_ALERT_ACTIONTYPE = "actionType";
	public static final String CONST_ALERT_ACTIONTYPE_SMS = "sms";
	public static final String CONST_ALERT_ACTIONTYPE_MNS = "mns";
	public static final String CONST_ALERT_ACTIONTYPE_WEBHOOK = "webhook";
	public static final String CONST_ALERT_ACTIONTYPE_DINGTALK = "dingtalk";
	public static final String CONST_ALERT_ACTIONTYPE_NOTIFICATION = "notification";
	public static final String CONST_ALERT_ACTIONDETAIL = "actionDetail";
	public static final String CONST_ALERT_ACTIONDETAIL_PHONENUMBER = "phoneNumber";
	public static final String CONST_ALERT_ACTIONDETAIL_MNS_PARAM = "param";
	public static final String CONST_ALERT_ACTIONDETAIL_MESSAGE = "message";
	public static final String CONST_ALERT_ACTIONDETAIL_WEBHOOK = "webhook";
	
	public static final String CONST_ALERTTIME = "alertTime";
	public static final String CONST_TRIGGERSTATUS = "triggerStatus";
	public static final String CONST_TRIGGERMESSAGE = "triggerMessage";
	public static final String CONST_ACTIONSTATUS = "actionStatus";
	public static final String CONST_ACTIONMESSAGE = "actionMessage";
	
	// logtail profile
	public static final String CONST_GETLOGTAILPROFILE_URI = "/logtailprofile";
	public static final String CONST_GETLOGTAILPROFILE_SOURCE = "source";
	
	// index type
	public static final String CONST_INDEX_TEXT = "text";
	public static final String CONST_INDEX_LONG = "long";
	public static final String CONST_INDEX_DOUBLE = "double";
	
	// oss shipper
	public static final String CONST_OSSSHIPPER_STORAGEFORMAT = "format";

	// ETL
	public static final String CONST_ETLJOB_URI = "/etljobs";
	public static final String FUNCTION_PROVIDER_FC = "FunctionCompute";
	public static final String FUNCTION_PROVIDER_LOG_DISPATCH = "CloudProdLogDispatch";
	public static final String ETL_JOB_NAME = "etlJobName";
	public static final String ETL_JOB_SOURCE_CONFIG = "sourceConfig";
	public static final String ETL_JOB_TRIGGER_CONFIG = "triggerConfig";
	public static final String ETL_JOB_TRIGGER_INTERVAL = "triggerInterval";
	public static final String ETL_JOB_TRIGGER_MAX_RETRY_TIME = "maxRetryTime";
	public static final String ETL_JOB_TRIGGER_ROLEARN = "roleArn";
	public static final String ETL_JOB_FUNCTION_CONFIG = "functionConfig";
	public static final String ETL_JOB_FUNCTION_PROVIDER = "functionProvider";
	public static final String ETL_JOB_FUNCTION_PARAMETER = "functionParameter";
	public static final String ETL_JOB_FC_ENDPOINT = "endpoint";
	public static final String ETL_JOB_FC_ACCOUNT_ID = "accountId";
	public static final String ETL_JOB_FC_REGION_NAME = "regionName";
	public static final String ETL_JOB_FC_SERVICE_NAME = "serviceName";
	public static final String ETL_JOB_FC_FUNCTION_NAME = "functionName";
	public static final String ETL_JOB_LOG_ENDPOINT = "endpoint";
	public static final String ETL_JOB_LOG_PROJECT_NAME = "projectName";
	public static final String ETL_JOB_LOG_LOGSTORE_NAME = "logstoreName";
	public static final String ETL_JOB_LOG_CONFIG = "logConfig";
	public static final String ETL_ENABLE = "enable";

	public static final String CONST_ETLMETA_URI = "/etlmetas";
	public static final String CONST_ETLMETANAME_URI = "/etlmetanames";
	public static final String CONST_ETLMETA_ALL_TAG_MATCH = "__all_etl_meta_tag_match__";
	public static final String ETL_META_NAME = "etlMetaName";
	public static final String ETL_META_KEY = "etlMetaKey";
	public static final String ETL_META_TAG = "etlMetaTag";
	public static final String ETL_META_VALUE = "etlMetaValue";
	public static final String ETL_META_BATCH_DELETE_RANGE = "batchDeleteRange";
	public static final String ETL_META_BATCH_DELETE_RANGE_ALL = "all";
	public static final String ETL_META_BATCH_DELETE_RANGE_LIST = "list";
	public static final String ETL_META_KEY_LIST = "etlMetaKeyList";
	public static final String ETL_META_LIST = "etlMetaList";
	public static final String ETL_META_CREATE_TIME = "createTime";
	public static final String ETL_META_LAST_MODIFY_TIME = "lastModifyTime";

	public static final String LOGGING_URI = "/logging";
}
