package com.example.bankcards.controller.user;

import com.example.bankcards.dto.query.BlockCardQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QueryToBlockCard;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.QueryService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/query")
public class QueryController {
    private final CardService cardService;
    private final UserService userService;
    private final QueryService requestService;

    @PostMapping("/make-query-to-block")
    public ResponseEntity<String> blockCard(@RequestBody BlockCardQuery request) {
        userService.checkUserAndPassword(request.username(), request.password());
        Card card = cardService.findCardByNumber(request.cardNumber()).orElseThrow();
        QueryToBlockCard requestToBlockCard = new QueryToBlockCard();
        requestToBlockCard.setCard(card);
        requestToBlockCard.setDescription(request.description());
        requestService.saveRequest(requestToBlockCard);
        return ResponseEntity.ok("success");
    }

}
