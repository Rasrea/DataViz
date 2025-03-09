package com.lucky.data_visual.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Schema(description = "样本数据集类")
@Component
@ConfigurationProperties(prefix = "sample-data-path")
public class SampleDataPath {

    @Schema(description = "豆瓣读书Top250")
    private String doubanBookPath;

    @Schema(description = "猫眼电影榜单")
    private String maoyanFilmPath;

    @Schema(description = "Metacritic游戏")
    private String metacriticGamePath;

    @Schema(description = "随机生成的误差数据")
    private String randomDataPath;

    // Getter and Setter
    public String getDoubanBookPath() {
        return doubanBookPath;
    }

    public void setDoubanBookPath(String doubanBookPath) {
        this.doubanBookPath = doubanBookPath;
    }

    public String getMaoyanFilmPath() {
        return maoyanFilmPath;
    }

    public void setMaoyanFilmPath(String maoyanFilmPath) {
        this.maoyanFilmPath = maoyanFilmPath;
    }

    public String getMetacriticGamePath() {
        return metacriticGamePath;
    }

    public void setMetacriticGamePath(String metacriticGamePath) {
        this.metacriticGamePath = metacriticGamePath;
    }

    public String getRandomDataPath() {
        return randomDataPath;
    }

    public void setRandomDataPath(String randomDataPath) {
        this.randomDataPath = randomDataPath;
    }
}
