package com.example.bankcards.controller.user;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UserService userService;

    public record Transaction(String login, String password, String cardNumberFrom, String cardNumberTo, int amount) {}
    public record UserRequest(String login, String password) {}


    @PostMapping("/get-all")
    public ResponseEntity<List<Card>> getAllCards(UserRequest userRequest) {
        User user = userService.checkUserAndPassword(userRequest.login, userRequest.password);
        List<Card> cards = cardService.getCardsByUser(user);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/make-transaction")
    public ResponseEntity<String> makeTransaction(Transaction transaction) {
        userService.checkUserAndPassword(transaction.login, transaction.password);

        Card cardFrom = cardService.findCardByNumber(transaction.cardNumberFrom())
                .orElseThrow(() -> new RuntimeException("Card not found: " + transaction.cardNumberFrom()));
        Card cardTo = cardService.findCardByNumber(transaction.cardNumberTo())
                .orElseThrow(() -> new RuntimeException("Card not found: " + transaction.cardNumberTo()));

        boolean success = cardService.makeTransaction(cardFrom, cardTo, transaction.amount());
        if (success) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.badRequest().body("transaction failed");
    }
}
