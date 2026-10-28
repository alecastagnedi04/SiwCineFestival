package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siwcinefestival.model.Actor;


public interface ActorRepository
        extends JpaRepository<Actor, Long> {


    /*
     * =====================================================
     * ELENCO ATTORI
     * =====================================================
     *
     * Ordina prima per cognome
     * e poi per nome.
     *
     * Esempio:
     *
     * Al Pacino
     * Marlon Brando
     * Robert De Niro
     */
    List<Actor> findAllByOrderBySurnameAscNameAsc();

}