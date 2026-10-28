package it.uniroma3.siw.siwcinefestival.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Role;
import it.uniroma3.siw.siwcinefestival.model.User;
import it.uniroma3.siw.siwcinefestival.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * =====================================================
     * ELENCO UTENTI
     * =====================================================
     */

    @Transactional(readOnly = true)
    public List<User> findAll() {

        return userRepository.findAllByOrderByUsernameAsc();
    }


    /*
     * =====================================================
     * RICERCA UTENTE
     * =====================================================
     */

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByUsername(
                username.trim().toLowerCase(Locale.ROOT)
        );
    }


    /*
     * =====================================================
     * REGISTRAZIONE UTENTE
     * =====================================================
     */

    @Transactional
    public User register(
            String username,
            String email,
            String password) {

        validateRegistration(username, email, password);

        String normalizedUsername =
                username.trim().toLowerCase(Locale.ROOT);

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);


        // Controlliamo se lo username esiste già

        if (userRepository.existsByUsernameIgnoreCase(
                normalizedUsername)) {

            throw new IllegalStateException(
                    "Username già utilizzato."
            );
        }


        // Controlliamo se l'email esiste già

        if (userRepository.existsByEmailIgnoreCase(
                normalizedEmail)) {

            throw new IllegalStateException(
                    "Email già utilizzata."
            );
        }


        // Creiamo il nuovo utente

        User user = new User();

        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);


        // Password cifrata con BCrypt

        user.setPassword(
                passwordEncoder.encode(password)
        );


        // Ogni nuovo utente è USER

        user.setRole(Role.USER);


        // Salviamo nel database

        return userRepository.save(user);
    }


    /*
     * =====================================================
     * VALIDAZIONE REGISTRAZIONE
     * =====================================================
     */

    private void validateRegistration(
            String username,
            String email,
            String password) {

        if (username == null || username.isBlank()) {

            throw new IllegalArgumentException(
                    "Lo username è obbligatorio."
            );
        }


        if (username.trim().length() < 3) {

            throw new IllegalArgumentException(
                    "Lo username deve avere almeno 3 caratteri."
            );
        }


        if (email == null || email.isBlank()) {

            throw new IllegalArgumentException(
                    "L'email è obbligatoria."
            );
        }


        if (!email.contains("@")) {

            throw new IllegalArgumentException(
                    "L'indirizzo email non è valido."
            );
        }


        if (password == null || password.length() < 8) {

            throw new IllegalArgumentException(
                    "La password deve avere almeno 8 caratteri."
            );
        }
    }
}