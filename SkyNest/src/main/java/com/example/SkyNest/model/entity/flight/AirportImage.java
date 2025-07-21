package com.example.SkyNest.model.entity.flight;

import com.example.SkyNest.model.entity.hotel.Hotel;
import jakarta.persistence.*;

@Entity
@Table(name = "airport_image")
public class AirportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    private String type;

    @ManyToOne
    @JoinColumn(name = "airport_id",referencedColumnName = "id")
    private Airport airport;

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

    public Airport getAirport() {
        return this.airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }
}
