package com.aliyun.openservices.log.functiontest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    File prepareCsvFile() throws Exception {
        File tempFile = File.createTempFile("tempCSV", ".csv");
        tempFile.deleteOnExit();

        FileWriter fw = new FileWriter(tempFile);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Name,Age,City,score\n");
        bw.write("John Doe,30,New York,99.5\n");
        bw.write("Jane Smith,25,London,88.5\n");
        bw.write("Bob Johnson,35,Paris,59\n");
        bw.close();
        return tempFile;
    }

    @Test
    public void testCurd() throws Exception {
        String externalStoreName = "test_external";
        File tempFile = prepareCsvFile();

        String p = tempFile.getAbsolutePath();
        CsvExternalStore store = CsvExternalStore.CreateCsvExternalStore(externalStoreName, p, csvColumns);
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
