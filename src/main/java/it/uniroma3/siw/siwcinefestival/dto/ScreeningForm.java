package it.uniroma3.siw.siwcinefestival.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import it.uniroma3.siw.siwcinefestival.model.ScreeningStatus;

import jakarta.validation.constraints.NotNull;


public class ScreeningForm {


    @NotNull(message = "Seleziona un festival.")
    private Long festivalId;


    @NotNull(message = "Seleziona un film.")
    private Long filmId;


    @NotNull(message = "Seleziona una sala.")
    private Long roomId;


    @NotNull(message = "La data è obbligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;


    @NotNull(message = "L'orario è obbligatorio.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime time;


    @NotNull(message = "Lo stato è obbligatorio.")
    private ScreeningStatus status = ScreeningStatus.SCHEDULED;



    public ScreeningForm() {
    }



    public Long getFestivalId() {
        return festivalId;
    }


    public void setFestivalId(Long festivalId) {
        this.festivalId = festivalId;
    }


    public Long getFilmId() {
        return filmId;
    }


    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }


    public Long getRoomId() {
        return roomId;
    }


    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
}