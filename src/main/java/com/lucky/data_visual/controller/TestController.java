package com.lucky.data_visual.controller;

import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.CsvFileServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    private final JsonResult<List<Map<String, String>>> jsonResult;
    private final CsvFileServer csvFileServer;

    @Autowired
    public TestController(@Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult,
                          CsvFileServer csvFileServer) {
        this.jsonResult = jsonResult;
        this.csvFileServer = csvFileServer;
    }

    @GetMapping("/fetch-csv")
    public JsonResult<List<Map<String, Object>>> getCsvData() {
        return csvFileServer.convertToNumberOrString(jsonResult);
    }
}
