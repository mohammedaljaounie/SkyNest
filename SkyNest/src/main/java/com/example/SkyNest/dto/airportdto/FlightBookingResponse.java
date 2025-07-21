package com.example.SkyNest.dto.airportdto;

import java.time.LocalDateTime;

public class FlightBookingResponse {

    private Long id;
    private String startingPoint;
    private String destination;
    private LocalDateTime StartingPointDate;
    private LocalDateTime destinationDate;
    private String status;
    private int numberOfChairs;
    private String tripType;
    private double basePrice;
    private double currentPrice;
    private String userName;
    private String airportName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfChairs() {
        return numberOfChairs;
    }

    public void setNumberOfChairs(int numberOfChairs) {
        this.numberOfChairs = numberOfChairs;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }
}
