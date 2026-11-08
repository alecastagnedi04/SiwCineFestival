package it.uniroma3.siw.siwcinefestival.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")  //l'ho rinominata in users per evitare conflitti con la parola chiave user di SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Username univoco */
    @Column(nullable = false, unique = true)
    private String username;

    /* Indirizzo email univoco */
    @Column(nullable = false, unique = true)
    private String email;

    /* La password verrà cifrata con BCrypt nel Service */
    @Column(nullable = false)
    private String password;

    /* Ruolo utente */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  //il ruolo viene inizializzato a user, ma può essere modificato in admin tramite il Service.

    /* USER - REVIEW  Relazione 1:N
     * Un utente può scrivere molte recensioni.
     *
     * Ogni recensione ha un solo autore.
     */
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}