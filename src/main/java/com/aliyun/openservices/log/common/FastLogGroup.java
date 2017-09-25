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
    private int categoryOffset; //contain two varint to support getBytes of FastLogGroup
    private int topicOffset;
    private int sourceOffset;
    private int machineUUIDOffset;
    private byte[] logGroupBytes = null;

    public FastLogGroup(byte[] rawBytes, int offset, int length) {
        this.rawBytes = rawBytes;
        this.beginOffset = offset;
        this.endOffset = offset + length;
        this.categoryOffset = -1;
        this.topicOffset = -1;
        this.sourceOffset = -1;
        this.machineUUIDOffset = -1;
        this.logs = new ArrayList<FastLog>();
        this.tags = new ArrayList<FastLogTag>();
        if (!parse()) {
            this.logs.clear();
            this.tags.clear();
            this.categoryOffset = -1;
            this.topicOffset = -1;
            this.sourceOffset = -1;
            this.machineUUIDOffset = -1;
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
                        this.categoryOffset = pos;
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
                    default:
                }
                pos = value[2];
                value = VarintUtil.DecodeVarInt32(this.rawBytes, pos, this.endOffset);
                if (value[0] == 0) {
                    return false;
                }
                pos = value[2] + value[1];
                if (index == 1) {
                    this.logs.add(new FastLog(this.rawBytes, value[2], value[1]));
                } else if (index == 6) {
                    this.tags.add(new FastLogTag(this.rawBytes, value[2], value[1]));
                }
            } else if (mode == 5) {
                pos = value[2] + 4;
            } else {
                return false;
            }
        }
        return (pos == this.endOffset);
    }

    public byte[] getBytes() {
        if (this.logGroupBytes == null) {
            int length = this.endOffset - this.beginOffset;
            if (this.categoryOffset < 0) {
                this.logGroupBytes = new byte[length];
                System.arraycopy(this.rawBytes, this.beginOffset, this.logGroupBytes, 0, length);
            } else {
                // remove category field for logGroup transmission
                int prefixLength = this.categoryOffset - this.beginOffset;
                int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.categoryOffset, this.endOffset);
                if (value[0] == 0) {
                    return null;
                }
                int mode = value[1] & 0x7;
                int index = value[1] >> 3;
                if (mode != 2 && index != 2) {
                    return null;
                }
                value = VarintUtil.DecodeVarInt32(this.rawBytes, value[2], this.endOffset);
                if (value[0] == 0) {
                    return null;
                }
                int postfixLength = this.endOffset - value[2] - value[1];
                logGroupBytes = new byte[prefixLength + postfixLength];
                System.arraycopy(this.rawBytes, this.beginOffset, logGroupBytes, 0, prefixLength);
                System.arraycopy(this.rawBytes, value[1] + value[2], logGroupBytes, prefixLength, postfixLength);
            }
        }
        return this.logGroupBytes;
    }

    public String getCategory() {
        if (this.categoryOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.categoryOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        int mode = value[1] & 0x7;
        int index = value[1] >> 3;
        if (mode != 2 && index != 2) {
            return null;
        }
        value = VarintUtil.DecodeVarInt32(this.rawBytes, value[2], this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        return new String(this.rawBytes, value[2], value[1]);
    }

    public String getTopic() {
        if (this.topicOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.topicOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        return new String(this.rawBytes, value[2], value[1]);
    }

    public String getSource() {
        if (this.sourceOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.sourceOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        return new String(this.rawBytes, value[2], value[1]);
    }

    public String getMachineUUID() {
        if (this.machineUUIDOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.machineUUIDOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        return new String(this.rawBytes, value[2], value[1]);
    }

    public byte[] getCategoryBytes() {
        if (this.categoryOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.categoryOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        int mode = value[1] & 0x7;
        int index = value[1] >> 3;
        if (mode != 2 && index != 2) {
            return null;
        }
        value = VarintUtil.DecodeVarInt32(this.rawBytes, value[2], this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        byte[] categoryBytes = new byte[value[1]];
        System.arraycopy(this.rawBytes, value[2], categoryBytes, 0, value[1]);
        return categoryBytes;
    }

    public byte[] getTopicBytes() {
        if (this.topicOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.topicOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        byte[] topicBytes = new byte[value[1]];
        System.arraycopy(this.rawBytes, value[2], topicBytes, 0, value[1]);
        return topicBytes;
    }

    public byte[] getSourceBytes() {
        if (this.sourceOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.sourceOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        byte[] sourceBytes = new byte[value[1]];
        System.arraycopy(this.rawBytes, value[2], sourceBytes, 0, value[1]);
        return sourceBytes;
    }

    public byte[] getMachineUUIDBytes() {
        if (this.machineUUIDOffset < 0) {
            return null;
        }
        int[] value = VarintUtil.DecodeVarInt32(this.rawBytes, this.machineUUIDOffset, this.endOffset);
        if (value[0] == 0) {
            return null;
        }
        byte[] machineUUIDBytes = new byte[value[1]];
        System.arraycopy(this.rawBytes, value[2], machineUUIDBytes, 0, value[1]);
        return machineUUIDBytes;
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
}
