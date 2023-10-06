package io.github.mateusz00.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.mateusz00.entity.SharedDeck;
import io.github.mateusz00.service.deck.shared.SharedDeckPageQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSharedDeckRepositoryImpl implements CustomSharedDeckRepository
{
    public static final String NAME = "name";
    public static final String LANGUAGE = "language";
    public static final String TAGS = "tags";
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<SharedDeck> listSharedDecks(SharedDeckPageQuery pageQuery)
    {
        PageRequest pageRequest = pageQuery.getPageRequest();
        var query = new Query();
        query.addCriteria(new Criteria(NAME).is(pageQuery.getName()));
        query.addCriteria(new Criteria(LANGUAGE).is(pageQuery.getLanguage()));
        query.addCriteria(new Criteria(TAGS).is(pageQuery.getTag()));

        long count = mongoTemplate.count(query, SharedDeck.class);
        List<SharedDeck> decks = mongoTemplate.find(query.with(pageRequest), SharedDeck.class);
        return new PageImpl<>(decks, pageRequest, count);
    }
}
