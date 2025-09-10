package com.example.bankcards.util.translator;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.Card;

public class FromCardToCardResponse {
    public static CardResponse doResponse(
            Card card
    ){
        return new CardResponse(
                card.getNumber(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance(),
                card.getOwner().getUsername()
        );
    }

}
