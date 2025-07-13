package com.example.SkyNest.model.entity.flight;

import com.example.SkyNest.myEnum.StatusEnum;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String startingPoint;
    private String destination;
    private LocalDateTime StartingPointDate;
    private LocalDateTime destinationDate;
    private StatusEnum status;
    private int numberOfChairs;
    private TripTypeAndReservation tripType;
    @ManyToOne
    @JoinColumn(name = "airport_id",referencedColumnName = "id")
    private Airport airport;

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

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public StatusEnum getStatus() {
        return status;
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
}
