package it.uniroma3.siw.siwcinefestival.dto;

import java.time.LocalDate;

import it.uniroma3.siw.siwcinefestival.model.Festival;

public record FestivalSummaryResponse(

        Long id,
        String name,
        Integer year,
        String city,
        LocalDate startDate,
        LocalDate endDate

) {

    /*
     * Converte un'entità Festival
     * in un DTO utilizzabile da React.
     */

    public static FestivalSummaryResponse fromEntity(
            Festival festival) {

        return new FestivalSummaryResponse(

                festival.getId(),
                festival.getName(),
                festival.getYear(),
                festival.getCity(),
                festival.getStartDate(),
                festival.getEndDate()
        );
    }
}