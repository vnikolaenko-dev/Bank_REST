package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.user.QueryController;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QueryToBlockCard;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/query-control")
@RequiredArgsConstructor
public class QueryManipulationController {
    private final QueryService queryService;
    private final CardService cardService;

    @GetMapping("get-all")
    public List<QueryToBlockCard> getQueries(){
        return queryService.getAllQueries();
    }

    @GetMapping("approve/{card-number}")
    public void approveQuery(@PathVariable("card-number") String cardNumber){
        Card card = cardService.findCardByNumber(cardNumber).orElseThrow();
        card.setStatus(Card.CardStatus.BLOCKED);
        cardService.updateCard(card);
        queryService.deleteRequestByCard(card);
    }

    @GetMapping("reject/{card-number}")
    public void rejectQuery(@PathVariable("card-number") String cardNumber){
        Card card = cardService.findCardByNumber(cardNumber).orElseThrow();
        queryService.deleteRequestByCard(card);
    }
}
