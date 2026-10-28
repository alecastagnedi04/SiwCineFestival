package it.uniroma3.siw.siwcinefestival.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.siwcinefestival.dto.FestivalSummaryResponse;
import it.uniroma3.siw.siwcinefestival.service.FestivalService;

@RestController
@RequestMapping("/api/festivals")
public class FestivalApiController {

    private final FestivalService festivalService;


    public FestivalApiController(
            FestivalService festivalService) {

        this.festivalService = festivalService;
    }


    /*
     * GET /api/festivals
     *
     * Restituisce tutti i festival
     * in formato JSON.
     */

    @GetMapping
    public List<FestivalSummaryResponse> getFestivals() {

        return festivalService.findAll()

                .stream()

                .map(FestivalSummaryResponse::fromEntity)

                .toList();
    }
}