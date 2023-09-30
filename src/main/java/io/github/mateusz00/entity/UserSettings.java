package io.github.mateusz00.entity;

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
@TypeAlias("UserSettings")
public class UserSettings
{
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private UserSiteSettings siteSettings;
    private DeckSettings defaultDecksSettings;
    private GrammarSettingsDetails defaultGrammarSettings;
}
