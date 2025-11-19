package com.itis.oris.service;

import com.itis.oris.model.TradeOffer;
import com.itis.oris.model.TradeResponse;
import com.itis.oris.model.User;
import com.itis.oris.model.enums.Status;
import com.itis.oris.repository.TradeOfferRepository;
import com.itis.oris.repository.TradeResponseRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TradeResponseService {

    private static final Logger log = LogManager.getLogger(TradeResponseService.class);
    private final TradeResponseRepository responseRepository;
    private final TradeOfferRepository offerRepository;
    private final Connection conn;

    public TradeResponseService(TradeResponseRepository responseRepository, TradeOfferRepository offerRepository, Connection conn) {
        this.responseRepository = responseRepository;
        this.offerRepository = offerRepository;
        this.conn = conn;
    }

    public void respond(Integer offerId, User responder, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        TradeOffer offer = offerRepository.findById(offerId);
        if (offer == null || !offer.getStatus().equals(Status.ACTIVE)) {
            throw new IllegalArgumentException("Предложение недоступно");
        }
        if (offer.getUser().getId().equals(responder.getId())) {
            throw new IllegalArgumentException("Нельзя откликаться на своё предложение");
        }

        TradeResponse response = TradeResponse.builder()
                .tradeOffer(offer)
                .responder(responder)
                .message(message.trim())
                .build();

        try {
            conn.setAutoCommit(false);
            responseRepository.save(response);
            offer.setStatus(Status.IN_PROGRESS);
            offerRepository.updateStatus(offerId, Status.IN_PROGRESS);
            conn.commit();
            log.info("Отклик на предложение {} от {}", offerId, responder.getUsername());
        } catch (SQLException e) {
            rollback();
            log.error("Ошибка отправки отклика", e);
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    private void rollback() {
        try {
            if (conn != null && !conn.getAutoCommit()) conn.rollback();
        } catch (SQLException e) {
            log.error("Ошибка rollback", e);
        }
    }

    public List<TradeResponse> findByOfferId(Integer offerId) {
        return responseRepository.findByOfferId(offerId);
    }
}