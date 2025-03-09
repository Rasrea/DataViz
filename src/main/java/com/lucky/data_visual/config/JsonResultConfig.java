package com.lucky.data_visual.config;

import com.lucky.data_visual.model.JsonResult;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class JsonResultConfig {

    @Schema(description = "原始数据")
    @Bean(name = "originalData")
    public JsonResult<List<Map<String, String>>> jsonResult1() {
        return new JsonResult<>();
    }

    @Schema(description = "数据类型转换后的数据")
    @Bean(name = "operationalData")
    public JsonResult<List<Map<String, Object>>> jsonResult2() {
        return new JsonResult<>();
    }

    @Schema(description = "数据类型转换后的单列数据")
    @Bean(name = "colData")
    public JsonResult<Map<String, List<Object>>> jsonResult3() {
        return new JsonResult<>();
    }

    @Schema(description = "存储Excel的多个表格")
    @Bean(name = "excelData")
    public JsonResult<Map<String, List<Map<String, String>>>> jsonResult4() {
        return new JsonResult<>();
    }
}