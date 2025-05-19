package com.example.SkyNest.dto;

import com.example.SkyNest.model.entity.Hotel;
import jakarta.persistence.Column;

public class HotelRequestUpdate {

    private String name;

    private String description;

    private String location;

    private Hotel hotel;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
