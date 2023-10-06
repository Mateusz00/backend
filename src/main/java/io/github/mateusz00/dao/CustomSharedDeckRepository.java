package io.github.mateusz00.dao;

import org.springframework.data.domain.Page;

import io.github.mateusz00.entity.SharedDeck;
import io.github.mateusz00.service.deck.shared.SharedDeckPageQuery;

public interface CustomSharedDeckRepository
{
    Page<SharedDeck> listSharedDecks(SharedDeckPageQuery pageQuery);
}
