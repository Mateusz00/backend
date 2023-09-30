package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSiteSettings
{
    private SupportedLanguage displayLanguage;
    private SupportedLanguage instructionsLanguage;
}
