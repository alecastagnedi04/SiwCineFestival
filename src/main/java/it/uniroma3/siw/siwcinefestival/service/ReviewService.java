package it.uniroma3.siw.siwcinefestival.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Film;
import it.uniroma3.siw.siwcinefestival.model.Review;
import it.uniroma3.siw.siwcinefestival.model.User;

import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;
import it.uniroma3.siw.siwcinefestival.repository.ReviewRepository;
import it.uniroma3.siw.siwcinefestival.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;


    public ReviewService(
            ReviewRepository reviewRepository,
            FilmRepository filmRepository,
            UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }


    /*
     * =====================================================
     * RECENSIONI DI UN FILM
     * =====================================================
     */

    @Transactional(readOnly = true)
    public List<Review> findByFilmId(Long filmId) {

        return reviewRepository
                .findByFilmIdOrderByDateDescIdDesc(filmId);
    }


    /*
     * RECENSIONE DI UN UTENTE SU UN FILM
     */

    @Transactional(readOnly = true)
    public Optional<Review> findByFilmIdAndUsername(
            Long filmId,
            String username) {

        return reviewRepository
                .findByFilmIdAndAuthorUsername(
                        filmId,
                        username
                );
    }


    /*
     * RECENSIONE APPARTENENTE ALL'UTENTE
     */

    @Transactional(readOnly = true)
    public Optional<Review> findOwnedReview(
            Long reviewId,
            String username) {

        return reviewRepository
                .findByIdAndAuthorUsername(
                        reviewId,
                        username
                );
    }


    /*
     * =====================================================
     * CREAZIONE RECENSIONE
     * =====================================================
     */

    @Transactional
    public Review createReview(
            Long filmId,
            String username,
            String text,
            Integer rating) {

        validateReview(text, rating);

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );

        User author = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Utente non trovato."
                        )
                );

        /*
         * Ogni utente può inserire
         * una sola recensione per film.
         */

        if (reviewRepository
                .existsByFilmIdAndAuthorId(
                        filmId,
                        author.getId())) {

            throw new IllegalStateException(
                    "Hai già recensito questo film. "
                    + "Puoi modificare la recensione esistente."
            );
        }

        Review review = new Review();

        review.setFilm(film);
        review.setAuthor(author);

        review.setText(text.trim());
        review.setRating(rating);

        review.setDate(LocalDate.now());

        return reviewRepository.save(review);
    }


    /*
     * =====================================================
     * MODIFICA RECENSIONE
     * =====================================================
     */

    @Transactional
    public Review updateReview(
            Long reviewId,
            String username,
            String text,
            Integer rating) {

        validateReview(text, rating);

        /*
         * Il Repository recupera la recensione
         * soltanto se appartiene all'utente.
         */

        Review review = reviewRepository
                .findByIdAndAuthorUsername(
                        reviewId,
                        username
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Recensione non trovata "
                                + "o non autorizzata."
                        )
                );

        review.setText(text.trim());
        review.setRating(rating);

        return reviewRepository.save(review);
    }


    /*
     * =====================================================
     * ELIMINAZIONE RECENSIONE
     * =====================================================
     */

    @Transactional
    public void deleteReview(
            Long reviewId,
            String username) {

        Review review = reviewRepository
                .findByIdAndAuthorUsername(
                        reviewId,
                        username
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Recensione non trovata "
                                + "o non autorizzata."
                        )
                );

        reviewRepository.delete(review);
    }


    /*
     * =====================================================
     * VALIDAZIONE RECENSIONE
     * =====================================================
     */

    private void validateReview(
            String text,
            Integer rating) {

        if (text == null || text.isBlank()) {

            throw new IllegalArgumentException(
                    "Il testo della recensione è obbligatorio."
            );
        }

        if (rating == null
                || rating < 1
                || rating > 5) {

            throw new IllegalArgumentException(
                    "Il voto deve essere compreso tra 1 e 5."
            );
        }
    }

    /*
 * =====================================================
 * RECENSIONI SCRITTE DA UN UTENTE
 * =====================================================
 *
 * Utilizzato nella pagina profilo.
 *
 * Operazione di sola lettura.
 */

@Transactional(readOnly = true)
public List<Review> findByAuthorUsername(
        String username) {

    return reviewRepository
            .findByAuthorUsernameOrderByDateDescIdDesc(
                    username
            );
}

/*
 * =====================================================
 * PAGINAZIONE RECENSIONI
 * =====================================================
 *
 * Mostriamo 3 recensioni per pagina.
 *
 * Utilizzato nel dettaglio del film.
 *
 * La pagina con tutte le recensioni
 * continuerà a usare findByFilmId().
 */

@Transactional(readOnly = true)
public Page<Review> findPageByFilmId(
        Long filmId,
        int requestedPage) {


    /*
     * Tre recensioni per pagina.
     */

    final int PAGE_SIZE = 3;


    /*
     * Numero della pagina richiesta.
     */

    int pageNumber = Math.max(
            0,
            requestedPage
    );


    /*
     * Ordiniamo dalla recensione
     * più recente alla più vecchia.
     */

    Sort sort = Sort.by(
            Sort.Order.desc("date"),
            Sort.Order.desc("id")
    );


    /*
     * Creiamo Pageable.
     */

    PageRequest pageable = PageRequest.of(
            pageNumber,
            PAGE_SIZE,
            sort
    );


    /*
     * Recuperiamo le recensioni.
     */

    Page<Review> result =
            reviewRepository.findByFilmId(
                    filmId,
                    pageable
            );


    /*
     * Se la pagina richiesta
     * non esiste più,
     * ritorniamo all'ultima disponibile.
     */

    if (result.getTotalPages() > 0
            && pageNumber >= result.getTotalPages()) {

        int lastPage =
                result.getTotalPages() - 1;

        pageable = PageRequest.of(
                lastPage,
                PAGE_SIZE,
                sort
        );

        result = reviewRepository.findByFilmId(
                filmId,
                pageable
        );
    }


    return result;
}
}