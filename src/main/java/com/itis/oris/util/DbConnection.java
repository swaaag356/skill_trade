package com.itis.oris.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DbConnection {

    private static DataSource dataSource;

    public static void init() {
        HikariConfig config = new HikariConfig("/db.properties");
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            try {
                init();
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void destroy() {
        ((HikariDataSource) dataSource).close();
    }

}