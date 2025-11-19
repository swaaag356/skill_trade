package com.itis.oris.model;

import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String email;
    private String about;
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    private Set<TradeOffer> offers;

    private Set<TradeResponse> responses;

    private Set<Review> givenReviews;

    private Set<Review> receivedReviews;
}