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

/**
 * CSV external store
 * <p>
 * To get a {@code CsvExternalStore} object, use
 * {@code CsvExternalStore.CreateCsvExternalStore}.
 */
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
    public static CsvExternalStore CreateCsvExternalStore(String externalStoreName, String csvFilePath,
            List<CsvColumn> columns) throws LogException {

        List<String> objects = Arrays.asList(csvFilePath);
        String csvFilePathTrimmed = csvFilePath;
        if (csvFilePath.startsWith("file://")) {
            csvFilePathTrimmed = csvFilePath.substring(7);
        }

        // encode to gzip compressed base64
        byte[] fileContent = readCsvFile(csvFilePathTrimmed);
        byte[] compressed = GzipUtils.compress(fileContent);
        if (compressed.length > MAX_FILE_SIZE_COMPRESSED) {
            throw new LogException("InvalidExternalStoreCsvConfig",
                    "The compressed csv file is too large, max size is" + MAX_FILE_SIZE_COMPRESSED, "");
        }
        String fileContentBase64 = new String(Base64.encodeBase64(compressed));

        Parameter parameter = new Parameter(fileContentBase64, fileContent.length, objects, columns);
        return new CsvExternalStore(externalStoreName, STORE_TYPE_CSV, parameter);
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

    private CsvExternalStore(String externalStoreName, String storeType, Parameter parameter) {
        super(externalStoreName, storeType, parameter);
    }

    private static byte[] readCsvFile(String csvFilePath) throws LogException {
        if (csvFilePath == null || csvFilePath.isEmpty()) {
            throw new LogException("InvalidExternalStoreCsvConfig", "The csv file path is empty", "");
        }
        Path path = Paths.get(csvFilePath);
        if (!Files.exists(path)) {
            throw new LogException("InvalidExternalStoreCsvConfig", "The csv file path is not exist", "");
        }
        try {
            if (Files.size(path) > MAX_FILE_SIZE) {
                throw new LogException("InvalidExternalStoreCsvConfig",
                        "The csv file is too large, max size is " + MAX_FILE_SIZE,
                        "");
            }
        } catch (IOException e) {
            throw new LogException("InvalidExternalStoreCsvConfig", "Read csv file size error", e, "");
        }

        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new LogException("InvalidExternalStoreCsvConfig", "Read csv file rror", e, "");
        }

        if (bytes.length > MAX_FILE_SIZE) {
            throw new LogException("InvalidExternalStoreCsvConfig",
                    "The csv file is too large, max size is " + MAX_FILE_SIZE,
                    "");
        }
        return bytes;
    }
}
