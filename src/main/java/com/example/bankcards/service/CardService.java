package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public Card createCard(CreateCardRequest createCardRequest) {
        User user = userService.findUserByLogin(createCardRequest.login())
                .orElseThrow(() -> new RuntimeException("User not found: " + createCardRequest.login()));
        Card card = new Card();
        card.setBankUser(user);
        card.setExpiryDate(YearMonth.now().plusYears(3));
        card.setStatus(Card.CardStatus.INACTIVE);
        card.setNumber(generateCardNumber());
        return cardRepository.save(card);
    }


    public ArrayList<Card> getAllCards() {
        return (ArrayList<Card>) cardRepository.findAll();
    }

    private String generateCardNumber() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int digit = (int) (Math.random() * 10);
            number.append(digit);
        }
        return number.toString();
    }

}
