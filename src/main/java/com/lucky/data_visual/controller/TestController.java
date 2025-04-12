package com.lucky.data_visual.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.model.DatabaseConfPath;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private DatabaseConfPath databaseConfPath;

    @GetMapping("/databaseConfPath")
    public JsonNode getConfPath() {
        String mysqlConfPath = databaseConfPath.getMysqlConfPath();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(new File(mysqlConfPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
