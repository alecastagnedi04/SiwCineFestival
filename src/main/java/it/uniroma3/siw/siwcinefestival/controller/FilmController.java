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
    @GetMapping("/{id}")
    public String showFilm(

            @PathVariable("id") Long id,

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
         *
         * L'associazione viene caricata
         * nel metodo EntityGraph.
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
         * Festival ai quali
         * partecipa il film.
         *
         * Questa associazione deve essere
         * inizializzata nel Service.
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
         * Recensioni del film.
         */

        model.addAttribute(
                "reviews",
                reviewService.findByFilmId(id)
        );


        /*
         * Se l'utente è autenticato,
         * cerchiamo una sua eventuale
         * recensione già pubblicata.
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