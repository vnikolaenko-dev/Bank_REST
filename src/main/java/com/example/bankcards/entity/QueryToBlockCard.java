package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "request")
@Getter
@Setter
@NoArgsConstructor
public class QueryToBlockCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Card card;

    private String description;

    public QueryToBlockCard(Card card, String description) {
        this.description = description;
        this.card = card;
    }
}
