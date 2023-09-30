package io.github.mateusz00.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Exercise")
public class Exercise
{
    @Id
    private String id;
    private String instruction;
    private List<TranslatedInstruction> translatedInstructions;
    private String imgUrl;
    private List<Answer> answers;
    private String body;
    private String audioUrl;
    private String useCase;
    private String grammar;
}
