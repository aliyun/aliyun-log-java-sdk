package com.aliyun.openservices.log;

import com.aliyun.openservices.log.http.utils.CodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceUtils {


    public static InputStream getTestInputStream(String resourceName)
            throws FileNotFoundException {
        assert (!CodingUtils.isNullOrEmpty(resourceName));

        return new FileInputStream(getTestFilename(resourceName));
    }

    public static String getTestFilename(String resourceName) {
        assert (!CodingUtils.isNullOrEmpty(resourceName));

        return ResourceUtils.class.getClassLoader().getResource(resourceName).getFile();
    }

}
