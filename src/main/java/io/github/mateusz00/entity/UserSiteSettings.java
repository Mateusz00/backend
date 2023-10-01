package io.github.mateusz00.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSiteSettings
{
    private SupportedLanguage displayLanguage;
    private SupportedLanguage instructionsLanguage; // Null means use non-translated instruction
}
