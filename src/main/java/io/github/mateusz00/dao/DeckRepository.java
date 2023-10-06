package io.github.mateusz00.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.Deck;

public interface DeckRepository extends MongoRepository<Deck, String>
{
    Page<Deck> findAllByUserIdAndLanguage(String userId, String language, Pageable pageable);

    List<Deck> findAllByUserId(String userId);
}