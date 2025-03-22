package com.lucky.data_visual.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lucky.data_visual.model.Chart;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServer {

    /**
     * 根据 x, y 轴标签名读取原始数据
     *
     * @param axisLabels x, y 轴标签名
     *                   x 轴标签名为 "xAxis"
     *                   y 轴标签名为 "yAxis"
     * @param fileData   原始数据
     */
    public Map<String, Object> getRawAxisData(Map<String, List<String>> axisLabels, List<Map<String, Object>> fileData) {
        // 读取 X 和 Y 轴标签
        String xAxisLabel = axisLabels.get("xColName").get(0);
        List<String> yAxisLabels = axisLabels.get("yColNames");

        // 读取对应列的数据
        List<Object> xAxisData = new ArrayList<>();
        for (Map<String, Object> map : fileData) { // 只取一列数据
            xAxisData.add(map.get(xAxisLabel));
        }
        List<List<Object>> yAxisData = new ArrayList<>();
        for (String yAxisLabel : yAxisLabels) { // 可获取多列数据
            List<Object> yAxis = new ArrayList<>();
            for (Map<String, Object> map : fileData) {
                yAxis.add(map.get(yAxisLabel));
            }
            yAxisData.add(yAxis);
        }

        // 使用 HashMap 存储数据并返回
        Map<String, Object> result = new HashMap<>();
        result.put("labels", xAxisData);
        result.put("values", yAxisData);
        return result;
    }

    /**
     * 根据 X 轴类型，排序（Number）或聚合（String）数据，返回数据类型：
     * labels: [x1, x2, ...]
     * values: [[y11, y12, ...], [y21, y22, ...], ...]
     */
    public Map<String, Object> sortedOrGroupAxisData(Map<String, Object> rawAxisData, JsonNode chartConfig) {
        // 合并 XY 轴数据，如: [[x1, y11, y21], [x2, y12, y22], ...]
        List<List<Object>> mergeData = new ArrayList<>();
        List<Object> labels = (List<Object>) rawAxisData.get("labels");
        List<List<Object>> values = (List<List<Object>>) rawAxisData.get("values");
        for (int i = 0; i < labels.size(); i++) {
            List<Object> row = new ArrayList<>();
            row.add(labels.get(i));
            for (List<Object> value : values) {
                row.add(value.get(i));
            }
            mergeData.add(row);
        }

        // 判断 xAxisData 是否为 Number 类型
        boolean allNumbers = labels.stream().allMatch(e -> e instanceof Number);

        if (allNumbers) {
            ((ObjectNode) chartConfig.get("xAxis")).put("type", "Value");
            mergeData.sort(Comparator.comparingDouble(o -> ((Number) o.get(0)).doubleValue()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", mergeData.stream().map(e -> e.get(0)).collect(Collectors.toList()));
        result.put("values", mergeData.stream()
                .map(e -> e.subList(1, e.size()))
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 处理图表数据，返回数据类型：
     * data: [[[x1, y11], [x1, y12], ...], [[x1, y21], [x1, y22], ...], ...]
     */
    public List<Map<String, List<Object>>> processedChartData(Map<String, Object> sortedOrGroupAxisData) {
        List<Object> labels = (List<Object>) sortedOrGroupAxisData.get("labels");
        List<List<Object>> values = (List<List<Object>>) sortedOrGroupAxisData.get("values");
        int yAxisSize = values.get(0).size(); // Y 轴数据列数

        List<Map<String, List<Object>>> result = new ArrayList<>();
        for (int i = 0; i < yAxisSize; i++) {
            List<Object> yAxisData = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                List<Object> dataCouple = new ArrayList<>(); // 一对数据
                dataCouple.add(labels.get(j));
                dataCouple.add(values.get(j).get(i));
                yAxisData.add(dataCouple);
            }
            Map<String, List<Object>> map = new HashMap<>();
            map.put("data", yAxisData); // 一列数据
            result.add(map);
        }
        return result;
    }

    /**
     * 将数据列插入到图表配置中
     *
     * @param chart 图表对象
     * @return 处理后的图表对象
     */
    public JsonNode insertDataIntoChartConfig(Chart chart) {
        JsonNode chartConfigAfterProcess = chart.getColumnStrategy().designDataForm(chart.getProcessedChartData());
        JsonNode chartConfig = chart.getChartConfig(); // 原始图表配置信息
        String[] elementConfig = {"title", "xAxis", "yAxis", "series", "tooltip", "legend"};

        for (String element : elementConfig) {
            if (chartConfig.has(element)) {
                if (element.equals("series")) {
                    ArrayNode updatedSeries = new ObjectMapper().createArrayNode();
                    for (JsonNode seriesNode : chartConfigAfterProcess.get(element)) {
                        ObjectNode mergedSeriesNode = new ObjectMapper().createObjectNode();
                        mergedSeriesNode.setAll((ObjectNode) seriesNode);
                        mergedSeriesNode.setAll((ObjectNode) chartConfig.get("series").get(0));
                        updatedSeries.add(mergedSeriesNode);
                    }
                    ((ObjectNode) chartConfigAfterProcess).set("series", updatedSeries);
                } else {
                    // 其他元素合并
                    ObjectNode mergedNode = new ObjectMapper().createObjectNode();
                    if (chartConfigAfterProcess.has(element)) {
                        mergedNode.setAll((ObjectNode) chartConfigAfterProcess.get(element));
                    }
                    mergedNode.setAll((ObjectNode) chartConfig.get(element));
                    ((ObjectNode) chartConfigAfterProcess).set(element, mergedNode);
                }
            }
        }

        return chartConfigAfterProcess;
    }
}
