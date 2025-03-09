package com.lucky.data_visual.enums;

public enum TableOperation {
    COL_NAME("colName", "数据列名称"),
    COL_TYPE("colType", "用户选择的数据类型"),
    IS_DROP_NA("isDropNa", "是否删除数据列中的空值"),
    FILL_TYPE("fillType", "填充空值的方式");

    private final String operation;
    private final String description;

    TableOperation(String operation, String description) {
        this.operation = operation;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getOperation() {
        return operation;
    }
}


