package it.uniroma3.siw.siwcinefestival.config;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwcinefestival.model.Role;
import it.uniroma3.siw.siwcinefestival.model.User;

import it.uniroma3.siw.siwcinefestival.repository.UserRepository;


@Component
public class AdminInitializer implements ApplicationRunner {


    private static final Logger logger =
            LoggerFactory.getLogger(AdminInitializer.class);


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final String adminUsername;

    private final String adminEmail;

    private final String adminPassword;


    public AdminInitializer(

            UserRepository userRepository,

            PasswordEncoder passwordEncoder,

            @Value("${ADMIN_USERNAME:}")
            String adminUsername,

            @Value("${ADMIN_EMAIL:}")
            String adminEmail,

            @Value("${ADMIN_PASSWORD:}")
            String adminPassword) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.adminUsername = adminUsername;

        this.adminEmail = adminEmail;

        this.adminPassword = adminPassword;
    }


    /*
     * =====================================================
     * INIZIALIZZAZIONE ADMIN
     * =====================================================
     *
     * Questo metodo viene eseguito
     * all'avvio dell'applicazione.
     *
     * Le credenziali vengono lette
     * dalle variabili d'ambiente.
     */

    @Override
    @Transactional
    public void run(ApplicationArguments args) {


        /*
         * Se non sono state fornite
         * credenziali, non creiamo
         * automaticamente un ADMIN.
         */

        if (adminUsername.isBlank()
                && adminEmail.isBlank()
                && adminPassword.isBlank()) {

            logger.info(
                    "Nessun amministratore iniziale configurato."
            );

            return;
        }


        /*
         * Se viene fornita soltanto
         * una parte delle credenziali,
         * segnaliamo un errore.
         */

        if (adminUsername.isBlank()
                || adminEmail.isBlank()
                || adminPassword.isBlank()) {

            throw new IllegalStateException(
                    "Per creare l'ADMIN devi configurare "
                    + "ADMIN_USERNAME, ADMIN_EMAIL "
                    + "e ADMIN_PASSWORD."
            );
        }


        /*
         * Normalizzazione.
         */

        String username =
                adminUsername.trim()
                        .toLowerCase(Locale.ROOT);

        String email =
                adminEmail.trim()
                        .toLowerCase(Locale.ROOT);


        /*
         * Validazione minima.
         */

        if (username.length() < 3) {

            throw new IllegalStateException(
                    "Lo username ADMIN deve avere "
                    + "almeno 3 caratteri."
            );
        }


        if (!email.contains("@")) {

            throw new IllegalStateException(
                    "Email ADMIN non valida."
            );
        }


        if (adminPassword.length() < 8) {

            throw new IllegalStateException(
                    "La password ADMIN deve avere "
                    + "almeno 8 caratteri."
            );
        }


        /*
         * Se lo username esiste già,
         * non modifichiamo automaticamente
         * ruolo e password di quell'utente.
         */

        if (userRepository
                .existsByUsernameIgnoreCase(username)) {

            logger.info(
                    "Username amministratore già presente."
            );

            return;
        }


        /*
         * Impediamo di utilizzare
         * un'email già presente.
         */

        if (userRepository
                .existsByEmailIgnoreCase(email)) {

            throw new IllegalStateException(
                    "L'email ADMIN è già utilizzata."
            );
        }


        /*
         * Creazione amministratore.
         */

        User admin = new User();

        admin.setUsername(username);

        admin.setEmail(email);

        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setRole(Role.ADMIN);


        /*
         * Salvataggio PostgreSQL.
         */

        userRepository.save(admin);


        logger.info(
                "Amministratore iniziale creato correttamente."
        );
    }

}