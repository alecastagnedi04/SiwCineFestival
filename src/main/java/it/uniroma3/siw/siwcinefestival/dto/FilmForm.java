package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class FilmForm {


    @NotBlank(message = "Il titolo del film è obbligatorio.")
    @Size(max = 255, message = "Il titolo è troppo lungo.")
    private String title;


    @NotNull(message = "L'anno è obbligatorio.")
    @Min(value = 1888, message = "L'anno del film non è valido.")
    private Integer year;


    @NotNull(message = "La durata è obbligatoria.")
    @Positive(message = "La durata deve essere maggiore di zero.")
    private Integer duration;


    @NotBlank(message = "Il genere è obbligatorio.")
    private String genre;


    @NotBlank(message = "Il paese di produzione è obbligatorio.")
    private String productionCountry;


    @NotNull(message = "Seleziona un regista.")
    private Long directorId;



    public FilmForm() {
    }



    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public Integer getYear() {
        return year;
    }


    public void setYear(Integer year) {
        this.year = year;
    }


    public Integer getDuration() {
        return duration;
    }


    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public String getGenre() {
        return genre;
    }


    public void setGenre(String genre) {
        this.genre = genre;
    }


    public String getProductionCountry() {
        return productionCountry;
    }


    public void setProductionCountry(String productionCountry) {
        this.productionCountry = productionCountry;
    }


    public Long getDirectorId() {
        return directorId;
    }


    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }
}