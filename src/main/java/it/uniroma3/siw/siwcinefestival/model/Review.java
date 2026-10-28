package it.uniroma3.siw.siwcinefestival.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(
    name = "reviews",

    uniqueConstraints = @UniqueConstraint(
        name = "uk_review_film_author",
        columnNames = {
            "film_id",
            "author_id"
        }
    )
)
public class Review {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    /*
     * Testo della recensione.
     */
    @Column(
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String text;



    /*
     * Voto della recensione.
     *
     * Utilizzeremo un intervallo
     * da 1 a 5.
     */
    @Column(nullable = false)
    private Integer rating;



    /*
     * Data della recensione.
     */
    @Column(nullable = false)
    private LocalDate date;



    /*
     * =====================================================
     * REVIEW - FILM
     * =====================================================
     *
     * Molte recensioni possono
     * riguardare lo stesso film.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "film_id",
        nullable = false
    )
    private Film film;



    /*
     * =====================================================
     * REVIEW - USER
     * =====================================================
     *
     * Molte recensioni possono
     * essere scritte dallo stesso utente.
     *
     * Ogni recensione ha un solo autore.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "author_id",
        nullable = false
    )
    private User author;



    public Review() {
    }



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public Film getFilm() {
        return film;
    }


    public void setFilm(Film film) {
        this.film = film;
    }


    public User getAuthor() {
        return author;
    }


    public void setAuthor(User author) {
        this.author = author;
    }
}