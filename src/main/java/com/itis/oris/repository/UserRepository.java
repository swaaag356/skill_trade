package com.itis.oris.repository;

import com.itis.oris.model.User;
import com.itis.oris.util.DbConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? userMapper(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по ID", e);
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? userMapper(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по логину", e);
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? userMapper(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по email", e);
        }
    }

    public void save(User user) {
        String sql = """
                INSERT INTO users (username, password_hash, email, about, rating)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAbout());
            ps.setObject(5, user.getRating() != null ? user.getRating() : BigDecimal.ZERO);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения пользователя", e);
        }
    }

    public void updateRating(Integer userId, BigDecimal rating) {
        String sql = "UPDATE users SET rating = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, rating.setScale(1, BigDecimal.ROUND_HALF_UP));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления рейтинга", e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(userMapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех пользователей", e);
        }
        return users;
    }

    public User userMapper(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .email(rs.getString("email"))
                .about(rs.getString("about"))
                .rating(rs.getObject("rating", BigDecimal.class))
                .build();
    }
}