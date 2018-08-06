package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.VarintUtil;

import java.util.ArrayList;

public class FastLogGroup {

    private byte[] rawBytes;
    // [beginOffset, endOffset)
    private int beginOffset;
    private int endOffset;
    private ArrayList<FastLog> logs;
    private ArrayList<FastLogTag> tags;
    private FastLogGroupMeta meta;
    private int categoryOffset;
    private int categoryMessageOffset = -1;
    private int categoryMessageEnd = -1;
    private int topicOffset;
    private int sourceOffset;
    private int machineUUIDOffset;
    private int metaOffset;
    private int metaMessageOffset = -1;
    private int metaMessageEnd = -1;
    private byte[] logGroupBytes = null;
    private byte[] logGroupBytesWithoutMeta = null;


    public FastLogGroup(byte[] rawBytes, int offset, int length) {
        this.rawBytes = rawBytes;
        this.beginOffset = offset;
        this.endOffset = offset + length;
        this.categoryOffset = -1;
        this.topicOffset = -1;
        this.sourceOffset = -1;
        this.machineUUIDOffset = -1;
        this.metaOffset = -1;
        this.logs = new ArrayList<FastLog>();
        this.tags = new ArrayList<FastLogTag>();
        if (!parse()) {
            this.logs.clear();
            this.tags.clear();
            this.categoryOffset = -1;
            this.categoryMessageOffset = -1;
            this.topicOffset = -1;
            this.sourceOffset = -1;
            this.machineUUIDOffset = -1;
            this.metaOffset = -1;
            this.metaMessageOffset = -1;
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
            mode = value[1] & 0x7;
            index = value[1] >> 3;
            if (mode == 0) {
                pos = value[2];
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2];
            } else if (mode == 1) {
                pos = value[2] + 8;
            } else if (mode == 2) {
                switch (index) {
                    case 1:
                        //logs
                        break;
                    case 2:
                        categoryMessageOffset = pos;
                        categoryOffset = value[2];
                        break;
                    case 3:
                        this.topicOffset = value[2];
                        break;
                    case 4:
                        this.sourceOffset = value[2];
                        break;
                    case 5:
                        this.machineUUIDOffset = value[2];
                        break;
                    case 6:
                        //tags
                        break;
                    case 7:
                        metaMessageOffset = pos;
                        metaOffset = value[2];
                        break;
                    default:
                        break;
                }
                pos = value[2];
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2] + value[1];
                if (index == 1) {
                    this.logs.add(new FastLog(this.rawBytes, value[2], value[1]));
                } else if (index == 2) {
                    categoryMessageEnd = pos;
                } else if (index == 6) {
                    this.tags.add(new FastLogTag(this.rawBytes, value[2], value[1]));
                } else if (index == 7) {
                    this.meta = new FastLogGroupMeta(this.rawBytes, value[2], value[1]);
                    this.metaMessageEnd = pos;
                }
            } else if (mode == 5) {
                pos = value[2] + 4;
            } else {
                return false;
            }
        }
        return (pos == this.endOffset);
    }

    public byte[] getBytesWithoutMeta() {
        if (logGroupBytesWithoutMeta == null) {
            logGroupBytesWithoutMeta = excludeCategoryAndMeta();
        }
        return logGroupBytesWithoutMeta;
    }

    private byte[] excludeCategoryAndMeta() {
        byte[] logGroupBytes;
        int length = this.endOffset - this.beginOffset;
        if (categoryMessageOffset >= 0 && metaMessageOffset >= 0) {
            length -= (metaMessageEnd - metaMessageOffset) + (categoryMessageEnd - categoryMessageOffset);
            logGroupBytes = new byte[length];
            // copy [beginOffset, s1), [s2,s3), [s4, endOffset) to logGroupBytes
            int s1 = Math.min(metaMessageOffset, categoryMessageOffset);
            int s4 = Math.max(metaMessageEnd, categoryMessageEnd);
            int s2, s3;
            if (metaMessageOffset < categoryMessageOffset) {
                s2 = metaMessageEnd;
                s3 = categoryMessageOffset;
            } else {
                s2 = categoryMessageEnd;
                s3 = metaMessageOffset;
            }
            System.arraycopy(this.rawBytes, beginOffset, logGroupBytes, 0, s1 - beginOffset);
            int len = s1 - beginOffset;
            System.arraycopy(this.rawBytes, s2, logGroupBytes, len, s3 - s2);
            len += s3 - s2;
            System.arraycopy(this.rawBytes, s4, this.logGroupBytes, len, endOffset - s4);
        } else if (categoryMessageOffset >= 0) {
            length -= categoryMessageEnd - categoryMessageOffset;
            logGroupBytes = new byte[length];
            final int prefixSize = categoryMessageOffset - beginOffset;
            System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, prefixSize);
            System.arraycopy(this.rawBytes, categoryMessageEnd, logGroupBytes, prefixSize, endOffset - categoryMessageEnd);
        } else if (metaMessageOffset >= 0) {
            length -= metaMessageEnd - metaMessageOffset;
            logGroupBytes = new byte[length];
            final int prefixSize = metaMessageOffset - beginOffset;
            System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, prefixSize);
            System.arraycopy(this.rawBytes, metaMessageEnd, logGroupBytes, prefixSize, endOffset - metaMessageEnd);
        } else {
            // No category and meta found.
            logGroupBytes = new byte[length];
            System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, length);
        }
        return logGroupBytes;
    }

    public byte[] getBytes() {
        if (logGroupBytes == null) {
            logGroupBytes = excludeCategory();
        }
        return logGroupBytes;
    }

    private byte[] excludeCategory() {
        byte[] logGroupBytes;
        int length = this.endOffset - this.beginOffset;
        if (categoryMessageOffset >= 0) {
            length -= categoryMessageEnd - categoryMessageOffset;
            logGroupBytes = new byte[length];
            final int prefixSize = categoryMessageOffset - beginOffset;
            System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, prefixSize);
            System.arraycopy(this.rawBytes, categoryMessageEnd, logGroupBytes, prefixSize, endOffset - categoryMessageEnd);
        } else {
            logGroupBytes = new byte[length];
            System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, length);
        }
        return logGroupBytes;
    }

    public String getCategory() {
        return decodeString(categoryOffset);
    }

    public String getTopic() {
        return decodeString(topicOffset);
    }

    public String getSource() {
        return decodeString(sourceOffset);
    }

    public String getMachineUUID() {
        return decodeString(machineUUIDOffset);
    }

    private String decodeString(int offset) {
        // Decode the length of string from offset and then extract
        // string based on the length and new offset.
        if (offset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, offset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        return new String(this.rawBytes, value[2], value[1]);
    }

    public byte[] getCategoryBytes() {
        return cutBytes(categoryOffset);
    }

    public byte[] getTopicBytes() {
        return cutBytes(topicOffset);
    }

    public byte[] getSourceBytes() {
        return cutBytes(sourceOffset);
    }

    public byte[] getMachineUUIDBytes() {
        return cutBytes(machineUUIDOffset);
    }

    private byte[] cutBytes(int offset) {
        // Decode the length of string and then copy bytes of string within
        // new offset and new offset + length.
        if (offset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, offset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        byte[] cutBytes = new byte[value[1]];
        System.arraycopy(this.rawBytes, value[2], cutBytes, 0, value[1]);
        return cutBytes;
    }

    public int getLogTagsCount() {
        return this.tags.size();
    }

    public FastLogTag getLogTags(int i) {
        if (i < this.tags.size()) {
            return this.tags.get(i);
        } else {
            return null;
        }
    }

    public int getLogsCount() {
        return this.logs.size();
    }

    public FastLog getLogs(int i) {
        if (i < this.logs.size()) {
            return this.logs.get(i);
        } else {
            return null;
        }
    }

    public FastLogGroupMeta getMeta() {
        return meta;
    }

    public boolean hasCategory() {
        return this.categoryOffset >= 0;
    }

    public boolean hasTopic() {
        return this.topicOffset >= 0;
    }

    public boolean hasSource() {
        return this.sourceOffset >= 0;
    }

    public boolean hasMachineUUID() {
        return this.machineUUIDOffset >= 0;
    }

    public boolean hasMeta() {
        return this.metaOffset >= 0;
    }
}
