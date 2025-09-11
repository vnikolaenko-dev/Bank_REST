package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test-user");
    }

    @Test
    void findCardByNumber() {
        String number = "01234556789012345";
        Card card = new Card(1L, number, YearMonth.now().plusYears(3), Card.CardStatus.INACTIVE, 1000.0, user);
        when(cardRepository.findByNumber(number)).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.findCardByNumber(number);

        assert (result).isPresent();
        assert (result.get().getNumber().equals(number));
    }

    @Test
    void createCard_success() {
        CardCreateRequest request = new CardCreateRequest(user.getUsername(), 500.0);

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = cardService.createCard(request);

        assert (result.getOwner().getId().equals(user.getId()));
        assert (result.getStatus().equals(Card.CardStatus.INACTIVE));
        assert (result.getBalance().equals(500.0));
        verify(cardRepository).save(result);
    }

    @Test
    void createCard_userNotFound() {
        CardCreateRequest request = new CardCreateRequest("wrong-user", 500.0);
        when(userRepository.findUserByUsername("wrong-user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cardService.createCard(request));

        assert (ex.getMessage()).contains("User not found");
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getCardsByUser() {
        List<Card> cards = List.of(new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user));
        when(cardRepository.findByOwner(user)).thenReturn(cards);

        List<Card> result = cardService.getCardsByUser(user);

        assert (result.size() == 1);
        assert (result.get(0).getOwner().getId().equals(user.getId()));
    }

    @Test
    void getAllCards() {
        List<Card> cards =  List.of(new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user));
        when(cardRepository.findAll()).thenReturn(cards);

        List<Card> result = cardService.getAllCards();

        assert (result.size() == 1);
    }

    @Test
    void makeTransaction_success() {
        Card cardFrom = new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 1000.0, user);
        Card cardTo = new Card(2L, "2222", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user);

        boolean result = cardService.makeTransaction(cardFrom, cardTo, 300);

        assert (result);
        assert (cardFrom.getBalance().equals(700.0));
        assert (cardTo.getBalance().equals(500.0));
        verify(cardRepository).save(cardFrom);
        verify(cardRepository).save(cardTo);
    }

    @Test
    void makeTransaction_failsWhenNotEnoughBalance() {
        Card cardFrom = new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 100.0, user);
        Card cardTo = new Card(2L, "2222", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user);

        boolean result = cardService.makeTransaction(cardFrom, cardTo, 300);

        assert (!result);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateCard() {
        Card card = new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user);

        cardService.updateCard(card);

        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard() {
        Card card = new Card(1L, "1111", YearMonth.now().plusYears(1), Card.CardStatus.ACTIVE, 200.0, user);

        cardService.deleteCard(card);

        verify(cardRepository).delete(card);
    }

    @Test
    void checkCards_updatesExpiredCardStatus() {
        Card expired = new Card(1L, "1111", YearMonth.now().minusMonths(1), Card.CardStatus.ACTIVE, 200.0, user);
        Card valid = new Card(2L, "2222", YearMonth.now().plusMonths(6), Card.CardStatus.ACTIVE, 300.0, user);

        when(cardRepository.findByOwner(user)).thenReturn(List.of(expired, valid));

        cardService.checkCards(user);

        assert (expired.getStatus().equals(Card.CardStatus.SERVICE_PERIOD_IS_OVER));
        assert (valid.getStatus().equals(Card.CardStatus.ACTIVE));
        verify(cardRepository).save(expired);
        verify(cardRepository, never()).save(valid);
    }
}