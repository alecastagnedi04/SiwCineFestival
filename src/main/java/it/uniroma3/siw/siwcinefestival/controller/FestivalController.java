package it.uniroma3.siw.siwcinefestival.controller;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.server.ResponseStatusException;

import it.uniroma3.siw.siwcinefestival.model.Festival;

import it.uniroma3.siw.siwcinefestival.service.FestivalService;
import it.uniroma3.siw.siwcinefestival.service.ScreeningService;


@Controller
@RequestMapping("/festivals")
public class FestivalController {

    private final FestivalService festivalService;
    private final ScreeningService screeningService;


    public FestivalController(
            FestivalService festivalService,
            ScreeningService screeningService) {

        this.festivalService = festivalService;
        this.screeningService = screeningService;
    }


    /*
     * =====================================================
     * ELENCO FESTIVAL
     * =====================================================
     *
     * URL:
     * GET /festivals
     *
     * Pagina pubblica.
     */
    @GetMapping
    public String listFestivals(Model model) {

        model.addAttribute(
                "festivals",
                festivalService.findAll()
        );

        return "festivals/list";
    }


    /*
     * =====================================================
     * DETTAGLIO FESTIVAL
     * =====================================================
     *
     * URL:
     * GET /festivals/{id}
     *
     * Esempio:
     * GET /festivals/1
     */
    @GetMapping("/{id}")
    public String showFestival(

            @PathVariable("id") Long id,

            Model model) {

        /*
         * Recuperiamo il festival
         * caricando anche i film
         * tramite EntityGraph.
         */

        Festival festival = festivalService
                .findByIdWithFilms(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Festival non trovato."
                        )
                );


        /*
         * Recuperiamo il programma
         * delle proiezioni.
         *
         * Il Repository utilizza
         * EntityGraph per caricare
         * Film e CinemaRoom.
         */

        model.addAttribute(
                "screenings",
                screeningService.findProgramByFestivalId(id)
        );


        /*
         * Dati del festival.
         */

        model.addAttribute(
                "festival",
                festival
        );


        /*
         * Film partecipanti.
         *
         * La collezione è già stata
         * caricata tramite EntityGraph.
         */

        model.addAttribute(
                "films",
                festival.getFilms()
        );


        return "festivals/show";
    }

}