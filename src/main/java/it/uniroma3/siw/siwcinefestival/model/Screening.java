package it.uniroma3.siw.siwcinefestival.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "screenings")
public class Screening {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    /*
     * Data della proiezione.
     */
    @Column(nullable = false)
    private LocalDate date;



    /*
     * Ora di inizio della proiezione.
     */
    @Column(nullable = false)
    private LocalTime time;



    /*
     * Stato:
     *
     * SCHEDULED
     * COMPLETED
     * CANCELLED
     *
     * EnumType.STRING memorizza
     * il nome dello stato nel database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreeningStatus status =
            ScreeningStatus.SCHEDULED;



    /*
     * =====================================================
     * SCREENING - FESTIVAL
     * =====================================================
     *
     * Ogni proiezione appartiene
     * a un solo festival.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "festival_id",
        nullable = false
    )
    private Festival festival;



    /*
     * =====================================================
     * SCREENING - FILM
     * =====================================================
     *
     * Ogni proiezione riguarda
     * un solo film.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "film_id",
        nullable = false
    )
    private Film film;



    /*
     * =====================================================
     * SCREENING - CINEMA ROOM
     * =====================================================
     *
     * Ogni proiezione si svolge
     * in una sola sala.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "room_id",
        nullable = false
    )
    private CinemaRoom room;



    public Screening() {
    }



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public LocalTime getTime() {
        return time;
    }


    public void setTime(LocalTime time) {
        this.time = time;
    }


    public ScreeningStatus getStatus() {
        return status;
    }


    public void setStatus(ScreeningStatus status) {
        this.status = status;
    }


    public Festival getFestival() {
        return festival;
    }


    public void setFestival(Festival festival) {
        this.festival = festival;
    }


    public Film getFilm() {
        return film;
    }


    public void setFilm(Film film) {
        this.film = film;
    }


    public CinemaRoom getRoom() {
        return room;
    }


    public void setRoom(CinemaRoom room) {
        this.room = room;
    }
}