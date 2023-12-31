package io.github.mateusz00.entity;

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
@TypeAlias("GrammarSettings")
public class GrammarSettings
{
    @Id
    private String id;
    private SupportedLanguage language;
    private String userId;
    private String grammar;
    private CustomGrammarSettingsDetails settings;
}
