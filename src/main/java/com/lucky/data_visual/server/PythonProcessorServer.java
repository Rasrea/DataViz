package com.lucky.data_visual.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.controller.CsvController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class PythonProcessorServer {
    private final static Logger logger = LoggerFactory.getLogger(PythonProcessorServer.class);

    public static List<Map<String, Object>> Run(String[] cmd) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();

        try (BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString;
            jsonString = stdOutReader.readLine();
            List<Map<String, Object>> newCsvData = objectMapper.readValue(jsonString, List.class);

            // 读取错误流信息
            while ((jsonString = stdErrReader.readLine()) != null) {
                logger.info(jsonString);
            }

            return newCsvData;
        }
    }
}