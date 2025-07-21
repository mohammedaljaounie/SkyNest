package com.example.SkyNest.dto.airportdto;

import com.example.SkyNest.dto.hoteldto.ImageDTO;

import java.util.ArrayList;
import java.util.List;

public class AirportResponse {

    private Long airportId;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String location;
    private String ownerName;
    private double avgRating;
    private int ratingCount;
    private List<ImageDTO> airportImages = new ArrayList<>();

    public Long getAirportId() {
        return airportId;
    }

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<ImageDTO> getAirportImages() {
        return airportImages;
    }

    public void setAirportImages(List<ImageDTO> airportImages) {
        this.airportImages = airportImages;
    }
}
