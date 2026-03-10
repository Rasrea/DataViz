package com.lucky.data_visual.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "配置信息接口")
@RestController
@RequestMapping("/config")
public class ConfigController {
    @Value("${config.baseURL}")
    private String baseURL;

    @GetMapping("/api")
    public Map<String, String> getBaseURL() {
        Map<String, String> config = new HashMap<>();
        config.put("baseUrl", baseURL);
        return config;
    }
}
