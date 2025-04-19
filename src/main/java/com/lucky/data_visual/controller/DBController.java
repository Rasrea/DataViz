package com.lucky.data_visual.controller;

import com.lucky.data_visual.model.DBRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "数据库操作接口")
@RestController
@RequestMapping("/api/db")
public class DBController {
    private final static Logger logger = LoggerFactory.getLogger(DBController.class);

    @PostMapping("/addDB")
    public ResponseEntity<List<String>> addDB(@RequestBody DBRequest dbRequest) {
        String dbName = dbRequest.getDbName();
        String dbUrl = dbRequest.getDbUrl();
        String dbUser = dbRequest.getDbUser();
        String dbPassword = dbRequest.getDbPassword();

        // 连接数据库
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


}
