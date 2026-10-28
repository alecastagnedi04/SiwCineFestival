package it.uniroma3.siw.siwcinefestival.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import it.uniroma3.siw.siwcinefestival.model.Screening;


public record ScreeningResponse(

        Long id,

        Long festivalId,

        Long filmId,

        String filmTitle,

        Long roomId,

        String roomName,

        LocalDate date,

        LocalTime time,

        String status

) {


    /*
     * =====================================================
     * CONVERSIONE DA ENTITY A DTO
     * =====================================================
     *
     * Riceviamo una Screening
     * e costruiamo un oggetto contenente
     * solo le informazioni necessarie
     * al frontend React.
     *
     * La conversione deve avvenire
     * quando le associazioni richieste
     * sono disponibili.
     */
    public static ScreeningResponse fromEntity(
            Screening screening) {


        return new ScreeningResponse(

                screening.getId(),

                screening.getFestival().getId(),

                screening.getFilm().getId(),

                screening.getFilm().getTitle(),

                screening.getRoom().getId(),

                screening.getRoom().getName(),

                screening.getDate(),

                screening.getTime(),

                screening.getStatus().name()
        );
    }
}