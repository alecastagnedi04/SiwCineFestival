package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siwcinefestival.model.Director;


public interface DirectorRepository
        extends JpaRepository<Director, Long> {


    /*
     * =====================================================
     * ELENCO REGISTI
     * =====================================================
     *
     * Recupera i registi
     * ordinati per cognome e nome.
     */
    List<Director> findAllByOrderBySurnameAscNameAsc();

}