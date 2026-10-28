package it.uniroma3.siw.siwcinefestival.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "cinema_rooms")
public class CinemaRoom {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String address;


    @Column(nullable = false)
    private Integer capacity;



    /*
     * =====================================================
     * CINEMA ROOM - SCREENING
     * =====================================================
     *
     * Una sala può ospitare
     * molte proiezioni.
     *
     * Ogni proiezione si svolge
     * in una sola sala.
     *
     * Relazione 1:N.
     */
    @OneToMany(
        mappedBy = "room",
        fetch = FetchType.LAZY
    )
    private List<Screening> screenings =
            new ArrayList<>();



    public CinemaRoom() {
    }



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public List<Screening> getScreenings() {
        return screenings;
    }
}