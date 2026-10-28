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

import it.uniroma3.siw.siwcinefestival.dto.ScreeningForm;

import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.model.ScreeningStatus;

import it.uniroma3.siw.siwcinefestival.service.CinemaRoomService;
import it.uniroma3.siw.siwcinefestival.service.FestivalService;
import it.uniroma3.siw.siwcinefestival.service.FilmService;
import it.uniroma3.siw.siwcinefestival.service.ScreeningService;


@Controller
@RequestMapping("/admin/screenings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScreeningController {

    private final ScreeningService screeningService;
    private final FestivalService festivalService;
    private final FilmService filmService;
    private final CinemaRoomService roomService;


    public AdminScreeningController(
            ScreeningService screeningService,
            FestivalService festivalService,
            FilmService filmService,
            CinemaRoomService roomService) {

        this.screeningService = screeningService;
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.roomService = roomService;
    }


    /*
     * =====================================================
     * ELENCO PROIEZIONI
     * =====================================================
     *
     * GET /admin/screenings
     *
     * Possiamo filtrare per festival:
     *
     * /admin/screenings?festivalId=1
     */

    @GetMapping
    public String listScreenings(

            @RequestParam(
                    value = "festivalId",
                    required = false
            )
            Long festivalId,

            Model model) {

        model.addAttribute(
                "festivals",
                festivalService.findAll()
        );

        model.addAttribute(
                "selectedFestivalId",
                festivalId
        );

        /*
         * Se è selezionato un festival,
         * carichiamo il suo programma.
         *
         * Altrimenti mostriamo un elenco vuoto.
         */

        if (festivalId != null) {

            model.addAttribute(
                    "screenings",
                    screeningService.findProgramByFestivalId(
                            festivalId
                    )
            );

        } else {

            model.addAttribute(
                    "screenings",
                    List.of()
            );
        }

        return "admin/screenings/list";
    }


    /*
     * =====================================================
     * FORM NUOVA PROIEZIONE
     * =====================================================
     */

    @GetMapping("/new")
    public String newScreeningForm(

            @RequestParam(
                    value = "festivalId",
                    required = false
            )
            Long festivalId,

            Model model) {

        ScreeningForm form = new ScreeningForm();

        if (festivalId != null) {
            form.setFestivalId(festivalId);
        }

        form.setStatus(ScreeningStatus.SCHEDULED);

        model.addAttribute(
                "screeningForm",
                form
        );

        prepareForm(model, false, null);

        return "admin/screenings/form";
    }


    /*
     * =====================================================
     * CREAZIONE PROIEZIONE
     * =====================================================
     *
     * POST /admin/screenings/new
     */

    @PostMapping("/new")
    public String createScreening(

            @Valid
            @ModelAttribute("screeningForm")
            ScreeningForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareForm(model, false, null);

            return "admin/screenings/form";
        }

        try {

            screeningService.createScreening(
                    form.getFestivalId(),
                    form.getFilmId(),
                    form.getRoomId(),
                    form.getDate(),
                    form.getTime(),
                    form.getStatus()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Proiezione programmata con successo!"
            );

            return "redirect:/admin/screenings?festivalId="
                    + form.getFestivalId();

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            /*
             * Esempi di errore:
             *
             * - Film non appartenente al festival
             * - Data fuori dal festival
             * - Sala già occupata
             */

            bindingResult.reject(
                    "screeningError",
                    ex.getMessage()
            );

            prepareForm(model, false, null);

            return "admin/screenings/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA PROIEZIONE
     * =====================================================
     */

    @GetMapping("/{id}/edit")
    public String editScreeningForm(

            @PathVariable("id") Long id,

            Model model) {

        Screening screening = requireScreening(id);

        ScreeningForm form = new ScreeningForm();

        form.setFestivalId(
                screening.getFestival().getId()
        );

        form.setFilmId(
                screening.getFilm().getId()
        );

        form.setRoomId(
                screening.getRoom().getId()
        );

        form.setDate(screening.getDate());
        form.setTime(screening.getTime());
        form.setStatus(screening.getStatus());

        model.addAttribute(
                "screeningForm",
                form
        );

        prepareForm(model, true, id);

        return "admin/screenings/form";
    }


    /*
     * =====================================================
     * MODIFICA PROIEZIONE
     * =====================================================
     */

    @PostMapping("/{id}/edit")
    public String updateScreening(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("screeningForm")
            ScreeningForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireScreening(id);

        if (bindingResult.hasErrors()) {

            prepareForm(model, true, id);

            return "admin/screenings/form";
        }

        try {

            screeningService.updateScreening(
                    id,
                    form.getFestivalId(),
                    form.getFilmId(),
                    form.getRoomId(),
                    form.getDate(),
                    form.getTime(),
                    form.getStatus()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Proiezione modificata con successo!"
            );

            return "redirect:/admin/screenings?festivalId="
                    + form.getFestivalId();

        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException ex) {

            bindingResult.reject(
                    "screeningError",
                    ex.getMessage()
            );

            prepareForm(model, true, id);

            return "admin/screenings/form";
        }
    }


    /*
     * =====================================================
     * ELIMINAZIONE PROIEZIONE
     * =====================================================
     *
     * POST /admin/screenings/{id}/delete
     */

    @PostMapping("/{id}/delete")
    public String deleteScreening(

            @PathVariable("id") Long id,

            RedirectAttributes redirectAttributes) {

        requireScreening(id);

        screeningService.deleteScreening(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Proiezione eliminata con successo."
        );

        return "redirect:/admin/screenings";
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private Screening requireScreening(Long id) {

        return screeningService
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Proiezione non trovata."
                        )
                );
    }


    private void prepareForm(
            Model model,
            boolean isEdit,
            Long screeningId) {

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "screeningId",
                screeningId
        );

        /*
         * Festival disponibili.
         */

        model.addAttribute(
                "festivals",
                festivalService.findAll()
        );

        /*
         * Film disponibili.
         *
         * Il Service verificherà
         * che il film selezionato
         * partecipi al festival.
         */

        model.addAttribute(
                "films",
                filmService.findAll()
        );

        /*
         * Sale cinematografiche.
         */

        model.addAttribute(
                "rooms",
                roomService.findAll()
        );

        /*
         * Stati delle proiezioni.
         */

        model.addAttribute(
                "statuses",
                ScreeningStatus.values()
        );
    }
}