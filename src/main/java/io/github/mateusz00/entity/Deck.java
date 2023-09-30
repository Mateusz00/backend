package io.github.mateusz00.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Deck")
public class Deck
{
    @Id
    private String id;
    private String sharedDeckId;
    private String name;
    @Indexed
    private String userId;
    private String language;
    private CustomDeckSettings customSettings;
    private DeckSettings effectiveSettings;
}
