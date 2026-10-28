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

import it.uniroma3.siw.siwcinefestival.dto.DirectorForm;
import it.uniroma3.siw.siwcinefestival.model.Director;
import it.uniroma3.siw.siwcinefestival.service.DirectorService;


@Controller
@RequestMapping("/admin/directors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDirectorController {

    private final DirectorService directorService;


    public AdminDirectorController(
            DirectorService directorService) {

        this.directorService = directorService;
    }


    /*
     * =====================================================
     * ELENCO REGISTI
     * =====================================================
     */

    @GetMapping({"", "/new"})
    public String listDirectors(Model model) {

        model.addAttribute(
                "directorForm",
                new DirectorForm()
        );

        prepareForm(model, false, null);

        return "admin/directors/form";
    }


    /*
     * =====================================================
     * CREAZIONE REGISTA
     * =====================================================
     */

    @PostMapping("/new")
    public String createDirector(

            @Valid
            @ModelAttribute("directorForm")
            DirectorForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareForm(model, false, null);

            return "admin/directors/form";
        }

        try {

            directorService.createDirector(
                    form.getName(),
                    form.getSurname()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Regista creato con successo!"
            );

            return "redirect:/admin/directors";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "directorError",
                    ex.getMessage()
            );

            prepareForm(model, false, null);

            return "admin/directors/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA REGISTA
     * =====================================================
     */

    @GetMapping("/{id}/edit")
    public String editDirectorForm(

            @PathVariable("id") Long id,

            Model model) {

        Director director = requireDirector(id);

        DirectorForm form = new DirectorForm();

        form.setName(director.getName());
        form.setSurname(director.getSurname());

        model.addAttribute(
                "directorForm",
                form
        );

        prepareForm(model, true, id);

        return "admin/directors/form";
    }


    /*
     * =====================================================
     * MODIFICA REGISTA
     * =====================================================
     */

    @PostMapping("/{id}/edit")
    public String updateDirector(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("directorForm")
            DirectorForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireDirector(id);

        if (bindingResult.hasErrors()) {

            prepareForm(model, true, id);

            return "admin/directors/form";
        }

        try {

            directorService.updateDirector(
                    id,
                    form.getName(),
                    form.getSurname()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Regista modificato con successo!"
            );

            return "redirect:/admin/directors";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "directorError",
                    ex.getMessage()
            );

            prepareForm(model, true, id);

            return "admin/directors/form";
        }
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private Director requireDirector(Long id) {

        return directorService.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Regista non trovato."
                        )
                );
    }


    private void prepareForm(
            Model model,
            boolean isEdit,
            Long directorId) {

        model.addAttribute(
                "directors",
                directorService.findAll()
        );

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "directorId",
                directorId
        );
    }
}