package com.itis.oris.service;

import com.itis.oris.model.Review;
import com.itis.oris.model.User;
import com.itis.oris.repository.ReviewRepository;
import com.itis.oris.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private static final Logger log = LogManager.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final Connection conn;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, Connection conn) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.conn = conn;
    }

    public void leaveReview(User from, User to, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Нельзя оставить отзыв себе");
        }

        Review review = Review.builder()
                .fromUser(from)
                .toUser(to)
                .rating(rating)
                .comment(comment != null ? comment.trim() : null)
                .build();

        try {
            conn.setAutoCommit(false);
            reviewRepository.save(review);
            recalculateUserRating(to.getId());
            conn.commit();
            log.info("Отзыв от {} к {}: {}", from.getUsername(), to.getUsername(), rating);
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка при оставлении отзыва", e);
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    private void recalculateUserRating(Integer userId) {
        List<Review> reviews = reviewRepository.findByToUserId(userId);
        if (reviews.isEmpty()) {
            userRepository.updateRating(userId, BigDecimal.ZERO);
            return;
        }

        BigDecimal sum = reviews.stream()
                .map(Review::getRating)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avg = sum.divide(BigDecimal.valueOf(reviews.size()), 1, RoundingMode.HALF_UP);
        userRepository.updateRating(userId, avg);
    }

    private void rollback() {
        try {
            if (conn != null && !conn.getAutoCommit()) conn.rollback();
        } catch (SQLException e) {
            log.error("Ошибка rollback", e);
        }
    }
}