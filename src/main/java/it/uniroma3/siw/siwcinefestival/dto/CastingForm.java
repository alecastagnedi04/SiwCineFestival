package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CastingForm {


    @NotNull(message = "Seleziona un attore.")
    private Long actorId;


    @NotBlank(message = "Il nome del personaggio è obbligatorio.")
    @Size(
        max = 200,
        message = "Il nome del personaggio è troppo lungo."
    )
    private String characterName;



    public CastingForm() {
    }



    public Long getActorId() {
        return actorId;
    }


    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }


    public String getCharacterName() {
        return characterName;
    }


    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}