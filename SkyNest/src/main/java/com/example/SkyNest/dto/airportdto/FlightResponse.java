package com.example.SkyNest.dto.airportdto;

import com.example.SkyNest.dto.hoteldto.ImageDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightResponse {

    private  Long id;
    private String startingPoint;
    private String destination;
    private LocalDateTime StartingPointDate;
    private LocalDateTime destinationDate;
    private int numberOfChairs;
    private int numberOfEmptyChairs;
    private String tripType;
    private String status;
    private String airportName;
    private double basePrice;
    private double currentPrice;
    private String flightStatus;
    private List<ImageDTO> flightImage = new ArrayList<>();


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
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

    public List<ImageDTO> getFlightImage() {
        return flightImage;
    }

    public void setFlightImage(List<ImageDTO> flightImage) {
        this.flightImage = flightImage;
    }

    public int getNumberOfEmptyChairs() {
        return numberOfEmptyChairs;
    }

    public void setNumberOfEmptyChairs(int numberOfEmptyChairs) {
        this.numberOfEmptyChairs = numberOfEmptyChairs;
    }

    public String getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }
}
