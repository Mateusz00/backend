package io.github.mateusz00.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deck
{
    @Id
    private String id;
    private String sharedDeckId;
    private String name;
    private String userId;
    private String language;
    private DeckSettings customSettings;
    private DeckSettings effectiveSettings;
}
