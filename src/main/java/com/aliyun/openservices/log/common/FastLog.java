package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.VarintUtil;

import java.util.ArrayList;

public class FastLog {

    private byte[] rawBytes;
    // [beginOffset, endOffset)
    private int beginOffset;
    private int endOffset;
    private int time = -1;
    private ArrayList<FastLogContent> contents;

    public FastLog(byte[] rawBytes, int offset, int length) {

        this.rawBytes = rawBytes;
        this.beginOffset = offset;
        this.endOffset = offset + length;
        this.contents = new ArrayList<FastLogContent>();
        if (!parse()) {
            this.contents.clear();
        }
    }

    private boolean parse() {
        int pos = this.beginOffset;
        int mode, index;
        boolean findTime = false;
        while (pos < this.endOffset) {
            int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
            if (value[0] == 0) {
                return false;
            }
            mode = value[1] & 0x7;
            index = value[1] >> 3;
            if (mode == 0) {
                pos = value[2];
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2];
                if (index == 1) {
                    this.time = value[1];
                    findTime = true;
                }
            } else if (mode == 1) {
                pos = value[2] + 8;
            } else if (mode == 2) {
                pos = value[2];
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2] + value[1];
                if (index == 2) {
                    this.contents.add(new FastLogContent(this.rawBytes, value[2], value[1]));
                }
            } else if (mode == 5) {
                pos = value[2] + 4;
            } else {
                return false;
            }
        }
        return findTime && (pos == this.endOffset);
    }

    public int getTime() {
        return this.time;
    }

    public int getContentsCount() {
        return this.contents.size();
    }

    public FastLogContent getContents(int i) {
        if (i < this.contents.size()) {
            return this.contents.get(i);
        } else {
            return null;
        }
    }
}
