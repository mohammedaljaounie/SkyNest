package com.example.SkyNest.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class HotelRoomRequest {
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
}
