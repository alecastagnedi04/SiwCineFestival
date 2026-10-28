package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwcinefestival.model.Festival;


public interface FestivalRepository
        extends JpaRepository<Festival, Long> {


    /*
     * =====================================================
     * ELENCO FESTIVAL
     * =====================================================
     *
     * Recupera tutti i festival
     * ordinati dalla data di inizio
     * più recente alla più vecchia.
     */
    List<Festival> findAllByOrderByStartDateDesc();



    /*
     * =====================================================
     * FESTIVAL CON FILM
     * =====================================================
     *
     * Recupera un festival e carica
     * anche la collezione films.
     *
     * EntityGraph permette di richiedere
     * esplicitamente questa associazione.
     *
     * Sarà utile nella pagina di dettaglio
     * del festival.
     */
    @EntityGraph(attributePaths = {"films"})
    @Query("""
        SELECT f
        FROM Festival f
        WHERE f.id = :festivalId
        """)
    Optional<Festival> findByIdWithFilms(
            @Param("festivalId") Long festivalId
    );



    /*
     * =====================================================
     * VERIFICA APPARTENENZA FILM AL FESTIVAL
     * =====================================================
     *
     * Restituisce true se il film
     * partecipa al festival.
     *
     * Esempio:
     *
     * Festival 1
     * Film 5
     *
     * true = il film 5 partecipa
     *        al festival 1.
     *
     * Questa verifica sarà importante
     * quando programmeremo una proiezione.
     */
    @Query("""
        SELECT CASE WHEN COUNT(f) > 0
                    THEN true
                    ELSE false
               END
        FROM Festival f
        JOIN f.films film
        WHERE f.id = :festivalId
          AND film.id = :filmId
        """)
    boolean isFilmInFestival(

            @Param("festivalId")
            Long festivalId,

            @Param("filmId")
            Long filmId
    );

}