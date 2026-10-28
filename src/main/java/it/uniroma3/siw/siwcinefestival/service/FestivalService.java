package it.uniroma3.siw.siwcinefestival.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Festival;
import it.uniroma3.siw.siwcinefestival.model.Film;
import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.repository.FestivalRepository;
import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;
import it.uniroma3.siw.siwcinefestival.repository.ScreeningRepository;


@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;
    private final ScreeningRepository screeningRepository;


    public FestivalService(
            FestivalRepository festivalRepository,
            FilmRepository filmRepository,
            ScreeningRepository screeningRepository) {

        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
        this.screeningRepository = screeningRepository;
    }


    /*
     * =====================================================
     * LETTURA
     * =====================================================
     */

    @Transactional(readOnly = true)
    public List<Festival> findAll() {

        return festivalRepository
                .findAllByOrderByStartDateDesc();
    }


    @Transactional(readOnly = true)
    public Optional<Festival> findById(Long id) {

        return festivalRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public Optional<Festival> findByIdWithFilms(Long id) {

        return festivalRepository.findByIdWithFilms(id);
    }


    /*
     * =====================================================
     * CREAZIONE FESTIVAL
     * =====================================================
     */

    @Transactional
    public Festival createFestival(
            String name,
            Integer year,
            String city,
            LocalDate startDate,
            LocalDate endDate,
            String description) {

        validateFestival(
                name, year, city, startDate, endDate
        );

        Festival festival = new Festival();

        festival.setName(name.trim());
        festival.setYear(year);
        festival.setCity(city.trim());
        festival.setStartDate(startDate);
        festival.setEndDate(endDate);
        festival.setDescription(
                description != null ? description.trim() : ""
        );

        return festivalRepository.save(festival);
    }


    /*
     * =====================================================
     * MODIFICA FESTIVAL
     * =====================================================
     */

    @Transactional
    public Festival updateFestival(
            Long festivalId,
            String name,
            Integer year,
            String city,
            LocalDate startDate,
            LocalDate endDate,
            String description) {

        Festival festival = festivalRepository
                .findById(festivalId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Festival non trovato."
                        )
                );

        validateFestival(
                name, year, city, startDate, endDate
        );

        /*
         * Prima di modificare le date controlliamo
         * che le proiezioni esistenti rimangano valide.
         */

        List<Screening> screenings =
                screeningRepository
                        .findByFestivalIdOrderByDateAscTimeAsc(
                                festivalId
                        );

        for (Screening screening : screenings) {

            LocalDate date = screening.getDate();

            if (date.isBefore(startDate)
                    || date.isAfter(endDate)) {

                throw new IllegalStateException(
                        "Impossibile modificare le date: "
                        + "esistono proiezioni fuori "
                        + "dal nuovo intervallo."
                );
            }
        }

        festival.setName(name.trim());
        festival.setYear(year);
        festival.setCity(city.trim());
        festival.setStartDate(startDate);
        festival.setEndDate(endDate);
        festival.setDescription(
                description != null ? description.trim() : ""
        );

        return festivalRepository.save(festival);
    }


    /*
     * =====================================================
     * ASSOCIA FILM A FESTIVAL
     * =====================================================
     */

    @Transactional
    public void addFilmToFestival(
            Long festivalId,
            Long filmId) {

        Festival festival = festivalRepository
                .findByIdWithFilms(festivalId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Festival non trovato."
                        )
                );

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );

        if (festivalRepository.isFilmInFestival(
                festivalId, filmId)) {

            throw new IllegalStateException(
                    "Questo film partecipa già al festival."
            );
        }

        /*
         * Aggiorniamo entrambi i lati
         * della relazione ManyToMany.
         */

        festival.addFilm(film);

        festivalRepository.save(festival);
    }


    /*
     * =====================================================
     * RIMUOVI FILM DA FESTIVAL
     * =====================================================
     */

    @Transactional
    public void removeFilmFromFestival(
            Long festivalId,
            Long filmId) {

        Festival festival = festivalRepository
                .findByIdWithFilms(festivalId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Festival non trovato."
                        )
                );

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );

        /*
         * Non eliminiamo l'associazione se
         * esistono proiezioni del film
         * all'interno di questo festival.
         */

        if (screeningRepository
                .existsByFestivalIdAndFilmId(
                        festivalId, filmId)) {

            throw new IllegalStateException(
                    "Impossibile rimuovere il film: "
                    + "esistono ancora proiezioni "
                    + "associate a questo festival."
            );
        }

        festival.removeFilm(film);

        festivalRepository.save(festival);
    }


    /*
     * =====================================================
     * VALIDAZIONE
     * =====================================================
     */

    private void validateFestival(
            String name,
            Integer year,
            String city,
            LocalDate startDate,
            LocalDate endDate) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Il nome del festival è obbligatorio."
            );
        }

        if (year == null || year < 1895) {

            throw new IllegalArgumentException(
                    "L'anno del festival non è valido."
            );
        }

        if (city == null || city.isBlank()) {

            throw new IllegalArgumentException(
                    "La città è obbligatoria."
            );
        }

        if (startDate == null || endDate == null) {

            throw new IllegalArgumentException(
                    "Le date del festival sono obbligatorie."
            );
        }

        if (endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "La data di fine non può precedere "
                    + "la data di inizio."
            );
        }
    }
}