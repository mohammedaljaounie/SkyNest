package com.example.SkyNest.dto.hoteldto;

import com.example.SkyNest.myEnum.TripTypeAndReservation;

public class RoomRequest {

    private TripTypeAndReservation roomType;

    private double price;

    private int numberOfPerson;
    private int numberOfBed;
    private boolean isHasKitchen;


    public TripTypeAndReservation getRoomType() {
        return roomType;
    }

    public void setRoomType(TripTypeAndReservation roomType) {
        this.roomType = roomType;
    }



    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public int getNumberOfBed() {
        return numberOfBed;
    }

    public void setNumberOfBed(int numberOfBed) {
        this.numberOfBed = numberOfBed;
    }

    public boolean isHasKitchen() {
        return isHasKitchen;
    }

    public void setHasKitchen(boolean hasKitchen) {
        isHasKitchen = hasKitchen;
    }
}
