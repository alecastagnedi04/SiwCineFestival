package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siwcinefestival.model.Review;


public interface ReviewRepository
        extends JpaRepository<Review, Long> {


    /*
     * =====================================================
     * RECENSIONI DI UN FILM
     * =====================================================
     *
     * Recupera le recensioni
     * ordinate dalla più recente.
     *
     * Carica anche l'autore,
     * perché dobbiamo mostrarne
     * lo username nella pagina.
     */
    @EntityGraph(attributePaths = {"author"})
    List<Review> findByFilmIdOrderByDateDescIdDesc(
            Long filmId
    );



    /*
     * =====================================================
     * RECENSIONE DI UN UTENTE PER UN FILM
     * =====================================================
     *
     * Ogni utente può recensire
     * uno stesso film una sola volta.
     *
     * Se esiste una recensione,
     * restituiamo quella.
     */
    Optional<Review> findByFilmIdAndAuthorUsername(

            Long filmId,

            String username
    );



    /*
     * =====================================================
     * CONTROLLO PROPRIETÀ DELLA RECENSIONE
     * =====================================================
     *
     * Cerca una recensione tramite:
     *
     * - ID recensione
     * - Username autore
     *
     * Sarà utilizzato per impedire
     * che un utente modifichi o elimini
     * recensioni appartenenti ad altri.
     */
    Optional<Review> findByIdAndAuthorUsername(

            Long reviewId,

            String username
    );



    /*
     * =====================================================
     * VERIFICA RECENSIONE DUPLICATA
     * =====================================================
     *
     * Restituisce true se l'utente
     * ha già recensito il film.
     *
     * Il controllo sarà effettuato
     * anche nel ReviewService.
     *
     * Inoltre, nel model Review
     * abbiamo già definito
     * un vincolo UNIQUE a livello SQL.
     */
    boolean existsByFilmIdAndAuthorId(

            Long filmId,

            Long authorId
    );

}