package it.uniroma3.siw.siwcinefestival.controller;

import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import it.uniroma3.siw.siwcinefestival.dto.RegistrationForm;
import it.uniroma3.siw.siwcinefestival.service.UserService;


@Controller
public class AuthController {

    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }


    /*
     * =====================================================
     * PAGINA LOGIN
     * =====================================================
     *
     * URL:
     * GET /login
     *
     * Il controllo della password
     * sarà effettuato da Spring Security.
     */
    @GetMapping("/login")
    public String login(Principal principal) {

        /*
         * Un utente già autenticato
         * viene rimandato alla homepage.
         */

        if (principal != null) {
            return "redirect:/";
        }

        return "login";
    }


    /*
     * =====================================================
     * PAGINA REGISTRAZIONE
     * =====================================================
     *
     * URL:
     * GET /register
     */
    @GetMapping("/register")
    public String registrationForm(
            Model model,
            Principal principal) {

        if (principal != null) {
            return "redirect:/";
        }

        /*
         * Prepariamo un DTO vuoto
         * per ricevere i dati del form.
         */

        model.addAttribute(
                "registrationForm",
                new RegistrationForm()
        );

        return "register";
    }


    /*
     * =====================================================
     * REGISTRAZIONE UTENTE
     * =====================================================
     *
     * URL:
     * POST /register
     *
     * Riceve:
     * username
     * email
     * password
     */
    @PostMapping("/register")
    public String register(

            @Valid
            @ModelAttribute("registrationForm")
            RegistrationForm form,

            BindingResult bindingResult,

            Principal principal,

            RedirectAttributes redirectAttributes) {

        /*
         * Non permettiamo a un utente
         * già autenticato di registrarsi
         * nuovamente tramite questo form.
         */

        if (principal != null) {
            return "redirect:/";
        }


        /*
         * Controlliamo gli errori rilevati
         * dalle annotazioni del DTO.
         *
         * Esempi:
         *
         * @NotBlank
         * @Email
         * @Size
         */

        if (bindingResult.hasErrors()) {
            return "register";
        }


        try {

            /*
             * La logica di registrazione
             * si trova in UserService.
             *
             * Qui NON cifriamo la password
             * e NON accediamo al Repository.
             */

            userService.register(
                    form.getUsername(),
                    form.getEmail(),
                    form.getPassword()
            );


            /*
             * Registrazione completata.
             *
             * Mostriamo un messaggio
             * nella pagina di login.
             */

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registrazione completata! Ora puoi accedere."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            /*
             * Il Service ha rilevato
             * un problema nei dati.
             */

            bindingResult.reject(
                    "registrationError",
                    ex.getMessage()
            );

            return "register";

        } catch (DataIntegrityViolationException ex) {

            /*
             * Gestiamo anche un possibile
             * conflitto con i vincoli UNIQUE
             * del database.
             */

            bindingResult.reject(
                    "registrationError",
                    "Username o email già utilizzati."
            );

            return "register";
        }
    }

}