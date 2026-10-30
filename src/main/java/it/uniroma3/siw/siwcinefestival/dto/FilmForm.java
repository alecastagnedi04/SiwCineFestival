package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class FilmForm {

    /*
     * =====================================================
     * DATI DEL FILM
     * =====================================================
     */

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



    /*
     * =====================================================
     * MODALITÀ DI SELEZIONE DEL REGISTA
     * =====================================================
     *
     * existing:
     *   utilizziamo un regista già presente.
     *
     * new:
     *   creiamo un nuovo regista insieme al film.
     *
     * Durante la modifica del film
     * utilizziamo sempre la modalità existing.
     */

    @NotBlank(message = "Seleziona una modalità per il regista.")
    @Pattern(
        regexp = "existing|new",
        message = "Modalità regista non valida."
    )
    private String directorMode = "existing";



    /*
     * =====================================================
     * REGISTA ESISTENTE
     * =====================================================
     *
     * Non utilizziamo @NotNull.
     *
     * Se l'utente sceglie un nuovo regista,
     * directorId può essere null.
     *
     * Il controllo viene effettuato
     * nel Service in base alla modalità.
     */

    private Long directorId;



    /*
     * =====================================================
     * NUOVO REGISTA
     * =====================================================
     */

    @Size(max = 255, message = "Il nome del regista è troppo lungo.")
    private String newDirectorName;


    @Size(max = 255, message = "Il cognome del regista è troppo lungo.")
    private String newDirectorSurname;



    /*
     * =====================================================
     * COSTRUTTORE
     * =====================================================
     */

    public FilmForm() {
    }



    /*
     * =====================================================
     * GETTER E SETTER — FILM
     * =====================================================
     */

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



    /*
     * =====================================================
     * GETTER E SETTER — REGISTA
     * =====================================================
     */

    public String getDirectorMode() {
        return directorMode;
    }


    public void setDirectorMode(String directorMode) {
        this.directorMode = directorMode;
    }


    public Long getDirectorId() {
        return directorId;
    }


    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }


    public String getNewDirectorName() {
        return newDirectorName;
    }


    public void setNewDirectorName(String newDirectorName) {
        this.newDirectorName = newDirectorName;
    }


    public String getNewDirectorSurname() {
        return newDirectorSurname;
    }


    public void setNewDirectorSurname(String newDirectorSurname) {
        this.newDirectorSurname = newDirectorSurname;
    }
}