package com.aliyun.openservices.log.functiontest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.aliyun.openservices.log.common.CsvColumn;
import com.aliyun.openservices.log.common.CsvExternalStore;
import com.aliyun.openservices.log.common.CsvColumn.CsvColumnType;
import com.aliyun.openservices.log.request.CreateCsvExternalStoreRequest;
import com.aliyun.openservices.log.request.DeleteExternalStoreRequest;
import com.aliyun.openservices.log.request.GetCsvExternalStoreRequest;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.request.UpdateCsvExternalStoreRequest;

public class CsvExternalStoreTest extends BaseDataTest {

    List<CsvColumn> csvColumns = Arrays.asList(
            new CsvColumn("Name", CsvColumnType.VARCHAR),
            new CsvColumn("Age", CsvColumnType.BIGINT),
            new CsvColumn("City", CsvColumnType.VARCHAR));

    byte[] prepareCsvFile() throws Exception {
        String result = "";

        result += "Name,Age,City,score\n";
        result += "John Doe,30,New York,99.5\n";
        result += "Jane Smith,25,London,88.5\n";
        result += "Bob Johnson,35,Paris,59\n";
        return result.getBytes();
    }

    @Test
    public void testCurd() throws Exception {
        String externalStoreName = "test_external";
        byte[] tempFile = prepareCsvFile();

        CsvExternalStore store = new CsvExternalStore(externalStoreName, tempFile, csvColumns);
        client.createCsvExternalStore(new CreateCsvExternalStoreRequest(project, store));

        CsvExternalStore resp = client.getCsvExternalStore(new GetCsvExternalStoreRequest(project, externalStoreName))
                .getCsvExternalStore();
        assertEquals(csvColumns.size(), resp.getColumns().size());
        for (int i = 0; i < csvColumns.size(); i++) {
            assertEquals(csvColumns.get(i), resp.getColumns().get(i));
        }
        enableIndex();
        String query = "* | select * from " + externalStoreName;
        client.GetLogs(
                new GetLogsRequest(project, logStore.GetLogStoreName(), timestamp - 1800, timestamp + 1800, "", query));

        client.updateCsvExternalStore(new UpdateCsvExternalStoreRequest(project, store));
        client.deleteCsvExternalStore(new DeleteExternalStoreRequest(project, externalStoreName));
    }
}
