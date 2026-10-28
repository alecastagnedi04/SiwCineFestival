package it.uniroma3.siw.siwcinefestival.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.CinemaRoom;
import it.uniroma3.siw.siwcinefestival.model.Director;
import it.uniroma3.siw.siwcinefestival.model.Film;
import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.model.ScreeningStatus;

import it.uniroma3.siw.siwcinefestival.repository.CinemaRoomRepository;
import it.uniroma3.siw.siwcinefestival.repository.DirectorRepository;
import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;
import it.uniroma3.siw.siwcinefestival.repository.ScreeningRepository;


@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    private final ScreeningRepository screeningRepository;
    private final CinemaRoomRepository cinemaRoomRepository;


    public FilmService(
            FilmRepository filmRepository,
            DirectorRepository directorRepository,
            ScreeningRepository screeningRepository,
            CinemaRoomRepository cinemaRoomRepository) {

        this.filmRepository = filmRepository;
        this.directorRepository = directorRepository;
        this.screeningRepository = screeningRepository;
        this.cinemaRoomRepository = cinemaRoomRepository;
    }


    /*
     * =====================================================
     * LETTURA
     * =====================================================
     */

    @Transactional(readOnly = true)
    public List<Film> findAll() {

        return filmRepository.findAllByOrderByTitleAsc();
    }


    @Transactional(readOnly = true)
    public Optional<Film> findById(Long id) {

        return filmRepository.findById(id);
    }


    @Transactional(readOnly = true)
public Optional<Film> findByIdWithDirectorAndCast(
        Long id) {

    Optional<Film> result =
            filmRepository.findByIdWithDirectorAndCast(id);

    /*
     * Director e Casting vengono caricati
     * tramite EntityGraph nel Repository.
     *
     * Inizializziamo separatamente i festival
     * mentre la transazione è ancora aperta.
     */

    result.ifPresent(film -> {
        film.getFestivals().size();
    });

    return result;
}


    @Transactional(readOnly = true)
    public List<Film> findByFestivalId(Long festivalId) {

        return filmRepository
                .findFilmsByFestivalId(festivalId);
    }


    /*
     * =====================================================
     * CREAZIONE FILM
     * =====================================================
     */

    @Transactional
    public Film createFilm(
            String title,
            Integer year,
            Integer duration,
            String genre,
            String productionCountry,
            Long directorId) {

        validateFilm(
                title, year, duration,
                genre, productionCountry
        );

        Director director = directorRepository
                .findById(directorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Regista non trovato."
                        )
                );

        Film film = new Film();

        film.setTitle(title.trim());
        film.setYear(year);
        film.setDuration(duration);
        film.setGenre(genre.trim());
        film.setProductionCountry(
                productionCountry.trim()
        );
        film.setDirector(director);

        return filmRepository.save(film);
    }


    /*
     * =====================================================
     * MODIFICA FILM
     * =====================================================
     */

    @Transactional
    public Film updateFilm(
            Long filmId,
            String title,
            Integer year,
            Integer duration,
            String genre,
            String productionCountry,
            Long directorId) {

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );

        validateFilm(
                title, year, duration,
                genre, productionCountry
        );

        Director director = directorRepository
                .findById(directorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Regista non trovato."
                        )
                );

        /*
         * Se cambia la durata del film,
         * controlliamo tutte le proiezioni
         * già programmate.
         */

        if (!film.getDuration().equals(duration)) {

            validateDurationChange(
                    filmId,
                    duration
            );
        }

        film.setTitle(title.trim());
        film.setYear(year);
        film.setDuration(duration);
        film.setGenre(genre.trim());
        film.setProductionCountry(
                productionCountry.trim()
        );
        film.setDirector(director);

        return filmRepository.save(film);
    }


    /*
     * =====================================================
     * VERIFICA MODIFICA DURATA
     * =====================================================
     *
     * Controlliamo che la nuova durata
     * non produca sovrapposizioni
     * nelle sale interessate.
     *
     * Per coerenza con ScreeningService,
     * acquisiamo i lock delle sale
     * in ordine crescente.
     */

    private void validateDurationChange(
            Long filmId,
            Integer newDuration) {

        List<Screening> affectedScreenings =
                screeningRepository
                        .findByFilmIdOrderByDateAscTimeAsc(
                                filmId
                        );

        TreeSet<Long> roomIds = new TreeSet<>();

        for (Screening screening : affectedScreenings) {

            if (screening.getStatus()
                    != ScreeningStatus.CANCELLED) {

                roomIds.add(
                        screening.getRoom().getId()
                );
            }
        }

        /*
         * Blocchiamo le sale interessate.
         */

        for (Long roomId : roomIds) {

            cinemaRoomRepository
                    .findByIdForUpdate(roomId)
                    .orElseThrow(() ->
                            new NoSuchElementException(
                                    "Sala non trovata."
                            )
                    );
        }

        /*
         * Per ogni sala controlliamo
         * l'intero programma.
         */

        for (Long roomId : roomIds) {

            List<Screening> screenings =
                    screeningRepository
                            .findByRoomIdAndStatusNotOrderByDateAscTimeAsc(
                                    roomId,
                                    ScreeningStatus.CANCELLED
                            );

            /*
             * Confrontiamo ogni proiezione
             * con quelle successive.
             */

            for (int i = 0; i < screenings.size(); i++) {

                Screening first = screenings.get(i);

                LocalDateTime firstStart =
                        LocalDateTime.of(
                                first.getDate(),
                                first.getTime()
                        );

                int firstDuration =
                        first.getFilm().getId().equals(filmId)
                                ? newDuration
                                : first.getFilm().getDuration();

                LocalDateTime firstEnd =
                        firstStart.plusMinutes(firstDuration);

                for (int j = i + 1;
                     j < screenings.size();
                     j++) {

                    Screening second = screenings.get(j);

                    LocalDateTime secondStart =
                            LocalDateTime.of(
                                    second.getDate(),
                                    second.getTime()
                            );

                    int secondDuration =
                            second.getFilm().getId().equals(filmId)
                                    ? newDuration
                                    : second.getFilm().getDuration();

                    LocalDateTime secondEnd =
                            secondStart.plusMinutes(
                                    secondDuration
                            );

                    boolean overlap =
                            firstStart.isBefore(secondEnd)
                            &&
                            secondStart.isBefore(firstEnd);

                    if (overlap) {

                        throw new IllegalStateException(
                                "La nuova durata del film "
                                + "causerebbe una sovrapposizione "
                                + "di proiezioni nella stessa sala."
                        );
                    }
                }
            }
        }
    }


    /*
     * =====================================================
     * VALIDAZIONE FILM
     * =====================================================
     */

    private void validateFilm(
            String title,
            Integer year,
            Integer duration,
            String genre,
            String productionCountry) {

        if (title == null || title.isBlank()) {

            throw new IllegalArgumentException(
                    "Il titolo del film è obbligatorio."
            );
        }

        if (year == null || year < 1888) {

            throw new IllegalArgumentException(
                    "L'anno del film non è valido."
            );
        }

        if (duration == null || duration <= 0) {

            throw new IllegalArgumentException(
                    "La durata deve essere maggiore di zero."
            );
        }

        if (genre == null || genre.isBlank()) {

            throw new IllegalArgumentException(
                    "Il genere è obbligatorio."
            );
        }

        if (productionCountry == null
                || productionCountry.isBlank()) {

            throw new IllegalArgumentException(
                    "Il paese di produzione è obbligatorio."
            );
        }
    }
}