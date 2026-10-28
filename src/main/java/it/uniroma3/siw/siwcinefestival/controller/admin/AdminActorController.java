package it.uniroma3.siw.siwcinefestival.controller.admin;

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

import it.uniroma3.siw.siwcinefestival.dto.ActorForm;
import it.uniroma3.siw.siwcinefestival.model.Actor;
import it.uniroma3.siw.siwcinefestival.service.ActorService;


@Controller
@RequestMapping("/admin/actors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActorController {

    private final ActorService actorService;


    public AdminActorController(
            ActorService actorService) {

        this.actorService = actorService;
    }


    /*
     * =====================================================
     * ELENCO E FORM NUOVO ATTORE
     * =====================================================
     */

    @GetMapping({"", "/new"})
    public String listActors(Model model) {

        model.addAttribute(
                "actorForm",
                new ActorForm()
        );

        prepareForm(model, false, null);

        return "admin/actors/form";
    }


    /*
     * =====================================================
     * CREAZIONE ATTORE
     * =====================================================
     */

    @PostMapping("/new")
    public String createActor(

            @Valid
            @ModelAttribute("actorForm")
            ActorForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareForm(model, false, null);

            return "admin/actors/form";
        }

        try {

            actorService.createActor(
                    form.getName(),
                    form.getSurname(),
                    form.getBirthDate(),
                    form.getNationality()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attore creato con successo!"
            );

            return "redirect:/admin/actors";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "actorError",
                    ex.getMessage()
            );

            prepareForm(model, false, null);

            return "admin/actors/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA ATTORE
     * =====================================================
     */

    @GetMapping("/{id}/edit")
    public String editActorForm(

            @PathVariable("id") Long id,

            Model model) {

        Actor actor = requireActor(id);

        ActorForm form = new ActorForm();

        form.setName(actor.getName());
        form.setSurname(actor.getSurname());
        form.setBirthDate(actor.getBirthDate());
        form.setNationality(actor.getNationality());

        model.addAttribute(
                "actorForm",
                form
        );

        prepareForm(model, true, id);

        return "admin/actors/form";
    }


    /*
     * =====================================================
     * MODIFICA ATTORE
     * =====================================================
     */

    @PostMapping("/{id}/edit")
    public String updateActor(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("actorForm")
            ActorForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireActor(id);

        if (bindingResult.hasErrors()) {

            prepareForm(model, true, id);

            return "admin/actors/form";
        }

        try {

            actorService.updateActor(
                    id,
                    form.getName(),
                    form.getSurname(),
                    form.getBirthDate(),
                    form.getNationality()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attore modificato con successo!"
            );

            return "redirect:/admin/actors";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "actorError",
                    ex.getMessage()
            );

            prepareForm(model, true, id);

            return "admin/actors/form";
        }
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private Actor requireActor(Long id) {

        return actorService.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Attore non trovato."
                        )
                );
    }


    private void prepareForm(
            Model model,
            boolean isEdit,
            Long actorId) {

        model.addAttribute(
                "actors",
                actorService.findAll()
        );

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "actorId",
                actorId
        );
    }
}