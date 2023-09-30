package io.github.mateusz00.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.SharedCard;

public interface SharedCardRepository extends MongoRepository<SharedCard, String>
{
    void deleteAllBySharedDeckId(String sharedDeckId);

    long countBySharedDeckId(String sharedDeckId);

    Page<SharedCard> findBySharedDeckId(String sharedDeckId, Pageable pageable);
}