package it.uniroma3.siw.siwcinefestival.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.server.ResponseStatusException;

import it.uniroma3.siw.siwcinefestival.model.Film;

import it.uniroma3.siw.siwcinefestival.service.FilmService;
import it.uniroma3.siw.siwcinefestival.service.ReviewService;
import it.uniroma3.siw.siwcinefestival.service.ScreeningService;

import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siwcinefestival.model.Review;


@Controller
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final ReviewService reviewService;
    private final ScreeningService screeningService;


    public FilmController(
            FilmService filmService,
            ReviewService reviewService,
            ScreeningService screeningService) {

        this.filmService = filmService;
        this.reviewService = reviewService;
        this.screeningService = screeningService;
    }


    /*
     * =====================================================
     * DETTAGLIO FILM
     * =====================================================
     *
     * URL:
     * GET /films/{id}
     *
     * Esempio:
     * GET /films/5
     */
    /*
 * =====================================================
 * DETTAGLIO FILM CON RECENSIONI PAGINATE
 * =====================================================
 *
 * GET /films/{id}
 *
 * GET /films/{id}?reviewPage=0
 *
 * GET /films/{id}?reviewPage=1
 *
 * Mostriamo al massimo 3 recensioni
 * per pagina.
 */

@GetMapping("/{id}")
public String showFilm(

        @PathVariable("id") Long id,

        @RequestParam(
                name = "reviewPage",
                defaultValue = "0"
        )
        int reviewPage,

        Model model,

        Principal principal) {


    /*
     * Recuperiamo il film
     * con regista e cast.
     */

    Film film = filmService
            .findByIdWithDirectorAndCast(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Film non trovato."
                    )
            );


    /*
     * Informazioni principali.
     */

    model.addAttribute(
            "film",
            film
    );


    /*
     * Cast del film.
     */

    model.addAttribute(
            "castings",
            film.getCastings()
    );


    /*
     * Regista.
     */

    model.addAttribute(
            "director",
            film.getDirector()
    );


    /*
     * Festival ai quali partecipa.
     */

    model.addAttribute(
            "festivals",
            film.getFestivals()
    );


    /*
     * Proiezioni del film.
     */

    model.addAttribute(
            "screenings",
            screeningService.findByFilmId(id)
    );


    /*
     * Recuperiamo soltanto
     * 3 recensioni per pagina.
     */

    Page<Review> result =
            reviewService.findPageByFilmId(
                    id,
                    reviewPage
            );


    /*
     * Elenco delle recensioni
     * presenti nella pagina corrente.
     */

    model.addAttribute(
            "reviews",
            result.getContent()
    );


    /*
     * Informazioni sulla paginazione.
     */

    model.addAttribute(
            "reviewPage",
            result
    );


    /*
     * Recensione dell'utente autenticato.
     */

    if (principal != null) {

        model.addAttribute(
                "myReview",
                reviewService.findByFilmIdAndUsername(
                        id,
                        principal.getName()
                ).orElse(null)
        );

    } else {

        model.addAttribute(
                "myReview",
                null
        );
    }


    return "films/show";
}
}