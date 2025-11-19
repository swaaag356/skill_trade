package com.itis.oris.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeResponse {
    private Integer id;
    private TradeOffer tradeOffer;
    private User responder;
    private String message;
}