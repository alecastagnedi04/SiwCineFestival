package it.uniroma3.siw.siwcinefestival.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "directors")
public class Director {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String surname;



    /*
     * =====================================================
     * DIRECTOR - FILM
     * =====================================================
     *
     * Un regista può dirigere molti film.
     *
     * Ogni film ha un regista
     * nel modello iniziale del progetto.
     *
     * Relazione 1:N.
     */
    @OneToMany(
        mappedBy = "director",
        fetch = FetchType.LAZY
    )
    private List<Film> films =
            new ArrayList<>();



    public Director() {
    }



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


    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }


    public List<Film> getFilms() {
        return films;
    }
}