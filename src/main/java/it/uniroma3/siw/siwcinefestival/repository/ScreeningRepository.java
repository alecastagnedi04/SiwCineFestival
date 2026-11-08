package it.uniroma3.siw.siwcinefestival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwcinefestival.model.Screening;
import it.uniroma3.siw.siwcinefestival.model.ScreeningStatus;

public interface ScreeningRepository
        extends JpaRepository<Screening, Long> {

    /* PROGRAMMA DI UN FESTIVAL
     * Recupera tutte le proiezioni di un festival ordinate per data e ora.
     *
     * Carico anche Film e CinemaRoom per mostrare il programma.
     */
    @EntityGraph(attributePaths = {
            "film",
            "room"
    })
    List<Screening> findByFestivalIdOrderByDateAscTimeAsc(
            Long festivalId);

    /* PROIEZIONI DI UN FILM
     * Recupera le proiezioni associate a un Film.
     *
     * Carico anche Festival e Sala per mostrarli nel dettaglio film.
     */
    @EntityGraph(attributePaths = {
            "festival",
            "room"
    })
    List<Screening> findByFilmIdOrderByDateAscTimeAsc(
            Long filmId);

    /* PROIEZIONI DI UNA SALA
     * Esclude le proiezioni con lo stato indicato. es: excludedStatus = CANCELLED  Recupero le proiezioni non cancellate.
     *
     * Carico anche Film perché mi serve la durata del film per calcolare l'orario di fine.
     */
    @EntityGraph(attributePaths = { "film" })
    List<Screening> findByRoomIdAndStatusNotOrderByDateAscTimeAsc(

            Long roomId,

            ScreeningStatus excludedStatus);

    /* VERIFICA PROIEZIONI FILM - FESTIVAL
     * Serve quando vogliamo eliminare l'associazione tra Film e Festival.
     * Restituisce true se esiste almeno una proiezione che collega quel festival a quel film.
     */
    boolean existsByFestivalIdAndFilmId(

            Long festivalId,

            Long filmId);

    /* PROIEZIONI DI UN FILM IN UN FESTIVAL
     * Restituisce le proiezioni di un film in uno specifico festival.
     */
    List<Screening> findByFestivalIdAndFilmId(

            Long festivalId,

            Long filmId);
            

    /*BENCHMARK 1 - LAZY
     * Recupera le proiezioni del festival senza richiedere esplicitamente il caricamento di Film e CinemaRoom.
     *
     * Entrambe le associazioni sono LAZY nell'entità Screening.
     *
     * Quando accederemo a: screening.getFilm().getTitle(), screening.getRoom().getName()
     * Hibernate potrà eseguire query SQL aggiuntive.
     */
    @Query("""
            SELECT s
            FROM Screening s
            WHERE s.festival.id = :festivalId
            ORDER BY s.date ASC, s.time ASC, s.id ASC
            """)
    List<Screening> findProgramLazyByFestivalId(

            @Param("festivalId") Long festivalId);

    /* BENCHMARK 2 - ENTITY GRAPH
     * EntityGraph richiede il caricamento di Film e CinemaRoom.
     *
     * Il benchmark confronterà:
     * - Numero query SQL
     * - Tempo di esecuzione
     * - Numero di oggetti caricati
     */
    @EntityGraph(attributePaths = {
            "film",
            "room"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("""
            SELECT s
            FROM Screening s
            WHERE s.festival.id = :festivalId
            ORDER BY s.date ASC, s.time ASC, s.id ASC
            """)
    List<Screening> findProgramWithEntityGraphByFestivalId(

            @Param("festivalId") Long festivalId);

}