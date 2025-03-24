package com.lucky.data_visual.model.columnStrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class SingleColumnStrategy extends ColumnStrategy{

    /**
     * 设计数据表现形式
     * @param objectChartData 数据
     * @return [{name: 'name', value: 2}, {name: 'name', value: 3}, ...]
     */
    @Override
    public JsonNode designDataForm(Object objectChartData) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> chartData = (Map<String, Double>) objectChartData;
        ObjectNode jsonNodes = mapper.createObjectNode();
        jsonNodes.set("data", mapper.valueToTree(chartData));
        return jsonNodes;
    }
}
