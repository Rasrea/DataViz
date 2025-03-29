package com.lucky.data_visual.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lucky.data_visual.model.Chart;
import com.lucky.data_visual.model.columnStrategy.MultiColumnStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            ((ObjectNode) chartConfig.get("xAxis")).put("type", "value");
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
    public List<Map<String, Object>> processedChartData(Map<String, Object> sortedOrGroupAxisData) {
        List<Object> labels = (List<Object>) sortedOrGroupAxisData.get("labels");
        List<List<Object>> values = (List<List<Object>>) sortedOrGroupAxisData.get("values");
        int yAxisSize = values.get(0).size(); // Y 轴数据列数

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < yAxisSize; i++) {
            List<Object> yAxisData = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                List<Object> dataCouple = new ArrayList<>(); // 一对数据
                dataCouple.add(labels.get(j));
                dataCouple.add(values.get(j).get(i));
                yAxisData.add(dataCouple);
            }
            Map<String, Object> map = new HashMap<>();
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

        // 根据绘图策略设置不同的 elementConfig
        String[] elementConfig = {"title", "series", "tooltip", "legend"};
        if (chart.getColumnStrategy().getClass() ==  MultiColumnStrategy.class) {
            elementConfig = new String[]{"title", "xAxis", "yAxis", "series", "tooltip", "legend"};
        }

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

    /**
     * 读取数据列
     *
     * @param fileData 数据
     * @param colName  列名
     * @return 数据列
     */
    public List<Object> readLineData(List<Map<String, Object>> fileData, String colName) {
        List<Object> colData = new ArrayList<>();
        for (Map<String, Object> map : fileData) {
            colData.add(map.get(colName));
        }
        return colData;
    }

    /**
     * 统计数据列中空值和非空值的数据量
     *
     * @param colData 数据列
     * @return [{name: '空值', value: 2}, {name: '非空值', value: 3}]
     */
    public List<Map<String, Object>> countEmptyAndNonEmptyData(List<Object> colData) {
        // 统计空值数量
        double nullCount = 0;
        for (Object data : colData) {
            if (data == null || data.equals("")) {
                nullCount++;
            }
        }

        // 整合数据
        Map<String, Object> nullData = new HashMap<>();
        nullData.put("name", "空值");
        nullData.put("value", nullCount);
        Map<String, Object> nonNullData = new HashMap<>();
        nonNullData.put("name", "非空值");
        nonNullData.put("value", colData.size() - nullCount);
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(nullData);
        resultList.add(nonNullData);
        return resultList;
    }

    /**
     * 统计前 K 项频数，以及 K 项目之后的总数
     *
     * @param colData 数据列
     * @param k       前 K 项
     * @return [{name: 'A', value: 2}, {name: 'B', value: 3}]
     */
    public List<Map<String, Object>> countTopKFrequency(List<Object> colData, int k) {
        // 统计频数
        Map<Object, Integer> frequencyMap = new HashMap<>();
        for (Object data : colData) {
            frequencyMap.put(data, frequencyMap.getOrDefault(data, 0) + 1);
        }

        // 排序
        List<Map.Entry<Object, Integer>> frequencyList = new ArrayList<>(frequencyMap.entrySet());
        frequencyList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // 整合数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        int limit = Math.min(k, frequencyList.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", frequencyList.get(i).getKey());
            data.put("value", frequencyList.get(i).getValue());
            resultList.add(data);
        }

        if (k < frequencyList.size()) {
            resultList.add(new HashMap<>() {{
                put("name", "Others");
                put("value", frequencyList.stream().skip(k).mapToInt(Map.Entry::getValue).sum());
            }});
        }

        return resultList;
    }

    /**
     * 将单列数据转换为标签和值
     *
     * @param colData 单列数据
     * @return [[1, values1], [2, values2], ...]
     */
    public List<Object> convertSingleColumnDataToLabelsAndValues(List<Object> colData) {
        List<Object> data = new ArrayList<>();
        for (int i = 0; i < colData.size(); i++) {
            List<Object> dataCouple = new ArrayList<>();
            dataCouple.add(i + 1);
            dataCouple.add(colData.get(i));
            data.add(dataCouple);
        }
        return data;
    }

    /**
     * 计算四分位数和上下须
     *
     * @param numbers 数据列
     * @return [Q1, Q2, Q3, lowerWhisker, upperWhisker]
     */
    public List<Double> calculateQuartilesAndWhisker(List<Double> numbers) {
        // 将数据列转换为 Double 类型 并排序
        List<Double> sortedNumbers = numbers.stream()
                .sorted()
                .toList();

        // 计算四分位数
        double Q1 = calculateQuartile(sortedNumbers, 25);
        double Q2 = calculateQuartile(sortedNumbers, 50);
        double Q3 = calculateQuartile(sortedNumbers, 75);

        // 计算 四分位距 和 上下须
        double IQR = Q3 - Q1;
        double lowerWhisker = Math.max(Q1 - 1.5 * IQR, sortedNumbers.get(0));
        double upperWhisker = Math.min(Q3 + 1.5 * IQR, sortedNumbers.get(sortedNumbers.size() - 1));

        return Stream.of(lowerWhisker, Q1, Q2, Q3, upperWhisker)
                .map(e -> BigDecimal.valueOf(e).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .collect(Collectors.toList());
    }

    /**
     * 计算离群点
     * @param numbers 数据序列
     * @param lowerWhisker 上须
     * @param upperWhisker 下须
     * @return [1, 2, 3, ...]
     */
    public List<Double> calculateOutliers(List<Double> numbers, Double lowerWhisker, Double upperWhisker) {
        return numbers.stream()
                .filter(e -> e < lowerWhisker || e > upperWhisker)
                .collect(Collectors.toList());
    }


    /**
     * 计算四分位数
     *
     * @param sortedData 排序后的数据
     * @param percentile 百分位数
     * @return 四分位数
     */
    private static double calculateQuartile(List<Double> sortedData, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * sortedData.size()) - 1;
        return sortedData.get(index);
    }

}
