package it.uniroma3.siw.siwcinefestival.controller;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siwcinefestival.model.Festival;

import it.uniroma3.siw.siwcinefestival.service.FestivalService;

@Controller
public class HomeController {  //gestisce la home page e la ricerca dei festival

        private final FestivalService festivalService;

        public HomeController(
                        FestivalService festivalService) {

                this.festivalService = festivalService;
        }

        /* HOMEPAGE CON PAGINAZIONE */
        @GetMapping("/")
        public String home(

                        @RequestParam(name = "festivalPage", defaultValue = "0") int festivalPage,  //per sapere quale pagina di festival mostrare nella home page
                        @RequestParam(name = "q", defaultValue = "") String q, //per recuperare il testo cercato dall'utente nella barra di ricerca
                        Model model) {

                /*
                 * Recuperiamo la pagina richiesta.
                 */
                Page<Festival> result = festivalService.findHomePage(
                                q,
                                festivalPage);

                /*
                 * I festival da mostrare. Al massimo 3.
                 */
                model.addAttribute(
                                "festivals",
                                result.getContent());

                /*
                 * Informazioni sulla paginazione.
                 */
                model.addAttribute(
                                "festivalPage",
                                result);
                                
                /*
                 * Testo della ricerca.
                 */
                model.addAttribute(
                                "q",
                                q.trim());

                return "index";
        }
}