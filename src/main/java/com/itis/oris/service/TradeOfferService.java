package com.itis.oris.service;

import com.itis.oris.model.Skill;
import com.itis.oris.model.TradeOffer;
import com.itis.oris.model.User;
import com.itis.oris.model.enums.Status;
import com.itis.oris.repository.SkillRepository;
import com.itis.oris.repository.TradeOfferRepository;
import com.itis.oris.repository.TradeResponseRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TradeOfferService {

    private static final Logger log = LogManager.getLogger(TradeOfferService.class);
    private final TradeOfferRepository tradeOfferRepository;
    private final SkillRepository skillRepository;
    private final TradeResponseRepository responseRepository;
    private final Connection conn;

    public TradeOfferService(TradeOfferRepository tradeOfferRepository, SkillRepository skillRepository,
                             TradeResponseRepository responseRepository, Connection conn) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.skillRepository = skillRepository;
        this.responseRepository = responseRepository;
        this.conn = conn;
    }

    public TradeOffer findById(int id) {
        return tradeOfferRepository.findById(id);
    }

    public List<TradeOffer> getInProgressOffers() {
        return tradeOfferRepository.findInProgressOffers();
    }

    public List<TradeOffer> getActiveOffers() {
        return tradeOfferRepository.findActiveOffers();
    }

    public TradeOffer create(User user, String offerSkillName, String requestSkillName, String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }

        Skill offerSkill = getOrCreateSkill(offerSkillName);
        Skill requestSkill = getOrCreateSkill(requestSkillName);

        if (offerSkill.getId().equals(requestSkill.getId())) {
            throw new IllegalArgumentException("Навыки должны быть разными");
        }

        TradeOffer offer = TradeOffer.builder()
                .user(user)
                .offerSkill(offerSkill)
                .requestSkill(requestSkill)
                .description(description.trim())
                .status(Status.ACTIVE)
                .build();

        try {
            conn.setAutoCommit(false);
            tradeOfferRepository.save(offer);
            conn.commit();
            log.info("Создано предложение от {}: {} ↔ {}", user.getUsername(), offerSkill.getName(), requestSkill.getName());
            return offer;
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка создания предложения", e);
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    public void completeOffer(Integer offerId, User user) {
        TradeOffer offer = tradeOfferRepository.findById(offerId);
        if (offer == null) throw new IllegalArgumentException("Предложение не найдено");
        if (!offer.getStatus().equals(Status.IN_PROGRESS))
            throw new IllegalArgumentException("Можно завершить только предложение в статусе IN_PROGRESS");
        if (!offer.getUser().getId().equals(user.getId()) &&
                responseRepository.findByOfferId(offerId).stream()
                        .noneMatch(r -> r.getResponder().getId().equals(user.getId())))
            throw new IllegalArgumentException("Вы не участвуете в этом предложении");

        try {
            conn.setAutoCommit(false);
            tradeOfferRepository.updateStatus(offerId, Status.DONE);
            conn.commit();
            log.info("Предложение {} завершено пользователем {}", offerId, user.getUsername());
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка завершения предложения", e);
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    public void closeOffer(Integer offerId, User user) {
        TradeOffer offer = tradeOfferRepository.findById(offerId);
        if (offer == null) throw new IllegalArgumentException("Предложение не найдено");
        if (!offer.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Нельзя закрыть чужое предложение");
        }
        tradeOfferRepository.updateStatus(offerId, Status.DONE);
        log.info("Предложение {} закрыто пользователем {}", offerId, user.getUsername());
    }

    public List<TradeOffer> getOffersByUserId(Integer userId) {
        return tradeOfferRepository.findOffersByUserId(userId);
    }

    private Skill getOrCreateSkill(String name) {
        Skill skill = skillRepository.findByName(name.trim());
        if (skill != null) return skill;
        skill = Skill.builder().name(name.trim()).build();
        skillRepository.save(skill);
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