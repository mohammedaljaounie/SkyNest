package com.example.SkyNest.model.entity.flight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "flightImage")
public class FlightImage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    private String type;

    @ManyToOne
    @JoinColumn(name = "flight_id",referencedColumnName = "id")
    @JsonIgnore
    private Flight flight;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Flight getFlight() {
        return this.flight;
    }

    public void setFlight(Flight airport) {
        this.flight = airport;
    }
}
