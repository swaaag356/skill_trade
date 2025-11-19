package com.itis.oris.service;

import com.itis.oris.model.Skill;
import com.itis.oris.repository.SkillRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SkillService {

    private static final Logger log = LogManager.getLogger(SkillService.class);
    private final SkillRepository skillRepository;
    private final Connection conn;

    public SkillService(SkillRepository skillRepository, Connection conn) {
        this.skillRepository = skillRepository;
        this.conn = conn;
    }

    public Skill createIfNotExists(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Попытка создать пустой навык");
            throw new IllegalArgumentException("Название навыка не может быть пустым");
        }

        String normalized = name.trim();
        Skill existing = skillRepository.findByName(normalized);
        if (existing != null) {
            return existing;
        }

        Skill skill = Skill.builder().name(normalized).build();
        try {
            conn.setAutoCommit(false);
            skillRepository.save(skill);
            conn.commit();
            log.info("Создан новый навык: {}", normalized);
            return skill;
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка при создании навыка: {}", normalized, e);
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public Skill findById(Integer id) {
        Skill skill = skillRepository.findById(id);
        if (skill == null) {
            log.warn("Навык не найден: {}", id);
            throw new IllegalArgumentException("Навык не найден");
        }
        return skill;
    }

    private void rollback() {
        try {
            if (conn != null && !conn.getAutoCommit()) conn.rollback();
        } catch (SQLException e) {
            log.error("Ошибка rollback", e);
        }
    }
}