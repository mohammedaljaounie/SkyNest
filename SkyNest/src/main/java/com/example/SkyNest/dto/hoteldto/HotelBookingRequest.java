package com.example.SkyNest.dto.hoteldto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.Set;

public class HotelBookingRequest {

    @Column(nullable = false)
    private int numberOfPerson;
    @Column(nullable = false)
    private LocalDate launchDate;
    @Column(nullable = false)
    private LocalDate departureDate;
    @Min(value = 20)
    @Max(value = 100)
    @Column(nullable = false)
    private int paymentRatio;
    private Set<Long> setOfRooms;
    private long  hotelId;

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }


    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public int getPaymentRatio() {
        return paymentRatio;
    }

    public void setPaymentRatio(int paymentRatio) {
        this.paymentRatio = paymentRatio;
    }

    public Set<Long> getSetOfRooms() {
        return setOfRooms;
    }

    public void setSetOfRooms(Set<Long> setOfRooms) {
        this.setOfRooms = setOfRooms;
    }

    public long  getHotelId() {
        return hotelId;
    }

    public void setHotelId(long  hotel) {
        this.hotelId = hotel;
    }
}
