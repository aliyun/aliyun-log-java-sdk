package com.aliyun.openservices.log.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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

    public static byte[] uncompress(byte[] data) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(data);
            final byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
            return bos.toByteArray();
        } finally {
            decompressor.end();
        }
    }
}
