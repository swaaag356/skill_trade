package com.itis.oris.repository;

import com.itis.oris.model.Review;
import com.itis.oris.model.User;
import com.itis.oris.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    public void save(Review review) {
        String sql = """
            INSERT INTO reviews (from_user_id, to_user_id, rating, comment)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, review.getFromUser().getId());
            ps.setInt(2, review.getToUser().getId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения отзыва", e);
        }
    }

    public List<Review> findByToUserId(Integer userId) {
        String sql = """
            SELECT r.*, fu.*, tu.*
            FROM reviews r
            JOIN users fu ON r.from_user_id = fu.id
            JOIN users tu ON r.to_user_id = tu.id
            WHERE r.to_user_id = ?
            ORDER BY r.id DESC
            """;
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(mapReview(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения отзывов", e);
        }
        return reviews;
    }

    private Review mapReview(ResultSet rs) throws SQLException {
        User fromUser = new UserRepository().userMapper(rs);
        User toUser = new UserRepository().userMapper(rs);

        return Review.builder()
                .id(rs.getInt("id"))
                .fromUser(fromUser)
                .toUser(toUser)
                .rating(rs.getInt("rating"))
                .comment(rs.getString("comment"))
                .build();
    }
}