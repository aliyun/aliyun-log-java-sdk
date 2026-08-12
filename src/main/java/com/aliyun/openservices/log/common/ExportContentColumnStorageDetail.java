package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.ArrayList;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExportContentColumnStorageDetail extends ExportContentDetail {

    private ArrayList<ExportContentStorageColumn> columns;

    public ArrayList<ExportContentStorageColumn> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<ExportContentStorageColumn> columns) {
        this.columns = columns;
    }

    public ExportContentColumnStorageDetail() {}

    public ExportContentColumnStorageDetail(ArrayList<ExportContentStorageColumn> columns) {
        this.columns = columns;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (columns != null) {
            JSONArray columnsArray = new JSONArray();
            for (ExportContentStorageColumn column : columns) {
                if (column == null) {
                    columnsArray.add((JSONObject) null);
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("name", column.getName());
                item.put("type", column.getType());
                columnsArray.add(item);
            }
            value.put("columns", columnsArray);
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        JSONArray columnsArray = value.getJSONArray("columns");
        columns = new ArrayList<ExportContentStorageColumn>();
        if (columnsArray != null) {
            for (int i=0; i < columnsArray.size(); i++) {
                JSONObject obj = columnsArray.getJSONObject(i);
                if (obj == null) {
                    continue;
                }
                columns.add(new ExportContentStorageColumn(
                        obj.getString("name"),
                        obj.getString("type")
                ));
            }
        }
    }
}
