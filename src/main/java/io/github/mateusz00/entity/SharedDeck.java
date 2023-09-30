package io.github.mateusz00.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
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
@TypeAlias("SharedDeck")
public class SharedDeck
{
    @Id
    private String id;
    private String ownerUserId;
    private String ownerUsername;
    @CreatedDate
    private Instant sharedAt;
    private String name;
    @Indexed
    private String language;
    @Indexed
    private List<String> tags;
    private String description;
    private int cardCount;
    @Indexed
    private int popularity;
}
