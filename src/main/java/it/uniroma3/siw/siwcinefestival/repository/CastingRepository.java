package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siwcinefestival.model.Casting;

public interface CastingRepository
                extends JpaRepository<Casting, Long> {

        /*CAST DI UN FILM
         * Recupera tutti i Casting associati a un determinato Film.
         *
         * Carico anche gli Actor, perché mostro nome e cognome.
         */
        @EntityGraph(attributePaths = { "actor" })
        List<Casting> findByFilmIdOrderByIdAsc(
                        Long filmId);

        /* VERIFICA ASSOCIAZIONE FILM - ACTOR
         * Controlla se un attore è già presente nel cast di un film.
         *
         * Nel model impedisco che la stessa coppia film-attore venga inserita due volte.
         */
        boolean existsByFilmIdAndActorId(
                        Long filmId,
                        Long actorId);

        /* CERCA UN CASTING SPECIFICO
         * Restituisce l'associazione  tra un film e un attore.
         */
        Optional<Casting> findByFilmIdAndActorId(
                        Long filmId,
                        Long actorId);

        /* FILMOGRAFIA DI UN ATTORE
         * Recupera i Casting di un attore caricando anche i Film associati.
         */
        @EntityGraph(attributePaths = { "film" })
        List<Casting> findByActorIdOrderByIdAsc(
                        Long actorId);

}