package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;

import java.time.YearMonth;

public record CardWithOwnerResponse(String number, YearMonth expiryDate, Card.CardStatus status, Double balance, String owner) {
}
