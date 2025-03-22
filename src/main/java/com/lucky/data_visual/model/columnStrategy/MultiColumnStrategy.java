package com.lucky.data_visual.model.columnStrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

public class MultiColumnStrategy extends ColumnStrategy {

    @Override
    public JsonNode designDataForm(List<Map<String, List<Object>>> chartData) {
        ObjectMapper mapper = new ObjectMapper();

        // 创建 X 轴
        ObjectNode xAxis = mapper.createObjectNode();
        xAxis.put("type", "category");

        // 创建 Y 轴（空对象）
        ObjectNode yAxis = mapper.createObjectNode();

        // 整合数据
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.set("series", mapper.valueToTree(chartData)); // 将 List<Map<String, List<Object>>> 转换为 JsonNode
        jsonObject.set("xAxis", xAxis);
        jsonObject.set("yAxis", yAxis);

        return jsonObject;
    }

    public String getColumnStrategy() {
        return "multiColumn";
    }
}
