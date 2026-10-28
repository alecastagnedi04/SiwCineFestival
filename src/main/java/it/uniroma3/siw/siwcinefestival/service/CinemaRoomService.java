package it.uniroma3.siw.siwcinefestival.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.CinemaRoom;
import it.uniroma3.siw.siwcinefestival.repository.CinemaRoomRepository;


@Service
public class CinemaRoomService {

    private final CinemaRoomRepository cinemaRoomRepository;


    public CinemaRoomService(
            CinemaRoomRepository cinemaRoomRepository) {

        this.cinemaRoomRepository = cinemaRoomRepository;
    }


    @Transactional(readOnly = true)
    public List<CinemaRoom> findAll() {

        return cinemaRoomRepository
                .findAllByOrderByNameAsc();
    }


    @Transactional(readOnly = true)
    public Optional<CinemaRoom> findById(Long id) {

        return cinemaRoomRepository.findById(id);
    }


    /*
     * CREAZIONE SALA
     */

    @Transactional
    public CinemaRoom createRoom(
            String name,
            String address,
            Integer capacity) {

        validateRoom(name, address, capacity);

        CinemaRoom room = new CinemaRoom();

        room.setName(name.trim());
        room.setAddress(address.trim());
        room.setCapacity(capacity);

        return cinemaRoomRepository.save(room);
    }


    /*
     * MODIFICA SALA
     */

    @Transactional
    public CinemaRoom updateRoom(
            Long roomId,
            String name,
            String address,
            Integer capacity) {

        CinemaRoom room = cinemaRoomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Sala non trovata."
                        )
                );

        validateRoom(name, address, capacity);

        room.setName(name.trim());
        room.setAddress(address.trim());
        room.setCapacity(capacity);

        return cinemaRoomRepository.save(room);
    }


    /*
     * VALIDAZIONE
     */

    private void validateRoom(
            String name,
            String address,
            Integer capacity) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Il nome della sala è obbligatorio."
            );
        }

        if (address == null || address.isBlank()) {

            throw new IllegalArgumentException(
                    "L'indirizzo è obbligatorio."
            );
        }

        if (capacity == null || capacity <= 0) {

            throw new IllegalArgumentException(
                    "La capienza deve essere maggiore di zero."
            );
        }
    }
}