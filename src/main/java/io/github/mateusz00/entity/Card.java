package io.github.mateusz00.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Card")
public class Card
{
    @Id
    private String id;
    @Indexed
    @NonNull
    private String deckId;
    private List<CardContent> front;
    private List<CardContent> back;
    @NonNull
    private CardStatus status = CardStatus.NOT_SEEN;
    @Indexed
    private Instant nextReview;
    private float easeRate = 1.f;
    private int interval = 0;
    private boolean suspended;
    private Integer currentStep;
    @NonNull
    private CardStatistics statistics = new CardStatistics();
    private boolean leech = false;
}
