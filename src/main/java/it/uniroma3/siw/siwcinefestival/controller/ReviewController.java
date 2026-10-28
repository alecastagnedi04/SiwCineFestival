package it.uniroma3.siw.siwcinefestival.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import it.uniroma3.siw.siwcinefestival.dto.ReviewForm;

import it.uniroma3.siw.siwcinefestival.model.Film;
import it.uniroma3.siw.siwcinefestival.model.Review;

import it.uniroma3.siw.siwcinefestival.service.FilmService;
import it.uniroma3.siw.siwcinefestival.service.ReviewService;


@Controller
@RequestMapping("/films/{filmId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final FilmService filmService;


    public ReviewController(
            ReviewService reviewService,
            FilmService filmService) {

        this.reviewService = reviewService;
        this.filmService = filmService;
    }


    /*
     * =====================================================
     * ELENCO RECENSIONI
     * =====================================================
     *
     * URL:
     * GET /films/{filmId}/reviews
     *
     * Accessibile anche ai visitatori.
     */
    @GetMapping
    public String listReviews(

            @PathVariable Long filmId,

            Model model,

            Principal principal) {

        Film film = requireFilm(filmId);

        model.addAttribute(
                "film",
                film
        );

        model.addAttribute(
                "reviews",
                reviewService.findByFilmId(filmId)
        );


        /*
         * Eventuale recensione
         * dell'utente autenticato.
         */

        if (principal != null) {

            model.addAttribute(
                    "myReview",
                    reviewService.findByFilmIdAndUsername(
                            filmId,
                            principal.getName()
                    ).orElse(null)
            );

        } else {

            model.addAttribute(
                    "myReview",
                    null
            );
        }

        return "reviews/list";
    }


    /*
     * =====================================================
     * FORM NUOVA RECENSIONE
     * =====================================================
     *
     * URL:
     * GET /films/{filmId}/reviews/new
     */
    @GetMapping("/new")
    public String newReviewForm(

            @PathVariable Long filmId,

            Model model,

            Principal principal) {

        /*
         * Serve un utente autenticato.
         */

        if (principal == null) {
            return "redirect:/login";
        }

        Film film = requireFilm(filmId);


        /*
         * Se l'utente ha già recensito
         * questo film, lo mandiamo
         * al form di modifica.
         */

        Review existingReview = reviewService
                .findByFilmIdAndUsername(
                        filmId,
                        principal.getName()
                )
                .orElse(null);

        if (existingReview != null) {

            return "redirect:/films/"
                    + filmId
                    + "/reviews/"
                    + existingReview.getId()
                    + "/edit";
        }


        model.addAttribute(
                "reviewForm",
                new ReviewForm()
        );

        model.addAttribute(
                "film",
                film
        );

        model.addAttribute(
                "isEdit",
                false
        );

        model.addAttribute(
                "pageTitle",
                "Scrivi una recensione"
        );

        return "reviews/form";
    }


    /*
     * =====================================================
     * CREAZIONE RECENSIONE
     * =====================================================
     *
     * URL:
     * POST /films/{filmId}/reviews
     */
    @PostMapping
    public String createReview(

            @PathVariable Long filmId,

            @Valid
            @ModelAttribute("reviewForm")
            ReviewForm form,

            BindingResult bindingResult,

            Principal principal,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Film film = requireFilm(filmId);


        if (bindingResult.hasErrors()) {

            prepareReviewForm(
                    model,
                    film,
                    false
            );

            return "reviews/form";
        }


        try {

            reviewService.createReview(
                    filmId,
                    principal.getName(),
                    form.getText(),
                    form.getRating()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Recensione pubblicata con successo!"
            );

            return "redirect:/films/" + filmId;

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            bindingResult.reject(
                    "reviewError",
                    ex.getMessage()
            );

            prepareReviewForm(
                    model,
                    film,
                    false
            );

            return "reviews/form";

        } catch (
                org.springframework.dao.DataIntegrityViolationException ex) {

            bindingResult.reject(
                    "reviewError",
                    "Hai già pubblicato una recensione per questo film."
            );

            prepareReviewForm(
                    model,
                    film,
                    false
            );

            return "reviews/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA RECENSIONE
     * =====================================================
     *
     * URL:
     * GET /films/{filmId}/reviews/{reviewId}/edit
     */
    @GetMapping("/{reviewId}/edit")
    public String editReviewForm(

            @PathVariable Long filmId,

            @PathVariable Long reviewId,

            Principal principal,

            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }


        /*
         * Recuperiamo la recensione
         * controllando anche l'autore.
         */

        Review review = requireOwnedReview(
                reviewId,
                filmId,
                principal.getName()
        );


        ReviewForm form = new ReviewForm();

        form.setText(review.getText());
        form.setRating(review.getRating());


        model.addAttribute(
                "reviewForm",
                form
        );

        model.addAttribute(
                "film",
                requireFilm(filmId)
        );

        model.addAttribute(
                "reviewId",
                reviewId
        );

        model.addAttribute(
                "isEdit",
                true
        );

        model.addAttribute(
                "pageTitle",
                "Modifica recensione"
        );

        return "reviews/form";
    }


    /*
     * =====================================================
     * SALVATAGGIO MODIFICA
     * =====================================================
     *
     * URL:
     * POST /films/{filmId}/reviews/{reviewId}/edit
     */
    @PostMapping("/{reviewId}/edit")
    public String updateReview(

            @PathVariable Long filmId,

            @PathVariable Long reviewId,

            @Valid
            @ModelAttribute("reviewForm")
            ReviewForm form,

            BindingResult bindingResult,

            Principal principal,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }


        /*
         * Verifichiamo che la recensione
         * appartenga all'utente e al film.
         */

        requireOwnedReview(
                reviewId,
                filmId,
                principal.getName()
        );

        Film film = requireFilm(filmId);


        if (bindingResult.hasErrors()) {

            prepareReviewForm(
                    model,
                    film,
                    true
            );

            model.addAttribute(
                    "reviewId",
                    reviewId
            );

            return "reviews/form";
        }


        try {

            reviewService.updateReview(
                    reviewId,
                    principal.getName(),
                    form.getText(),
                    form.getRating()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Recensione modificata con successo!"
            );

            return "redirect:/films/" + filmId;

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            bindingResult.reject(
                    "reviewError",
                    ex.getMessage()
            );

            prepareReviewForm(
                    model,
                    film,
                    true
            );

            model.addAttribute(
                    "reviewId",
                    reviewId
            );

            return "reviews/form";
        }
    }


    /*
     * =====================================================
     * ELIMINAZIONE RECENSIONE
     * =====================================================
     *
     * URL:
     * POST /films/{filmId}/reviews/{reviewId}/delete
     *
     * Non utilizziamo GET
     * per eliminare dati.
     */
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(

            @PathVariable Long filmId,

            @PathVariable Long reviewId,

            Principal principal,

            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }


        /*
         * Controlliamo prima la proprietà
         * e l'appartenenza al film.
         */

        requireOwnedReview(
                reviewId,
                filmId,
                principal.getName()
        );


        /*
         * Il Service effettua nuovamente
         * la verifica dell'autore
         * prima dell'eliminazione.
         */

        reviewService.deleteReview(
                reviewId,
                principal.getName()
        );


        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Recensione eliminata con successo."
        );

        return "redirect:/films/" + filmId;
    }


    /*
     * =====================================================
     * METODI PRIVATI DI SUPPORTO
     * =====================================================
     */


    /*
     * Recupera un film oppure
     * restituisce HTTP 404.
     */
    private Film requireFilm(Long filmId) {

        return filmService
                .findById(filmId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Film non trovato."
                        )
                );
    }


    /*
     * Recupera una recensione soltanto
     * se appartiene all'utente.
     *
     * Controlliamo anche che appartenga
     * al film indicato nell'URL.
     */
    private Review requireOwnedReview(

            Long reviewId,

            Long filmId,

            String username) {

        Review review = reviewService
                .findOwnedReview(
                        reviewId,
                        username
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Recensione non trovata."
                        )
                );


        /*
         * Evitiamo che qualcuno utilizzi
         * l'ID di una recensione
         * appartenente a un altro film.
         */

        if (!review.getFilm().getId().equals(filmId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Recensione non trovata."
            );
        }

        return review;
    }


    /*
     * Prepara gli attributi del form.
     *
     * Utilizzato sia per creare
     * sia per modificare recensioni.
     */
    private void prepareReviewForm(

            Model model,

            Film film,

            boolean isEdit) {

        model.addAttribute(
                "film",
                film
        );

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "pageTitle",
                isEdit
                        ? "Modifica recensione"
                        : "Scrivi una recensione"
        );
    }

}