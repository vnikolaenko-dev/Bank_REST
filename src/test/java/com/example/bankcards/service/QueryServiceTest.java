package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QueryToBlockCard;
import com.example.bankcards.repository.QueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueryServiceTest {
    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    private QueryService queryService;

    @Test
    public void testSaveRequest() {
        QueryToBlockCard request = new QueryToBlockCard();
        request.setId(1L);
        when(queryRepository.save(any(QueryToBlockCard.class))).thenReturn(request);
        QueryToBlockCard savedRequest = queryService.saveRequest(request);
        assert savedRequest != null;
        assert savedRequest.getId() == 1L;
        verify(queryRepository, times(1)).save(request);
    }

    @Test
    public void testGetAllQueries() {
        QueryToBlockCard request1 = new QueryToBlockCard();
        request1.setId(1L);
        QueryToBlockCard request2 = new QueryToBlockCard();
        request2.setId(2L);
        List<QueryToBlockCard> expectedRequests = Arrays.asList(request1, request2);
        when(queryRepository.findAll()).thenReturn(expectedRequests);
        List<QueryToBlockCard> actualRequests = queryService.getAllQueries();
        assert actualRequests != null;
        assert actualRequests.size() == 2;
        assert actualRequests.equals(expectedRequests);
        verify(queryRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllQueries_EmptyList() {
        when(queryRepository.findAll()).thenReturn(List.of());
        List<QueryToBlockCard> actualRequests = queryService.getAllQueries();
        assert actualRequests != null;
        assert actualRequests.isEmpty();
        verify(queryRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteRequestByCard() {
        Card card = new Card();
        card.setId(1L);
        doNothing().when(queryRepository).deleteRequestToBlockCardByCard(any(Card.class));
        queryService.deleteRequestByCard(card);
        verify(queryRepository, times(1)).deleteRequestToBlockCardByCard(card);
    }

    @Test
    public void testDeleteRequestByCard_NullCard() {
        doNothing().when(queryRepository).deleteRequestToBlockCardByCard(isNull());
        queryService.deleteRequestByCard(null);
        verify(queryRepository, times(1)).deleteRequestToBlockCardByCard(null);
    }

    @Test
    public void testSaveRequest_NullInput() {
        when(queryRepository.save(null)).thenThrow(new IllegalArgumentException());
        try {
            queryService.saveRequest(null);
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }
}