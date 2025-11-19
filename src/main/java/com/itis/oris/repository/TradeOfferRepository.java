package com.itis.oris.repository;

import com.itis.oris.model.TradeOffer;
import com.itis.oris.model.enums.Status;
import com.itis.oris.util.DbConnection;
import com.itis.oris.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradeOfferRepository {
    private final TradeResponseRepository responseRepo;

    public TradeOfferRepository(TradeResponseRepository responseRepo) {
        this.responseRepo = responseRepo;
    }

    public List<TradeOffer> findInProgressOffers() {
        String sql = """
            SELECT toff.*, u.*,
            s1.id AS s1id, s1.name AS s1name,
            s2.id AS s2id, s2.name AS s2name
            FROM trade_offers toff
            JOIN users u ON toff.user_id = u.id
            JOIN skills s1 ON toff.offer_skill_id = s1.id
            JOIN skills s2 ON toff.request_skill_id = s2.id
            WHERE toff.status = 'in_progress'
            ORDER BY toff.id DESC
            """;
        return findOffersBySql(sql);
    }

    public List<TradeOffer> findActiveOffers() {
        String sql = """
                SELECT toff.*, u.*, 
                
                s1.id AS s1id,
                s1.name AS s1name,
                
                s2.id AS s2id,
                s2.name AS s2name
                
                FROM trade_offers toff
                JOIN users u ON toff.user_id = u.id
                JOIN skills s1 ON toff.offer_skill_id = s1.id
                JOIN skills s2 ON toff.request_skill_id = s2.id
                WHERE toff.status = 'active'
                ORDER BY toff.id DESC
                """;
        return findOffersBySql(sql);
    }

    public List<TradeOffer> findOffersByUserId(Integer userId) {
        String sql = """
            SELECT toff.*, u.*,
            
            s1.id AS s1id,
            s1.name AS s1name,
            
            s2.id AS s2id,
            s2.name AS s2name
            
            FROM trade_offers toff
            JOIN users u ON toff.user_id = u.id
            JOIN skills s1 ON toff.offer_skill_id = s1.id
            JOIN skills s2 ON toff.request_skill_id = s2.id
            WHERE toff.user_id = ?""";

        List<TradeOffer> offers = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Сначала задаём параметр
            ps.setInt(1, userId);

            // Потом выполняем запрос
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offers.add(mapTradeOffer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска предложения", e);
        }
        return offers;
    }

    public TradeOffer findById(Integer id) {
        String sql = """
            SELECT toff.*, u.*,
            s1.id AS s1id, s1.name AS s1name,
            s2.id AS s2id, s2.name AS s2name
            FROM trade_offers toff
            JOIN users u ON toff.user_id = u.id
            JOIN skills s1 ON toff.offer_skill_id = s1.id
            JOIN skills s2 ON toff.request_skill_id = s2.id
            WHERE toff.id = ?
            """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TradeOffer offer = mapTradeOffer(rs);
                    offer.setResponses(responseRepo.findByOfferId(id));
                    return offer;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска предложения", e);
        }
    }

    public void save(TradeOffer offer) {
        String sql = """
                INSERT INTO trade_offers (user_id, offer_skill_id, request_skill_id, description, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, offer.getUser().getId());
            ps.setInt(2, offer.getOfferSkill().getId());
            ps.setInt(3, offer.getRequestSkill().getId());
            ps.setString(4, offer.getDescription());
            ps.setString(5, offer.getStatus().getValue());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                offer.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания предложения", e);
        }
    }

    public void updateStatus(Integer offerId, Status status) {
        String sql = "UPDATE trade_offers SET status = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.getValue());
            ps.setInt(2, offerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления статуса", e);
        }
    }

    private List<TradeOffer> findOffersBySql(String sql) {
        List<TradeOffer> offers = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                offers.add(mapTradeOffer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения предложений", e);
        }
        return offers;
    }

    private TradeOffer mapTradeOffer(ResultSet rs) throws SQLException {
        User user = new UserRepository().userMapper(rs);
        Skill offerSkill = Skill.builder()
                .id(rs.getInt("s1id"))
                .name(rs.getString("s1name"))
                .build();;
        Skill requestSkill = Skill.builder()
                .id(rs.getInt("s2id"))
                .name(rs.getString("s2name"))
                .build();

        return TradeOffer.builder()
                .id(rs.getInt("id"))
                .user(user)
                .offerSkill(offerSkill)
                .requestSkill(requestSkill)
                .description(rs.getString("description"))
                .status(Status.fromValue(rs.getString("status")))
                .build();
    }
}