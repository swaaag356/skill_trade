package com.itis.oris.model;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    private Integer id;
    private String name;

    private Set<User> users;

    private Set<TradeOffer> offeredIn;

    private Set<TradeOffer> requestedIn;
}