package com.lucky.data_visual.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * OpenApi配置类，用于定义 API 文档的一些基本信息
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot 示例 API",
                description = "这是一个用于展示 Spring Boot 学习的项目",
                version = "1.0",
                license = @License(name = "MIT Licence")
        )
)
public class OpenApiConfig {
//    @Bean
//    public GroupedOpenApi testApi() {
//        return GroupedOpenApi.builder()
//                .group("test-api") // 设置分组名称
//                .pathsToMatch("/test/**") // 配置只扫描以 "/test/" 开头的路径
//                .build();
//    }

//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("admin-api") // 设置分组名称
//                .pathsToMatch("/admin/**") // 配置只扫描以 "/admin/" 开头的路径
//                .build();
//    }
}