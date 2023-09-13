package io.github.mateusz00.entity;

import java.util.List;

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
public class SharedCard
{
    @Id
    private String id;
    private String sharedDeckId;
    private List<CardContent> front;
    private List<CardContent> back;
}
