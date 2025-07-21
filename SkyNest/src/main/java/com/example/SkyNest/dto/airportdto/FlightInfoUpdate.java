package com.example.SkyNest.dto.airportdto;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public class FlightInfoUpdate {

    private String destination;
    private LocalDateTime StartingPointDate;
    private LocalDateTime destinationDate;
    private double price;
    @Column(nullable = false)
    private Long flightId;


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getStartingPointDate() {
        return StartingPointDate;
    }

    public void setStartingPointDate(LocalDateTime startingPointDate) {
        StartingPointDate = startingPointDate;
    }

    public LocalDateTime getDestinationDate() {
        return destinationDate;
    }

    public void setDestinationDate(LocalDateTime destinationDate) {
        this.destinationDate = destinationDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long airportId) {
        this.flightId = airportId;
    }
}
