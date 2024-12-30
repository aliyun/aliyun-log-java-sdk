package com.aliyun.openservices.log.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.GzipUtils;

public class CsvExternalStore extends ExternalStore {
    private static final long serialVersionUID = 6493204290917634292L;
    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;
    private static final long MAX_FILE_SIZE_COMPRESSED = 10L * 1024 * 1024 - 10 * 1024;
    private static final String STORE_TYPE_CSV = "csv";

    /**
     * @param externalStoreName external store name
     * @param csvFilePath       csv file path on the local file system, either
     *                          relative path or absolute path
     * @param columns           column names in the csv file, must be in the exact
     *                          same order as the csv file
     */
    public CsvExternalStore(String externalStoreName, byte[] csvFileContent,
            List<CsvColumn> columns) throws LogException {
        super(externalStoreName, STORE_TYPE_CSV, null);
        if (csvFileContent.length > MAX_FILE_SIZE) {
            throw new LogException("InvalidExternalStoreCsvConfig",
                    "The csv file content is too large, max size is " + MAX_FILE_SIZE,
                    "");
        }

        byte[] compressed = GzipUtils.compress(csvFileContent);
        if (compressed.length > MAX_FILE_SIZE_COMPRESSED) {
            throw new LogException("InvalidExternalStoreCsvConfig",
                    "The compressed csv file content is too large, max size is" + MAX_FILE_SIZE_COMPRESSED, "");
        }
        String fileContentBase64 = new String(Base64.encodeBase64(compressed));
        List<String> objects = Arrays.asList("table.csv");
        setParameter(new Parameter(fileContentBase64, csvFileContent.length, objects, columns));
    }

    public List<CsvColumn> getColumns() {
        return getParameter().getColumns();
    }

    public String getExternalStoreCsv() {
        return getParameter().getObjects().get(0);
    }

    public CsvExternalStore(JSONObject object) throws LogException {
        super(object);
    }
}
