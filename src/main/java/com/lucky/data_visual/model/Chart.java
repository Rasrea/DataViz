package com.lucky.data_visual.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucky.data_visual.model.columnStrategy.ColumnStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Schema(description = "图表类")
@Component
public class Chart {
    private JsonNode chartConfigAfterProcess; // 图像处理后的配置信息
    private JsonNode chartConfig; // 图像默认配置信息
    private Map<String, List<String>> axisLabels; // x, y 轴标签名
    private String chartType; // 图表类型
    private Map<String, Object> rawChartData; // 原始数据
    private List<Map<String, List<Object>>> processedChartData; // 处理后的数据
    private ColumnStrategy columnStrategy; // 列策略

    // Getter and Setter
    public JsonNode getChartConfig() {
        return chartConfig;
    }

    public void setChartConfig(JsonNode chartConfig) {
        this.chartConfig = chartConfig;
    }

    public JsonNode getChartConfigAfterProcess() {
        return chartConfigAfterProcess;
    }

    public void setChartConfigAfterProcess(JsonNode chartConfigAfterProcess) {
        this.chartConfigAfterProcess = chartConfigAfterProcess;
    }

    public Map<String, List<String>> getAxisLabels() {
        return axisLabels;
    }

    public void setAxisLabels(Map<String, List<String>> axisLabels) {
        this.axisLabels = axisLabels;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public Map<String, Object> getRawChartData() {
        return rawChartData;
    }

    public void setRawChartData(Map<String, Object> rawChartData) {
        this.rawChartData = rawChartData;
    }

    public List<Map<String, List<Object>>> getProcessedChartData() {
        return processedChartData;
    }

    public void setProcessedChartData(List<Map<String, List<Object>>> processedChartData) {
        this.processedChartData = processedChartData;
    }

    public ColumnStrategy getColumnStrategy() {
        return columnStrategy;
    }

    public void setColumnStrategy(ColumnStrategy columnStrategy) {
        this.columnStrategy = columnStrategy;
    }
}
