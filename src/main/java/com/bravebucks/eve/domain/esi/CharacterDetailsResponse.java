package com.bravebucks.eve.domain.esi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDetailsResponse {

    @JsonProperty("CharacterID")
    private Integer characterId;

    @JsonProperty("CharacterName")
    private String characterName;

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Integer characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(final String characterName) {
        this.characterName = characterName;
    }
}
