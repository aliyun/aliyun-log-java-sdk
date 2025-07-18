/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

public class Consts {
    public enum CompressType {
        NONE(""),
        LZ4(Consts.CONST_LZ4),
        GZIP(Consts.CONST_GZIP_ENCODING),
        ZSTD(Consts.CONST_ZSTD);
        private final String strValue;

        CompressType(String strValue) {
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }

        public static CompressType fromString(final String compressType) {
            for (CompressType type : values()) {
                if (type.strValue.equals(compressType)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("invalid CompressType: " + compressType + ", should be (" + CompressType.NONE + ", " + CompressType.GZIP + ", " + CompressType.LZ4 + ")");
        }
    }

    public enum CursorMode {
        NONE(""), BEGIN("begin"), END("end");

        private String strValue;

        CursorMode(String strValue) {
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }
    }

    public enum BatchModifyEtlMetaType {
        BATCH_ENABLE_ETL_META("batch_enable"),
        BATCH_DISABLE_ETL_META("batch_disable"),
        BATCH_DELETE_ETL_META("batch_delete");

        private String strValue;

        BatchModifyEtlMetaType(String strValue) {
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }
    }

    public static final String ACTION = "action";
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
    public static final String CONST_BATCH_GROUP = "batch_group";

    public static final String CONST_X_SLS_APIVERSION = "x-log-apiversion";
    public static final String CONST_X_SLS_COMPRESSTYPE = "x-log-compresstype";
    public static final String CONST_X_SLS_BODYRAWSIZE = "x-log-bodyrawsize";
    public static final String CONST_X_SLS_RAWDATACOUNT = "x-log-rawdatacount";
    public static final String CONST_X_SLS_RAWDATASIZE = "x-log-rawdatasize";
    public static final String CONST_X_LOG_CURSOR_TIME = "x-log-cursor-time";
    public static final String CONST_X_LOG_END_OF_CURSOR = "x-log-end-of-cursor";
    public static final String CONST_X_SLS_SIGNATUREMETHOD = "x-log-signaturemethod";
    public static final String CONST_X_SLS_REQUESTID = "x-log-requestid";
    public static final String CONST_X_SLS_HOSTIP = "x-log-hostip";
    public static final String CONST_X_LOG_MODE = "x-log-mode";
    public static final String CONST_X_SLS_IP = "x-log-ip";
    public static final String CONST_X_SLS_SSL = "x-log-ssl";
    public static final String CONST_X_SLS_PREFIX = "x-log-";
    public static final String CONST_X_ACS_PREFIX = "x-acs-";
    public static final String CONST_X_SLS_CURSOR = "x-log-cursor";
    public static final String CONST_X_SLS_COUNT = "x-log-count";
    public static final String CONST_X_SLS_PROCESS = "x-log-progress";
    public static final String CONST_X_SLS_SCANBYTES = "x-log-scanbytes";
    public static final String CONST_X_SLS_NEXT_TOKEN = "x-log-nexttoken";
    public static final String CONST_X_ACS_SECURITY_TOKEN = "x-acs-security-token";
    public static final String CONST_X_LOG_RESOURCEOWNERACCOUNT = "x-log-resourceowneraccount";
    public static final String X_LOG_DATE = "x-log-date";
    public static final String X_LOG_CONTENT_SHA256 = "x-log-content-sha256";
    public static final String EMPTY_STRING_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    public static final String EMPTY_STRING_LENGTH = "0";
    public static final String EMPTY_STRING = "";

    public static final String CONST_X_LOG_AGGQUERY = "x-log-agg-query";
    public static final String CONST_X_LOG_WHEREQUERY = "x-log-where-query";
    public static final String CONST_X_LOG_HASSQL = "x-log-has-sql";
    public static final String CONST_X_LOG_PROCESSEDROWS = "x-log-processed-rows";
    public static final String CONST_X_LOG_ELAPSEDMILLISECOND = "x-log-elapsed-millisecond";
    public static final String CONST_X_LOG_QUERY_INFO = "x-log-query-info";
    public static final String CONST_X_LOG_CPU_SEC = "x-log-cpu-sec";
    public static final String CONST_X_LOG_CPU_CORES = "x-log-cpu-cores";

    public static final String CONST_HOST = "Host";
    public static final String CONST_DATE = "Date";

    public static final String CONST_USER_AGENT = "User-Agent";
    public static final String CONST_TOPIC = "topic";
    public static final String CONST_FROM = "from";
    public static final String CONST_TO = "to";
    public static final String CONST_LINE = "line";
    public static final String CONST_OFFSET = "offset";
    public static final String CONST_REVERSE = "reverse";
    public static final String CONST_POWER_SQL = "powerSql";
    public static final String CONST_FORWARD = "forward";
    public static final String CONST_SHARD = "shard";
    public static final String CONST_SESSION = "session";
    public static final String CONST_ACCURATE = "accurate";
    public static final String CONST_HIGHLIGHT = "highlight";
    public static final String CONST_FROM_NS_PART = "fromNs";
    public static final String CONST_TO_NS_PART = "toNs";
    public static final String CONST_QUERY = "query";
    public static final String CONST_PROCESSOR = "processor";
    public static final String CONST_TOKEN = "token";
    public static final String CONST_CURSOR = "cursor";
    public static final String CONST_END_CURSOR = "end_cursor";
    public static final String CONST_COUNT = "count";
    public static final String CONST_CONFIGNAME = "configName";
    public static final String CONST_GROUPNAME = "groupName";
    public static final String CONST_PRINCIPLEID = "principleId";
    public static final String CONST_LOGSTORE_NAME = "logstoreName";
    public static final String CONST_TETEMETRY_TYPE = "telemetryType";
    public static final String CONST_LOGSTORE_MODE = "mode";
    public static final String CONST_SPLIT_MID_HASH = "key";
    public static final String CONST_SPLIT_SHARDCOUNT = "shardCount";
    public static final String CONST_ACTION = "action";
    public static final String CONST_ACTION_SPLIT = "split";
    public static final String CONST_ACTION_MERGE = "merge";
    public static final String CONST_ROUTE_KEY = "key";

    // context logs
    public static final String CONST_PACK_META = "pack_meta";
    public static final String CONST_PACK_ID = "pack_id";
    public static final String CONST_TOTAL_LINES = "total_lines";
    public static final String CONST_BACK_LINES = "back_lines";
    public static final String CONST_FORWARD_LINES = "forward_lines";

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
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED = "advanced";
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
    public static final String CONST_CONFIG_INPUTDETAIL_PLUGINDETAIL = "plugin";
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED_FORCEMULTICONFIG = "force_multiconfig";
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST = "blacklist";
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_DIR = "dir_blacklist";
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILENAME = "filename_blacklist";
    public static final String CONST_CONFIG_INPUTDETAIL_ADVANCED_BLACKLIST_FILEPATH = "filepath_blacklist";

    public static final String CONST_TYPE = "type";
    public static final String CONST_TYPE_CURSOR = "cursor";
    public static final String CONST_TYPE_CURSOR_TIME = "cursor_time";
    public static final String CONST_TYPE_HISTOGRAM = "histogram";
    public static final String CONST_TYPE_LOG = "log";
    public static final String CONST_TYPE_CONTEXT_LOG = "context_log";
    public static final String CONST_TYPE_TOPIC = "topic";
    public static final String CONST_TTL = "ttl";

    public static final String CONST_RESULT_LOGS = "logs";
    public static final String CONST_RESULT_LOG_STORES = "logstores";
    public static final String CONST_RESULT_SUB_STORES = "substores";
    public static final String CONST_RESULT_EXTERNAL_STORES = "externalstores";
    public static final String CONST_EXTERNAL_NAME = "externalStoreName";
    public static final String CONST_RESULT_HISTOGRAMS = "histograms";
    public static final String CONST_RESULT_PROCESS = "progress";
    public static final String CONST_RESULT_COUNT = "count";
    public static final String CONST_RESULT_COMPLETE = "Complete";
    public static final String CONST_RESULT_INCOMPLETE = "Incomplete";

    public static final String CONST_RESULT_TOPICS = "topics";
    public static final String CONST_RESULT_TIME = "__time__";
    public static final String CONST_RESULT_TIME_NS_PART = "__time_ns_part__";
    public static final String CONST_RESULT_SOURCE = "__source__";

    public static final String CONST_ERROR_CODE = "errorCode";
    public static final String CONST_ERROR_MESSAGE = "errorMessage";

    public static final String CONST_MD5 = "MD5";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String HMAC_SHA1 = "hmac-sha1";
    public static final String HMAC_SHA1_JAVA = "HmacSHA1";

    public static final String CONST_LOCAL_IP = "127.0.0.1";
    public static int CONST_MAX_PUT_SIZE = 50 * 1024 * 1024;
    public static int CONST_MAX_POST_BODY_SIZE = 10 * 1024 * 1024;

    public static final int CONST_HTTP_OK = 200;
    public static int HTTP_CONNECT_MAX_COUNT = 1000;
    public static int HTTP_CONNECT_TIME_OUT = 5 * 1000;
    public static int HTTP_SEND_TIME_OUT = 60 * 1000;

    public static final String CONST_GZIP = "gzip";
    public static final String CONST_LZ4 = "lz4";

    public static final String CONST_ZSTD = "zstd";
    public static final String CONST_HTTP_ACCEPT = "accept";

    // common
    public static final String CONST_CREATTIME = "createTime";
    public static final String CONST_LASTMODIFYTIME = "lastModifyTime";
    public static final String CONST_TOTAL = "total";
    public static final String CONST_SIZE = "size";
    public static final String CONST_NEXT_TOKEN = "nextToken";
    public static final String CONST_MAX_RESULTS = "maxResults";
    public static final String RESULTS = "results";

    // project relate
    public static final String CONST_PROJECTNAME = "projectName";
    public static final String CONST_PROJECTSTATUS = "status";
    public static final String CONST_PROJECTOWNER = "owner";
    public static final String CONST_PROJECTDESC = "description";
    public static final String CONST_PROJECTREGION = "region";
    public static final String CONST_RESOURCEGROUPID = "resourceGroupId";
    public static final String CONST_QUOTA = "quota";

    // domain
    public static final String CONST_DOMAIN_URI = "/domains";
    public static final String CONST_DOMAIN_NAME = "domainName";

    // savedsearch api
    public static final String CONST_SAVEDSEARCH_URI = "/savedsearches";
    public static final String CONST_SAVEDSEARCH_NAME = "savedsearchName";
    public static final String CONST_DISPLAY_NAME = "displayName";
    public static final String CONST_SAVEDSEARCH_DISPLAYNAME = "displayName";
    public static final String CONST_SAVEDSEARCH_QUERY = "searchQuery";
    public static final String CONST_SAVEDSEARCH_LOGSTORE = "logstore";
    public static final String CONST_SAVEDSEARCH_TOPIC = "topic";

    // logtail profile
    public static final String CONST_GETLOGTAILPROFILE_URI = "/logtailprofile";
    public static final String CONST_GETLOGTAILPROFILE_SOURCE = "source";

    // index type
    public static final String CONST_INDEX_TEXT = "text";
    public static final String CONST_INDEX_LONG = "long";
    public static final String CONST_INDEX_DOUBLE = "double";

    // oss shipper
    public static final String CONST_OSSSHIPPER_STORAGEFORMAT = "format";

    public static final String ENABLE = "enable";
    public static final String DISABLE = "disable";
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
    public static final String ETL_JOB_TRIGGER_STARTING_POSITION = "startingPosition";
    public static final String ETL_JOB_TRIGGER_STARTING_UNIXTIME = "startingUnixtime";
    public static final String ETL_JOB_TRIGGER_STARTING_POSITION_LATEST = "latest";
    public static final String ETL_JOB_TRIGGER_STARTING_POSITION_AT_UNIXTIME = "at-unixtime";
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
    public static final String ETL_META_BATCH_MODIFY_STATUS_RANGE = "range";
    public static final String ETL_META_BATCH_MODIFY_STATUS_RANGE_ALL = "all";
    public static final String ETL_META_BATCH_MODIFY_STATUS_RANGE_LIST = "list";
    public static final String ETL_META_KEY_LIST = "etlMetaKeyList";
    public static final String ETL_META_LIST = "etlMetaList";
    public static final String ETL_META_CREATE_TIME = "createTime";
    public static final String ETL_META_LAST_MODIFY_TIME = "lastModifyTime";
    public static final String ETL_META_ENABLE = "enable";
    public static final String EMAIL_LIST = "emailList";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_TYPE = "jobType";
    public static final String JOB_URI = "/jobs";
    public static final String DISPLAY_NAME = "displayName";

    public static final String CONST_RESOURCE_URI = "/resources";
    public static final String CONST_RESOURCE_NAME_URI = CONST_RESOURCE_URI + "/%s";
    public static final String RESOURCE_NAME = "name";
    public static final String RESOURCE_NAMES = "names";
    public static final String RESOURCE_TYPE = "type";
    public static final String RESOURCE_SCHEMA = "schema";
    public static final String RESOURCE_DESCRIPTION = "description";
    public static final String RESOURCE_EXT_INFO = "extInfo";
    public static final String RESOURCE_ACL = "acl";
    public static final String RESOURCE_CREATE_TIME = "createTime";
    public static final String RESOURCE_LAST_MODIFY_TIME = "lastModifyTime";

    public static final String CONST_MIGRATION_URI = "/shippermigrations";
    public static final String CONST_MIGRATION_NAME_URI = CONST_MIGRATION_URI + "/%s";

    public static final String CONST_RESOURCE_RECORD_URI = CONST_RESOURCE_NAME_URI + "/records";
    public static final String CONST_NEXT_RESOURCE_RECORD_URI = CONST_RESOURCE_NAME_URI + "/next_records";
    public static final String CONST_RESOURCE_RECORD_ID_URI = CONST_RESOURCE_RECORD_URI + "/%s";
    public static final String RESOURCE_RECORD_ID = "id";
    public static final String RESOURCE_RECORD_IDS = "ids";
    public static final String RESOURCE_RECORD_TAG = "tag";
    public static final String RESOURCE_RECORD_VALUE = "value";
    public static final String RESOURCE_RECORDS = "records";
    public static final String RESOURCE_OWNER = "owner";
    public static final String RESOURCE_SEARCHED_JSON = "sjson";
    public static final String RESOURCE_SEARCHED_VALUE = "search";
    public static final String RESOURCE_JSON_PATH = "jsonPath";
    public static final String RESOURCE_JSON_PATH_VALUE = "jsonPathValue";
    public static final String RESOURCE_REVERSE = "reverse";
    public static final String RESOURCE_SYSTEM_RECORDS = "includeSystemRecords";

    public static final String TOPOSTORE_URI = "/topostores";
    public static final String TOPOSTORE_OWNER = "owner";
    public static final String TOPOSTORE_NAME = "name";
    public static final String TOPOSTORE_NAME_LIST = "names";
    public static final String TOPOSTORE_TAG = "tag";
    public static final String TOPOSTORE_TAG_KEY = "tagKey";
    public static final String TOPOSTORE_SCHEMA = "schema";
    public static final String TOPOSTORE_EXTINFO = "extInfo";
    public static final String TOPOSTORE_ACL = "acl";
    public static final String TOPOSTORE_TAG_VALUE = "tagValue";
    public static final String TOPOSTORE_TAGS = "tags";
    public static final String TOPOSTORE_LIST_OFFSET = "offset";
    public static final String TOPOSTORE_LIST_LIMIT = "limit";
    public static final String TOPOSTORE_DESCRIPTION = "description";
    public static final String TOPOSTORE_CREATE_TIME = "createTime";
    public static final String TOPOSTORE_LAST_MODIFY_TIME = "lastModifyTime";

    public static final String TOPOSTORE_NODE_ID = "nodeId";
    public static final String TOPOSTORE_NODE_ID_LIST = "nodeIds";
    public static final String TOPOSTORE_NODE_TYPE = "nodeType";
    public static final String TOPOSTORE_NODE_TYPE_LIST = "nodeTypes";
    public static final String TOPOSTORE_NODE_PROPERTY = "property";
    public static final String TOPOSTORE_NODE_PROPERTY_KEY = "propertyKey";
    public static final String TOPOSTORE_NODE_PROPERTY_VALUE = "propertyValue";
    public static final String TOPOSTORE_NODE_PROPERTIES = "properties";
    public static final String TOPOSTORE_NODE_DESCRIPTION = "description";
    public static final String TOPOSTORE_NODE_DISPLAY_NAME = "displayName";
    public static final String TOPOSTORE_NODE_CREATE_TIME = "createTime";
    public static final String TOPOSTORE_NODE_LAST_MODIFY_TIME = "lastModifyTime";
    public static final String TOPOSTORE_NODE_COUNT = "count";
    public static final String TOPOSTORE_NODE_TOTAL = "total";
    public static final String TOPOSTORE_NODE_ITEMS = "items";

    public static final String TOPOSTORE_RELATION_ID = "relationId";
    public static final String TOPOSTORE_RELATION_ID_LIST = "relationIds";
    public static final String TOPOSTORE_RELATION_SRC_NODE_ID = "srcNodeId";
    public static final String TOPOSTORE_RELATION_SRC_NODE_ID_LIST = "srcNodeIds";
    public static final String TOPOSTORE_RELATION_DST_NODE_ID = "dstNodeId";
    public static final String TOPOSTORE_RELATION_DST_NODE_ID_LIST = "dstNodeIds";
    public static final String TOPOSTORE_RELATION_TYPE = "relationType";
    public static final String TOPOSTORE_RELATION_TYPE_LIST = "relationTypes";
    public static final String TOPOSTORE_RELATION_PROPERTY = "property";
    public static final String TOPOSTORE_RELATION_PROPERTY_KEY = "propertyKey";
    public static final String TOPOSTORE_RELATION_PROPERTIES = "properties";
    public static final String TOPOSTORE_RELATION_DESCRIPTION = "description";
    public static final String TOPOSTORE_RELATION_DISPLAY_NAME = "displayName";
    public static final String TOPOSTORE_RELATION_PROPERTY_VALUE = "propertyValue";
    public static final String TOPOSTORE_RELATION_CREATE_TIME = "createTime";
    public static final String TOPOSTORE_RELATION_LAST_MODIFY_TIME = "lastModifyTime";
    public static final String TOPOSTORE_RELATION_COUNT = "count";
    public static final String TOPOSTORE_RELATION_TOTAL = "total";
    public static final String TOPOSTORE_RELATION_ITEMS = "items";
    public static final String TOPOSTORE_RELATION_DIRECTION_IN = "in";
    public static final String TOPOSTORE_RELATION_DIRECTION_OUT = "out";
    public static final String TOPOSTORE_RELATION_DIRECTION_BOTH = "both";

    public static final String ERROR_CODE_TOPOSTORE_NOT_EXIST = "TopostoreNotExist";
    public static final String ERROR_CODE_TOPOSTORE_NODE_NOT_EXIST = "TopostoreNodeNotExist";
    public static final String ERROR_CODE_TOPOSTORE_RELATION_NOT_EXIST = "TopostoreRelationNotExist";
    public static final String ERROR_CODE_TOPOSTORE_ALREADY_EXIST = "TopostoreAlreadyExist";
    public static final String ERROR_CODE_TOPOSTORE_NODE_ALREADY_EXIST = "TopostoreNodeAlreadyExist";
    public static final String ERROR_CODE_TOPOSTORE_RELATION_ALREADY_EXIST = "TopostoreRelationAlreadyExist";
    public static final String ERROR_CODE_TOPOSTORE_QUOTA_EXCEED = "TopostoreQuotaExceed";
    public static final String ERROR_CODE_TOPOSTORE_NODE_QUOTA_EXCEED = "TopostoreNodeQuotaExceed";
    public static final String ERROR_CODE_TOPOSTORE_RELATION_QUOTA_EXCEED = "TopostoreRelationQuotaExceed";
    public static final String ERROR_CODE_REGION_TOPOSTORE_IS_READONLY = "RegionTopostoreIsReadOnly";

    public static final String LOGGING_URI = "/logging";
    public static final String METHOD = "method";
    public static final String MOBILE_LIST = "mobileList";
    public static final String RESOURCE_PROVIDER = "resourceProvider";
    static final String SERVICE_URI = "serviceUri";
    static final String SUBJECT = "subject";
    static final String TITLE = "title";
    static final String AT_MOBILES = "atMobiles";
    static final String HEADERS = "headers";

    public static final String STOP = "STOP";
    public static final String START = "START";
    public static final String RESTART = "RESTART";
    public static final String DASHBOARD_NAME_KEY = "dashboardName";
    public static final String LOGSTORE_KEY = "logstore";
    public static final String JOB_SCHEDULE_URI = "/jobschedules";

    // Scheduled SQL
    public static final String JOB_INSTANCES_URI = "/jobinstances";
    public static final String JOB_INSTANCES_RESULT = "result";
    public static final String JOB_INSTANCES_STATE = "state";
    public static final String JOB_INSTANCES_START_TIME = "start";
    public static final String JOB_INSTANCES_END_TIME = "end";
    public static final String SCHEDULED_SQL_RUNNING = "RUNNING";

    public static final String INVALID_LOG_TIME = "InvalidLogTime";
    public static final String CONST_CNAME_URI = "/cname";

    // data replication
    public static final String CONST_LOGSTORE_REPLICATION_URI = "replication";
    public static final String CONST_LOGSTORE_REPLICATION = "enableReplication";

    // EventStore
    public static final String EVENT_STORE_TELEMETRY_TYPE = "Event";
    public static final String EVENT_STORE_INDEX = "{\"max_text_len\":16384,\"ttl\":7,\"log_reduce\":false,\"line\":{\"caseSensitive\":false,\"chn\":true,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"keys\":{\"specversion\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"id\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"source\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\"\\n\",\"\\t\",\"\\r\"]},\"type\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"subject\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"datacontenttype\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"dataschema\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"data\":{\"type\":\"json\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"],\"index_all\":true,\"max_depth\":-1,\"json_keys\":{}},\"time\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"title\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"message\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]},\"status\":{\"type\":\"text\",\"doc_value\":true,\"alias\":\"\",\"caseSensitive\":false,\"chn\":false,\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}}}";

    public static final String CHARGE_BY_DATA_INGEST = "ChargeByDataIngest";
    public static final String CHARGE_BY_FUNCTION = "ChargeByFunction";
}
