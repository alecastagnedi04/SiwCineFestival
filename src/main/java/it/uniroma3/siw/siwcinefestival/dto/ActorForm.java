package it.uniroma3.siw.siwcinefestival.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;


public class ActorForm {


    @NotBlank(message = "Il nome è obbligatorio.")
    @Size(max = 255, message = "Il nome è troppo lungo.")
    private String name;


    @NotBlank(message = "Il cognome è obbligatorio.")
    @Size(max = 255, message = "Il cognome è troppo lungo.")
    private String surname;


    @NotNull(message = "La data di nascita è obbligatoria.")
    @PastOrPresent(
        message = "La data di nascita non può essere futura."
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;


    @NotBlank(message = "La nazionalità è obbligatoria.")
    private String nationality;



    public ActorForm() {
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }


    public LocalDate getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    public String getNationality() {
        return nationality;
    }


    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}