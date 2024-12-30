package com.aliyun.openservices.log.sample;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.CsvColumn;
import com.aliyun.openservices.log.common.CsvExternalStore;
import com.aliyun.openservices.log.common.CsvColumn.CsvColumnType;
import com.aliyun.openservices.log.request.CreateCsvExternalStoreRequest;

public class CsvExternalStoreExample {

    private static final String endPoint = "";
	private static final String akId = "your_access_id";
	private static final String ak = "your_access_key";
	private static final Client client = new Client(endPoint, akId, ak);
	private static final String project = "your_project_name";
	private static final String logStore = "your_log_store";
    private static final String externalStoreName = "external_store_name_1";

    public static void main(String[] args) throws Exception {
        // read from file
        byte[] fileContent = readCsvFile("my.csv");
        // csv columns
        List<CsvColumn> columns = Arrays.asList(new CsvColumn("Name", CsvColumnType.VARCHAR),
                new CsvColumn("age", CsvColumnType.BIGINT),
                new CsvColumn("Score", CsvColumnType.DOUBLE));

        client.createCsvExternalStore(new CreateCsvExternalStoreRequest(project, new CsvExternalStore(
                externalStoreName, fileContent, columns)));

        // then you can query external store using query
        String query = "* | select * from " + externalStoreName;
        int from = (int) (System.currentTimeMillis() / 1000) - 1800;
        client.GetLogs(project, logStore, from, from + 1800, "", query);
    }

    private static byte[] readCsvFile(String csvFilePath) throws Exception {
        return Files.readAllBytes(Paths.get(csvFilePath));
    }
}
