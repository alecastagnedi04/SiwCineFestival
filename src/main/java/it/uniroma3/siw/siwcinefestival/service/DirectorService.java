package it.uniroma3.siw.siwcinefestival.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Director;
import it.uniroma3.siw.siwcinefestival.repository.DirectorRepository;


@Service
public class DirectorService {

    private final DirectorRepository directorRepository;


    public DirectorService(
            DirectorRepository directorRepository) {

        this.directorRepository = directorRepository;
    }


    @Transactional(readOnly = true)
    public List<Director> findAll() {

        return directorRepository
                .findAllByOrderBySurnameAscNameAsc();
    }


    @Transactional(readOnly = true)
    public Optional<Director> findById(Long id) {

        return directorRepository.findById(id);
    }


    /*
     * CREAZIONE REGISTA
     */

    @Transactional
    public Director createDirector(
            String name,
            String surname) {

        validateDirector(name, surname);

        Director director = new Director();

        director.setName(name.trim());
        director.setSurname(surname.trim());

        return directorRepository.save(director);
    }


    /*
     * MODIFICA REGISTA
     */

    @Transactional
    public Director updateDirector(
            Long directorId,
            String name,
            String surname) {

        Director director = directorRepository
                .findById(directorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Regista non trovato."
                        )
                );

        validateDirector(name, surname);

        director.setName(name.trim());
        director.setSurname(surname.trim());

        return directorRepository.save(director);
    }


    /*
     * VALIDAZIONE
     */

    private void validateDirector(
            String name,
            String surname) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Il nome del regista è obbligatorio."
            );
        }

        if (surname == null || surname.isBlank()) {

            throw new IllegalArgumentException(
                    "Il cognome del regista è obbligatorio."
            );
        }
    }
}