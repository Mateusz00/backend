package io.github.mateusz00.entity;

import java.util.List;

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
@TypeAlias("SharedCard")
public class SharedCard
{
    @Id
    private String id;
    @Indexed
    private String sharedDeckId;
    private List<CardContent> front;
    private List<CardContent> back;
}
