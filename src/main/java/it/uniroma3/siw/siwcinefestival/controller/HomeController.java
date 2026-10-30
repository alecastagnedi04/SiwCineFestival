package it.uniroma3.siw.siwcinefestival.controller;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siwcinefestival.model.Festival;

import it.uniroma3.siw.siwcinefestival.service.FestivalService;


@Controller
public class HomeController {

    private final FestivalService festivalService;


    public HomeController(
            FestivalService festivalService) {

        this.festivalService = festivalService;
    }


    /*
     * =====================================================
     * HOMEPAGE CON PAGINAZIONE
     * =====================================================
     *
     * GET /
     *
     * GET /?festivalPage=0
     *
     * GET /?festivalPage=1
     *
     * GET /?q=Venezia
     *
     * La paginazione riguarda soltanto
     * l'anteprima dei festival nella Home.
     */

    @GetMapping("/")
    public String home(

            @RequestParam(
                    name = "festivalPage",
                    defaultValue = "0"
            )
            int festivalPage,

            @RequestParam(
                    name = "q",
                    defaultValue = ""
            )
            String q,

            Model model) {


        /*
         * Recuperiamo la pagina richiesta.
         */

        Page<Festival> result =
                festivalService.findHomePage(
                        q,
                        festivalPage
                );


        /*
         * I festival da mostrare.
         *
         * Al massimo 3.
         */

        model.addAttribute(
                "festivals",
                result.getContent()
        );


        /*
         * Informazioni sulla paginazione.
         */

        model.addAttribute(
                "festivalPage",
                result
        );


        /*
         * Numero totale di festival
         * presenti nel database.
         */

        model.addAttribute(
                "totalFestivalCount",
                festivalService.countFestivals()
        );


        /*
         * Testo della ricerca.
         */

        model.addAttribute(
                "q",
                q.trim()
        );


        return "index";
    }
}