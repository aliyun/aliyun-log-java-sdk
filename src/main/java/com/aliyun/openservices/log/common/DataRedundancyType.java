package com.aliyun.openservices.log.common;

/**
 * Data redundancy type
 */
public enum DataRedundancyType {
    /**
     * LRS
     */
    LRS("LRS"),

    /**
     * ZRS
     */
    ZRS("ZRS"),

    /**
     * Unknown
     */
    Unknown("Unknown");

    private final String dataRedundancyTypeString;

    DataRedundancyType(String dataRedundancyTypeString) {
        this.dataRedundancyTypeString = dataRedundancyTypeString;
    }

    @Override
    public String toString() {
        return dataRedundancyTypeString;
    }

    /**
     * Returns the DataRedundancyType enum corresponding to the given string
     *
     * @param dataRedundancyTypeString
     *            data redundancy type.
     *
     * @return  The {@link DataRedundancyType} instance.
     *
     * @throws IllegalArgumentException if the specified dataRedundancyTypeString is not
     * supported.
     */
    public static DataRedundancyType parse(String dataRedundancyTypeString) {
        for (DataRedundancyType e: DataRedundancyType.values()) {
            if (e.toString().equals(dataRedundancyTypeString))
                return e;
        }
        return Unknown;
    }
}