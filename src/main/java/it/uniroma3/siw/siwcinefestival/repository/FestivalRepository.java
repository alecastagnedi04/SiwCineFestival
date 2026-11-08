package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwcinefestival.model.Festival;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FestivalRepository
        extends JpaRepository<Festival, Long> {

    /* ELENCO FESTIVAL
     * Recupera tutti i festival ordinati dalla data di inizio più recente alla più vecchia.
     */
    List<Festival> findAllByOrderByStartDateDesc();

    /* FESTIVAL CON FILM
     * Recupera un festival e carica anche la collezione films.
     *
     * EntityGraph permette di recuperare anche i film associati.
     *
     * Sarà utile nella pagina di dettaglio del festival.
     */
    @EntityGraph(attributePaths = { "films" })
    @Query("""
            SELECT f
            FROM Festival f
            WHERE f.id = :festivalId
            """)
    Optional<Festival> findByIdWithFilms(
            @Param("festivalId") Long festivalId);  //@Param collega il parametro java al parametro della query JPQL.

    /* VERIFICA APPARTENENZA FILM AL FESTIVAL
     * Restituisce true se il film partecipa al festival.
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

            @Param("festivalId") Long festivalId,

            @Param("filmId") Long filmId);

    /* RICERCA FESTIVAL CON PAGINAZIONE
     * questa query permette di cercare festival in base a nome, città, anno.
     * Pageable stabilisce quanti risultati devono essere recuperati dal database.
     * lower converte in nome in minuscolo per effettuare ricerche senza distinguere tra maiuscole e minuscole.
     * like concat permette di cercare il nome del festival o della città anche se non è scritto esattamente come nel database.
     */
    @Query("""
            SELECT f
            FROM Festival f
            WHERE LOWER(f.name) LIKE CONCAT('%', :search, '%')   
               OR LOWER(f.city) LIKE CONCAT('%', :search, '%')
               OR f.year = :year
            """)
    Page<Festival> searchForHome(

            @Param("search") String search,

            @Param("year") Integer year,

            Pageable pageable);  //posso mostrare solamente 3 risultati alla volta per pagina.

}