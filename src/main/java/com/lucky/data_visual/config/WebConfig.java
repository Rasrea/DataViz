package com.lucky.data_visual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域请求
        registry.addMapping("/**").allowedOrigins("*"); // 前端地址
//        registry.addMapping("/api/**").allowedOrigins("http://localhost:63342");
    }
}
