package com.example.SkyNest.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class HotelBookingRequest {

    @Column(nullable = false)
    private int numberOfPerson;
    @Column(nullable = false)
    private int numberOfRoom;
    @Column(nullable = false)
    private String roomType;
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

    public int getNumberOfRoom() {
        return numberOfRoom;
    }

    public void setNumberOfRoom(int numberOfRoom) {
        this.numberOfRoom = numberOfRoom;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
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
