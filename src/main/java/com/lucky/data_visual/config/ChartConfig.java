package com.lucky.data_visual.config;

import com.lucky.data_visual.model.Chart;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChartConfig {
    @Schema(description = "原始数据")
    @Bean(name = "chartParams")
    public Chart chart1() {
        return new Chart();
    }
}
