package com.lucky.data_visual;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306"; // 不指定数据库
        String user = "dataViz";
        String password = "password";

        // 获取数据库连接
        Connection conn = DatabaseConnectionManager.connect(url, user, password);

        // 系统数据库名称列表
        Set<String> systemDatabases = new HashSet<>(Arrays.asList(
                "information_schema", "mysql", "performance_schema", "sys"
        ));

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");

            System.out.println("📚 用户创建的数据库：");
            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!systemDatabases.contains(dbName)) {
                    System.out.println(" - " + dbName);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ 查询失败：" + e.getMessage());
        } finally {
            // 关闭数据库连接
            DatabaseConnectionManager.close();
        }
    }
}