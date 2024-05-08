package com.aliyun.openservices.log.util;

import com.github.luben.zstd.Zstd;

public class ZSTDEncoder {

    public static byte[] compress(byte[] data) {
        return Zstd.compress(data);
    }

    public static byte[] decompress(byte[] compressedData, int rawSize) {
        return Zstd.decompress(compressedData, rawSize);
    }
}
