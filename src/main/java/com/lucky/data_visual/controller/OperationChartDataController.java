package com.lucky.data_visual.controller;

import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.ChartServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "operation页面的数据接口")
@RestController
@RequestMapping("/api/operation")
public class OperationChartDataController {
    private final static Logger logger = LoggerFactory.getLogger(OperationChartDataController.class);
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final ChartServer chartServer = new ChartServer();

    @Autowired
    public OperationChartDataController(@Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult) {
        this.operateJsonResult = operateJsonResult;
    }

    @Operation(
            summary = "获取空值和非空值的数量",
            description = "获取指定列的空值和非空值的数量"
    )
    @GetMapping("/emptyAndNonEmptyData")
    public List<Map<String, Object>> getEmptyAndNonEmptyData(@RequestParam String colName) {
        List<Object> colData = chartServer.readLineData(operateJsonResult.getData(), colName);
        return chartServer.countEmptyAndNonEmptyData(colData);
    }

    @Operation(
            summary = "获取前 5 项频数",
            description = "获取指定列的前 5 项频数"
    )
    @GetMapping("/topKFrequency")
    public List<Map<String, Object>> getTopKFrequency(@RequestParam String colName, int K) {
        List<Object> colData = chartServer.readLineData(operateJsonResult.getData(), colName);
        return chartServer.countTopKFrequency(colData, K);
    }
}
