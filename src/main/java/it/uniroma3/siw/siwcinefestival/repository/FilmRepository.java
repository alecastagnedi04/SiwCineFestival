package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwcinefestival.model.Film;


public interface FilmRepository
        extends JpaRepository<Film, Long> {


    /*
     * =====================================================
     * TUTTI I FILM
     * =====================================================
     *
     * Restituisce tutti i film
     * ordinati alfabeticamente.
     */
    List<Film> findAllByOrderByTitleAsc();



    /*
     * =====================================================
     * FILM DI UN FESTIVAL
     * =====================================================
     *
     * Recupera soltanto i film associati
     * al festival specificato.
     *
     * Usiamo DISTINCT per evitare
     * eventuali duplicati nel risultato.
     */
    @Query("""
        SELECT DISTINCT f
        FROM Film f
        JOIN f.festivals festival
        WHERE festival.id = :festivalId
        ORDER BY f.title ASC
        """)
    List<Film> findFilmsByFestivalId(

            @Param("festivalId")
            Long festivalId
    );



    /*
     * =====================================================
     * FILM CON REGISTA
     * =====================================================
     *
     * Carica il film e il relativo regista.
     *
     * La relazione director è LAZY
     * nel modello Film.
     *
     * EntityGraph permette di richiederla
     * insieme al film.
     */
    @EntityGraph(attributePaths = {"director"})
    @Query("""
        SELECT f
        FROM Film f
        WHERE f.id = :filmId
        """)
    Optional<Film> findByIdWithDirector(

            @Param("filmId")
            Long filmId
    );



    /*
     * =====================================================
     * FILM CON REGISTA E CAST
     * =====================================================
     *
     * Recupera:
     *
     * - Film
     * - Director
     * - Casting
     * - Actor associato a ogni Casting
     *
     * Questo metodo sarà utile
     * nella pagina del dettaglio film.
     *
     * Le altre collezioni, come reviews
     * e screenings, non vengono
     * esplicitamente richieste.
     */
    @EntityGraph(
        attributePaths = {
            "director",
            "castings",
            "castings.actor"
        }
    )
    @Query("""
        SELECT DISTINCT f
        FROM Film f
        WHERE f.id = :filmId
        """)
    Optional<Film> findByIdWithDirectorAndCast(

            @Param("filmId")
            Long filmId
    );

}