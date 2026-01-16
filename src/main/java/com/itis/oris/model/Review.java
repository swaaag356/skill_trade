package com.itis.oris.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Integer id;
    private User fromUser;
    private User toUser;
    private Integer rating;
    private String comment;


}