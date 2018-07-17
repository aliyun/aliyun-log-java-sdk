package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.VarintUtil;


public class FastLogGroupMeta {
    private byte[] rawBytes;
    private int beginOffset;
    private int endOffset;
    private int clientIPOffset = -1;
    private int clientIPLength = -1;
    private String clientIP;
    private byte[] clientIPBytes;
    private int receiveTime;

    public FastLogGroupMeta(byte[] rawBytes, int offset, int length) {
        this.rawBytes = rawBytes;
        this.beginOffset = offset;
        this.endOffset = offset + length;
        if (!parse()) {
            clientIPOffset = -1;
            receiveTime = -1;
        }
    }

    private boolean parse() {
        int pos = this.beginOffset;
        int mode, index;
        while (pos < this.endOffset) {
            int value[] = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
            if (value[0] == 0) {
                return false;
            }
            pos = value[2];
            mode = value[1] & 0x7;
            index = value[1] >> 3;

            if (index == 1 && mode == 0) {
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                receiveTime = value[1];
                pos = value[2];
            } else if (mode == 2 && index == 2) {
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                clientIPOffset = value[2];
                clientIPLength = value[1];
                pos = value[2] + value[1];
            } else {
                return false;
            }
        }
        return pos == this.endOffset;
    }

    public String getClientIP() {
        if (clientIP == null && hasClientIP()) {
            clientIP = new String(rawBytes, clientIPOffset, clientIPLength);
        }
        return clientIP;
    }

    public byte[] getClientIPBytes() {
        if (clientIPBytes == null && hasClientIP()) {
            clientIPBytes = new byte[clientIPLength];
            System.arraycopy(this.rawBytes, clientIPOffset, clientIPBytes, 0, clientIPLength);
        }
        return clientIPBytes;
    }

    public boolean hasClientIP() {
        return clientIPOffset >= 0;
    }

    public int getReceiveTime() {
        return receiveTime;
    }

    public boolean hasReceiveTime() {
        return receiveTime >= 0;
    }
}
