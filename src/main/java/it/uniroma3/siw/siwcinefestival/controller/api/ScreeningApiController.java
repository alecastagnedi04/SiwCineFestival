package it.uniroma3.siw.siwcinefestival.controller.api;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import it.uniroma3.siw.siwcinefestival.dto.ScreeningResponse;

import it.uniroma3.siw.siwcinefestival.service.ScreeningService;


@RestController
@RequestMapping("/api/festivals")
public class ScreeningApiController {


    private final ScreeningService screeningService;


    public ScreeningApiController(
            ScreeningService screeningService) {

        this.screeningService = screeningService;
    }


    /*
     * =====================================================
     * API PROGRAMMA FESTIVAL
     * =====================================================
     *
     * GET /api/festivals/{festivalId}/screenings
     *
     * Restituisce le proiezioni
     * del festival in formato JSON.
     *
     * L'endpoint è pubblico.
     */

    @GetMapping("/{festivalId}/screenings")
    public List<ScreeningResponse> getFestivalScreenings(

            @PathVariable("festivalId")
            Long festivalId) {


        try {

            /*
             * La logica di accesso ai dati
             * si trova nel Service.
             *
             * Il Service recupera le proiezioni
             * e le converte in DTO all'interno
             * della transazione.
             */

            return screeningService
                    .findProgramForApi(festivalId);

        } catch (NoSuchElementException ex) {

            /*
             * Festival inesistente:
             * HTTP 404.
             */

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        }
    }

}