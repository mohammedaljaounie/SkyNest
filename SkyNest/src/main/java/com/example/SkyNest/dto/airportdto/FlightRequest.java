package com.example.SkyNest.dto.airportdto;

import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.myEnum.TripTypeAndReservation;

import java.time.LocalDateTime;

public class FlightRequest {

    private String startingPoint;
    private String destination;
    private LocalDateTime StartingPointDate;
    private LocalDateTime destinationDate;
    private int numberOfChairs;
    private TripTypeAndReservation tripType;
    private Long  airportId;

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

    public TripTypeAndReservation getTripType() {
        return tripType;
    }

    public void setTripType(TripTypeAndReservation tripType) {
        this.tripType = tripType;
    }

    public Long getAirportId() {
        return airportId;
    }

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }
}
