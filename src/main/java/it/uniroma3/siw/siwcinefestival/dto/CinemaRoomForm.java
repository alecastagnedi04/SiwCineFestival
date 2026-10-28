package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class CinemaRoomForm {


    @NotBlank(message = "Il nome della sala è obbligatorio.")
    @Size(max = 255, message = "Il nome è troppo lungo.")
    private String name;


    @NotBlank(message = "L'indirizzo è obbligatorio.")
    private String address;


    @NotNull(message = "La capienza è obbligatoria.")
    @Positive(message = "La capienza deve essere maggiore di zero.")
    private Integer capacity;



    public CinemaRoomForm() {
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public Integer getCapacity() {
        return capacity;
    }


    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}