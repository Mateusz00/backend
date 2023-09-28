package io.github.mateusz00.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
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
@TypeAlias("UseCaseStatus")
public class UseCaseStatus
{
    @Id
    private String id;
    private SupportedLanguage language;
    private String useCase;
    private String grammar;
    private String userId;
    private Instant nextReview;
    private float easeRate;
    private int interval;
    private int completedExercisesToday;
    private int failedExercisesToday;
    private List<String> suspendedExercisesIds;
    private List<String> uncompletedExercisesIds;
    private boolean enabled;
}
