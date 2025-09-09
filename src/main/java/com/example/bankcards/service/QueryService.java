package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QueryToBlockCard;
import com.example.bankcards.repository.QueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {
    private final QueryRepository requestRepository;

    public QueryToBlockCard saveRequest(QueryToBlockCard request) {
        return requestRepository.save(request);
    }

    public List<QueryToBlockCard> getAllQueries() {
        return requestRepository.findAll();
    }

    public void deleteRequestByCard(Card card) {
        requestRepository.deleteRequestToBlockCardByCard(card);
    }
}
