package it.uniroma3.siw.siwcinefestival.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    /*
     * =====================================================
     * PASSWORD ENCODER
     * =====================================================
     *
     * Le password non vengono salvate
     * in chiaro nel database.
     *
     * Usiamo BCrypt per calcolare
     * un hash sicuro della password.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * =====================================================
     * SECURITY FILTER CHAIN
     * =====================================================
     *
     * Definisce:
     *
     * - Pagine pubbliche
     * - Pagine riservate agli utenti
     * - Pagine riservate agli ADMIN
     * - Login
     * - Logout
     */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            /*
             * =================================================
             * AUTORIZZAZIONI
             * =================================================
             */

            .authorizeHttpRequests(auth -> auth

                /*
                 * Risorse statiche pubbliche.
                 */

                .requestMatchers(
                    "/css/**",
                    "/images/**",
                    "/react-program/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()


                /*
                 * Pagine pubbliche.
                 *
                 * Limitiamo questi permessi
                 * alle richieste GET.
                 */

                .requestMatchers(
    HttpMethod.GET,

    "/",
    "/login",
    "/register",

    "/festivals",
    "/festivals/**",

    "/films/*",

    "/films/*/reviews",

    "/api/festivals",
    "/api/festivals/*/screenings"

).permitAll()


                /*
                 * Registrazione pubblica.
                 */

                .requestMatchers(
                    HttpMethod.POST,
                    "/register"
                ).permitAll()


                /*
                 * Tutte le funzionalità ADMIN.
                 */

                .requestMatchers(
                    "/admin",
                    "/admin/**"
                ).hasRole("ADMIN")


                /*
                 * Creazione delle recensioni.
                 */

                .requestMatchers(
                    "/films/*/reviews"
                ).authenticated()


                /*
                 * Modifica, eliminazione e
                 * form delle recensioni.
                 */

                .requestMatchers(
                    "/films/*/reviews/**"
                ).authenticated()


                /*
                 * Tutte le altre richieste
                 * richiedono autenticazione.
                 */

                .anyRequest().authenticated()
            )


            /*
             * =================================================
             * LOGIN
             * =================================================
             *
             * GET /login:
             * mostra la pagina Thymeleaf.
             *
             * POST /login:
             * elaborato da Spring Security.
             */

            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .defaultSuccessUrl("/", false)

                .failureUrl("/login?error")

                .permitAll()
            )


            /*
             * =================================================
             * LOGOUT
             * =================================================
             *
             * Il logout viene effettuato
             * attraverso una richiesta POST.
             */

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout")

                .invalidateHttpSession(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            );


        /*
         * La protezione CSRF rimane
         * attiva per impostazione predefinita.
         *
         * I form Thymeleaf POST dovranno
         * inviare il token CSRF.
         */

        return http.build();
    }

}