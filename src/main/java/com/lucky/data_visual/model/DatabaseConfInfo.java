package com.lucky.data_visual.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Schema(description = "数据库配置信息类")
@Component
public class DatabaseConfInfo {
    private String url;
    private String username;
    private String password;

    // getter 和 setter 方法
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
