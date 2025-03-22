package com.lucky.data_visual.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Schema(description = "图表类")
@Component
public class Chart {
    private JsonNode chartConfig; // 图像默认配置信息
    private JsonNode chartConfigAfterProcess; // 图像处理后的配置信息
    private Map<String, List<String>> axisLabels; // x, y 轴标签名
    private String chartType; // 图表类型
    private Map<String, Object> rawChartData; // 原始数据

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
}
