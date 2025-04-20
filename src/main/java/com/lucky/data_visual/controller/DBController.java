package com.lucky.data_visual.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.enums.FileType;
import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.model.DBRequest;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.CsvFileServer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数据库操作接口")
@RestController
@RequestMapping("/api/db")
public class DBController {
    private final static Logger logger = LoggerFactory.getLogger(DBController.class);

    private final JsonResult<List<Map<String, String>>> jsonResult; // 原始数据
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final CsvFileServer csvFileServer;

    @Autowired
    public DBController(@Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult,
                         @Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                         CsvFileServer csvFileServer) {
        this.jsonResult = jsonResult;
        this.operateJsonResult = operateJsonResult;
        this.csvFileServer = csvFileServer;
    }

    @Autowired
    public void setOperateJsonResult(JsonResult<List<Map<String, Object>>> result) {
        operateJsonResult.setStatusCode(result.getStatusCode());
        operateJsonResult.setMsgList(result.getMsgList());
        operateJsonResult.setColTypes(result.getColTypes());
        operateJsonResult.setData(result.getData());
    }

    @PostMapping("/addDB")
    public ResponseEntity<List<String>> addDB(@RequestBody DBRequest dbRequest) {
        String dbName = dbRequest.getDbName();
        String dbUrl = dbRequest.getDbUrl();
        String dbUser = dbRequest.getDbUser();
        String dbPassword = dbRequest.getDbPassword();

        // 连接数据库
        logger.info("连接信息: {}", dbRequest);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // 查询对应数据库的所有表
            List<String> tableNames = new ArrayList<>(); // 存储对应数据库的列表

            // 切换到指定数据库
            String useDbQuery = "USE " + dbName;

            // 查询所有表
            String showTablesQuery = "SHOW TABLES";

            try (var stmt = conn.createStatement()) {
                stmt.execute(useDbQuery); // 切换数据库

                try (ResultSet rs = stmt.executeQuery(showTablesQuery)) {
                    // 获取所有表名
                    while (rs.next()) {
                        String tableName = rs.getString(1);
                        tableNames.add(tableName);
                    }

                    logger.info("查询到的表: {}", tableNames);
                    return ResponseEntity.ok(tableNames);
                }
            }

        } catch (Exception e) {
            logger.error("数据库连接失败: {}", e.getMessage());
            return ResponseEntity.status(500).body(List.of("数据库连接失败"));
        }
    }

    @PostMapping("/readTable")
    public ResponseEntity<String> getTable(@RequestBody Map<String, Object> requestBody) {
        // 读取连接信息和查询表
        ObjectMapper objectMapper = new ObjectMapper();
        DBRequest dbRequest = objectMapper.convertValue(requestBody.get("dbRequest"), DBRequest.class);
        String tName = (String) requestBody.get("tName");

        // 连接数据库
        String dbUrl = dbRequest.getDbUrl();
        String dbUser = dbRequest.getDbUser();
        String dbPassword = dbRequest.getDbPassword();
        String dbName = dbRequest.getDbName();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

            logger.info("连接信息：{}，表名：{}", dbRequest, tName);

            // 查询表数据
            String query = "SELECT * FROM " + dbName + "." + tName;
            try (var stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                List<Map<String, String>> tableData = convertTableToJson(rs);

                logger.info("查询到的表数据: {}", tableData);

                // 将表数据注入 jsonResult
                jsonResult.setData(tableData);
                jsonResult.setStatusCode(HttpStatusCode.OK);

                // 将表数据注入操作JSON中
                JsonResult<List<Map<String, Object>>> result = csvFileServer.convertToNumberOrString(jsonResult);
                setOperateJsonResult(result);
                operateJsonResult.setFileType(FileType.DB);
                logger.info("获取表 {} 成功", tName);
                return ResponseEntity.ok("获取表 " + tName + " 成功");
            }
        } catch (Exception e) {
            logger.error("查询表失败: {}", e.getMessage());
            return ResponseEntity.status(500).body("查询表失败");
        }
    }


    /**
     * 将 ResultSet 转换为 JSON 格式
     *
     * @param rs ResultSet 对象
     * @return JSON 格式的表数据
     */
    private List<Map<String, String>> convertTableToJson(ResultSet rs) {
        List<Map<String, String>> tableData = new ArrayList<>();
        try {
            int columnCount = rs.getMetaData().getColumnCount(); // 获取列数
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);
                    if (value == null) {
                        value = "";
                    }
                    row.put(columnName, value);
                }
                tableData.add(row);
            }
        } catch (Exception e) {
            logger.error("转换表数据失败: {}", e.getMessage());
        }
        return tableData;
    }
}
