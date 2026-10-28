package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siwcinefestival.model.User;


public interface UserRepository
        extends JpaRepository<User, Long> {


    /*
     * =====================================================
     * CERCA UTENTE PER USERNAME
     * =====================================================
     *
     * Sarà utilizzato da Spring Security
     * per effettuare il login.
     */
    Optional<User> findByUsername(
            String username
    );



    /*
     * =====================================================
     * CERCA UTENTE PER EMAIL
     * =====================================================
     */
    Optional<User> findByEmailIgnoreCase(
            String email
    );



    /*
     * =====================================================
     * USERNAME GIÀ UTILIZZATO?
     * =====================================================
     *
     * IgnoreCase significa che:
     *
     * Alessia
     * alessia
     * ALESSIA
     *
     * vengono considerati equivalenti
     * durante questo controllo.
     */
    boolean existsByUsernameIgnoreCase(
            String username
    );



    /*
     * =====================================================
     * EMAIL GIÀ UTILIZZATA?
     * =====================================================
     */
    boolean existsByEmailIgnoreCase(
            String email
    );



    /*
     * =====================================================
     * ELENCO UTENTI
     * =====================================================
     *
     * Ordinati alfabeticamente
     * per username.
     */
    List<User> findAllByOrderByUsernameAsc();

}