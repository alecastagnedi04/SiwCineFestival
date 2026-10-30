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


@Entity
@Table(name = "festivals")
public class Festival {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
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

    /*
 * Copertina del festival.
 */

@Column(name = "image_file_name")
private String imageFileName;



    /*
     * =====================================================
     * FESTIVAL - FILM
     * =====================================================
     *
     * Relazione MOLTI A MOLTI.
     *
     * Un festival può presentare molti film.
     *
     * Un film può partecipare a molti festival.
     *
     * Questa è la parte proprietaria
     * della relazione @ManyToMany.
     *
     * Nel database viene creata
     * una tabella di associazione:
     *
     * festival_films
     *
     * con le colonne:
     *
     * festival_id
     * film_id
     *
     * Non utilizziamo CascadeType.REMOVE:
     * rimuovere un film dal festival non deve
     * eliminare il film dal database.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "festival_films",

        joinColumns = @JoinColumn(
            name = "festival_id"
        ),

        inverseJoinColumns = @JoinColumn(
            name = "film_id"
        ),

        uniqueConstraints = @UniqueConstraint(
            name = "uk_festival_film",
            columnNames = {
                "festival_id",
                "film_id"
            }
        )
    )
    private Set<Film> films = new HashSet<>();



    /*
     * =====================================================
     * FESTIVAL - SCREENING
     * =====================================================
     *
     * Un festival può avere molte proiezioni.
     *
     * Ogni proiezione appartiene
     * a un solo festival.
     *
     * Relazione 1:N.
     *
     * mappedBy = "festival" indica
     * il campo presente in Screening.
     *
     * LAZY significa che le proiezioni
     * vengono caricate quando richieste.
     */
    @OneToMany(
        mappedBy = "festival",
        fetch = FetchType.LAZY
    )
    private List<Screening> screenings =
            new ArrayList<>();



    /*
     * COSTRUTTORE VUOTO
     *
     * Necessario per JPA/Hibernate.
     */
    public Festival() {
    }



    /*
     * GETTER E SETTER
     */

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



    /*
     * =====================================================
     * METODO PER AGGIUNGERE UN FILM
     * =====================================================
     *
     * Aggiorna entrambi i lati
     * della relazione bidirezionale.
     */
    public void addFilm(Film film) {

        if (film == null) {
            return;
        }

        this.films.add(film);

        film.getFestivals().add(this);
    }



    /*
     * =====================================================
     * METODO PER RIMUOVERE UN FILM
     * =====================================================
     *
     * Rimuove soltanto l'associazione.
     *
     * Il Film rimane nel database.
     */
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