package it.uniroma3.siw.siwcinefestival;

import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.repository.ScreeningRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "logging.level.root=ERROR",
        "logging.level.org.hibernate.SQL=OFF",
        "logging.level.org.hibernate.orm.jdbc.bind=OFF",
        "spring.main.banner-mode=off",
        "ADMIN_USERNAME=",
        "ADMIN_EMAIL=",
        "ADMIN_PASSWORD="
})

@Transactional(readOnly = true)
class AccessoDatiTest {


    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Test
    void confrontaLazyEdEntityGraph() {

        /*
         * Cerchiamo un festival con almeno una proiezione.
         * Questa ricerca NON fa parte del benchmark.
         */

        List<Screening> tutteLeProiezioni =
                screeningRepository.findAll();

        if (tutteLeProiezioni.isEmpty()) {
            throw new IllegalStateException(
                    "Deve esistere almeno una proiezione nel database"
            );
        }

        Long festivalId =
                tutteLeProiezioni.get(0)
                        .getFestival()
                        .getId();


        /*
         * Recuperiamo le statistiche di Hibernate.
         */

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics =
                sessionFactory.getStatistics();

        statistics.setStatisticsEnabled(true);


        /*
         * ==========================================
         * STRATEGIA 1: LAZY
         * ==========================================
         */

        entityManager.clear();

        statistics.clear();

        long inizioLazy = System.nanoTime();


        List<Screening> proiezioniLazy =
                screeningRepository
                        .findProgramLazyByFestivalId(festivalId);


        /*
         * Accediamo ai dati collegati.
         *
         * Film e CinemaRoom sono LAZY.
         * Hibernate potrebbe eseguire query aggiuntive.
         */

        for (Screening screening : proiezioniLazy) {

            screening.getFilm().getTitle();

            screening.getRoom().getName();
        }


        long fineLazy = System.nanoTime();


        long queryLazy =
                statistics.getPrepareStatementCount();

        long entitaLazy =
                statistics.getEntityLoadCount();

        double tempoLazy =
                (fineLazy - inizioLazy) / 1_000_000.0;


        List<Long> idLazy =
                proiezioniLazy.stream()
                        .map(Screening::getId)
                        .toList();


        /*
         * ==========================================
         * STRATEGIA 2: ENTITY GRAPH
         * ==========================================
         */

        entityManager.clear();

        statistics.clear();

        long inizioEntityGraph = System.nanoTime();


        List<Screening> proiezioniEntityGraph =
                screeningRepository
                        .findProgramWithEntityGraphByFestivalId(festivalId);


        /*
         * Accediamo esattamente agli stessi dati.
         *
         * Con EntityGraph, Film e CinemaRoom
         * sono stati caricati insieme alle proiezioni.
         */

        for (Screening screening : proiezioniEntityGraph) {

            screening.getFilm().getTitle();

            screening.getRoom().getName();
        }


        long fineEntityGraph = System.nanoTime();


        long queryEntityGraph =
                statistics.getPrepareStatementCount();

        long entitaEntityGraph =
                statistics.getEntityLoadCount();

        double tempoEntityGraph =
                (fineEntityGraph - inizioEntityGraph)
                        / 1_000_000.0;


        List<Long> idEntityGraph =
                proiezioniEntityGraph.stream()
                        .map(Screening::getId)
                        .toList();


        /*
         * Verifichiamo che le strategie abbiano
         * recuperato le stesse proiezioni.
         */

        assertEquals(idLazy, idEntityGraph);


        /*
         * ==========================================
         * OUTPUT FINALE
         * ==========================================
         */

        System.out.println();

        System.out.println(
                "=== ANALISI ACCESSO AL PROGRAMMA DEL FESTIVAL ==="
        );

        System.out.println("Festival ID: " + festivalId);

        System.out.println();


        System.out.println("--- Strategia 1: LAZY ---");

        System.out.println(
                "Proiezioni caricate: " + proiezioniLazy.size()
        );

        System.out.println(
                "Entita' caricate: " + entitaLazy
        );

        System.out.println(
                "Query SQL eseguite: " + queryLazy
        );

        System.out.printf(
                "Tempo complessivo: %.2f ms%n",
                tempoLazy
        );


        System.out.println();


        System.out.println("--- Strategia 2: ENTITY GRAPH ---");

        System.out.println(
                "Proiezioni caricate: " + proiezioniEntityGraph.size()
        );

        System.out.println(
                "Entita' caricate: " + entitaEntityGraph
        );

        System.out.println(
                "Query SQL eseguite: " + queryEntityGraph
        );

        System.out.printf(
                "Tempo complessivo: %.2f ms%n",
                tempoEntityGraph
        );


        System.out.println();

        System.out.println(
                "Stesso insieme di dati: SI"
        );

        System.out.println(
                "-----------------------------------------"
        );
    }
}