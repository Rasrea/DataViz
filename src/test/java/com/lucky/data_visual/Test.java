package com.lucky.data_visual;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        // 示例 JSON 字符串
        String jsonString = "[{\"ID\": 2, \"Name\": \"David\", \"Age\": 61, \"City\": \"Los Angeles\", \"Salary\": 9524.95}, " +
                "{\"ID\": 3, \"Name\": \"Grace\", \"Age\": 18, \"City\": \"New York\", \"Salary\": 22222}, " +
                "{\"ID\": 5, \"Name\": \"Alice\", \"Age\": 55, \"City\": \"Chicago\", \"Salary\": 8897.75}]";

        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 将 JSON 字符串转换为 List<Map<String, Object>>
            List<Map<String, Object>> list = objectMapper.readValue(jsonString, List.class);

            // 输出 List 的内容
            for (Map<String, Object> item : list) {
                System.out.println(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
