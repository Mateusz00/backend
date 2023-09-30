package io.github.mateusz00.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.mateusz00.entity.Card;
import io.github.mateusz00.service.deck.CardPageQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCardRepositoryImpl implements CustomCardRepository
{
    public static final String DECK_ID = "deckId";
    public static final String STATUS = "status";
    public static final String LEECH = "leech";
    public static final String SUSPENDED = "suspended";
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Card> listCards(CardPageQuery pageQuery)
    {
        var pageRequest = pageQuery.getPageRequest();
        var query = new Query().with(pageRequest);

        query.addCriteria(new Criteria(DECK_ID).is(pageQuery.getDeckId()));
        var statusCriteria = switch (pageQuery.getStatusQuery())
        {
            case LEECH -> new Criteria(LEECH).is(true);
            case SUSPENDED -> new Criteria(SUSPENDED).is(true);
            default -> new Criteria(STATUS).is(pageQuery.getStatusQuery().name());
        };
        query.addCriteria(statusCriteria);

        long count = -1;
        if (pageQuery.isShouldGetTotal())
        {
             count = mongoTemplate.count(query, Card.class);
        }
        var cards = mongoTemplate.find(query, Card.class);
        return new PageImpl<>(cards , pageRequest, count);
    }
}
