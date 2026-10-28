package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class ReviewForm {


    @NotBlank(message = "Scrivi il testo della recensione.")
    @Size(
        max = 5000,
        message = "La recensione non può superare 5000 caratteri."
    )
    private String text;


    @NotNull(message = "Seleziona un voto.")
    @Min(value = 1, message = "Il voto minimo è 1.")
    @Max(value = 5, message = "Il voto massimo è 5.")
    private Integer rating;



    public ReviewForm() {
    }



    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public Integer getRating() {
        return rating;
    }


    public void setRating(Integer rating) {
        this.rating = rating;
    }
}