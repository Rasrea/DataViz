package com.lucky.data_visual.controller;

import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.ChartServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "operation页面的数据接口")
@RestController
@RequestMapping("/api/operation")
public class OperationChartDataController {
    private final static Logger logger = LoggerFactory.getLogger(OperationChartDataController.class);
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final ChartServer chartServer = new ChartServer();

    private List<Object> colData;

    @Autowired
    public OperationChartDataController(@Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult) {
        this.operateJsonResult = operateJsonResult;
    }

    @Operation(
            summary = "读取指定列的数据",
            description = "读取指定列的数据"
    )
    @GetMapping("/readColData")
    public ResponseEntity<String> readColData(@RequestParam String colName) {
        colData = chartServer.readLineData(operateJsonResult.getData(), colName);
        return ResponseEntity.ok("读取成功");
    }

    @Operation(
            summary = "获取空值和非空值的数量",
            description = "获取指定列的空值和非空值的数量"
    )
    @GetMapping("/emptyAndNonEmptyData")
    public List<Map<String, Object>> getEmptyAndNonEmptyData() {

        return chartServer.countEmptyAndNonEmptyData(colData);
    }

    @Operation(
            summary = "获取前 K 项频数",
            description = "获取指定列的前 K 项频数"
    )
    @GetMapping("/topKFrequency")
    public List<Map<String, Object>> getTopKFrequency(@RequestParam int K) {
        return chartServer.countTopKFrequency(colData, K);
    }

    @Operation(
            summary = "获取单列数据的标签和值",
            description = "获取指定列的数据的标签和值"
    )
    @GetMapping("/singleLabelsAndValues")
    public List<Object> getSingleLabelsAndValues() {
        return chartServer.convertSingleColumnDataToLabelsAndValues(colData);
    }

    @Operation(
            summary = "获取四分位数，上下须和离群点",
            description = "获取指定列的四分位数，上下须和离群点"
    )
    @GetMapping("/quartilesAndWhisker")
    public Map<String, List<Double>> getQuartilesAndWhisker(@RequestParam boolean isGetOutliers) {
        // 将数据列转换为 Double 类型
        List<Double> numbers = colData.stream()
                .filter(e -> e instanceof Number)
                .map(e -> (Number) e)
                .map(Number::doubleValue)
                .toList();

        // 计算 四分位数 和 离群点
        Map<String, List<Double>> result = new HashMap<>();
        List<Double> quartilesAndWhiskers = chartServer.calculateQuartilesAndWhisker(numbers);
        result.put("quartilesAndWhisker", quartilesAndWhiskers);

        if (isGetOutliers) {
            List<Double> outliers = chartServer.calculateOutliers(numbers, quartilesAndWhiskers.get(0), quartilesAndWhiskers.get(quartilesAndWhiskers.size() - 1));
            result.put("outliers", outliers);
        }
        return result;
    }
}
