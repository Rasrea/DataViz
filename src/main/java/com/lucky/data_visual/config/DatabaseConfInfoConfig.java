package com.lucky.data_visual.config;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Bean;

public class DatabaseConfInfoConfig {

    @Schema(description = "MySQL 配置信息")
    @Bean(name = "MySQL")
    public DatabaseConfInfoConfig databaseConfInfoConfig1() {
        return new DatabaseConfInfoConfig();
    }
}
