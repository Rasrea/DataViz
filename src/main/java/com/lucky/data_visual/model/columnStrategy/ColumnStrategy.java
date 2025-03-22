package com.lucky.data_visual.model.columnStrategy;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

// 统一绘图策略的基类
public abstract class ColumnStrategy {
    // 设计数据表现形式
    public abstract JsonNode designDataForm(List<Map<String, List<Object>>> chartData);
}
