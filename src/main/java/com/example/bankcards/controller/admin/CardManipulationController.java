package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardManipulateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.translator.FromCardToCardResponse;
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
    public ResponseEntity<CardResponse> createCard(@RequestBody CardCreateRequest request) {
        Card card = cardService.createCard(request);
        return ResponseEntity.ok(FromCardToCardResponse.doResponse(card));
    }

    @PostMapping("/change-status")
    public ResponseEntity<String> changeCardStatus(CardManipulateRequest request,
                                                   @RequestParam Card.CardStatus status) {
        Card card = cardService.findCardByNumber(request.number())
                .orElseThrow(() -> new RuntimeException("Card not found: " + request.number()));
        card.setStatus(status);
        cardService.updateCard(card);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCard(CardManipulateRequest request) {
        Card card = cardService.findCardByNumber(request.number())
                .orElseThrow(() -> new RuntimeException("Card not found: " + request.number()));
        cardService.deleteCard(card);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CardResponse>> getCards() {
        List<CardResponse> response = cardService.getAllCards().stream()
                .map(FromCardToCardResponse::doResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-user-cards/{username}")
    public ResponseEntity<List<CardResponse>> getCards(@PathVariable String username) {
        List<CardResponse> response = cardService.getAllCards().stream()
                .filter(card -> card.getOwner().getUsername().equals(username))
                .map(FromCardToCardResponse::doResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
