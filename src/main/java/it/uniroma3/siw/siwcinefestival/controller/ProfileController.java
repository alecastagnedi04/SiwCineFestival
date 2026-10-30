package it.uniroma3.siw.siwcinefestival.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.server.ResponseStatusException;

import it.uniroma3.siw.siwcinefestival.model.Review;
import it.uniroma3.siw.siwcinefestival.model.User;

import it.uniroma3.siw.siwcinefestival.service.ReviewService;
import it.uniroma3.siw.siwcinefestival.service.UserService;


@Controller
public class ProfileController {


    private final UserService userService;

    private final ReviewService reviewService;


    /*
     * =====================================================
     * COSTRUTTORE
     * =====================================================
     */

    public ProfileController(
            UserService userService,
            ReviewService reviewService) {

        this.userService = userService;

        this.reviewService = reviewService;
    }


    /*
     * =====================================================
     * PROFILO UTENTE
     * =====================================================
     *
     * GET /profile
     *
     * Pagina accessibile soltanto
     * agli utenti autenticati.
     */

    @GetMapping("/profile")
    public String profile(
            Principal principal,
            Model model) {


        /*
         * Verifichiamo l'autenticazione.
         */

        if (principal == null) {

            return "redirect:/login";
        }


        /*
         * Recuperiamo lo username
         * dell'utente autenticato.
         *
         * Non lo prendiamo dall'URL.
         */

        String username = principal.getName();


        /*
         * Recuperiamo l'utente dal Service.
         */

        User user = userService
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utente non trovato."
                        )
                );


        /*
         * Recuperiamo le recensioni
         * scritte dall'utente.
         */

        List<Review> reviews =
                reviewService.findByAuthorUsername(
                        username
                );


        /*
         * Prepariamo i dati
         * destinati a Thymeleaf.
         */

        model.addAttribute(
                "profileUser",
                user
        );


        model.addAttribute(
                "reviews",
                reviews
        );


        model.addAttribute(
                "reviewCount",
                reviews.size()
        );


        /*
         * Mostriamo la pagina profilo.
         */

        return "profile";
    }
}