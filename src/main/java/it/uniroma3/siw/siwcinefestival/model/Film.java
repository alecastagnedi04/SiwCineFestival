package it.uniroma3.siw.siwcinefestival.model;

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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "films")
public class Film {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;


    @Column(nullable = false)
    private Integer year;


    /*
     * Durata espressa in minuti.
     *
     * Esempio:
     * 120 = 2 ore.
     */
    @Column(nullable = false)
    private Integer duration;


    @Column(nullable = false)
    private String genre;


    @Column(nullable = false)
    private String productionCountry;



    /*
     * =====================================================
     * FILM - DIRECTOR
     * =====================================================
     *
     * Molti film possono avere
     * lo stesso regista.
     *
     * Ogni film ha un regista.
     *
     * Relazione N:1.
     *
     * @ManyToOne sarebbe EAGER
     * per impostazione predefinita.
     *
     * Impostiamo LAZY esplicitamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "director_id",
        nullable = false
    )
    private Director director;



    /*
     * =====================================================
     * FILM - FESTIVAL
     * =====================================================
     *
     * Relazione N:M.
     *
     * Il lato proprietario si trova
     * nell'entità Festival.
     *
     * mappedBy = "films" fa riferimento
     * al campo films di Festival.
     */
    @ManyToMany(
        mappedBy = "films",
        fetch = FetchType.LAZY
    )
    private Set<Festival> festivals =
            new HashSet<>();



    /*
     * =====================================================
     * FILM - CASTING
     * =====================================================
     *
     * Un film può avere molti attori.
     *
     * Il personaggio interpretato
     * è memorizzato in Casting.
     */
    @OneToMany(
        mappedBy = "film",
        fetch = FetchType.LAZY
    )
    private List<Casting> castings =
            new ArrayList<>();



    /*
     * =====================================================
     * FILM - SCREENING
     * =====================================================
     *
     * Un film può avere
     * molte proiezioni.
     */
    @OneToMany(
        mappedBy = "film",
        fetch = FetchType.LAZY
    )
    private List<Screening> screenings =
            new ArrayList<>();



    /*
     * =====================================================
     * FILM - REVIEW
     * =====================================================
     *
     * Un film può avere
     * molte recensioni.
     */
    @OneToMany(
        mappedBy = "film",
        fetch = FetchType.LAZY
    )
    private List<Review> reviews =
            new ArrayList<>();



    public Film() {
    }



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public Director getDirector() {
        return director;
    }


    public void setDirector(Director director) {
        this.director = director;
    }


    public Set<Festival> getFestivals() {
        return festivals;
    }


    public List<Casting> getCastings() {
        return castings;
    }


    public List<Screening> getScreenings() {
        return screenings;
    }


    public List<Review> getReviews() {
        return reviews;
    }
}