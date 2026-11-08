package it.uniroma3.siw.siwcinefestival.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity  //indica a jpa che Festival è una entità persistente.
@Table(name = "festivals")  //specifica il nome della tabella sql
public class Festival {

    @Id  //chiave primaria 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  //la colonna non può essere null, ma non equivale a una validazione completa del form html, per verificare chee il testo non sia vuoto uso ad esempio @NotBlank nei DTO e nei controlli del Service.
    private String name; 

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    /* Copertina del festival*/
    @Column(name = "image_file_name")  //contiene il nome del file dell'immagine di copertina del festival, che viene salvata nella cartella /images/festivals.
    private String imageFileName;

    /* FESTIVAL - FILM   Relazione MOLTI A MOLTI.
     * Un festival può presentare molti film.
     *
     * Un film può partecipare a molti festival.
     *
     * Nel database viene creata una tabella di associazione: festival_films
     * con le colonne: festival_id  film_id
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "festival_films",

            joinColumns = @JoinColumn(name = "festival_id"),  //indica la colonna che fa riferimento al festival
            inverseJoinColumns = @JoinColumn(name = "film_id"),  //indica la colonna che fa riferimento al film
            uniqueConstraints = @UniqueConstraint(name = "uk_festival_film", columnNames = {  //serve a impedire che una stessa coppia festival-film sia memorizzata due volte
                    "festival_id",
                    "film_id"
            }))
    private Set<Film> films = new HashSet<>();  //ogni festival ha una collezione di film

    /* FESTIVAL - SCREENING  Relazione 1:N.
     * Un festival può avere molte proiezioni.
     *
     * Ogni proiezione appartiene a un solo festival.
     * 
     * LAZY significa che le proiezioni vengono caricate quando richieste.
     */
    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY)  //la relazione è gestita dal campo chiamato festival nell'entità Screening.
    private List<Screening> screenings = new ArrayList<>(); //ogni festival ha una collezione di proiezioni


    /*COSTRUTTORE VUOTO per creare un oggetto festival senza specificare subito i suoi attributi*/
    public Festival() {
    }

    /* GETTER (per leggere il valore di un attributo) E SETTER (per modificare il valore di un attributo) */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Film> getFilms() {
        return films;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    /***** METODO PER AGGIUNGERE UN FILM *****/
    public void addFilm(Film film) {

        if (film == null) {
            return;
        }

        this.films.add(film);

        film.getFestivals().add(this);
    }

    /***** METODO PER RIMUOVERE UN FILM *****/
    public void removeFilm(Film film) {

        if (film == null) {
            return;
        }

        this.films.remove(film);

        film.getFestivals().remove(this);
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}