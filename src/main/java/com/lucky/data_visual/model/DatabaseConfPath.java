package com.lucky.data_visual.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Schema(description = "数据库配置路径")
@Component
@ConfigurationProperties(prefix = "database-conf-path")
public class DatabaseConfPath {

    @Schema(description = "MySQL 配置文件路径")
    private String mysqlConfPath;

    // Getter and Setter
    public String getMysqlConfPath() {
        return mysqlConfPath;
    }

    public void setMysqlConfPath(String mysqlConfPath) {
        this.mysqlConfPath = mysqlConfPath;
    }
}
