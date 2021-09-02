package com.aliyun.openservices.log.functiontest;

/**
 * SlsClientData defines the test data used by the unittest
 */
public class SlsClientTestData extends FunctionTest {

	public static final String TEST_GET_DATA =
			"{\"GetData\":{"
					+"\"Meta\":"
					+ "{\"Status\":\"OK/FAIL\", \"Lines\":200},"
					+"\"Data\":["
					+ "{\"__time__\":1347530401,\"key1\":\"value1\",\"key2\":\"value2\"},"
					+ "{\"__time__\":1347552000,\"key1\":\"value1\",\"key2\":\"value2\"}]}}";

	public static final String TEST_LOGS_1 = ("["
			+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'}]")
			.replace("'", "\"");
//	public static final String TEST_LOGS_2 = new String(
//			"{'progress':'Complete','count':2, 'logs':["
//					+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'},"
//					+ "{'__time__':200, '__source__':'20.20.20.20', 'key_3':'value_3', 'key_4':'value_4'}]}")
//			.replace("'", "\"");
	public static final String TEST_LOGS_2 = ("["
		+ "{'__time__':100, '__source__':'10.10.10.10', 'key_1':'value_1', 'key_2':'value_2'},"
		+ "{'__time__':200, '__source__':'20.20.20.20', 'key_3':'value_3', 'key_4':'value_4'}]")
			.replace("'", "\"");
	
	// TEST_LIST_TOPIC_HEX == '{"ListTopic":["Topic1", "Topic2"]}'.encode('hex') <python>
	public static final String TEST_LIST_TOPIC_HEX =
			"7b224c697374546f706963223a5b22546f70696331222c2022546f70696332225d7d";
//	public static final String TEST_HISTOGRAM_DATA_1 = new String(
//			"{'progress':'Incomplete','count':200,'histograms':"
//					+ "[{'from':100,'to':200,'count':100,'progress':'Complete'},"
//					+ "{'from':200,'to':300,'count':100,'progress':'Incomplete'}]}")
//			.replace("'", "\"");

//	public static final String TEST_HISTOGRAM_DATA_2 = new String(
//			"{'progress':'Complete','count':300,'histograms':"
//					+ "[{'from':100,'to':200,'count':100,'progress':'Complete'},"
//					+ "{'from':200,'to':300,'count':200,'progress':'Complete'}]}")
//			.replace("'", "\"");
	public static final String TEST_HISTOGRAM_DATA_2 = ("[{'from':100,'to':200,'count':100,'progress':'Complete'},"
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
