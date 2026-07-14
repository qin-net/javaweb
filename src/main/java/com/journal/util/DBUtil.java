package com.journal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接管理工具类，负责统一管理数据库连接的获取与资源释放。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.util
 *
 * 功能详述：
 *   通过静态代码块在类加载时自动注册 MySQL JDBC 驱动，
 *   并提供获取数据库连接的静态方法以及关闭 JDBC 资源的静态方法。
 *   所有数据库连接参数以常量形式定义，便于统一维护与修改。
 *
 * 依赖：
 *   mysql-connector-java（MySQL JDBC 驱动）
 */
public class DBUtil {

    /** JDBC 驱动类名 */
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    /** 数据库连接 URL */
    private static final String URL =
            "jdbc:mysql://localhost:3306/journal_submission_system"
                    + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";

    /** 数据库用户名 */
    private static final String USERNAME = "root";

    /** 数据库密码 */
    private static final String PASSWORD = "root";

    /*
     * 静态代码块：在类加载时自动注册 JDBC 驱动
     */
    static {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载数据库驱动失败：" + DRIVER_CLASS_NAME, e);
        }
    }

    /**
     * 获取数据库连接。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 已建立的数据库连接对象
     * @throws SQLException 如果获取数据库连接时发生错误
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭 JDBC 资源，依次关闭 ResultSet、Statement 和 Connection。
     * 每个资源在关闭前会进行空值判断，避免对 null 调用 close 方法。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param conn  数据库连接对象，可为 null
     * @param stmt  Statement 对象，可为 null
     * @param rs    ResultSet 对象，可为 null
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // 忽略关闭 ResultSet 时的异常
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // 忽略关闭 Statement 时的异常
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // 忽略关闭 Connection 时的异常
            }
        }
    }
}
