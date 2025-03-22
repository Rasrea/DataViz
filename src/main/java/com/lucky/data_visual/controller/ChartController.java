package com.lucky.data_visual.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.model.Chart;
import com.lucky.data_visual.model.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Tag(name = "图表操作接口")
@RestController
@RequestMapping("/api/chart")
public class ChartController {
    private final static Logger logger = LoggerFactory.getLogger(ChartController.class);
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private Chart chart; // 图表

    @Autowired
    public ChartController(@Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                           @Qualifier("chartParams") Chart chart) {
        this.operateJsonResult = operateJsonResult;
        this.chart = chart;
    }

    public JsonResult<List<Map<String, Object>>> getOperateJsonResult() {
        return operateJsonResult;
    }

    @Operation(
            summary = "读取前端选择的图表配置文件",
            description = "此接口用于读取前端选择的图表配置文件，返回配置文件的内容。",
            parameters = {
                    @Parameter(
                            name = "chartType",
                            description = "图表类型",
                            required = true,
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/chartConfig")
    public JsonNode getChartConfig(@RequestParam String chartType) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 只允许读取指定目录下的文件
        Path basePath = Paths.get("src/main/resources/static/plotCharts/configs").toAbsolutePath();
        Path targetPath = basePath.resolve(chartType + ".json").normalize();

        // 确认路径在允许的范围内
        if (!targetPath.startsWith(basePath)) {
            throw new SecurityException("Illegal path access detected");
        }

        try {
            return objectMapper.readTree(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
