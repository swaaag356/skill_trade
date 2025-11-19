package com.itis.oris.repository;

import com.itis.oris.model.Skill;
import com.itis.oris.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillRepository {

    public Skill findById(Integer id) {
        String sql = "SELECT * FROM skills WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapSkill(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска навыка", e);
        }
    }

    public Skill findByName(String name) {
        String sql = "SELECT * FROM skills WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapSkill(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска навыка по имени", e);
        }
    }

    public List<Skill> findAll() {
        String sql = "SELECT * FROM skills ORDER BY name";
        List<Skill> skills = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                skills.add(mapSkill(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех навыков", e);
        }
        return skills;
    }

    public void save(Skill skill) {
        String sql = "INSERT INTO skills (name) VALUES (?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, skill.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                skill.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения навыка", e);
        }
    }

    public Skill mapSkill(ResultSet rs) throws SQLException {
        return Skill.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }



}