package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardWithOwnerResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.ManipulateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/card-control")
@RequiredArgsConstructor
public class CardManipulationController {
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(@RequestBody CreateCardRequest request) {
        Card card = cardService.createCard(request);
        CardResponse cardResponse = new CardResponse(
                card.getNumber(),
                card.getExpiryDate(),
                card.getStatus()
        );
        return ResponseEntity.ok(cardResponse);
    }

    @PostMapping("/change-status")
    public ResponseEntity<String> changeCardStatus(ManipulateCardRequest request,
                                                   @RequestParam Card.CardStatus status) {
        Card card = cardService.findCardByNumber(request.number())
                .orElseThrow(() -> new RuntimeException("Card not found: " + request.number()));
        card.setStatus(status);
        cardService.updateCard(card);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCard(ManipulateCardRequest request) {
        Card card = cardService.findCardByNumber(request.number())
                .orElseThrow(() -> new RuntimeException("Card not found: " + request.number()));
        cardService.deleteCard(card);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CardWithOwnerResponse>> getCards() {
        List<CardWithOwnerResponse> response = cardService.getAllCards().stream()
                .map(card -> new CardWithOwnerResponse(
                        card.getNumber(),
                        card.getExpiryDate(),
                        card.getStatus(),
                        card.getBalance(),
                        card.getOwner().getUsername()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-user-cards/{username}")
    public ResponseEntity<List<CardWithOwnerResponse>> getCards(@PathVariable String username) {
        List<CardWithOwnerResponse> response = cardService.getAllCards().stream()
                .filter(card -> card.getOwner().getUsername().equals(username))
                .map(card ->
                        new CardWithOwnerResponse(
                        card.getNumber(),
                        card.getExpiryDate(),
                        card.getStatus(),
                        card.getBalance(),
                        card.getOwner().getUsername()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
