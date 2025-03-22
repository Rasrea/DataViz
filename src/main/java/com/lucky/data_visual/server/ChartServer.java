package com.lucky.data_visual.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lucky.data_visual.model.Chart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartServer {

    private Chart chart;

    @Autowired
    public ChartServer(@Qualifier("chartParams") Chart chart) {
        this.chart = chart;
    }

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
        String xAxisLabel = axisLabels.get("xAxis").get(0);
        List<String> yAxisLabels = axisLabels.get("yAxis");

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
     * 根据 X 轴类型，排序（Number）或聚合（String）数据
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
            mergeData.sort((o1, o2) -> {
                double x1 = (double) o1.get(0);
                double x2 = (double) o2.get(0);
                return Double.compare(x1, x2);
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", mergeData.stream().map(e -> e.get(0)).collect(Collectors.toList()));
        result.put("values", mergeData);
        return result;
    }

    public Chart getChart() {
        return chart;
    }
}
