package com.example.SkyNest.model.entity.hotel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "PlaceNearTheHotel")
public class PlaceNearTheHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String address;

    @ManyToOne
    @JoinColumn(name = "hotel_id",referencedColumnName = "id")
    private Hotel hotel;

    @OneToMany(mappedBy = "placeNearTheHotel",cascade = CascadeType.REMOVE)
    private List<PlaceNearTheHotelImage> placeNearTheHotelImageList;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<PlaceNearTheHotelImage> getPlaceNearTheHotelImageList() {
        return placeNearTheHotelImageList;
    }

    public void setPlaceNearTheHotelImageList(List<PlaceNearTheHotelImage> placeNearTheHotelImageList) {
        this.placeNearTheHotelImageList = placeNearTheHotelImageList;
    }
}
