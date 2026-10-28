package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class DirectorForm {


    @NotBlank(message = "Il nome è obbligatorio.")
    @Size(max = 255, message = "Il nome è troppo lungo.")
    private String name;


    @NotBlank(message = "Il cognome è obbligatorio.")
    @Size(max = 255, message = "Il cognome è troppo lungo.")
    private String surname;



    public DirectorForm() {
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
}