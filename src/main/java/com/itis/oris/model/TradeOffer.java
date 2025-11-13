package com.itis.oris.model;

import com.itis.oris.model.enums.Status;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeOffer {
    private Integer id;
    private User user;
    private Skill offerSkill;
    private Skill requestSkill;
    private String description;
    private Status status = Status.ACTIVE;

    private Set<TradeResponse> responses;
}