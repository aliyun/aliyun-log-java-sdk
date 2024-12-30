package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.Objects;

public class CsvColumn implements Serializable {
    public static final long serialVersionUID = 1382671890031L;
    private String name;
    private String type; // bigint varchar double

    public static enum CsvColumnType {
        BIGINT("bigint"), VARCHAR("varchar"), DOUBLE("double");

        private String strValue;

        CsvColumnType(String strValue) {
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }
    }

    public CsvColumn(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public CsvColumn(String name, CsvColumnType type) {
        this.name = name;
        this.type = type.strValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CsvColumn [name=" + name + ", type=" + type + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        CsvColumn other = (CsvColumn) obj;
        return name.equals(other.name) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
