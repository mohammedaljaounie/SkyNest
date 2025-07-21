package com.example.SkyNest.model.entity.flight;

import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "airport_booking")
public class FlightBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String startingPoint;
    @Column(nullable = false)
    private String destination;
    @Column(nullable = false)
    private LocalDateTime startingPointDate;
    @Column(nullable = false)
    private LocalDateTime destinationDate;
    @Column(nullable = false)
    private StatusEnumForBooking status;
    @Column(nullable = false)
    private int numberOfPerson;
    @Column(nullable = false)
    private TripTypeAndReservation tripType;
    @Column(nullable = false)
    private double basePrice;
    @Column(nullable = false)
    private double currentPrice;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id",referencedColumnName = "id")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "airport_id",referencedColumnName = "id")
    private Airport  airport;


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

    public StatusEnumForBooking getStatus() {
        return status;
    }

    public void setStatus(StatusEnumForBooking status) {
        this.status = status;
    }

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }
}
