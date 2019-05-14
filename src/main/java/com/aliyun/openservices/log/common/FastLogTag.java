package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.VarintUtil;

public class FastLogTag {

    private byte[] rawBytes;
    // [beginOffset, endOffset)
    private int beginOffset;
    private int endOffset;
    private int keyOffset = -1;
    private int keyLength = -1;
    private int valueOffset = -1;
    private int valueLength = -1;

    public FastLogTag(byte[] rawBytes, int offset, int length) {
        this.rawBytes = rawBytes;
        this.beginOffset = offset;
        this.endOffset = offset + length;
        if (!parse()) {
            this.keyOffset = -1;
            this.keyLength = -1;
            this.valueOffset = -1;
            this.valueLength = -1;
        }
    }

    private boolean parse() {
        int pos = this.beginOffset;
        int mode, index;
        while (pos < this.endOffset) {
            int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
            if (value[0] == 0) {
                return false;
            }
            pos = value[2];
            mode = value[1] & 0x7;
            index = value[1] >> 3;
            if (mode == 0) {
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2];
            } else if (mode == 1) {
                pos += 8;
            } else if (mode == 2) {
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2] + value[1];
                if (index == 1) {
                    keyOffset = value[2];
                    keyLength = value[1];
                } else if (index == 2) {
                    valueOffset = value[2];
                    valueLength = value[1];
                }
            } else if (mode == 5) {
                pos += 4;
            } else {
                return false;
            }
        }
        return (keyOffset != -1 && valueOffset != -1 && pos == this.endOffset);
    }

    public String getKey() {
        if (this.keyOffset < 0) {
            return null;
        }
        return new String(this.rawBytes, this.keyOffset, this.keyLength);
    }

    public String getValue() {
        if (this.valueOffset < 0) {
            return null;
        }
        return new String(this.rawBytes, this.valueOffset, this.valueLength);
    }


    public byte[] getKeyBytes() {
        if (this.keyOffset < 0) {
            return null;
        }
        byte[] keyBytes = new byte[this.keyLength];
        System.arraycopy(this.rawBytes, this.keyOffset, keyBytes, 0, this.keyLength);
        return keyBytes;
    }

    public byte[] getValueBytes() {
        if (this.valueOffset < 0) {
            return null;
        }
        byte[] valueBytes = new byte[this.valueLength];
        System.arraycopy(this.rawBytes, this.valueOffset, valueBytes, 0, this.valueLength);
        return valueBytes;
    }
}
