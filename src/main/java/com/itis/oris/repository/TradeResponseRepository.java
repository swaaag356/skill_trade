package com.itis.oris.repository;

import com.itis.oris.model.TradeResponse;
import com.itis.oris.util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.itis.oris.model.*;

public class TradeResponseRepository {

    public void save(TradeResponse response) {
        String sql = """
                INSERT INTO trade_responses (trade_offer_id, responder_id, message)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, response.getTradeOffer().getId());
            ps.setInt(2, response.getResponder().getId());
            ps.setString(3, response.getMessage());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                response.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения отклика", e);
        }
    }

    public List<TradeResponse> findByOfferId(Integer offerId) {
        String sql = """
                SELECT tr.*, u.id as uid
                FROM trade_responses tr
                JOIN users u ON tr.responder_id = u.id
                WHERE tr.trade_offer_id = ?
                ORDER BY tr.id
                """;
        List<TradeResponse> responses = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                responses.add(responseMapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения откликов", e);
        }
        return responses;
    }

    private TradeResponse responseMapper(ResultSet rs) throws SQLException {
        User responder = new UserRepository().findById(rs.getInt("uid"));
        TradeOffer offer = new TradeOffer();
        offer.setId(rs.getInt("trade_offer_id"));

        return TradeResponse.builder()
                .id(rs.getInt("id"))
                .tradeOffer(offer)
                .responder(responder)
                .message(rs.getString("message"))
                .build();
    }
}