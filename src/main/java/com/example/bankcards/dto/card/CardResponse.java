package com.example.bankcards.dto.card;

import com.example.bankcards.entity.Card;

import java.time.YearMonth;

public record CardResponse(String number, YearMonth expiryDate, Card.CardStatus status, Double balance, String owner) {
}
