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

    @Column(nullable = false) // riguarda il database postgres, non puo essere null
    private String name;

    @Column(nullable = false)
    private String surname;

    private LocalDate birthDate;

    private String nationality;

    @Column(name = "image_file_name") //Fotografia dell'attore
    private String imageFileName;

    /* ACTOR - CASTING  Relazione 1:N.
     * Un attore può avere molte partecipazioni cinematografiche.
     *
     * Ogni Casting riguarda un solo attore.
     */
    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    private List<Casting> castings = new ArrayList<>();  //la collezione non contiene direttamente oggetti film, ma oggetti casting, che contengono sia l'attore che il film e il personaggio interpretato.

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

    public List<Casting> getCastings() {  //permette di recuperare le partecipazioni cinematografiche dell'attore.
        return castings;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}