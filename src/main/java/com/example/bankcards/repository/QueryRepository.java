package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QueryToBlockCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<QueryToBlockCard, Long> {
    List<QueryToBlockCard> findAll();

    @Transactional
    void deleteRequestToBlockCardByCard(Card card);
}
