package it.uniroma3.siw.siwcinefestival.model;

import java.time.LocalDate;
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
@Table(name = "actors")
public class Actor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String surname;


    private LocalDate birthDate;


    private String nationality;



    /*
     * =====================================================
     * ACTOR - CASTING
     * =====================================================
     *
     * Un attore può avere molte partecipazioni
     * cinematografiche.
     *
     * Ogni Casting riguarda un solo attore.
     *
     * Relazione 1:N.
     */
    @OneToMany(
        mappedBy = "actor",
        fetch = FetchType.LAZY
    )
    private List<Casting> castings =
            new ArrayList<>();



    public Actor() {
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


    public LocalDate getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    public String getNationality() {
        return nationality;
    }


    public void setNationality(String nationality) {
        this.nationality = nationality;
    }


    public List<Casting> getCastings() {
        return castings;
    }
}