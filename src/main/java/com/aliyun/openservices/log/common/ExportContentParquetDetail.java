package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class ExportContentParquetDetail extends ExportContentDetail {

    private ArrayList<ExportContentStorageColumn> columns;

    public ArrayList<ExportContentStorageColumn> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<ExportContentStorageColumn> columns) {
        this.columns = columns;
    }

    public ExportContentParquetDetail() {}

    public ExportContentParquetDetail(ArrayList<ExportContentStorageColumn> columns) {
        this.columns = columns;
    }

    @Override
    public void deserialize(JSONObject value) {
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
