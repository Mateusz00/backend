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
public class GrammarSettings
{
    @Id
    private String id;
    private SupportedLanguage language;
    private String userId;
    private String grammar;
    private CustomGrammarSettingsDetails settings;
}
