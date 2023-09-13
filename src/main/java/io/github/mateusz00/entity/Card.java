package io.github.mateusz00.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
public class Card
{
    @Id
    private String id;
    @Indexed
    private String deckId;
    private List<CardContent> front;
    private List<CardContent> back;
    private CardStatus status;
    @Indexed
    private Instant nextReview;
    private float easeRate;
    private int interval;
    private boolean isSuspended;
    private Integer currentStep;
    private CardStatistics statistics;
    private boolean isLeech;
}
