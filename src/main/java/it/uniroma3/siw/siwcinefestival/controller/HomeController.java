package it.uniroma3.siw.siwcinefestival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.siwcinefestival.service.FestivalService;


@Controller
public class HomeController {

    private final FestivalService festivalService;


    public HomeController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }


    /*
     * =====================================================
     * HOMEPAGE
     * =====================================================
     *
     * URL:
     * GET /
     *
     * Mostra la homepage pubblica.
     *
     * Recuperiamo anche i festival
     * per mostrarli nella pagina iniziale.
     */
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "festivals",
                festivalService.findAll()
        );

        return "index";
    }

}