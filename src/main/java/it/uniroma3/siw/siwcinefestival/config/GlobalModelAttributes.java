package it.uniroma3.siw.siwcinefestival.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
public class GlobalModelAttributes {


    /*
     * =====================================================
     * ATTRIBUTI GLOBALI
     * =====================================================
     *
     * Questi attributi saranno disponibili
     * nei template Thymeleaf.
     *
     * Evitiamo di ripetere la stessa logica
     * in tutti i Controller.
     */

    @ModelAttribute
    public void addGlobalAttributes(Model model) {


        /*
         * Recuperiamo l'autenticazione
         * dal contesto di Spring Security.
         */

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        /*
         * Verifichiamo che non si tratti
         * di un utente anonimo.
         */

        boolean isAuthenticated =
                authentication != null
                && authentication.isAuthenticated()
                && !(authentication
                        instanceof AnonymousAuthenticationToken);


        /*
         * Username da mostrare
         * nella navbar.
         */

        String currentUsername =
                isAuthenticated
                        ? authentication.getName()
                        : null;


        /*
         * Verifichiamo il ruolo ADMIN.
         */

        boolean isAdmin =
                isAuthenticated
                &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );


        /*
         * Attributi per Thymeleaf.
         */

        model.addAttribute(
                "isAuthenticated",
                isAuthenticated
        );

        model.addAttribute(
                "currentUsername",
                currentUsername
        );

        model.addAttribute(
                "isAdmin",
                isAdmin
        );
    }

}