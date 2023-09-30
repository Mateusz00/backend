package io.github.mateusz00.dao;

import org.springframework.data.domain.Page;

import io.github.mateusz00.entity.Card;
import io.github.mateusz00.service.deck.CardPageQuery;

public interface CustomCardRepository
{
    Page<Card> listCards(CardPageQuery pageQuery);
}
