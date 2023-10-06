package io.github.mateusz00.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("SharedDeck")
@CompoundIndex(
        name = "sharedDeck_language_tags",
        def = "{'language': 1, 'tags': 1}")
@CompoundIndex(
        name = "sharedDeck_language_cardCount",
        def = "{'language': 1, 'cardCount': 1}")
@CompoundIndex(
        name = "sharedDeck_language_popularity",
        def = "{'language': 1, 'popularity': 1}")
@CompoundIndex(
        name = "sharedDeck_tags_cardCount",
        def = "{'tags': 1, 'cardCount': 1}")
@CompoundIndex(
        name = "sharedDeck_tags_popularity",
        def = "{'tags': 1, 'popularity': 1}")
public class SharedDeck
{
    @Id
    private String id;
    private String ownerUserId;
    private String ownerUsername;
    @CreatedDate
    private Instant sharedAt;
    @Indexed
    private String name;
    private String language;
    private List<String> tags;
    private String description;
    private int cardCount;
    @Indexed
    private int popularity;
}
