package it.uniroma3.siw.siwcinefestival.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.CinemaRoom;
import it.uniroma3.siw.siwcinefestival.model.Festival;
import it.uniroma3.siw.siwcinefestival.model.Film;
import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.model.ScreeningStatus;

import it.uniroma3.siw.siwcinefestival.repository.CinemaRoomRepository;
import it.uniroma3.siw.siwcinefestival.repository.FestivalRepository;
import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;
import it.uniroma3.siw.siwcinefestival.repository.ScreeningRepository;
import it.uniroma3.siw.siwcinefestival.dto.ScreeningResponse;


@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;
    private final CinemaRoomRepository cinemaRoomRepository;


    public ScreeningService(
            ScreeningRepository screeningRepository,
            FestivalRepository festivalRepository,
            FilmRepository filmRepository,
            CinemaRoomRepository cinemaRoomRepository) {

        this.screeningRepository = screeningRepository;
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
        this.cinemaRoomRepository = cinemaRoomRepository;
    }


    /*
     * =====================================================
     * LETTURA
     * =====================================================
     */

    @Transactional(readOnly = true)
public Optional<Screening> findById(Long id) {

    return screeningRepository.findById(id)
            .map(screening -> {

                /*
                 * Inizializziamo le associazioni
                 * necessarie al form di modifica.
                 */

                screening.getFestival().getName();

                screening.getFilm().getTitle();

                screening.getRoom().getName();

                return screening;
            });
}


    /*
     * Programma delle proiezioni di un festival.
     *
     * Il Repository carica Film e Sala
     * tramite EntityGraph.
     */

    @Transactional(readOnly = true)
    public List<Screening> findProgramByFestivalId(
            Long festivalId) {

        return screeningRepository
                .findByFestivalIdOrderByDateAscTimeAsc(
                        festivalId
                );
    }

/*
 * =====================================================
 * PROGRAMMA FESTIVAL PER API REST
 * =====================================================
 *
 * Recuperiamo le proiezioni del festival
 * e le convertiamo in ScreeningResponse.
 *
 * La conversione avviene all'interno
 * della transazione.
 *
 * In questo modo le associazioni JPA
 * necessarie sono ancora accessibili.
 */

@Transactional(readOnly = true)
public List<ScreeningResponse> findProgramForApi(
        Long festivalId) {


    /*
     * Controlliamo che il festival esista.
     */

    if (!festivalRepository.existsById(festivalId)) {

        throw new NoSuchElementException(
                "Festival non trovato."
        );
    }


    /*
     * Recuperiamo le proiezioni.
     *
     * Il Repository utilizza EntityGraph
     * per richiedere Film e CinemaRoom.
     */

    List<Screening> screenings =
            screeningRepository
                    .findByFestivalIdOrderByDateAscTimeAsc(
                            festivalId
                    );


    /*
     * Convertiamo le entità in DTO.
     *
     * Non restituiamo direttamente
     * gli oggetti JPA al frontend.
     */

    return screenings.stream()

            .map(ScreeningResponse::fromEntity)

            .toList();
}


    /*
     * Proiezioni di uno specifico film.
     */

    @Transactional(readOnly = true)
    public List<Screening> findByFilmId(Long filmId) {

        return screeningRepository
                .findByFilmIdOrderByDateAscTimeAsc(
                        filmId
                );
    }


    /*
 * =====================================================
 * CREAZIONE PROIEZIONE
 * =====================================================
 *
 * Crea una proiezione e, se necessario,
 * associa automaticamente il film al festival.
 *
 * Tutto avviene nella stessa transazione.
 */

@Transactional
public Screening createScreening(
        Long festivalId,
        Long filmId,
        Long roomId,
        LocalDate date,
        LocalTime time,
        ScreeningStatus status) {


    /*
     * ================================================
     * 1. VALIDAZIONE CAMPI OBBLIGATORI
     * ================================================
     */

    validateRequiredFields(
            festivalId,
            filmId,
            roomId,
            date,
            time
    );


    /*
     * ================================================
     * 2. RECUPERIAMO FESTIVAL E FILM
     * ================================================
     */

    Festival festival = requireFestival(festivalId);

    Film film = requireFilm(filmId);


    /*
     * ================================================
     * 3. BLOCCO DELLA SALA
     * ================================================
     *
     * Acquisiamo un lock sulla sala
     * per gestire correttamente
     * eventuali operazioni concorrenti.
     */

    CinemaRoom room = lockRoom(roomId);


    /*
     * ================================================
     * 4. STATO DELLA PROIEZIONE
     * ================================================
     */

    ScreeningStatus finalStatus =
            status != null
                    ? status
                    : ScreeningStatus.SCHEDULED;


    /*
     * ================================================
     * 5. CONTROLLO DATA DEL FESTIVAL
     * ================================================
     *
     * La data deve essere compresa
     * tra l'inizio e la fine del festival.
     */

    validateDateInFestival(
            date,
            festival
    );


    /*
     * ================================================
     * 6. CONTROLLO DISPONIBILITÀ DELLA SALA
     * ================================================
     *
     * Le proiezioni annullate
     * non occupano la sala.
     */

    if (finalStatus != ScreeningStatus.CANCELLED) {

        validateRoomAvailability(
                roomId,
                null,
                date,
                time,
                film.getDuration()
        );
    }


    /*
     * ================================================
     * 7. ASSOCIAZIONE AUTOMATICA FILM - FESTIVAL
     * ================================================
     *
     * Se il film non partecipa ancora
     * al festival, viene aggiunto.
     *
     * Se partecipa già, non succede nulla.
     */

    ensureFilmInFestival(
            festival,
            film
    );


    /*
     * ================================================
     * 8. CREAZIONE DELLA PROIEZIONE
     * ================================================
     */

    Screening screening = new Screening();

    screening.setFestival(festival);

    screening.setFilm(film);

    screening.setRoom(room);

    screening.setDate(date);

    screening.setTime(time);

    screening.setStatus(finalStatus);


    /*
     * ================================================
     * 9. SALVATAGGIO
     * ================================================
     *
     * Anche il salvataggio dell'associazione
     * Film-Festival appartiene a questa
     * transazione.
     */

    return screeningRepository.save(screening);
}


    /*
 * =====================================================
 * MODIFICA PROIEZIONE
 * =====================================================
 *
 * Modifica una proiezione esistente.
 *
 * Se il film selezionato non appartiene
 * ancora al festival, lo associa
 * automaticamente.
 */

@Transactional
public Screening updateScreening(
        Long screeningId,
        Long festivalId,
        Long filmId,
        Long roomId,
        LocalDate date,
        LocalTime time,
        ScreeningStatus status) {


    /*
     * ================================================
     * 1. VALIDAZIONE
     * ================================================
     */

    validateRequiredFields(
            festivalId,
            filmId,
            roomId,
            date,
            time
    );


    /*
     * ================================================
     * 2. RECUPERIAMO LA PROIEZIONE
     * ================================================
     */

    Screening screening = screeningRepository
            .findById(screeningId)
            .orElseThrow(() ->
                    new NoSuchElementException(
                            "Proiezione non trovata."
                    )
            );


    /*
     * ================================================
     * 3. BLOCCO DELLE SALE
     * ================================================
     */

    Long oldRoomId =
            screening.getRoom().getId();

    lockRoomsInOrder(
            oldRoomId,
            roomId
    );


    /*
     * ================================================
     * 4. RECUPERIAMO LE ENTITÀ
     * ================================================
     */

    Festival festival = requireFestival(festivalId);

    Film film = requireFilm(filmId);

    CinemaRoom room = cinemaRoomRepository
            .findById(roomId)
            .orElseThrow(() ->
                    new NoSuchElementException(
                            "Sala non trovata."
                    )
            );


    /*
     * ================================================
     * 5. STATO DELLA PROIEZIONE
     * ================================================
     */

    ScreeningStatus finalStatus =
            status != null
                    ? status
                    : ScreeningStatus.SCHEDULED;


    /*
     * ================================================
     * 6. CONTROLLO DATA
     * ================================================
     */

    validateDateInFestival(
            date,
            festival
    );


    /*
     * ================================================
     * 7. CONTROLLO DISPONIBILITÀ
     * ================================================
     */

    if (finalStatus != ScreeningStatus.CANCELLED) {

        validateRoomAvailability(
                roomId,
                screeningId,
                date,
                time,
                film.getDuration()
        );
    }


    /*
     * ================================================
     * 8. ASSOCIAZIONE AUTOMATICA
     * ================================================
     */

    ensureFilmInFestival(
            festival,
            film
    );


    /*
     * ================================================
     * 9. AGGIORNAMENTO PROIEZIONE
     * ================================================
     */

    screening.setFestival(festival);

    screening.setFilm(film);

    screening.setRoom(room);

    screening.setDate(date);

    screening.setTime(time);

    screening.setStatus(finalStatus);


    /*
     * ================================================
     * 10. SALVATAGGIO
     * ================================================
     */

    return screeningRepository.save(screening);
}

    /*
     * =====================================================
     * ELIMINAZIONE PROIEZIONE
     * =====================================================
     */

    @Transactional
    public void deleteScreening(Long screeningId) {

        Screening screening = screeningRepository
                .findById(screeningId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Proiezione non trovata."
                        )
                );

        /*
         * Acquisiamo il lock sulla sala
         * per coordinare l'eliminazione
         * con le operazioni di programmazione.
         */

        lockRoom(screening.getRoom().getId());

        screeningRepository.delete(screening);
    }


    /*
     * =====================================================
     * RECUPERO ENTITÀ
     * =====================================================
     */

    private Festival requireFestival(Long festivalId) {

        return festivalRepository
                .findById(festivalId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Festival non trovato."
                        )
                );
    }


    private Film requireFilm(Long filmId) {

        return filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );
    }


    /*
     * =====================================================
     * LOCK SALA
     * =====================================================
     */

    private CinemaRoom lockRoom(Long roomId) {

        return cinemaRoomRepository
                .findByIdForUpdate(roomId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Sala non trovata."
                        )
                );
    }


    private void lockRoomsInOrder(
            Long firstRoomId,
            Long secondRoomId) {

        TreeSet<Long> ids = new TreeSet<>();

        ids.add(firstRoomId);
        ids.add(secondRoomId);

        for (Long id : ids) {

            lockRoom(id);
        }
    }


    /*
 * =====================================================
 * ASSOCIAZIONE AUTOMATICA FILM - FESTIVAL
 * =====================================================
 *
 * Quando programmiamo una proiezione:
 *
 * 1. Controlliamo se il film appartiene al festival.
 *
 * 2. Se appartiene già al festival,
 *    non dobbiamo fare nulla.
 *
 * 3. Se non appartiene ancora al festival,
 *    creiamo automaticamente l'associazione.
 *
 * L'operazione viene eseguita all'interno
 * della transazione della proiezione.
 */

private void ensureFilmInFestival(
        Festival festival,
        Film film) {

    /*
     * Recuperiamo gli ID.
     */

    Long festivalId = festival.getId();

    Long filmId = film.getId();


    /*
     * Controlliamo se l'associazione
     * esiste già nel database.
     */

    boolean alreadyAssociated =
            festivalRepository.isFilmInFestival(
                    festivalId,
                    filmId
            );


    /*
     * Se il film partecipa già al festival,
     * non creiamo un'associazione duplicata.
     */

    if (alreadyAssociated) {
        return;
    }


    /*
     * Il film non appartiene ancora
     * al festival.
     *
     * Utilizziamo il metodo addFilm()
     * che abbiamo già definito in Festival.
     *
     * Questo metodo aggiorna entrambi
     * i lati della relazione ManyToMany.
     */

    festival.addFilm(film);


    /*
     * Salviamo l'associazione.
     *
     * La nuova relazione sarà registrata
     * nella tabella festival_films.
     */

    festivalRepository.save(festival);
}


    /*
     * =====================================================
     * CONTROLLA DATA
     * =====================================================
     */

    private void validateDateInFestival(
            LocalDate date,
            Festival festival) {

        if (date.isBefore(festival.getStartDate())
                || date.isAfter(festival.getEndDate())) {

            throw new IllegalArgumentException(
                    "La data della proiezione deve essere "
                    + "compresa nelle date del festival."
            );
        }
    }


    /*
     * =====================================================
     * CONTROLLO SOVRAPPOSIZIONI
     * =====================================================
     *
     * Due intervalli si sovrappongono quando:
     *
     * startA < endB
     * AND
     * startB < endA
     *
     * Usiamo LocalDateTime anche per
     * gestire le proiezioni che
     * terminano dopo mezzanotte.
     */

    private void validateRoomAvailability(
            Long roomId,
            Long excludedScreeningId,
            LocalDate date,
            LocalTime time,
            Integer durationMinutes) {

        LocalDateTime newStart =
                LocalDateTime.of(date, time);

        LocalDateTime newEnd =
                newStart.plusMinutes(
                        durationMinutes
                );

        List<Screening> existingScreenings =
                screeningRepository
                        .findByRoomIdAndStatusNotOrderByDateAscTimeAsc(
                                roomId,
                                ScreeningStatus.CANCELLED
                        );

        for (Screening existing : existingScreenings) {

            /*
             * Durante la modifica ignoriamo
             * la proiezione stessa.
             */

            if (excludedScreeningId != null
                    && excludedScreeningId.equals(
                            existing.getId())) {

                continue;
            }

            LocalDateTime existingStart =
                    LocalDateTime.of(
                            existing.getDate(),
                            existing.getTime()
                    );

            LocalDateTime existingEnd =
                    existingStart.plusMinutes(
                            existing.getFilm().getDuration()
                    );

            boolean overlaps =
                    newStart.isBefore(existingEnd)
                    &&
                    existingStart.isBefore(newEnd);

            if (overlaps) {

                throw new IllegalStateException(
                        "La sala è già occupata "
                        + "nell'intervallo selezionato."
                );
            }
        }
    }


    /*
     * =====================================================
     * VALIDAZIONE CAMPI OBBLIGATORI
     * =====================================================
     */

    private void validateRequiredFields(
            Long festivalId,
            Long filmId,
            Long roomId,
            LocalDate date,
            LocalTime time) {

        if (festivalId == null) {

            throw new IllegalArgumentException(
                    "Seleziona un festival."
            );
        }

        if (filmId == null) {

            throw new IllegalArgumentException(
                    "Seleziona un film."
            );
        }

        if (roomId == null) {

            throw new IllegalArgumentException(
                    "Seleziona una sala."
            );
        }

        if (date == null) {

            throw new IllegalArgumentException(
                    "La data della proiezione è obbligatoria."
            );
        }

        if (time == null) {

            throw new IllegalArgumentException(
                    "L'orario della proiezione è obbligatorio."
            );
        }
    }
}