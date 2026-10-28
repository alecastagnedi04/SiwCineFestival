package it.uniroma3.siw.siwcinefestival.controller.admin;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siwcinefestival.dto.FestivalForm;
import it.uniroma3.siw.siwcinefestival.model.Festival;

import it.uniroma3.siw.siwcinefestival.service.FestivalService;
import it.uniroma3.siw.siwcinefestival.service.FilmService;


@Controller
@RequestMapping("/admin/festivals")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFestivalController {

    private final FestivalService festivalService;
    private final FilmService filmService;


    public AdminFestivalController(
            FestivalService festivalService,
            FilmService filmService) {

        this.festivalService = festivalService;
        this.filmService = filmService;
    }


    /*
     * =====================================================
     * ELENCO FESTIVAL
     * =====================================================
     *
     * GET /admin/festivals
     */

    @GetMapping
    public String listFestivals(Model model) {

        model.addAttribute(
                "festivals",
                festivalService.findAll()
        );

        return "admin/festivals/list";
    }


    /*
     * =====================================================
     * FORM CREAZIONE
     * =====================================================
     *
     * GET /admin/festivals/new
     */

    @GetMapping("/new")
    public String newFestivalForm(Model model) {

        model.addAttribute(
                "festivalForm",
                new FestivalForm()
        );

        prepareForm(model, false, null);

        return "admin/festivals/form";
    }


    /*
     * =====================================================
     * CREAZIONE FESTIVAL
     * =====================================================
     *
     * POST /admin/festivals/new
     */

    @PostMapping("/new")
    public String createFestival(

            @Valid
            @ModelAttribute("festivalForm")
            FestivalForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareForm(model, false, null);

            return "admin/festivals/form";
        }

        try {

            Festival festival =
                    festivalService.createFestival(
                            form.getName(),
                            form.getYear(),
                            form.getCity(),
                            form.getStartDate(),
                            form.getEndDate(),
                            form.getDescription()
                    );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Festival creato con successo!"
            );

            return "redirect:/admin/festivals/"
                    + festival.getId()
                    + "/edit";

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            bindingResult.reject(
                    "festivalError",
                    ex.getMessage()
            );

            prepareForm(model, false, null);

            return "admin/festivals/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA
     * =====================================================
     *
     * GET /admin/festivals/{id}/edit
     */

    @GetMapping("/{id}/edit")
    public String editFestivalForm(

            @PathVariable("id") Long id,

            Model model) {

        Festival festival = requireFestival(id);

        FestivalForm form = new FestivalForm();

        form.setName(festival.getName());
        form.setYear(festival.getYear());
        form.setCity(festival.getCity());
        form.setStartDate(festival.getStartDate());
        form.setEndDate(festival.getEndDate());
        form.setDescription(festival.getDescription());

        model.addAttribute(
                "festivalForm",
                form
        );

        prepareForm(model, true, id);

        return "admin/festivals/form";
    }


    /*
     * =====================================================
     * SALVATAGGIO MODIFICA
     * =====================================================
     *
     * POST /admin/festivals/{id}/edit
     */

    @PostMapping("/{id}/edit")
    public String updateFestival(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("festivalForm")
            FestivalForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireFestival(id);

        if (bindingResult.hasErrors()) {

            prepareForm(model, true, id);

            return "admin/festivals/form";
        }

        try {

            festivalService.updateFestival(
                    id,
                    form.getName(),
                    form.getYear(),
                    form.getCity(),
                    form.getStartDate(),
                    form.getEndDate(),
                    form.getDescription()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Festival modificato con successo!"
            );

            return "redirect:/admin/festivals/"
                    + id
                    + "/edit";

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            bindingResult.reject(
                    "festivalError",
                    ex.getMessage()
            );

            prepareForm(model, true, id);

            return "admin/festivals/form";
        }
    }


    /*
     * =====================================================
     * AGGIUNGI FILM AL FESTIVAL
     * =====================================================
     *
     * POST /admin/festivals/{id}/films/add
     */

    @PostMapping("/{id}/films/add")
    public String addFilmToFestival(

            @PathVariable("id") Long festivalId,

            @RequestParam("filmId") Long filmId,

            RedirectAttributes redirectAttributes) {

        try {

            festivalService.addFilmToFestival(
                    festivalId,
                    filmId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Film aggiunto al festival!"
            );

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/festivals/"
                + festivalId
                + "/edit";
    }


    /*
     * =====================================================
     * RIMUOVI FILM DAL FESTIVAL
     * =====================================================
     *
     * POST /admin/festivals/{id}/films/{filmId}/remove
     */

    @PostMapping("/{id}/films/{filmId}/remove")
    public String removeFilmFromFestival(

            @PathVariable("id") Long festivalId,

            @PathVariable("filmId") Long filmId,

            RedirectAttributes redirectAttributes) {

        try {

            festivalService.removeFilmFromFestival(
                    festivalId,
                    filmId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Film rimosso dal festival."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/festivals/"
                + festivalId
                + "/edit";
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private Festival requireFestival(Long id) {

        return festivalService.findByIdWithFilms(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Festival non trovato."
                        )
                );
    }


    private void prepareForm(
            Model model,
            boolean isEdit,
            Long festivalId) {

        model.addAttribute("isEdit", isEdit);

        model.addAttribute("festivalId", festivalId);

        model.addAttribute(
                "allFilms",
                filmService.findAll()
        );

        if (isEdit && festivalId != null) {

            Festival festival = requireFestival(festivalId);

            model.addAttribute(
                    "festival",
                    festival
            );

            model.addAttribute(
                    "festivalFilms",
                    festival.getFilms()
            );

        } else {

            model.addAttribute(
                    "festivalFilms",
                    List.of()
            );
        }
    }
}