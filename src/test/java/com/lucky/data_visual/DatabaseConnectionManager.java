package com.lucky.data_visual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static Connection connection;

    // 创建数据库连接
    public static Connection connect(String url, String user, String password) {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("✅ 数据库连接成功！");
            } catch (SQLException e) {
                System.out.println("❌ 数据库连接失败：" + e.getMessage());
            }
        }
        return connection;
    }

    // 关闭数据库连接
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ 数据库连接已关闭！");
            } catch (SQLException e) {
                System.out.println("❌ 关闭数据库连接失败：" + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}