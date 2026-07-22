package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.ArrayList;
import com.aliyun.openservices.log.annotation.InternalApi;

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
    @InternalApi
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
