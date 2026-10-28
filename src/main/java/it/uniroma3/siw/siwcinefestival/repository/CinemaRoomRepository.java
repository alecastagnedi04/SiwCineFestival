package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwcinefestival.model.CinemaRoom;


public interface CinemaRoomRepository
        extends JpaRepository<CinemaRoom, Long> {


    /*
     * =====================================================
     * ELENCO SALE
     * =====================================================
     *
     * Restituisce tutte le sale
     * ordinate per nome.
     */
    List<CinemaRoom> findAllByOrderByNameAsc();



    /*
     * =====================================================
     * RECUPERO SALA CON LOCK
     * =====================================================
     *
     * PESSIMISTIC_WRITE permette
     * di ottenere un lock sulla sala
     * durante una transazione.
     *
     * Sarà utilizzato dal ScreeningService
     * quando programmiamo o modifichiamo
     * una proiezione.
     *
     * Due transazioni che tentano di
     * programmare contemporaneamente
     * nella stessa sala dovranno
     * coordinarsi attraverso il lock.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM CinemaRoom r
        WHERE r.id = :roomId
        """)
    Optional<CinemaRoom> findByIdForUpdate(

            @Param("roomId")
            Long roomId
    );

}