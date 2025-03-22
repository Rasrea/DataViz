package com.lucky.data_visual.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Schema(description = "图表类")
@Component
public class Chart {
    JsonNode chartConfig; // 图像默认配置信息
    JsonNode chartConfigAfterProcess; // 图像处理后的配置信息

    public Chart() {};

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
}
