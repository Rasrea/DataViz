package com.lucky.data_visual.config;

import com.lucky.data_visual.server.CsvFileServer;
import com.lucky.data_visual.server.ExcelFileServer;
import com.lucky.data_visual.server.PythonProcessorServer;
import com.lucky.data_visual.server.SingleColDataServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeverConfig {

    @Bean
    public CsvFileServer csvFileServer(SingleColDataServer singleColDataServer) {
        return new CsvFileServer(singleColDataServer);
    }

    @Bean
    public ExcelFileServer excelFileServer() {
        return new ExcelFileServer();
    }

    @Bean
    public SingleColDataServer singleColDataServer() {
        return new SingleColDataServer();
    }

    @Bean
    public PythonProcessorServer pythonProcessorServer() {
        return new PythonProcessorServer();
    }
}
