package com.example.SkyNest.model.entity.flight;

import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String startingPoint;
    private String destination;
    private LocalDateTime startingPointDate;
    private LocalDateTime destinationDate;
    private StatusEnumForFlight status;
    private int numberOfChairs;
    private int numberOfEmptyChairs;
    private TripTypeAndReservation tripType;
    private double basePrice;
    private double currentPrice;
    private boolean enable;
    @ManyToOne
    @JoinColumn(name = "airport_id",referencedColumnName = "id")
    private Airport airport;

    @OneToMany(mappedBy = "flight")
    private List<FlightImage> flightImages = new ArrayList<>();

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
        return startingPointDate;
    }

    public void setStartingPointDate(LocalDateTime startingPointDate) {
        this.startingPointDate = startingPointDate;
    }

    public LocalDateTime getDestinationDate() {
        return destinationDate;
    }

    public void setDestinationDate(LocalDateTime destinationDate) {
        this.destinationDate = destinationDate;
    }

    public void setStatus(StatusEnumForFlight status) {
        this.status = status;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public StatusEnumForFlight getStatus() {
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

    public List<FlightImage> getFlightImages() {
        return flightImages;
    }

    public void setFlightImages(List<FlightImage> flightImages) {
        this.flightImages = flightImages;
    }

    public int getNumberOfEmptyChairs() {
        return numberOfEmptyChairs;
    }

    public void setNumberOfEmptyChairs(int numberOfEmptyChairs) {
        this.numberOfEmptyChairs = numberOfEmptyChairs;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
