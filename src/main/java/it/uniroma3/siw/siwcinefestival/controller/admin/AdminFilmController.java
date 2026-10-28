package it.uniroma3.siw.siwcinefestival.controller.admin;

import java.util.NoSuchElementException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import it.uniroma3.siw.siwcinefestival.dto.CastingForm;
import it.uniroma3.siw.siwcinefestival.dto.FilmForm;

import it.uniroma3.siw.siwcinefestival.model.Film;

import it.uniroma3.siw.siwcinefestival.service.ActorService;
import it.uniroma3.siw.siwcinefestival.service.CastingService;
import it.uniroma3.siw.siwcinefestival.service.DirectorService;
import it.uniroma3.siw.siwcinefestival.service.FilmService;


@Controller
@RequestMapping("/admin/films")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFilmController {

    private final FilmService filmService;
    private final DirectorService directorService;
    private final ActorService actorService;
    private final CastingService castingService;


    public AdminFilmController(
            FilmService filmService,
            DirectorService directorService,
            ActorService actorService,
            CastingService castingService) {

        this.filmService = filmService;
        this.directorService = directorService;
        this.actorService = actorService;
        this.castingService = castingService;
    }


    /*
     * =====================================================
     * ELENCO FILM
     * =====================================================
     *
     * GET /admin/films
     */

    @GetMapping
    public String listFilms(Model model) {

        model.addAttribute(
                "films",
                filmService.findAll()
        );

        return "admin/films/list";
    }


    /*
     * =====================================================
     * FORM NUOVO FILM
     * =====================================================
     */

    @GetMapping("/new")
    public String newFilmForm(Model model) {

        model.addAttribute(
                "filmForm",
                new FilmForm()
        );

        prepareFilmForm(model, false, null);

        return "admin/films/form";
    }


    /*
     * =====================================================
     * CREAZIONE FILM
     * =====================================================
     */

    @PostMapping("/new")
    public String createFilm(

            @Valid
            @ModelAttribute("filmForm")
            FilmForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareFilmForm(model, false, null);

            return "admin/films/form";
        }

        try {

            Film film = filmService.createFilm(
                    form.getTitle(),
                    form.getYear(),
                    form.getDuration(),
                    form.getGenre(),
                    form.getProductionCountry(),
                    form.getDirectorId()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Film creato con successo!"
            );

            return "redirect:/admin/films/"
                    + film.getId()
                    + "/cast";

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            bindingResult.reject(
                    "filmError",
                    ex.getMessage()
            );

            prepareFilmForm(model, false, null);

            return "admin/films/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA FILM
     * =====================================================
     */

    @GetMapping("/{id}/edit")
    public String editFilmForm(

            @PathVariable("id") Long id,

            Model model) {

        Film film = requireFilm(id);

        FilmForm form = new FilmForm();

        form.setTitle(film.getTitle());
        form.setYear(film.getYear());
        form.setDuration(film.getDuration());
        form.setGenre(film.getGenre());

        form.setProductionCountry(
                film.getProductionCountry()
        );

        form.setDirectorId(
                film.getDirector().getId()
        );

        model.addAttribute(
                "filmForm",
                form
        );

        prepareFilmForm(model, true, id);

        return "admin/films/form";
    }


    /*
     * =====================================================
     * MODIFICA FILM
     * =====================================================
     */

    @PostMapping("/{id}/edit")
    public String updateFilm(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("filmForm")
            FilmForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireFilm(id);

        if (bindingResult.hasErrors()) {

            prepareFilmForm(model, true, id);

            return "admin/films/form";
        }

        try {

            filmService.updateFilm(
                    id,
                    form.getTitle(),
                    form.getYear(),
                    form.getDuration(),
                    form.getGenre(),
                    form.getProductionCountry(),
                    form.getDirectorId()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Film modificato con successo!"
            );

            return "redirect:/admin/films/"
                    + id
                    + "/edit";

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            bindingResult.reject(
                    "filmError",
                    ex.getMessage()
            );

            prepareFilmForm(model, true, id);

            return "admin/films/form";
        }
    }


    /*
     * =====================================================
     * GESTIONE CAST
     * =====================================================
     *
     * GET /admin/films/{filmId}/cast
     */

    @GetMapping("/{filmId}/cast")
    public String manageCast(

            @PathVariable("filmId") Long filmId,

            Model model) {

        model.addAttribute(
                "castingForm",
                new CastingForm()
        );

        prepareCastPage(model, filmId);

        return "admin/films/cast";
    }


    /*
     * =====================================================
     * AGGIUNGI ATTORE AL CAST
     * =====================================================
     */

    @PostMapping("/{filmId}/cast/add")
    public String addActorToCast(

            @PathVariable("filmId") Long filmId,

            @Valid
            @ModelAttribute("castingForm")
            CastingForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareCastPage(model, filmId);

            return "admin/films/cast";
        }

        try {

            castingService.addActorToFilm(
                    filmId,
                    form.getActorId(),
                    form.getCharacterName()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attore aggiunto al cast!"
            );

            return "redirect:/admin/films/"
                    + filmId
                    + "/cast";

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            bindingResult.reject(
                    "castingError",
                    ex.getMessage()
            );

            prepareCastPage(model, filmId);

            return "admin/films/cast";
        }
    }


    /*
     * =====================================================
     * MODIFICA CASTING
     * =====================================================
     */

    @PostMapping("/{filmId}/cast/{castingId}/edit")
    public String updateCasting(

            @PathVariable("filmId") Long filmId,

            @PathVariable("castingId") Long castingId,

            @Valid
            @ModelAttribute("castingForm")
            CastingForm form,

            BindingResult bindingResult,

            RedirectAttributes redirectAttributes) {

        /*
         * Controlliamo che il Casting
         * appartenga al film indicato.
         */

        requireCastingInFilm(
                filmId,
                castingId
        );

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Controlla i dati del personaggio."
            );

            return "redirect:/admin/films/"
                    + filmId
                    + "/cast";
        }

        try {

            castingService.updateCasting(
                    castingId,
                    form.getActorId(),
                    form.getCharacterName()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Casting aggiornato!"
            );

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/films/"
                + filmId
                + "/cast";
    }


    /*
     * =====================================================
     * RIMUOVI ATTORE DAL CAST
     * =====================================================
     */

    @PostMapping("/{filmId}/cast/{castingId}/delete")
    public String removeActorFromCast(

            @PathVariable("filmId") Long filmId,

            @PathVariable("castingId") Long castingId,

            RedirectAttributes redirectAttributes) {

        requireCastingInFilm(filmId, castingId);

        castingService.removeActorFromFilm(
                castingId,
                filmId
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Attore rimosso dal cast."
        );

        return "redirect:/admin/films/"
                + filmId
                + "/cast";
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private Film requireFilm(Long id) {

        return filmService
                .findByIdWithDirectorAndCast(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Film non trovato."
                        )
                );
    }


    private void prepareFilmForm(
            Model model,
            boolean isEdit,
            Long filmId) {

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "filmId",
                filmId
        );

        model.addAttribute(
                "directors",
                directorService.findAll()
        );
    }


    private void prepareCastPage(
            Model model,
            Long filmId) {

        Film film = requireFilm(filmId);

        model.addAttribute(
                "film",
                film
        );

        model.addAttribute(
                "castings",
                castingService.findCastByFilmId(filmId)
        );

        model.addAttribute(
                "actors",
                actorService.findAll()
        );
    }


    private void requireCastingInFilm(
            Long filmId,
            Long castingId) {

        boolean exists = castingService
                .findCastByFilmId(filmId)
                .stream()
                .anyMatch(casting ->
                        casting.getId().equals(castingId)
                );

        if (!exists) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Casting non trovato in questo film."
            );
        }
    }
}