package com.lucky.data_visual.enums;

public enum ColumnStrategy {
    SINGLE_COLUMN("singleColumn", "单列策略"),
    DOUBLE_COLUMN("doubleColumn", "双列策略"),
    MULTI_COLUMN("multiColumn", "多列策略"),
    NULL_COLUMN("nullColumn", "空列策略");

    private final String columnStrategy;
    private final String description;
    ColumnStrategy(String columnStrategy, String description) {
        this.columnStrategy = columnStrategy;
        this.description = description;
    }

    public String getColumnStrategy() {
        return columnStrategy;
    }

    public String getDescription() {
        return description;
    }
}
