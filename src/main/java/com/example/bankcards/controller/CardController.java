package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardWithOwnerResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.ManipulateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/card-control")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(CreateCardRequest request) {
        try {
            Card card = cardService.createCard(request);
            CardResponse cardResponse = new CardResponse(card.getNumber(), card.getExpiryDate(), card.getStatus());
            return ResponseEntity.ok(cardResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/active")
    public ResponseEntity<String> activeCard(ManipulateCardRequest request) {
        try {

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("error");
        }

    }

    @PostMapping("/block")
    public ResponseEntity<String> blockCard(ManipulateCardRequest request) {
        try {

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("error");
        }

    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCard(ManipulateCardRequest request) {
        try {

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("error");
        }

    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CardWithOwnerResponse>> getCards() {
        try {
            ArrayList<Card> cards = cardService.getAllCards();
            List<CardWithOwnerResponse> response = cards.stream().map(
                    card -> new CardWithOwnerResponse(card.getNumber(), card.getExpiryDate(), card.getStatus(), card.getBankUser().getLogin())
            ).toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }
}
