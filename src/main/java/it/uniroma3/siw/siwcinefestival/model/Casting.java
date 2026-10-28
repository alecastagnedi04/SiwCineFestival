package it.uniroma3.siw.siwcinefestival.model;

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
    name = "castings",

    uniqueConstraints = @UniqueConstraint(
        name = "uk_casting_film_actor",
        columnNames = {
            "film_id",
            "actor_id"
        }
    )
)
public class Casting {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    /*
     * Personaggio interpretato dall'attore
     * all'interno del film.
     */
    @Column(
        nullable = false,
        length = 200
    )
    private String characterName;



    /*
     * =====================================================
     * CASTING - FILM
     * =====================================================
     *
     * Molti Casting possono appartenere
     * allo stesso Film.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "film_id",
        nullable = false
    )
    private Film film;



    /*
     * =====================================================
     * CASTING - ACTOR
     * =====================================================
     *
     * Molti Casting possono riferirsi
     * allo stesso Actor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "actor_id",
        nullable = false
    )
    private Actor actor;



    public Casting() {
    }



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getCharacterName() {
        return characterName;
    }


    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }


    public Film getFilm() {
        return film;
    }


    public void setFilm(Film film) {
        this.film = film;
    }


    public Actor getActor() {
        return actor;
    }


    public void setActor(Actor actor) {
        this.actor = actor;
    }
}