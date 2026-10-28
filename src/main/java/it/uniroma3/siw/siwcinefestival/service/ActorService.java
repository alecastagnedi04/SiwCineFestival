package it.uniroma3.siw.siwcinefestival.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Actor;
import it.uniroma3.siw.siwcinefestival.repository.ActorRepository;


@Service
public class ActorService {

    private final ActorRepository actorRepository;


    public ActorService(ActorRepository actorRepository) {

        this.actorRepository = actorRepository;
    }


    @Transactional(readOnly = true)
    public List<Actor> findAll() {

        return actorRepository
                .findAllByOrderBySurnameAscNameAsc();
    }


    @Transactional(readOnly = true)
    public Optional<Actor> findById(Long id) {

        return actorRepository.findById(id);
    }


    /*
     * CREAZIONE ATTORE
     */

    @Transactional
    public Actor createActor(
            String name,
            String surname,
            LocalDate birthDate,
            String nationality) {

        validateActor(
                name, surname, birthDate, nationality
        );

        Actor actor = new Actor();

        actor.setName(name.trim());
        actor.setSurname(surname.trim());
        actor.setBirthDate(birthDate);
        actor.setNationality(nationality.trim());

        return actorRepository.save(actor);
    }


    /*
     * MODIFICA ATTORE
     */

    @Transactional
    public Actor updateActor(
            Long actorId,
            String name,
            String surname,
            LocalDate birthDate,
            String nationality) {

        Actor actor = actorRepository
                .findById(actorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Attore non trovato."
                        )
                );

        validateActor(
                name, surname, birthDate, nationality
        );

        actor.setName(name.trim());
        actor.setSurname(surname.trim());
        actor.setBirthDate(birthDate);
        actor.setNationality(nationality.trim());

        return actorRepository.save(actor);
    }


    /*
     * VALIDAZIONE
     */

    private void validateActor(
            String name,
            String surname,
            LocalDate birthDate,
            String nationality) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Il nome dell'attore è obbligatorio."
            );
        }

        if (surname == null || surname.isBlank()) {

            throw new IllegalArgumentException(
                    "Il cognome dell'attore è obbligatorio."
            );
        }

        if (birthDate == null) {

            throw new IllegalArgumentException(
                    "La data di nascita è obbligatoria."
            );
        }

        if (birthDate.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La data di nascita non può essere futura."
            );
        }

        if (nationality == null || nationality.isBlank()) {

            throw new IllegalArgumentException(
                    "La nazionalità è obbligatoria."
            );
        }
    }
}