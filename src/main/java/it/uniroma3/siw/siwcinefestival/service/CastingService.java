package it.uniroma3.siw.siwcinefestival.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Actor;
import it.uniroma3.siw.siwcinefestival.model.Casting;
import it.uniroma3.siw.siwcinefestival.model.Film;

import it.uniroma3.siw.siwcinefestival.repository.ActorRepository;
import it.uniroma3.siw.siwcinefestival.repository.CastingRepository;
import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;


@Service
public class CastingService {

    private final CastingRepository castingRepository;
    private final FilmRepository filmRepository;
    private final ActorRepository actorRepository;


    public CastingService(
            CastingRepository castingRepository,
            FilmRepository filmRepository,
            ActorRepository actorRepository) {

        this.castingRepository = castingRepository;
        this.filmRepository = filmRepository;
        this.actorRepository = actorRepository;
    }


    /*
     * CAST DI UN FILM
     */

    @Transactional(readOnly = true)
    public List<Casting> findCastByFilmId(Long filmId) {

        return castingRepository
                .findByFilmIdOrderByIdAsc(filmId);
    }


    /*
     * FILMOGRAFIA DI UN ATTORE
     */

    @Transactional(readOnly = true)
    public List<Casting> findFilmographyByActorId(
            Long actorId) {

        return castingRepository
                .findByActorIdOrderByIdAsc(actorId);
    }


    /*
     * AGGIUNTA ATTORE AL CAST
     */

    @Transactional
    public Casting addActorToFilm(
            Long filmId,
            Long actorId,
            String characterName) {

        validateCharacterName(characterName);

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );

        Actor actor = actorRepository
                .findById(actorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Attore non trovato."
                        )
                );

        if (castingRepository.existsByFilmIdAndActorId(
                filmId, actorId)) {

            throw new IllegalStateException(
                    "Questo attore è già presente nel cast."
            );
        }

        Casting casting = new Casting();

        casting.setFilm(film);
        casting.setActor(actor);
        casting.setCharacterName(characterName.trim());

        return castingRepository.save(casting);
    }


    /*
     * MODIFICA CASTING
     */

    @Transactional
    public Casting updateCasting(
            Long castingId,
            Long actorId,
            String characterName) {

        validateCharacterName(characterName);

        Casting casting = castingRepository
                .findById(castingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Casting non trovato."
                        )
                );

        Actor actor = actorRepository
                .findById(actorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Attore non trovato."
                        )
                );

        Long filmId = casting.getFilm().getId();

        /*
         * Se cambiamo attore, controlliamo
         * che quello nuovo non sia già
         * presente nel cast dello stesso film.
         */

        if (!casting.getActor().getId().equals(actorId)
                && castingRepository.existsByFilmIdAndActorId(
                        filmId, actorId)) {

            throw new IllegalStateException(
                    "Questo attore è già presente nel cast."
            );
        }

        casting.setActor(actor);
        casting.setCharacterName(characterName.trim());

        return castingRepository.save(casting);
    }


    /*
     * ELIMINAZIONE DAL CAST
     */

    @Transactional
    public void removeActorFromFilm(
            Long castingId,
            Long filmId) {

        Casting casting = castingRepository
                .findById(castingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Casting non trovato."
                        )
                );

        if (!casting.getFilm().getId().equals(filmId)) {

            throw new IllegalArgumentException(
                    "Il casting non appartiene al film."
            );
        }

        castingRepository.delete(casting);
    }


    /*
     * VALIDAZIONE PERSONAGGIO
     */

    private void validateCharacterName(
            String characterName) {

        if (characterName == null
                || characterName.isBlank()) {

            throw new IllegalArgumentException(
                    "Il nome del personaggio è obbligatorio."
            );
        }

        if (characterName.trim().length() > 200) {

            throw new IllegalArgumentException(
                    "Il nome del personaggio è troppo lungo."
            );
        }
    }
}