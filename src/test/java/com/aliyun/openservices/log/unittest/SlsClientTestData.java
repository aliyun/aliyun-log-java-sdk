package com.aliyun.openservices.log.unittest;

/**
 * SlsClientData defines the test data used by the unittest
 * 
 * @author bozhi.ch
 *
 */
public class SlsClientTestData {

	public static final String TEST_ACCESS_KEY = "";
	public static final String TEST_ACCESS_KEY_ID = "";

	public static final String TEST_CATEGORY = "mock%5Fcategory";
	public static final String TEST_TOPIC = "mock";

	public static final String TEST_DATE = "Tue%2C%2023%20Oct%202012%2017%3A41%3A01%20GMT";
	public static final int TEST_BEGIN_TIME = 1347530401;
	public static final int TEST_END_TIME = 1347552000;
	public static final boolean TEST_REVERSE = false;
	public static final int TEST_LINES = 10;
	public static final int TEST_OFFSET = 0;
	public static final String TEST_QUERY = "";

	public static final String[][] TEST_HEX2MD5 = {
		{"20","7215EE9C7D9DC229D2921A40E899EC5F"}, // " "
		{"6f6c735f73646b","6130B39CFD626F8A4D48BFB3E44DAA79"}, // "ols_sdk"
		{"6d6435","1BC29B36F623BA82AAF6724FD3B16718"}, // "md5"
		{"6b6c3338797353016b64220a0964736938","802EAEB3D5AC8CBE65934997C0B83E4B"}, // "kl38ys\123\001kd\"\n\tdsi8"
		{"6b6c3338797353016b64220a090864736938","04568016387F3BF053211F01CF55E52E"}, // "kl38ys\123\001kd\"\n\t\bdsi8"
		{"89", "2854272FEC044D0BDB16DE12CB62D07E"}, // "\211"
		{"6b6c333879735301896b64220a090864736938","F2A4958FB6C4CCB82B129512BBB98205"}, // "kl38ys\123\001\211kd\"\n\t\bdsi8"
		{"6b6c33387973530001896b64220a090864736938","344676963785D56724F5B8228F9B2B69"} // "kl38ys\123\000\001\211kd\"\n\t\bdsi8"
	};

	public static final String TEST_GET_DATA =
			"{\"GetData\":{"
					+"\"Meta\":"
					+ "{\"Status\":\"OK/FAIL\", \"Lines\":200},"
					+"\"Data\":["
					+ "{\"__time__\":1347530401,\"key1\":\"value1\",\"key2\":\"value2\"},"
					+ "{\"__time__\":1347552000,\"key1\":\"value1\",\"key2\":\"value2\"}]}}";
	
//	public static final String TEST_LOGS_1 = new String(
//			"{'progress':'Incomplete','count':1, 'logs':["
//					+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'}]}")
//			.replace("'", "\"");
	public static final String TEST_LOGS_1 = new String(
			"["
					+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'}]")
			.replace("'", "\"");
//	public static final String TEST_LOGS_2 = new String(
//			"{'progress':'Complete','count':2, 'logs':["
//					+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'},"
//					+ "{'__time__':200, '__source__':'20.20.20.20', 'key_3':'value_3', 'key_4':'value_4'}]}")
//			.replace("'", "\"");
	public static final String TEST_LOGS_2 = new String(
			"["
					+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'},"
					+ "{'__time__':200, '__source__':'20.20.20.20', 'key_3':'value_3', 'key_4':'value_4'}]")
			.replace("'", "\"");
	
	public static final String TEST_LIST_LOGSTORE ="{\"count\": 2, \"logstores\":[\"log_store_1\", \"log_store_2\"]}";
	// TEST_LIST_TOPIC_HEX == '{"ListTopic":["Topic1", "Topic2"]}'.encode('hex') <python>
	public static final String TEST_LIST_TOPIC_HEX =
			"7b224c697374546f706963223a5b22546f70696331222c2022546f70696332225d7d";
//	public static final String TEST_HISTOGRAM_DATA_1 = new String(
//			"{'progress':'Incomplete','count':200,'histograms':"
//					+ "[{'from':100,'to':200,'count':100,'progress':'Complete'},"
//					+ "{'from':200,'to':300,'count':100,'progress':'Incomplete'}]}")
//			.replace("'", "\"");
	public static final String TEST_HISTOGRAM_DATA_1 = new String(
		
					 "[{'from':100,'to':200,'count':100,'progress':'Complete'},"
					+ "{'from':200,'to':300,'count':100,'progress':'Incomplete'}]")
			.replace("'", "\"");
//	public static final String TEST_HISTOGRAM_DATA_2 = new String(
//			"{'progress':'Complete','count':300,'histograms':"
//					+ "[{'from':100,'to':200,'count':100,'progress':'Complete'},"
//					+ "{'from':200,'to':300,'count':200,'progress':'Complete'}]}")
//			.replace("'", "\"");
	public static final String TEST_HISTOGRAM_DATA_2 = new String(
			"[{'from':100,'to':200,'count':100,'progress':'Complete'},"
					+ "{'from':200,'to':300,'count':200,'progress':'Complete'}]")
			.replace("'", "\"");
	// wrong type for end_time
	public static final String TEST_ILL_GET_DATA_META =
			"{\"GetDataMeta\":["
					+ "{\"total\":100, \"more_data\":true},"
					+ "{\"begin_time\":1347530401, \"end_time\":\"end_time\", \"count\":10, \"more_data\":false}]}";
	public static final String TEST_ERROR = "{\"error_code\":\"SLSMissingParameter\","
			+ " \"error_message\":\"The request must contain the parameter Category\"}";
	
	public static final String TEST_RESPONSE_RAW_DATA = "{\"projectName\":\"testproject\","
			+ "\"projectDesc\":\"test project\",\"projectStatus\":\"Normal\","
			+ "\"quota\":{\"logStream\":101,\"shard\":100},"
			+ "\"createTime\":12343235,\"lastModifyTime\":12343234}";
	
	public static final String TEST_RESPONSE_RAW_ARRAY = "[{\"shardID\":0,\"shardStatus\":\"OK\"},"
			+ "{\"shardID\":1,\"shardStatus\":\"OK\"}]";
	
	public static final String TEST_STANDARD_ERROR = "{\"errorCode\":\"code\",\"errorMessage\":\"message\"}";
	public static final String TEST_INVALID_JSON = "{\"key\":\"invalid json\"}";
}
