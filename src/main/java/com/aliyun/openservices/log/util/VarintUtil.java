package com.aliyun.openservices.log.util;

public class VarintUtil {

    public static int[] DecodeVarInt32(byte[] dataBytes, int pos, int maxPos) {
        int value[] = {0, 0, 0};
        int shift = 0;
        int b;
        for (int i = pos; i < maxPos; ++i) {
            b = dataBytes[i] & 0xff;
            value[1] |= (b & 127) << shift;
            shift += 7;
            if ((b & 128) == 0) {
                value[2] = i + 1;
                value[0] = 1;
                break;
            }
        }
        return value;
    }
}
