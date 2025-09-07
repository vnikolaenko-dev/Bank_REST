package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @EntityGraph(attributePaths = "user")
    List<Card> findAll();
}
