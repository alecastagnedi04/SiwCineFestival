package it.uniroma3.siw.siwcinefestival.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class FestivalForm {


    @NotBlank(message = "Il nome del festival è obbligatorio.")
    @Size(max = 255, message = "Il nome è troppo lungo.")
    private String name;


    @NotNull(message = "L'anno è obbligatorio.")
    @Min(value = 1895, message = "L'anno non è valido.")
    private Integer year;


    @NotBlank(message = "La città è obbligatoria.")
    private String city;


    @NotNull(message = "La data di inizio è obbligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;


    @NotNull(message = "La data di fine è obbligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;


    @Size(max = 5000, message = "La descrizione è troppo lunga.")
    private String description;



    public FestivalForm() {
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Integer getYear() {
        return year;
    }


    public void setYear(Integer year) {
        this.year = year;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public LocalDate getStartDate() {
        return startDate;
    }


    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }


    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}