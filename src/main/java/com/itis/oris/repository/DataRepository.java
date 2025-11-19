package com.itis.oris.repository;

import com.itis.oris.util.DbConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataRepository {

    public int getActiveOffersCount() {
        String sql = "SELECT COUNT(*) as count FROM trade_offers WHERE status IN ('active', 'in_progress')";
        return executeCountQuery(sql);
    }

    public int getCompletedTradesCount() {
        String sql = "SELECT COUNT(*) as count FROM trade_offers WHERE status = 'done'";
        return executeCountQuery(sql);
    }

    public int getUsersCount() {
        String sql = "SELECT COUNT(*) as count FROM users";
        return executeCountQuery(sql);
    }

    private int executeCountQuery(String sql) {
        try (Connection connection = DbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt("count");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}