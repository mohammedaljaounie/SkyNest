package com.example.SkyNest.model.entity.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "PlaceNearTheHotelImage")
public class PlaceNearTheHotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String name;
    private String type;

    @ManyToOne()
    @JoinColumn(name = "place_id",referencedColumnName = "id")
    @JsonIgnore
    private PlaceNearTheHotel placeNearTheHotel;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PlaceNearTheHotel getPlaceNearTheHotel() {
        return placeNearTheHotel;
    }

    public void setPlaceNearTheHotel(PlaceNearTheHotel placeNearTheHotel) {
        this.placeNearTheHotel = placeNearTheHotel;
    }
}
