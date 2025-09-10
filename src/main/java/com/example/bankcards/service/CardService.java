package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public Card createCard(CardCreateRequest createCardRequest) {
        System.out.println(createCardRequest);
        User user = userRepository.findUserByUsername(createCardRequest.username())
                .orElseThrow(() -> new RuntimeException("User not found: " + createCardRequest.username()));
        Card card = new Card();
        card.setOwner(user);
        card.setExpiryDate(YearMonth.now().plusYears(3));
        card.setStatus(Card.CardStatus.INACTIVE);
        card.setNumber(generateCardNumber());
        card.setBalance(createCardRequest.balance());
        return cardRepository.save(card);
    }

    public Optional<Card> findCardByNumber(String cardNumber) {
        return cardRepository.findByNumber(cardNumber);
    }

    public List<Card> getCardsByUser(User user) {
        return cardRepository.findByOwner(user);
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
        if (cardFrom.getOwner().getUsername().equals(cardTo.getOwner().getUsername())
                && amount >= 0
                && cardFrom.getStatus() == Card.CardStatus.ACTIVE
                && cardTo.getStatus() == Card.CardStatus.ACTIVE
                && cardFrom.getBalance() >= amount) {
            cardFrom.setBalance(cardFrom.getBalance() - amount);
            cardTo.setBalance(cardTo.getBalance() + amount);
            cardRepository.save(cardFrom);
            cardRepository.save(cardTo);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateCard(Card cardFrom){
        cardRepository.save(cardFrom);
    }

    @Transactional
    public void deleteCard(Card cardFrom) {
        cardRepository.delete(cardFrom);
    }

    public void checkCards(User user) {
        for (Card card: getCardsByUser(user)) {
            if (card.getExpiryDate().compareTo(YearMonth.now()) < 0) {
                card.setStatus(Card.CardStatus.SERVICE_PERIOD_IS_OVER);
                cardRepository.save(card);
            }
        }

    }
}
