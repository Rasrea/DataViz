package com.lucky.data_visual.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.model.Chart;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.ChartServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final Chart chart; // 图表
    private final ChartServer chartServer;

    @Autowired
    public ChartController(@Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                           @Qualifier("chartParams") Chart chart, ChartServer chartServer) {
        this.operateJsonResult = operateJsonResult;
        this.chart = chart;
        this.chartServer = chartServer;
    }

    public JsonResult<List<Map<String, Object>>> getOperateJsonResult() {
        return operateJsonResult;
    }

    @Operation(
            summary = "设置 x, y 轴标签名",
            description = "此接口用于设置 x, y 轴标签名，返回设置后的 x, y 轴标签名。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json")
            )
    )
    @PostMapping("/axisLabels")
    public ResponseEntity<Map<String, List<String>>> setAxisLabels(@RequestBody Map<String, Object> axisLabels) {
        String xAxisLabel = (String) axisLabels.get("xColName");
        List<String> yAxisLabels = (List<String>) axisLabels.get("yColNames");
        Map<String, List<String>> axisLabelsMap = Map.of(
                "xColName", List.of(xAxisLabel),
                "yColNames", yAxisLabels
        );
        chart.setAxisLabels(axisLabelsMap);
        return ResponseEntity.ok(chart.getAxisLabels());
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
    public ResponseEntity<String> setChartConfig(@RequestParam String chartType) {
        ObjectMapper objectMapper = new ObjectMapper();
        Path basePath = Paths.get("src/main/resources/static/plotCharts/configs").toAbsolutePath();
        Path targetPath = basePath.resolve(chartType + ".json").normalize();

        try {
            chart.setChartConfig(objectMapper.readTree(targetPath.toFile()));
            return ResponseEntity.ok("图像配置文件读取成功！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "读取原始数据列",
            description = "此接口用于读取原始数据列，返回原始数据列。",
            parameters = {
                    @Parameter(
                            name = "chartType",
                            description = "图表类型",
                            required = true,
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/rawChartData")
    public Map<String, Object> getRawChartData() {
        chart.setRawChartData(chartServer.getRawAxisData(chart.getAxisLabels(), operateJsonResult.getData()));
        return chart.getRawChartData();
    }

    @Operation(
            summary = "排序或分组数据",
            description = "此接口用于排序或分组数据，返回排序或分组后的数据。",
            parameters = {
                    @Parameter(
                            name = "chartType",
                            description = "图表类型",
                            required = true,
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/sortedOrGroupedData")
    public Map<String, Object> getSortedOrGroupedData() {
        chart.setRawChartData(chartServer.getRawAxisData(chart.getAxisLabels(), operateJsonResult.getData()));
        return chartServer.sortedOrGroupAxisData(chart.getRawChartData(), chart.getChartConfig());
    }

    @GetMapping("/processedChartData")
    public List<Map<String, List<Object>>> getProcessedChartData() {
        chart.setRawChartData(chartServer.getRawAxisData(chart.getAxisLabels(), operateJsonResult.getData()));
        Map<String, Object> sortedOrGroupedData = chartServer.sortedOrGroupAxisData(chart.getRawChartData(), chart.getChartConfig());
        return chartServer.processedChartData(sortedOrGroupedData);
    }

}
