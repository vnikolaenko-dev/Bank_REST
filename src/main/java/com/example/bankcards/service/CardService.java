package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserService userService;

    @Transactional
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

    public Optional<Card> findCardByNumber(String cardNumber) {
        return cardRepository.findByNumber(cardNumber);
    }

    public List<Card> getCardsByUser(User user) {
        return cardRepository.getCardsByBankUser(user);
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

    @Transactional
    @Modifying
    public boolean makeTransaction(Card cardFrom, Card cardTo, int amount) {
        if (cardFrom.getBankUser().getLogin().equals(cardTo.getBankUser().getLogin())
                && amount >= 0
                && cardFrom.getStatus() == Card.CardStatus.ACTIVE
                && cardTo.getStatus() == Card.CardStatus.ACTIVE
                && cardFrom.getBalance() >= amount) {
            cardRepository.updateCard(cardFrom);
            cardRepository.updateCard(cardTo);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateCard(Card cardFrom){
        cardRepository.updateCard(cardFrom);
    }

    @Transactional
    public void deleteCard(Card cardFrom) {
        cardRepository.delete(cardFrom);
    }
}
