package it.uniroma3.siw.siwcinefestival.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.siwcinefestival.service.ActorService;
import it.uniroma3.siw.siwcinefestival.service.CinemaRoomService;
import it.uniroma3.siw.siwcinefestival.service.DirectorService;
import it.uniroma3.siw.siwcinefestival.service.FestivalService;
import it.uniroma3.siw.siwcinefestival.service.FilmService;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FestivalService festivalService;
    private final FilmService filmService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final CinemaRoomService roomService;


    public AdminController(
            FestivalService festivalService,
            FilmService filmService,
            ActorService actorService,
            DirectorService directorService,
            CinemaRoomService roomService) {

        this.festivalService = festivalService;
        this.filmService = filmService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.roomService = roomService;
    }


    /*
     * =====================================================
     * DASHBOARD ADMIN
     * =====================================================
     *
     * GET /admin
     */

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "festivalCount",
                festivalService.findAll().size()
        );

        model.addAttribute(
                "filmCount",
                filmService.findAll().size()
        );

        model.addAttribute(
                "actorCount",
                actorService.findAll().size()
        );

        model.addAttribute(
                "directorCount",
                directorService.findAll().size()
        );

        model.addAttribute(
                "roomCount",
                roomService.findAll().size()
        );

        return "admin/dashboard";
    }
}