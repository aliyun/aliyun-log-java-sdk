package com.aliyun.openservices.log.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

public class GzipUtils {

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        Deflater compressor = new Deflater();
        try {
            compressor.setInput(data);
            compressor.finish();
            byte[] buf = new byte[10240];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                out.write(buf, 0, count);
            }
            return out.toByteArray();
        } finally {
            compressor.end();
        }
    }
}
