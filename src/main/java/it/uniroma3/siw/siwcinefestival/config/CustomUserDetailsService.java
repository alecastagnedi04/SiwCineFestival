package it.uniroma3.siw.siwcinefestival.config;

import java.util.Locale;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.User;

import it.uniroma3.siw.siwcinefestival.repository.UserRepository;


@Service
public class CustomUserDetailsService
        implements UserDetailsService {


    private final UserRepository userRepository;


    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    /*
     * =====================================================
     * CARICAMENTO UTENTE
     * =====================================================
     *
     * Spring Security richiama
     * automaticamente questo metodo
     * durante l'autenticazione.
     */

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {


        /*
         * Controlliamo che lo username
         * sia stato inserito.
         */

        if (username == null || username.isBlank()) {

            throw new UsernameNotFoundException(
                    "Username non valido."
            );
        }


        /*
         * Normalizziamo lo username.
         *
         * Esempio:
         *
         * Mario -> mario
         * MARIO -> mario
         */

        String normalizedUsername =
                username.trim()
                        .toLowerCase(Locale.ROOT);


        /*
         * Recuperiamo l'utente
         * dal database PostgreSQL.
         */

        User user = userRepository
                .findByUsername(normalizedUsername)

                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Utente non trovato."
                        )
                );


        /*
         * Convertiamo la nostra entità User
         * in un oggetto UserDetails.
         *
         * Spring Security utilizzerà:
         *
         * - username
         * - password cifrata
         * - ruolo
         */

        return org.springframework.security.core.userdetails.User

                .withUsername(user.getUsername())

                .password(user.getPassword())

                .roles(user.getRole().name())

                .build();
    }

}