package com.itis.oris.service;

import com.itis.oris.model.User;
import com.itis.oris.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Connection conn;

    public UserService(UserRepository userRepository, Connection conn) {
        this.userRepository = userRepository;
        this.conn = conn;
    }

    public User register(String username, String password, String email, String about) {
        if (username == null || username.trim().length() < 3) {
            log.warn("Попытка регистрации с коротким логином: {}", username);
            throw new IllegalArgumentException("Логин должен быть не менее 3 символов");
        }
        if (password == null || password.length() < 6) {
            log.warn("Попытка регистрации с коротким паролем");
            throw new IllegalArgumentException("Пароль должен быть не менее 6 символов");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            log.warn("Некорректный email: {}", email);
            throw new IllegalArgumentException("Неверный формат email");
        }

        if (userRepository.findByUsername(username) != null) {
            log.info("Попытка регистрации с существующим логином: {}", username);
            throw new IllegalArgumentException("Логин уже занят");
        }
        if (userRepository.findByEmail(email) != null) {
            log.info("Попытка регистрации с существующим email: {}", email);
            throw new IllegalArgumentException("Email уже используется");
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = User.builder()
                .username(username.trim())
                .passwordHash(passwordHash)
                .email(email.trim())
                .about(about != null ? about.trim() : null)
                .rating(BigDecimal.ZERO)
                .build();

        try {
            conn.setAutoCommit(false);
            userRepository.save(user);
            conn.commit();
            log.info("Пользователь зарегистрирован: {}", username);
            return user;
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка при регистрации пользователя {}", username, e);
            throw new RuntimeException("Ошибка базы данных при регистрации", e);
        }
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash()).verified) {
            log.warn("Неудачная попытка входа: {}", username);
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        log.info("Пользователь вошёл: {}", username);
        return user;
    }

    public User findById(Integer id) {
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Пользователь не найден по ID: {}", id);
            throw new IllegalArgumentException("Пользователь не найден");
        }
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private void rollback() {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                log.debug("Транзакция откатана");
            }
        } catch (SQLException e) {
            log.error("Ошибка при rollback", e);
        }
    }
}